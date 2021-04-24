package com.ioanmihai.eventis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {

    private EditText FullName;
    private Button SaveInfo;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private ProgressDialog loadingBar;
    private CheckBox checkGames, checkMusic, checkPolitical, checkNature, checkOther;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        SaveInfo = findViewById(R.id.setup_button);
        checkGames = findViewById(R.id.checkboxGames);
        checkMusic = findViewById(R.id.checkboxMusic);
        checkPolitical = findViewById(R.id.checkboxPolitical);
        checkNature = findViewById(R.id.checkboxNature);
        checkOther = findViewById(R.id.checkboxOther);

        FullName = findViewById(R.id.setup_fullname);
        loadingBar = new ProgressDialog(this);

        SaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

    }

    private void SaveAccountSetupInformation() {
        String fullname = FullName.getText().toString();
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

        if (TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please write your full name...", Toast.LENGTH_SHORT).show();
        }else if (nr == 0) {
            Toast.makeText(this, "Please tell us what events are you interested in...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are creating your new Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap  = new HashMap();
            userMap.put("fullname", fullname);
            userMap.put("games", games);
            userMap.put("music", music);
            userMap.put("political", political);
            userMap.put("nature", nature);
            userMap.put("other", other);

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account was updated successfully.", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }else{
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}