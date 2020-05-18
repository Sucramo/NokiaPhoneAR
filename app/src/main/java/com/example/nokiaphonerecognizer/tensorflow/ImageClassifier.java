package com.example.nokiaphonerecognizer.tensorflow;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import androidx.annotation.NonNull;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Classifies images with Tensorflow Lite.
 */
public class ImageClassifier {


    /**
     * Name of the model file stored in Assets.
     */

    private static final String MODEL_PATH = "model.tflite";

    /**
     * Name of the label file stored in Assets.
     */
    private static final String LABEL_PATH = "dict.txt";

    /**
     * Number of results to show in the UI.
     */
    private static final int RESULTS_TO_SHOW = 1;

    /**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    private Interpreter tflite;

    /**
     * Labels corresponding to the output of the vision model.
     */
    private List<String> labelList;

    /**
     * A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs.
     */
    private ByteBuffer imgData = null;

    /**
     * Dimensions of inputs.
     */
    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 3;

    static final int DIM_IMG_SIZE_X = 224;
    static final int DIM_IMG_SIZE_Y = 224;

    /**
     * An array to hold inference results, to be feed into Tensorflow Lite as outputs.
     */
    private byte[][] labelProbArray = null;
    /**
     * multi-stage low pass filter
     **/
    private byte[][] filterLabelProbArray = null;

    private static final int FILTER_STAGES = 3;
    private static final float FILTER_FACTOR = 0.4f;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    /* Preallocated buffers for storing image data in. */
    private int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

    /**
     * Initializes an {@code ImageClassifier}.
     */
    ImageClassifier(Activity activity) throws IOException {
        tflite = new Interpreter(loadModelFile(activity));
        labelList = loadLabelList(activity);
        imgData =
                ByteBuffer.allocateDirect(
                        DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        labelProbArray = new byte[1][labelList.size()];
        filterLabelProbArray = new byte[FILTER_STAGES][labelList.size()];
    }

    /**
     * Classifies a frame from the preview stream and returns the string to show in the ui.
     */
    String classifyFrame(Bitmap bitmap) {
        if (tflite == null) {
            return "";
        }
        convertBitmapToByteBuffer(bitmap);
        // Run the model
        tflite.run(imgData, labelProbArray);

        // smooth the results
        applyFilter();

        //Set fireBaseName-variable based on phone label
        setFireBaseName(phoneModel());
        // Return the text to show in the UI
        String textToShow = fireBaseName + " (" + confidenceLevel() + "%)";

        return textToShow;
    }

    /**
     * Method that returns the confidence level of the classifier
     */
    int confidenceLevel () {
        if (tflite == null) {
            return 0;
        }

        for (int i = 0; i < labelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(labelList.get(i), (float) labelProbArray[0][i]));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }
        int value = 0;
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            value = label.getValue().intValue();
        }
        return value;
    }

    /**
     * Method that returns the label of the phone
     */
    String phoneModel () {
        for (int i = 0; i < labelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(labelList.get(i), (float) labelProbArray[0][i]));
            if (sortedLabels.size() > RESULTS_TO_SHOW) {
                sortedLabels.poll();
            }
        }
        String name = "";
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            name = label.getKey();
        }
        return name;
    }

    /**
     * Method that set the variable fireBaseName depending on the classified phone label
     */
    private static String fireBaseName = "";
     private void setFireBaseName(String label) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Phones");

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fireBaseName = dataSnapshot.child(label).child("Model name").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    };

    void applyFilter() {
        int num_labels = labelList.size();

        // Low pass filter `labelProbArray` into the first stage of the filter.
        for (int j = 0; j < num_labels; ++j) {
            filterLabelProbArray[0][j] += FILTER_FACTOR * (labelProbArray[0][j] -
                    filterLabelProbArray[0][j]);
        }
        // Low pass filter each stage into the next.
        for (int i = 1; i < FILTER_STAGES; ++i) {
            for (int j = 0; j < num_labels; ++j) {
                filterLabelProbArray[i][j] += FILTER_FACTOR * (
                        filterLabelProbArray[i - 1][j] -
                                filterLabelProbArray[i][j]);

            }
        }
        // Copy the last stage filter output back to `labelProbArray`.
        for (int j = 0; j < num_labels; ++j) {
            labelProbArray[0][j] = filterLabelProbArray[FILTER_STAGES - 1][j];
        }
    }

    /**
     * Closes tflite to release resources.
     */
    public void close() {
        tflite.close();
        tflite = null;
    }

    /**
     * Reads label list from Assets.
     */
    private List<String> loadLabelList(Activity activity) throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    /**
     * Writes Image data into a {@code ByteBuffer}.
     */
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                imgData.put((byte) ((((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
                imgData.put((byte) ((((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
                imgData.put((byte) ((((val) & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
            }
        }
    }
}
