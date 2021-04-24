package com.ioanmihai.eventis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtName, txtEmail;
    private Button btnEdit;
    private CheckBox games, nature, music, political, other;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        txtName = findViewById(R.id.fullname);
        txtEmail = findViewById(R.id.email);
        btnEdit = findViewById(R.id.edit_profile);
        games = findViewById(R.id.checkboxGames);
        nature = findViewById(R.id.checkboxNature);
        music = findViewById(R.id.checkboxMusic);
        political = findViewById(R.id.checkboxPolitical);
        other = findViewById(R.id.checkboxOther);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = mAuth.getCurrentUser().getUid();

       UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.hasChild("fullname")){
                        String fullname = snapshot.child("fullname").getValue().toString();
                        txtName.setText("Name: " + fullname);
                    }
                    boolean ok = (boolean) snapshot.child("games").getValue();
                    games.setChecked(ok);
                    ok = (boolean) snapshot.child("nature").getValue();
                    nature.setChecked(ok);
                    ok = (boolean) snapshot.child("music").getValue();
                    music.setChecked(ok);
                    ok = (boolean) snapshot.child("political").getValue();
                    political.setChecked(ok);
                    ok = (boolean) snapshot.child("other").getValue();
                    other.setChecked(ok);
                    String mail = mAuth.getCurrentUser().getEmail();
                    txtEmail.setText("Email: "+ mail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToSetupActivity();
            }
        });
    }


    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(ProfileActivity.this, SetupActivity.class);
        startActivity(setupIntent);
    }
}