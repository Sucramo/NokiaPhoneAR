package productsearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import java.io.InputStream;
import java.net.URL;

/** Async task to download the image and then feed into the provided image view. */
class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "ImageDownloadTask";

    private final ImageView imageView;
    private final int maxImageWidth;

    ImageDownloadTask(ImageView imageView, int maxImageWidth) {
        this.imageView = imageView;
        this.maxImageWidth = maxImageWidth;
    }

    @Override
    @Nullable
    protected Bitmap doInBackground(String... urls) {
        if (TextUtils.isEmpty(urls[0])) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            InputStream inputStream = new URL(urls[0]).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Image download failed: " + urls[0]);
        }

        if (bitmap != null && bitmap.getWidth() > maxImageWidth) {
            int dstHeight = (int) ((float) maxImageWidth / bitmap.getWidth() * bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, maxImageWidth, dstHeight, /* filter= */ false);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(@Nullable Bitmap result) {
        if (result != null) {
            imageView.setImageBitmap(result);
        }
    }
}
