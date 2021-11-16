package com.ioanmihai.eventis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private Button postButton;
    private EditText postEdit, titleEdit;
    private ProgressDialog loadingBar;
    private ImageButton selectEventImage;
    private String Description, Title;
    private CheckBox checkGames, checkMusic, checkPolitical, checkNature, checkOther, checkWebinar, checkConference, checkTech, checkBusiness;
    private Uri ImageUri;

    private static final int Gallery_Pick = 1;

    private StorageReference EventsImagesReference;
    private DatabaseReference UsersRef, EventsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, eventRandomName, downloadUrl, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        EventsImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");

        selectEventImage = findViewById(R.id.eventImage);
        postButton = findViewById(R.id.post_next_button);
        titleEdit = findViewById(R.id.titleEdit);
        loadingBar = new ProgressDialog(this);
        checkGames = findViewById(R.id.checkboxGames);
        checkMusic = findViewById(R.id.checkboxMusic);
        checkPolitical = findViewById(R.id.checkboxPolitical);
        checkNature = findViewById(R.id.checkboxNature);
        checkOther = findViewById(R.id.checkboxOther);
        checkWebinar = findViewById(R.id.checkboxWebinar);
        checkConference = findViewById(R.id.checkboxConference);
        checkTech = findViewById(R.id.checkboxTech);
        checkBusiness = findViewById(R.id.checkboxBusiness);

        selectEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateEventInfo();
            }
        });

    }

    private void ValidateEventInfo() {
        Title = titleEdit.getText().toString();
        int nr = 0;
        if (checkGames.isChecked()) {
            nr++;
        }
        if (checkMusic.isChecked()) {
            nr++;
        }
        if (checkPolitical.isChecked()) {
            nr++;
        }
        if (checkNature.isChecked()) {
            nr++;
        }
        if (checkOther.isChecked()) {
            nr++;
        }
        if (checkBusiness.isChecked()){
            nr ++;
        }
        if (checkTech.isChecked()){
            nr ++;
        }
        if (checkConference.isChecked()){
            nr ++;
        }
        if (checkWebinar.isChecked()){
            nr ++;
        }
        // aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
        // 35 si 148
        if (ImageUri == null) {
            Toast.makeText(this, "Please select event image...", Toast.LENGTH_SHORT).show();
        } else if (nr == 0) {
            Toast.makeText(this, "Please tell us what kind of event this is...", Toast.LENGTH_SHORT).show();
        } else if (nr > 1) {
            Toast.makeText(this, "Please select only one type of event...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Title)) {
            Toast.makeText(this, "Please select a title for your event...", Toast.LENGTH_SHORT).show();
        } else if (Title.length() > 35) {
            Toast.makeText(this, "The title is longer than 35 characters...", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are creating your new event...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();
        }

    }

    private void StoringImageToFirebaseStorage() {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(time.getTime());

        eventRandomName = current_user_id + saveCurrentDate + saveCurrentTime;

        StorageReference filePath = EventsImagesReference.child("Event Images").child(ImageUri.getLastPathSegment() + eventRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    downloadUrl = task.getResult().toString();

                    SaveEventInfoToDataBase();
                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occured:" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SaveEventInfoToDataBase() {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String userFullName = snapshot.child("fullname").getValue().toString();

                    boolean games = checkGames.isChecked();
                    boolean music = checkMusic.isChecked();
                    boolean political = checkPolitical.isChecked();
                    boolean nature = checkNature.isChecked();
                    boolean other = checkOther.isChecked();
                    boolean business = checkBusiness.isChecked();
                    boolean conferece = checkConference.isChecked();
                    boolean webinar = checkWebinar.isChecked();
                    boolean tech = checkTech.isChecked();


                    HashMap<String, Object> eventsMap = new HashMap<>();
                    eventsMap.put("uid", current_user_id);
                    eventsMap.put("name", eventRandomName);
                    eventsMap.put("date", saveCurrentDate);
                    eventsMap.put("time", saveCurrentTime);
                    eventsMap.put("title", Title);
                    eventsMap.put("postimage", downloadUrl);
                    eventsMap.put("fullname", userFullName);
                    eventsMap.put("games", games);
                    eventsMap.put("music", music);
                    eventsMap.put("political", political);
                    eventsMap.put("nature", nature); 
                    eventsMap.put("other", other);
                    eventsMap.put("business", business);
                    eventsMap.put("tech", tech);
                    eventsMap.put("webinar", webinar);
                    eventsMap.put("conference", conferece);


                    EventsRef.child(eventRandomName).updateChildren(eventsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        SendUserToTimeActivity();
                                        loadingBar.dismiss();
                                    }else{
                                        Toast.makeText(PostActivity.this, "Error occured while updating your event", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendUserToTimeActivity() {
        Intent intent = new Intent(PostActivity.this, TimeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("eventname", eventRandomName);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            Glide.with(PostActivity.this).load(ImageUri).centerCrop().into(selectEventImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            SendUserToTimeActivity();
        }
        return super.onOptionsItemSelected(item);
    }
}