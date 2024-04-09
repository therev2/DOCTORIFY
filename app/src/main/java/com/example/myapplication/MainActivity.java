package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    TextView are_doc;

//    Button login_btn;

    TextView signup;

    com.google.android.material.textfield.TextInputEditText loginEmail, loginPassword;
    com.google.android.material.button.MaterialButton loginButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        linking all activities

        are_doc = findViewById(R.id.are_you_doc);
        are_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DocLoginActivity.class);
                startActivity(intent);
            }
        });
//        login_btn = findViewById(R.id.klop);
//        login_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Login Successful" , Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, AppointmentPage.class);
//                startActivity(intent);
//            }
//        });

        signup = findViewById(R.id.O);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, patient_register.class);
                startActivity(intent);
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });


        loginEmail = findViewById(R.id.loginemail);
        loginPassword = findViewById(R.id.loginpass);
        loginButton = findViewById(R.id.klop);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail() | !validatePassword()){

                } else {
                    checkEmail();
                }
            }
        });



    }

    public Boolean validateEmail() {
        String val = loginEmail.getText().toString();
        if (val.isEmpty()){
            loginEmail.setError("Username cannot be empty");
            return false;
        }else {
            loginEmail.setError(null);
            return true;
        }

    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()){
            loginPassword.setError("Password cannot be empty");
            return false;
        }else {
            loginPassword.setError(null);
            return true;
        }

    }

    public void checkEmail(){
        String userUseremail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patient");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userUseremail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    loginEmail.setError(null);
                    String passwordFromDB = snapshot.child(userUseremail).child("password").getValue(String.class);

                    if(passwordFromDB.equals(userPassword)){
                        loginEmail.setError(null);
                        Intent intent = new Intent(MainActivity.this, AppointmentPage.class);
                        startActivity(intent);
                    }else {
                        loginPassword.setError("invalid Credentials");
                        loginPassword.requestFocus();
                    }
                }else {
                    loginEmail.setError("User does not exist");
                    loginEmail.requestFocus();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}