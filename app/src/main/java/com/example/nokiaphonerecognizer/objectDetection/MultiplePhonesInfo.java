package com.example.nokiaphonerecognizer.objectDetection;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.widget.ImageView;

import android.view.View;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nokiaphonerecognizer.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MultiplePhonesInfo extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageReference mStorageRef;

    ImageView imageView;
    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<String> phonesExtra;
    ArrayList<String> dataBasePhoneNames;
    ArrayList<String> dataBaseReleases;
    ArrayList<String> dataBaseImageURLs;

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
        dataBaseImageURLs = new ArrayList<>();

        /*Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/imagerecog-271511.appspot.com/o/image%2F2300.jpg?alt=media&token=a95ec8a8-c876-4f4a-a6c1-663c485fdba7")
                .into(imageView);
*/
        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < phonesExtra.size(); i++) {
                    dataBasePhoneNames.add(dataSnapshot.child(phonesExtra.get(i)).child("Model name").getValue(String.class));
                    dataBaseReleases.add(dataSnapshot.child(phonesExtra.get(i)).child("Release date").getValue(String.class));
                    dataBaseImageURLs.add(dataSnapshot.child(phonesExtra.get(i)).child("image URL").getValue(String.class));


                }
                adapter.notifyDataSetChanged();
                System.out.println("gggg" + dataBaseImageURLs);
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
        adapter = new Adapter(this, dataBasePhoneNames, dataBaseReleases, dataBaseImageURLs);
        recyclerView.setAdapter(adapter);
    }

}
