package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class doctor_register extends AppCompatActivity {

    String[] specialist_array = {"Diabetes Management", "Diet and Nutrition", "Physiotherapist", "ENT Specialist",  "Eyes specialist", "Pulmonologist", "Dentist", "Sexual Health",  "Women's Health ",  "Gastroenterologist", "Cardiologist", "Skin and Hair",  "Child Specialist", "General physician"};


    EditText signupEmail, signupPassword, signupName, signupExp, signupCharge, signupTime, signupDegree;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        autoCompleteTextView = findViewById(R.id.specilist_doc);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, specialist_array);


        autoCompleteTextView.setAdapter(adapterItems);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString().trim();
                Toast.makeText(doctor_register.this,item, Toast.LENGTH_SHORT).show();
            }
        });


        signupName = findViewById(R.id.name_doc);
        signupEmail = findViewById(R.id.email_doc);
        signupPassword = findViewById(R.id.pass_doc);
        signupButton = findViewById(R.id.signupdoc);
        signupExp = findViewById(R.id.exp_doc);
        signupCharge = findViewById(R.id.charge_doc);
        signupTime = findViewById(R.id.time_doc);
        signupDegree = findViewById(R.id.degree_doc);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("doctor");

                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();
                String name = signupName.getText().toString();
                String exp = signupExp.getText().toString();
                String charge = signupCharge.getText().toString();
                String time = signupTime.getText().toString();
                String degree = signupDegree.getText().toString();
                String speacilist = item;



                if(validateEmail() && validatePassword()){
                    HelperClass helperClass = new HelperClass(email, password, name, exp, charge, time, degree, speacilist);
                    reference.child(email.replace(".",",")).setValue(helperClass);
                    Toast.makeText(doctor_register.this, "Signup Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(doctor_register.this, doc_home_page2.class);
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