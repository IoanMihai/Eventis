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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
    private EditText postEdit;
    private ProgressDialog loadingBar;
    private ImageButton selectEventImage;
    private String Description;
    private RadioButton checkGames, checkMusic, checkPolitical, checkNature, checkOther;
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
        postEdit = findViewById(R.id.descriptionEdit);
        loadingBar = new ProgressDialog(this);
        checkGames = findViewById(R.id.checkboxGames);
        checkMusic = findViewById(R.id.checkboxMusic);
        checkPolitical = findViewById(R.id.checkboxPolitical);
        checkNature = findViewById(R.id.checkboxNature);
        checkOther = findViewById(R.id.checkboxOther);

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
        Description = postEdit.getText().toString();

        boolean games = checkGames.isChecked();
        boolean music = checkMusic.isChecked();
        boolean political = checkPolitical.isChecked();
        boolean nature = checkNature.isChecked();
        boolean other = checkOther.isChecked();
        int nr = 0;
        if (checkGames.isChecked()){
            nr ++;
        }
        if (checkMusic.isChecked()){
            nr ++;
        }
        if (checkPolitical.isChecked()){
            nr ++;
        }
        if (checkNature.isChecked()){
            nr ++;
        }
        if (checkOther.isChecked()){
            nr ++;
        }

        if (ImageUri == null){
            Toast.makeText(this, "Please select event image...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please say something about your event...", Toast.LENGTH_SHORT).show();
        }else if (nr == 0){
            Toast.makeText(this, "Please tell us what kind of event this is...", Toast.LENGTH_SHORT).show();
        }else{
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
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(time.getTime());

        eventRandomName = saveCurrentDate + saveCurrentTime;

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

                    HashMap eventsMap = new HashMap();
                    eventsMap.put("uid", current_user_id);
                    eventsMap.put("date", saveCurrentDate);
                    eventsMap.put("time", saveCurrentTime);
                    eventsMap.put("description", Description);
                    eventsMap.put("postimage", downloadUrl);
                    eventsMap.put("fullname", userFullName);

                    EventsRef.child(current_user_id + eventRandomName).updateChildren(eventsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        //SendUserToTimeActivity();
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
            selectEventImage.setImageURI(ImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            //SendUserToTimeActivity();
        }
        return super.onOptionsItemSelected(item);
    }
}