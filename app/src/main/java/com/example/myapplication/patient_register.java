package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class patient_register extends AppCompatActivity {

    EditText signupEmail, signupPassword;
    Button signupButton;
    String phone;
    FirebaseDatabase database;
    DatabaseReference reference;
    EditText phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });


        signupEmail = findViewById(R.id.email_pat);
        signupPassword = findViewById(R.id.pass_pat);
        signupButton = findViewById(R.id.signuppat);
        phone_number = findViewById(R.id.phone_number);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("patient");

                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();
                phone = phone_number.getText().toString().trim();
                if(validateEmail() && validatePassword()){
                    HelperClass2 helperClass = new HelperClass2(email, password);
                    reference.child(email.replace(".", ",")).setValue(helperClass);

                    Toast.makeText(patient_register.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(patient_register.this, otp_screen.class);
                    intent.putExtra("phone",phone);
                    startActivity(intent);

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
        String val = signupEmail.getText().toString();
        if (val.isEmpty()) {
            signupEmail.setError("Email cannot be empty");
            return false;
        } else if (!properformat()) {
            signupEmail.setError("Invalid Format");
            return false;

        } else {
            signupEmail.setError(null);
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
                signupEmail.setError("Email can only be alphanumberic");
                return false;
            }
        }
    }

    public boolean properformat() {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


        Pattern pattern = Pattern.compile(emailRegex);
        String val = signupEmail.getText().toString();

        Matcher matcher = pattern.matcher(val);

        return matcher.matches();
    }


    public Boolean validatePassword() {
        String val = signupPassword.getText().toString();
        if (val.isEmpty()) {
            signupPassword.setError("Password cannot be empty");
            return false;
        } else {
            signupPassword.setError(null);
            if (val.isEmpty()) {
                signupPassword.setError("Password cannot be empty");
                return false;
            } else {
                signupPassword.setError(null);
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
                    signupPassword.setError("Password can only be alphanumberic");
                    return false;
                }
            }


        }


    }

}