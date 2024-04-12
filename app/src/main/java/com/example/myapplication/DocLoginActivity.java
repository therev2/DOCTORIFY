package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    CheckBox checkBox_btn_doc;

    //initialised shared storage for doc
    public static final String SHARED_PREFS="sharedPrefs_doc";

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

        //sign up now for doc
        signdoc = findViewById(R.id.singupfordoc);
        signdoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocLoginActivity.this, doctor_register.class);
                startActivity(intent);

            }
        });

        //initialised variables for login page
        loginEmail = findViewById(R.id.loginemail_doc);
        loginPassword = findViewById(R.id.loginpass_doc);
        loginButton = findViewById(R.id.loginbtn_doc);
        checkBox_btn_doc = findViewById(R.id.remember_me_chkb_doc);


        //initialised login in button on click action
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if either of email or password field are filled and have proper format or not.
                if(!validateEmail() | !validateEmail()){

                } else {
                    //if both validateEmail() validateEmail() is passed then this function will run to check if the email is present in database or not
                    //if yes then account is loged in
                    checkEmail();
                }
            }
        });


    }


    //function to check if the email and password is alphanumeric and not emoji etc..
    boolean isAlphanumeric(final int codePoint) {
        return (codePoint >= 64 && codePoint <= 90) ||
                (codePoint >= 97 && codePoint <= 122) ||
                (codePoint >= 32 && codePoint <= 57);
    }

    //function to validate email
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

    //function to validate password
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

    //function for login
    public void checkEmail(){
        //email and password from input field
        String userUseremail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        //refrencing database for parent "doctor"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doctor");

        //matching input email with database email
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userUseremail);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    loginEmail.setError(null);
                    //getting password from Database
                    String passwordFromDB = snapshot.child(userUseremail.replace(".",",")).child("password").getValue(String.class);
                    if(passwordFromDB.equals(Hash.hashedPassword(userPassword))){
                        loginEmail.setError(null);
                        SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences_doc.edit();
                        editor.putString("doc_email", userUseremail);
                        editor.apply();

                        if(checkBox_btn_doc.isChecked()){
                            editor.putString("remember", "true");
                            editor.apply();
                            System.out.println("does it run automatically");
                        }


                        Toast.makeText(DocLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DocLoginActivity.this, doc_landing_page.class);
                        startActivity(intent);
                        finish();


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