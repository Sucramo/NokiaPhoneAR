package com.example.nokiaphonerecognizer.MlKitObjectDetection.preference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.nokiaphonerecognizer.R;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel;

public class MlKitActivity extends AppCompatActivity {

    // Specify the name you assigned in the Firebase console.
    FirebaseAutoMLRemoteModel remoteModel =
            new FirebaseAutoMLRemoteModel.Builder("your_remote_model").build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ml_kit);
    }
}
