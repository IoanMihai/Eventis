package com.ioanmihai.eventis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView myEventsList;
    private DatabaseReference UsersRef, EventsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myEventsList = findViewById(R.id.recView);
        myEventsList.setHasFixedSize(true);
        myEventsList.setLayoutManager(new LinearLayoutManager(this));
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        fetch();
    }

    private void fetch() {
        //TODO: Recycler View
    }
}