 package com.ioanmihai.eventis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

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
    private FirebaseRecyclerAdapter adapter;
    private boolean isGames, isMusic, isNature, isOther, isPolitical, isWebinar, isConference, isBusiness, isTech;
    private LinearLayoutManager linearLayoutManager;

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

                        isGames = (boolean) snapshot.child("games").getValue();
                        isMusic = (boolean) snapshot.child("music").getValue();
                        isNature = (boolean) snapshot.child("nature").getValue();
                        isPolitical = (boolean) snapshot.child("political").getValue();
                        isOther = (boolean) snapshot.child("other").getValue();
                        isWebinar = (boolean) snapshot.child("webinar").getValue();
                        isConference = (boolean) snapshot.child("conference").getValue();
                        isBusiness = (boolean) snapshot.child("business").getValue();
                        isTech = (boolean) snapshot.child("tech").getValue();
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

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        eventList = (RecyclerView) findViewById(R.id.for_you);
        eventList.setHasFixedSize(true);
        eventList.setLayoutManager(linearLayoutManager);
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        fetch();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Events");

        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                    .setQuery(query, Posts.class)
                    .build();

        adapter = new FirebaseRecyclerAdapter<Posts, ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Posts posts) {

                boolean ok = false;
                boolean postisGames = posts.isGames();
                boolean postisMusic = posts.isMusic();
                boolean postisNature = posts.isNature();
                boolean postisPolitical = posts.isPolitical();
                boolean postisOther = posts.isOther();
                boolean postisWebinar = posts.isWebinar();
                boolean postisConference = posts.isConference();
                boolean postisBusiness= posts.isBusiness();
                boolean postisTech = posts.isTech();

                if (postisGames && isGames)
                    ok = true;
                else if (postisMusic && isMusic)
                    ok = true;
                else if (postisNature && isNature)
                    ok = true;
                else if (postisPolitical && isPolitical)
                    ok = true;
                else if (postisOther && isOther)
                    ok = true;
                else if (postisBusiness && isBusiness)
                    ok = true;
                else if (postisConference && isConference)
                    ok = true;
                else if (postisTech && isTech)
                    ok = true;
                else if (postisWebinar && isWebinar)
                    ok = true;

                if (ok) {
                    if (posts.getDescription().length() > 35) {
                        String des = posts.getDescription();
                        des = des.substring(0, 35) + "...";
                        viewHolder.setDescription(des);
                    } else {
                        viewHolder.setDescription(posts.getDescription());
                    }
                    viewHolder.setTitle(posts.getTitle());

                    viewHolder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SendUserToEventActivity(posts.getEventName());
                        }
                    });
                }else{
                    viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                }
            }

            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.events_list, parent, false);
                return new ViewHolder(view);
            }
        };

        eventList.setAdapter(adapter);
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
            case R.id.nav_my_events:
                SendUserToMyEventsActivity();
                break;
            default:
                mAuth.signOut();
                SendUserToRegisterActivity();
                break;
        }
    }

    private void SendUserToMyEventsActivity() {
        Intent intent = new Intent(MainActivity.this, MyEventsActivity.class);
        startActivity(intent);
    }

    private void SendUserToProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("About")
                .setMessage("Developed and created by Ioan Mihai")
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


    private void SendUserToEventActivity(String name) {
        Intent intent = new Intent(MainActivity.this, EventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("randomname", name);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}