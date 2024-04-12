package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class patient_register extends AppCompatActivity {

    EditText signupEmail, signupPassword;
    Button signupButton;
    String phone;
    EditText phone_number;
    public static final String SHARED_PREFS="sharedPrefs";


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
        signupEmail.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting entered data from the fields and storing them in variables
                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();
                phone = phone_number.getText().toString().trim();

                //checking valid email and password formats
                if (validateEmail() && validatePassword()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("patient_email",email);
                    editor.apply();
                    // Start the otp_screen activity and pass the phone number, email, and password as extras
                    Intent intent = new Intent(patient_register.this, otp_screen.class);
                    intent.putExtra("phone", phone);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
            }
        });
    }

    //function to check if the password is alpha numeric
    boolean isAlphanumeric(final int codePoint) {
        return (codePoint >= 64 && codePoint <= 90) ||
                (codePoint >= 97 && codePoint <= 122) ||
                (codePoint >= 32 && codePoint <= 57);
    }

    //function to check email
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

    //fucntion to check password format
    public boolean properformat() {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        String val = signupEmail.getText().toString();

        Matcher matcher = pattern.matcher(val);

        return matcher.matches();
    }

    //function to check password
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