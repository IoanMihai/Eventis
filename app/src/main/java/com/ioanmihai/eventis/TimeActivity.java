package com.ioanmihai.eventis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class TimeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button dateButton, timeButton, nextButton;
    private DatabaseReference UsersRef, EventsRef;
    private String current_user_id, eventRandomName;
    private FirebaseAuth mAuth;

    private DialogFragment datePicker, timePicker;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        eventRandomName = bundle.getString("eventname");
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        dateButton = findViewById(R.id.dateButton);
        nextButton = findViewById(R.id.nextButton);
        timeButton = findViewById(R.id.timeButton);
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Saving Information");
                loadingBar.setMessage("Please wait, while we are updating your new event...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                SendUserToMainActivity();
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(TimeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loadingBar.dismiss();
        startActivity(intent);
        finish();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth/*, int hour, int minute */) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);


        HashMap dateMap = new HashMap();
        dateMap.put("eventDate", c);

        EventsRef.child(current_user_id + eventRandomName).updateChildren(dateMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Toast.makeText(TimeActivity.this, "Event updated succesfully.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(TimeActivity.this, "Error occured while updating your event", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        HashMap timeMap = new HashMap();
        timeMap.put("eventTime", c);
        EventsRef.child(current_user_id + eventRandomName).updateChildren(timeMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Toast.makeText(TimeActivity.this, "Event updated succesfully.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(TimeActivity.this, "Error occured while updating your event", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}