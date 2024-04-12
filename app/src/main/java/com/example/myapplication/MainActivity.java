package com.example.myapplication;

import static android.service.controls.ControlsProviderService.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  MainActivity extends AppCompatActivity {
    TextView are_doc;
    TextView signup;
    EditText loginEmail, loginPassword;
    Button loginButton;
    Button b;
    String per1[] = {"android.permission.ACCESS_COARSE_LOCATION"};
    String per2[] = {"android.permission.ACCESS_FINE_LOCATION"};
    String per3[] = {"android.permission.ACCESS_BACKGROUND_LOCATION"};

    CheckBox checkBox_btn;
    public static final String SHARED_PREFS="sharedPrefs";
    public static final String SHARED_PREFS_DOC="sharedPrefs_doc";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        b = findViewById(R.id.klop);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(per1, 80);
                requestPermissions(per2, 80);
                requestPermissions(per3, 80);
            }
        });

//        linking all activities
        checkBox();
        checkBox_Doc();

        are_doc = findViewById(R.id.are_you_doc);
        are_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DocLoginActivity.class);
                startActivity(intent);
            }
        });

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
        checkBox_btn = findViewById(R.id.remember_me_chkb);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() | !validatePassword()) {

                } else {
                    checkEmail();
                }
            }
        });


    }

    private void checkBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("remember", "");
        if (check.equals("true")){
            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkBox_Doc() {
        SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS_DOC, MODE_PRIVATE);
        String check = sharedPreferences_doc.getString("remember", "");
        String check_email = sharedPreferences_doc.getString("doc_email","");
        if (check.equals("true")){
            if (!check_email.equals("null")){
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, doc_landing_page.class);
                startActivity(intent);
                finish();
            }
        }
    }


    boolean isAlphanumeric(final int codePoint) {
        return (codePoint >= 64 && codePoint <= 90) ||
                (codePoint >= 97 && codePoint <= 122) ||
                (codePoint >= 32 && codePoint <= 57);
    }


    public Boolean validateEmail() {
        String val = loginEmail.getText().toString().trim();
        if (val.isEmpty()) {
            loginEmail.setError("Email cannot be empty");
            return false;
        } else if (!properformat()) {
            loginEmail.setError("Invalid Format");
            return false;
        } else {
            loginEmail.setError(null);
            boolean result = true;
            for (int i = 0; i < val.length(); i++) {
                int codePoint = val.codePointAt(i);
                if (!isAlphanumeric(codePoint)) {
                    result = false;
                    break;
                }
            }
            if (result) {
                return true;
            } else {
                loginEmail.setError("Email can only be alphanumberic");
                return false;
            }
        }

    }

    public boolean properformat() {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


        Pattern pattern = Pattern.compile(emailRegex);
        String val = loginEmail.getText().toString().trim();

        Matcher matcher = pattern.matcher(val);

        return matcher.matches();
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            if (val.isEmpty()) {
                loginPassword.setError("Password cannot be empty");
                return false;
            } else {
                loginPassword.setError(null);
                boolean result = true;
                for (int i = 0; i < val.length(); i++) {
                    int codePoint = val.codePointAt(i);
                    if (!isAlphanumeric(codePoint)) {
                        result = false;
                        break;
                    }
                }
                if (result) {
                    return true;
                } else {
                    loginPassword.setError("Password can only be alphanumberic");
                    return false;
                }
            }


        }

    }

    public void checkEmail() {
        String userUseremail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patient");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userUseremail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    loginEmail.setError(null);
                    String passwordFromDB = snapshot.child(userUseremail.replace(".", ",")).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userPassword)) {
                        loginEmail.setError(null);
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("patient_email", userUseremail);
                        editor.apply();
                        if(checkBox_btn.isChecked()){
                            editor.putString("remember", "true");
                            editor.apply();
                        }
                        //For FireStore
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        HashMap<String,Object> data = new HashMap<>();
                        data.put("Email","harshit");
                        data.put("Password", "userPassword");

                        database.collection("users")
                                        .add(data).
                                addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(exception ->{
                                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                        ;

                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomePage.class);
                        startActivity(intent);
                    }
                    else {
                        loginPassword.setError("invalid Credentials");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginEmail.setError("User does not exist");
                    loginEmail.requestFocus();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 80) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
        }


    }
}