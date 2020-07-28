package com.example.nokiaphonerecognizer.objectDetection;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.nokiaphonerecognizer.R;

import androidx.annotation.Nullable;

public class MultiplePhonesInfo extends Activity {

    TextView multiplePhonesTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_multiple_phones_info);
        multiplePhonesTextView = (TextView) findViewById(R.id.multiple_phones_text_view);

        String phoneTitles = getIntent().getStringExtra("EXTRA_PHONE_MODELS");
        multiplePhonesTextView.setText(phoneTitles);



        super.onCreate(savedInstanceState);
    }
}
