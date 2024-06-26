package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class complete_your_profile extends AppCompatActivity {
    EditText name,age,address;
    String Email_of_pat,Name,Age,Address,Gender,Status,BloodGroup,gender_d,status_d,blood_d;
    Button submit_btn;
    String [] blood_group_array = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};
    String[] gender_array = {"Male","Female","Other"};
    String[] status_array = {"Bachelor","In relaltionship","Married"};
    AutoCompleteTextView genderTextView, statusTextView,bloodgroupTextView;
    ArrayAdapter<String> genderItems, statusItems,bloodgroupItems;


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
        submit_btn = findViewById(R.id.reg_complete);

        genderTextView = findViewById(R.id.gender_pat);
        genderItems = new ArrayAdapter<String>(this, R.layout.list_item, gender_array);

        genderTextView.setAdapter(genderItems);

        genderTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gender_d = adapterView.getItemAtPosition(i).toString().trim();
                Toast.makeText(complete_your_profile.this,gender_d, Toast.LENGTH_SHORT).show();
            }
        });

        statusTextView = findViewById(R.id.status_pat);
        statusItems = new ArrayAdapter<String>(this, R.layout.list_item, status_array);

        statusTextView.setAdapter(statusItems);

        statusTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 status_d = adapterView.getItemAtPosition(i).toString().trim();
                Toast.makeText(complete_your_profile.this,status_d, Toast.LENGTH_SHORT).show();
            }
        });



        bloodgroupTextView = findViewById(R.id.blood_group_pat);

        bloodgroupItems = new ArrayAdapter<String>(this, R.layout.list_item, blood_group_array);
        bloodgroupTextView.setAdapter(bloodgroupItems);

        bloodgroupTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                blood_d = adapterView.getItemAtPosition(i).toString().trim();
                Toast.makeText(complete_your_profile.this, blood_d, Toast.LENGTH_SHORT).show();
            }
        });

        bloodgroupTextView.setAdapter(bloodgroupItems);


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting user data from EditText fields
                Name = name.getText().toString().trim();
                Age = age.getText().toString().trim();
                Address = address.getText().toString().trim();
                Gender = gender_d.trim();
                Status = status_d.trim();
                BloodGroup = blood_d.trim();

                if (Name.isEmpty() && Age.isEmpty()){
                    name.setError("Mandatory field");
                    age.setError("Mandatory field");
                }
                else if (Age.isEmpty()){
                    age.setError("Mandatory field");
                }
                else if (Name.isEmpty()){
                    name.setError("Mandatory field");

                }
                else{
                    updateUserData(Email_of_pat, Name, Age, Address, Gender,Status,BloodGroup);
                    Intent intent = new Intent(complete_your_profile.this, HomePage.class);
                    startActivity(intent);
                    finish();

                }




            }
        });

    }

    private void updateUserData(String email, String name, String age, String address, String gender, String status,String bloodgroup) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("patient").child(email.replace(".", ","));

        // Create a HashMap to store the additional user data
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("age", age);
        userData.put("address", address);
        userData.put("gender", gender);
        userData.put("status",status);
        userData.put("bloodgroup",bloodgroup);

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