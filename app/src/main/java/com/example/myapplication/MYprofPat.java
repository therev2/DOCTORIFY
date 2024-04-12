package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MYprofPat extends AppCompatActivity {

    TextView textView,name_mp,age_mp,add_mp,ms_mp,gender_mp,phonenumber,bloodgroup;
    public static final String SHARED_PREFS="sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myprof_pat);

        //getting doc email from shared preference and storing it in variable
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String Email_of_pat = sharedPreferences.getString("patient_email","");

        name_mp = findViewById(R.id.name_mp);
        age_mp = findViewById(R.id.age_mp);
        add_mp = findViewById(R.id.address_mp);
        ms_mp = findViewById(R.id.status_mp);
        gender_mp =findViewById(R.id.gender_mp);
        textView = findViewById(R.id.edit);
        phonenumber = findViewById(R.id.phone);
        bloodgroup = findViewById(R.id.blood_grp_mp);

        //referencing database for parent "patient"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patient");

        //matching input email with database email
        Query checkUserDatabase = reference.orderByChild("email").equalTo(Email_of_pat);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    //getting patient name form database
                    String name = snapshot.child(Email_of_pat.replace(".",",")).child("name").getValue(String.class);
                    String address = snapshot.child(Email_of_pat.replace(".",",")).child("address").getValue(String.class);
                    String age = snapshot.child(Email_of_pat.replace(".",",")).child("age").getValue(String.class);
                    String gender = snapshot.child(Email_of_pat.replace(".",",")).child("gender").getValue(String.class);
                    String status = snapshot.child(Email_of_pat.replace(".",",")).child("status").getValue(String.class);
                    String phoneno = snapshot.child(Email_of_pat.replace(".",",")).child("phoneNumber").getValue(String.class);
                    String blood_gp = snapshot.child(Email_of_pat.replace(".",",")).child("bloodgroup").getValue(String.class);

                    //setting patient name
                    name_mp.setText(name);
                    add_mp.setText(address);
                    age_mp.setText(age);
                    gender_mp.setText(gender);
                    ms_mp.setText(status);
                    phonenumber.setText(phoneno);
                    bloodgroup.setText(blood_gp);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        textView.setOnClickListener(v->{
            Intent intent = new Intent(MYprofPat.this, complete_your_profile.class);
            startActivity(intent);
        });
    }}

