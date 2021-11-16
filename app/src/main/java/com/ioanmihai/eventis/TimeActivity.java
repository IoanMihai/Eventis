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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class TimeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button dateButton, timeButton, nextButton;
    private DatabaseReference UsersRef, EventsRef;
    private String current_user_id, eventRandomName;
    private FirebaseAuth mAuth;

    private DialogFragment datePicker, timePicker;
    private ProgressDialog loadingBar;
    private String saveCurrentTime, saveCurrentDate;
    public boolean okDate, okTime;
    private TextInputEditText eventDescrpition;
    private String desc;

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


        /*Calendar date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(time.getTime());*/

        eventDescrpition = findViewById(R.id.event_description);
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

                desc = eventDescrpition.getText().toString();

                if (okDate && okTime && desc.length() <= 140 && desc.length() != 0) {
                    HashMap descMap = new HashMap();
                    descMap.put("description", desc);
                    EventsRef.child(eventRandomName).updateChildren(descMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(TimeActivity.this, "Error occured.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    SendUserToMainActivity();
                }else{
                    loadingBar.dismiss();
                    Toast.makeText(TimeActivity.this, "Your date, time or description of the event are invalid.", Toast.LENGTH_SHORT).show();
                }
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

    public boolean equal;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth/*, int hour, int minute */) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Integer dayy = new Integer(dayOfMonth);
        Integer monthh = new Integer(month + 1);
        Integer yearr = new Integer(year);
        String d = dayy.toString() + '-' + monthh.toString() + '-' + yearr.toString();


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        Integer currentYear = Integer.valueOf(format.format(cal.getTime()));
        format = new SimpleDateFormat("MM");
        Integer currentMonth = Integer.valueOf(format.format(cal.getTime()));
        format = new SimpleDateFormat("dd");
        Integer currentDay = Integer.valueOf(format.format(cal.getTime()));

        System.out.println(currentYear);
        System.out.println(currentMonth);
        System.out.println(currentDay);

        System.out.println(yearr);
        System.out.println(monthh);
        System.out.println(dayy);
        okDate = false;
        if (currentYear > yearr){
            Toast.makeText(TimeActivity.this, "Invalid date.", Toast.LENGTH_SHORT).show();
        }else if (currentYear.equals(yearr) && currentMonth > monthh){
            Toast.makeText(TimeActivity.this, "Invalid date.", Toast.LENGTH_SHORT).show();
        }else if (currentYear.equals(yearr) && currentMonth.equals(monthh) && currentDay > dayy){
            Toast.makeText(TimeActivity.this, "Invalid date.", Toast.LENGTH_SHORT).show();
        }else{

        okDate = true;
        HashMap dateMap = new HashMap();
        dateMap.put("eventDate", d);

        EventsRef.child(eventRandomName).updateChildren(dateMap)
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

        if (currentYear.equals(yearr) && currentMonth.equals(monthh) && currentDay.equals(dayy)){
            equal = true;
        }
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        Integer hourr = new Integer(hourOfDay);
        Integer minutee = new Integer(minute);
        String t = hourr.toString() + ':' + minutee.toString();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH");
        Integer currentHour = Integer.valueOf(format.format(cal.getTime()));
        format = new SimpleDateFormat("mm");
        Integer currentMinute = Integer.valueOf(format.format(cal.getTime()));

        if (equal){
            if (currentHour > hourr){
                Toast.makeText(TimeActivity.this, "Invalid time.", Toast.LENGTH_SHORT).show();
            }else if (currentHour.equals(hourr) && currentMinute > minutee){
                Toast.makeText(TimeActivity.this, "Invalid time.", Toast.LENGTH_SHORT).show();
            }else{
                okTime = true;
            }
        }else{
            okTime = true;
        }

        if (okTime) {

            HashMap timeMap = new HashMap();
            timeMap.put("eventTime", t);
            EventsRef.child(eventRandomName).updateChildren(timeMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TimeActivity.this, "Event updated succesfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TimeActivity.this, "Error occured while updating your event", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}