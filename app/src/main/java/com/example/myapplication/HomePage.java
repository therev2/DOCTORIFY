package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.activity.OnBackPressedDispatcher;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    LottieAnimationView menu_toggle_btn;
    RecyclerView recyclerView;
    DatabaseReference database;
    Myadapter myAdapter;
    ArrayList<HelperClass> list;
    CardView card1, card2, card3, card4, card5, card6;
    TextView patName;
    ImageView locationbtn;
    public static final String SHARED_PREFS = "sharedPrefs";


    @Override
    public void onClick(View v) {

        String filterlist = (String) v.getTag();
        System.out.println(filterlist);
        searchList(filterlist);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.nav_drawer);
        locationbtn = findViewById(R.id.location_btn);

        locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Map.class);
                startActivity(intent);
            }
        });

        //initializing variable
        patName = findViewById(R.id.name_of_user);

        //getting patient email from shared preference and storing it in variable
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String Email_of_pat = sharedPreferences.getString("patient_email", "");

        //referencing database for parent "patient"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patient");

        //matching input email with database email
        Query checkUserDatabase = reference.orderByChild("email").equalTo(Email_of_pat);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    //getting patient name form database
                    String pat_name = snapshot.child(Email_of_pat.replace(".", ",")).child("name").getValue(String.class);

                    //setting patient name
                    patName.setText(pat_name);

                    // Find the "My_profile" MenuItem
                    MenuItem profileItem = navigationView.getMenu().findItem(R.id.My_profile);

                    // Set the new title
                    String newTitle = "Hi, " + pat_name;
                    profileItem.setTitle(newTitle);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //initializing navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        menu_toggle_btn = findViewById(R.id.menu_btn);


        menu_toggle_btn.setOnClickListener(v -> {
            menu_toggle_btn.playAnimation();
            drawerLayout.open();

        });

        //navigation item selection code part
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemID = menuItem.getItemId();

                if (itemID == R.id.My_app) {
                    Toast.makeText(HomePage.this, "MY appointments ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomePage.this, my_app_list.class);
                    startActivity(intent);
                }


                if (itemID == R.id.My_profile) {
                    Toast.makeText(HomePage.this, "My Profile ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomePage.this, MYprofPat.class);
                    startActivity(intent);
                }

                if (itemID == R.id.My_doctors) {
                    Toast.makeText(HomePage.this, "My Doctors ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomePage.this, MyDocChat.class);
                    startActivity(intent);
                }

                if (itemID == R.id.Logout_profile) {
                    Toast.makeText(HomePage.this, "Log out Successful", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    editor.putString("pat_email", "none");
                    editor.apply();
                    Intent intent = new Intent(HomePage.this, MainActivity.class);
                    startActivity(intent);

                }

                drawerLayout.close();
                return false;
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance().getReference("doctor");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new Myadapter(this, list);
        recyclerView.setAdapter(myAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HelperClass helper = dataSnapshot.getValue(HelperClass.class);
                    list.add(helper);

                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        card1 = findViewById(R.id.small_card1);
        card2 = findViewById(R.id.small_card2);
        card3 = findViewById(R.id.small_card3);
        card4 = findViewById(R.id.small_card4);
        card5 = findViewById(R.id.small_card5);
        card6 = findViewById(R.id.small_card6);

        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);
        card5.setOnClickListener(this);
        card6.setOnClickListener(this);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Close the application
                finishAffinity();
            }
        });


    }

//    @Override
//    public void onBackPressed() {
//        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }
//
//        else{
//            super.onBackPressed();
//        }
//
//    }


//    @NonNull
//    @Override
//    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
//        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
//            drawerLayout.closeDrawer(GravityCompat.START);
//        }
//
//        return super.getOnBackInvokedDispatcher();
//    }

    public void searchList(String text) {
        ArrayList<HelperClass> searchList = new ArrayList<>();
        for (HelperClass helperClass : list) {
            if (helperClass.getSpeacilist().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(helperClass);
            }
        }
        myAdapter.searchDataList(searchList);
    }


}