package com.ioanmihai.eventis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, confPassword;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confPassword = findViewById(R.id.register_confirm_password);
        btn_register = findViewById(R.id.register_create_account);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_conf_password = confPassword.getText().toString();
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_conf_password)){
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else if (txt_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                }else if (txt_password.compareTo(txt_conf_password) != 0){
                    Toast.makeText(RegisterActivity.this, "Confirmed password is not the same as password", Toast.LENGTH_SHORT).show();
                }else{
                    register(txt_email, txt_password);
                }
            }
        });
    }

    private void register(String txt_email, String txt_password) {
        auth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", "default");
                    hashMap.put("imageURL", "default");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}