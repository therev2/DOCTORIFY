package com.example.myapplication;

import android.content.Intent;
import static android.service.controls.ControlsProviderService.TAG;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class doctor_appointment_full_screen extends AppCompatActivity {

    TextView doctor_name;
    TextView doctor_specialist;
    ImageView doc_profile;
    Button appointmentButton;
    String docccc;
    FirebaseDatabase database;
    DatabaseReference reference;

    public static final String SHARED_PREFS="sharedPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_appointment_full_screen);

        doctor_name = findViewById(R.id.doc_namee);
        doctor_specialist = findViewById(R.id.doc_specialistt);
        doc_profile = findViewById(R.id.doc_dp);

        doctor_name.setText(getIntent().getStringExtra("username"));
        docccc = getIntent().getStringExtra("username");
        doctor_specialist.setText(getIntent().getStringExtra("specialist"));
        Glide.with(doctor_appointment_full_screen.this).load(getIntent().getStringExtra("Image")).into(doc_profile);

        appointmentButton = findViewById(R.id.Appointment_btn);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String pat_mail = sharedPreferences.getString("email", "");

        String date = "1/1/1";
        String time = "1:1";

        String doc_mail = getIntent().getStringExtra("doc_mail");

        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("appointment");

                Log.d(TAG, pat_mail+" "+doc_mail+" "+date+" "+time);
                HelperClass3 helperClass = new HelperClass3(pat_mail, doc_mail ,date ,time);
                reference.child(doc_mail.replace(".",",")).setValue(helperClass);

                Intent intent = new Intent(doctor_appointment_full_screen.this, booked_confirm.class);
                intent.putExtra("doctor_name",docccc);
                startActivity(intent);

            }
        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}