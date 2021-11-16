package com.ioanmihai.eventis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventActivity extends AppCompatActivity {

    private Bundle eventBundle;
    private String eventRandomName, title, description, imageUri, currentUserID;
    private DatabaseReference UsersRef, EventsRef;
    private FirebaseAuth mAuth;
    private TextView eventTitle, eventDescription, eventPrice;
    private ImageView eventImage;
    private Button eventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");

        Intent intent = getIntent();
        eventBundle = intent.getBundleExtra("bundle");
        eventRandomName = eventBundle.getString("randomname");

        eventTitle = findViewById(R.id.event_title);
        eventDescription = findViewById(R.id.event_description);
        eventPrice = findViewById(R.id.event_price);
        eventButton = findViewById(R.id.event_button);
        eventImage = findViewById(R.id.event_image);

        System.out.println(eventRandomName);

        EventsRef.child(eventRandomName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    eventTitle.setText(snapshot.child("title").getValue().toString());
                    eventDescription.setText(snapshot.child("description").getValue().toString());
                    imageUri = snapshot.child("postimage").getValue().toString();
                    Glide.with(EventActivity.this).load(imageUri).into(eventImage);
                }else{
                    Toast.makeText(EventActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}