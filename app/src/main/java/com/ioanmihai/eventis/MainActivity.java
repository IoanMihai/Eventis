package com.ioanmihai.eventis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView eventList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, EventsRef;

    private String currentUserID;
    private TextView NavProfileUserName;
    private List<Posts> postsList;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            SendUserToRegisterActivity();
        }

        navigationView = findViewById(R.id.scrollableMenu);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        drawerLayout = findViewById(R.id.parent);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_fullname);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.hasChild("fullname")) {
                        String FullName = snapshot.child("fullname").getValue(String.class);
                        NavProfileUserName.setText(FullName);
                    }else{
                        Toast.makeText(MainActivity.this, "Fara nume, fara CNP", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });

        eventList = (RecyclerView) findViewById(R.id.for_you);
        eventList.setHasFixedSize(true);
        eventList.setLayoutManager(new LinearLayoutManager(this));
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
    }

/*    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Posts, EventsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, EventsViewHolder>(
                Posts.class,
                R.layout.events_list,
                EventsViewHolder.class,
                EventsRef
        ) {
            @Override
            protected void populateViewHolder(EventsViewHolder viewHolder, Posts posts, int i) {
                viewHolder.setTitle(posts.title);
                viewHolder.setDescription(posts.description);
                viewHolder.setImage(MainActivity.this, posts.postimage );

                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SendUserToEventActivity();
                    }
                });
            }
        };

        eventList.setAdapter(firebaseRecyclerAdapter);
    }*/

    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public TextView title, description;
        public ImageView image;
        public LinearLayout root;
        public Button button;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            title = mView.findViewById(R.id.list_title);
            root = mView.findViewById(R.id.list_root);
            description = mView.findViewById(R.id.list_desc);
            image = mView.findViewById(R.id.list_image);
        }


        public void setTitle(String string){
            title.setText(string);
        }

        public void setDescription(String string){
            description.setText(string);
        }

        public void setImage(Context ctx, String img){
            Glide.with(ctx).load(img).into(image);
        }
    }

    private void SendUserToPostActivity() {
        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                SendUserToProfileActivity();
                break;
            case R.id.nav_future_events:
                break;
            case R.id.nav_past_events:
                break;
            case R.id.nav_about:
                showAlert();
                break;
            default:
                mAuth.signOut();
                SendUserToRegisterActivity();
                break;
        }
    }

    private void SendUserToProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("About")
                .setMessage("Develpoed and created by Ioan Mihai and Bazavan Andrei")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }


    private void SendUserToRegisterActivity() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void SendUserToEventActivity() {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}