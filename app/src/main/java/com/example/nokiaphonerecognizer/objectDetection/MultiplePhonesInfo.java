package com.example.nokiaphonerecognizer.objectDetection;

import android.app.Activity;
import android.os.Bundle;

import com.example.nokiaphonerecognizer.R;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MultiplePhonesInfo extends Activity {

    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<String> phones;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_multiple_phones_info);

        Bundle extra = getIntent().getBundleExtra("EXTRA_PHONE_ARRAY");
        phones = (ArrayList<String>) extra.getSerializable("phones");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, phones);
        recyclerView.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}
