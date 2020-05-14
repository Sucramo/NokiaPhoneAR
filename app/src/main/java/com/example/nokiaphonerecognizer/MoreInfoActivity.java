package com.example.nokiaphonerecognizer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.Nullable;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MoreInfoActivity extends Activity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    Button okButton;
    TextView textViewModelName;
    TextView textViewReleaseDate;
    TextView textViewBodyText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        okButton = (Button) findViewById(R.id.ok_button);
        textViewModelName = (TextView) findViewById(R.id.model_name);
        textViewReleaseDate = (TextView) findViewById(R.id.release_date);
        textViewBodyText = (TextView) findViewById(R.id.body_text);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String modelId = "iPhone 4";
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Phones").child(modelId);

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String stringModelName = dataSnapshot.child("Model name").getValue(String.class);
                textViewModelName.append(stringModelName);
                String stringReleaseDate = dataSnapshot.child("Release date").getValue(String.class);
                textViewReleaseDate.append("Released " + stringReleaseDate);
                String stringBodyText = dataSnapshot.child("Description").getValue(String.class);
                textViewBodyText.append(stringBodyText);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
