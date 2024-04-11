package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.N;

import java.sql.SQLOutput;
import java.util.HashMap;

public class complete_your_profile extends AppCompatActivity {
    TextInputEditText name,age,address,pincode;
    String Email_of_pat,Name,Age,Address,Pincode;
    Button submit_btn;

    //initialised shared storage for doc
    public static final String SHARED_PREFS="sharedPrefs";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complete_your_profile);

        //getting doc email from shared preference and storing it in variable
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Email_of_pat = sharedPreferences.getString("patient_email","");



        //initialising variables
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pin);
        submit_btn = findViewById(R.id.reg_complete);

        System.out.println(Name);
        System.out.println(Age);
        System.out.println(Address);
        System.out.println(Pincode);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting user data from EditText fields
                Name = name.getText().toString().trim();
                Age = age.getText().toString().trim();
                Address = address.getText().toString().trim();
                Pincode = pincode.getText().toString().trim();

                updateUserData(Email_of_pat, Name, Age, Address, Pincode);

                Intent intent = new Intent(complete_your_profile.this, HomePage.class);
                startActivity(intent);

            }
        });

    }

    private void updateUserData(String email, String name, String age, String address, String pincode) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("patient").child(email.replace(".", ","));

        // Create a HashMap to store the additional user data
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("age", age);
        userData.put("address", address);
        userData.put("pincode", pincode);

        // Update the user data in the database
        reference.updateChildren(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                            // You can start a new activity or finish the current activity
                        } else {
                            Toast.makeText(getApplicationContext(), "Profile Update Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}