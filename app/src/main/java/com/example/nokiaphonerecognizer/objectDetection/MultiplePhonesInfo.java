package com.example.nokiaphonerecognizer.objectDetection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.nokiaphonerecognizer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MultiplePhonesInfo extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<String> phonesExtra;
    ArrayList<String> dataBasePhoneNames;
    ArrayList<String> dataBaseReleases;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_multiple_phones_info);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extra = getIntent().getBundleExtra("EXTRA_PHONE_ARRAY");
        phonesExtra = (ArrayList<String>) extra.getSerializable("phones");

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Phones");

        dataBasePhoneNames = new ArrayList<>();
        dataBaseReleases = new ArrayList<>();

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < phonesExtra.size(); i++) {
                    dataBasePhoneNames.add(dataSnapshot.child(phonesExtra.get(i)).child("Model name").getValue(String.class));
                    dataBaseReleases.add(dataSnapshot.child(phonesExtra.get(i)).child("Release date").getValue(String.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
        setupListRecyclerView();

        super.onCreate(savedInstanceState);
    }

    private void setupListRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, dataBasePhoneNames, dataBaseReleases);
        recyclerView.setAdapter(adapter);
    }

}
