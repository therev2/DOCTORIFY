package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class DocLoginActivity extends AppCompatActivity {

    TextView signdoc;

    EditText loginEmail, loginPassword;
    Button loginButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.doc_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signdoc = findViewById(R.id.singupfordoc);
        signdoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocLoginActivity.this, doctor_register.class);
                startActivity(intent);
            }
        });

        loginEmail = findViewById(R.id.loginemail_doc);
        loginPassword = findViewById(R.id.loginpass_doc);
        loginButton = findViewById(R.id.loginbtn_doc);


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

    boolean isAlphanumeric(final int codePoint) {
        return (codePoint >= 64 && codePoint <= 90) ||
                (codePoint >= 97 && codePoint <= 122) ||
                (codePoint >= 32 && codePoint <= 57);
    }


    public Boolean validateEmail() {
        String val = loginEmail.getText().toString();
        if (val.isEmpty()){
            loginEmail.setError("Email cannot be empty");
            return false;
        }else {
            loginEmail.setError(null);
            if (val.isEmpty()){
                loginEmail.setError("Email cannot be empty");
                return false;
            }else {
                loginEmail.setError(null);
                boolean result = true;
                for (int i = 0; i < val.length(); i++) {
                    int codePoint = val.codePointAt(i);
                    if (!isAlphanumeric(codePoint)) {
                        result = false;
                        break;
                    }
                }
                if (result){
                    return true;
                } else {
                    loginEmail.setError("Email can only be alphanumberic");
                    return false;
                }

            }



        }

    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()){
            loginPassword.setError("Password cannot be empty");
            return false;
        }else {
            loginPassword.setError(null);
            if (val.isEmpty()){
                loginPassword.setError("Password cannot be empty");
                return false;
            }else {
                loginPassword.setError(null);
                boolean result = true;
                for (int i = 0; i < val.length(); i++) {
                    int codePoint = val.codePointAt(i);
                    if (!isAlphanumeric(codePoint)) {
                        result = false;
                        break;
                    }
                }
                if (result){
                    return true;
                } else {
                    loginPassword.setError("Password can only be alphanumberic");
                    return false;
                }

            }



        }

    }

    public void checkEmail(){
        String userUseremail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doctor");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userUseremail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    loginEmail.setError(null);
                    String passwordFromDB = snapshot.child(userUseremail.replace(".",",")).child("password").getValue(String.class);

                    if(passwordFromDB.equals(userPassword)){
                        loginEmail.setError(null);
                        Toast.makeText(DocLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

//                        --------->IMP
//                        page where to move doc after login
//                        Intent intent = new Intent(DocLoginActivity.this, AppointmentPage.class);
//                        startActivity(intent);


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