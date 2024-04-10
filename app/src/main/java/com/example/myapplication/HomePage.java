package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.activity.OnBackPressedDispatcher;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.grpc.LoadBalancer;

public class HomePage extends AppCompatActivity implements View.OnClickListener {



    LinearLayout doc1;
    LinearLayout layout;
    LinearLayout.LayoutParams params;

    RecyclerView recyclerView;
    DatabaseReference database;
    Myadapter myAdapter;
    ArrayList<HelperClass> list;
    CardView card1, card2, card3, card4, card5;
    Button logout_btn;
    public static final String SHARED_PREFS="sharedPrefs";


    @Override
    public void onClick(View v){
        System.out.println("button pressed");
        String filterlist = (String) v.getTag();
        System.out.println(filterlist);
        searchList(filterlist);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        logout_btn = findViewById(R.id.logout_pat);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePage.this,"Log out Successful",Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                startActivity(intent);

            }
        });




        recyclerView = findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance().getReference("doctor");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new Myadapter(this,list);
        recyclerView.setAdapter(myAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
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

        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);
        card5.setOnClickListener(this);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Close the application
                finishAffinity();
            }
        });


    }

    public void searchList(String text){
        ArrayList<HelperClass> searchList = new ArrayList<>();
        for (HelperClass helperClass: list){
            if (helperClass.getSpeacilist().toLowerCase().contains(text.toLowerCase())){
                searchList.add(helperClass);
            }
        }
        myAdapter.searchDataList(searchList);
    }


}