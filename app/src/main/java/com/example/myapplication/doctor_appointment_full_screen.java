package com.example.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import static android.service.controls.ControlsProviderService.TAG;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class doctor_appointment_full_screen extends AppCompatActivity implements View.OnClickListener {

    TextView doctor_name;
    TextView doctor_specialist;
    ImageView doc_profile;
    Button appointmentButton;
    String docccc;
    FirebaseDatabase database;
    DatabaseReference reference;
    Button timeButton;
    String date_for_database="";

    LinearLayout a18, a19, a20, a21,a22,a23,a24;

    public static final String SHARED_PREFS="sharedPrefs";

    @Override
    public void onClick(View v){

        date_for_database = (String) v.getTag();
        Toast.makeText(doctor_appointment_full_screen.this, date_for_database,Toast.LENGTH_SHORT).show();


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_appointment_full_screen);

        doctor_name = findViewById(R.id.doc_namee);
        doctor_specialist = findViewById(R.id.doc_specialistt);
        doc_profile = findViewById(R.id.doc_dp);
        timeButton = findViewById(R.id.time_btn);

        a18 = findViewById(R.id.a18);
        a19 = findViewById(R.id.a19);
        a20 = findViewById(R.id.a20);
        a21 = findViewById(R.id.a21);
        a22 = findViewById(R.id.a22);
        a23 = findViewById(R.id.a23);
        a24 = findViewById(R.id.a24);

        a18.setOnClickListener(this);
        a19.setOnClickListener(this);
        a20.setOnClickListener(this);
        a21.setOnClickListener(this);
        a22.setOnClickListener(this);
        a23.setOnClickListener(this);
        a24.setOnClickListener(this);



        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        doctor_name.setText(getIntent().getStringExtra("username"));
        docccc = getIntent().getStringExtra("username");
        doctor_specialist.setText(getIntent().getStringExtra("specialist"));
        Glide.with(doctor_appointment_full_screen.this).load(getIntent().getStringExtra("Image")).into(doc_profile);

        appointmentButton = findViewById(R.id.Appointment_btn);








        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(doctor_appointment_full_screen.this, booked_confirm.class);
                intent.putExtra("doctor_name",docccc);
                intent.putExtra("timee", selectedTime);
                Log.d(TAG, selectedTime);

                startActivity(intent);

            }
        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public String selectedTime = "";

    public String openDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int hour = hourOfDay;
                String amPm;

                amPm = (hour >= 12) ? "PM" : "AM";
                hour = (hour == 0) ? 12 : ((hour > 12) ? (hour - 12) : hour);

                selectedTime = String.format("%02d:%02d %s", hour, minute, amPm);
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("appointment");
                String doc_mail = getIntent().getStringExtra("doc_mail");
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                String pat_mail = sharedPreferences.getString("email", "");
                String date = date_for_database;
                String time = selectedTime;
                HelperClass3 helperClass = new HelperClass3(pat_mail, doc_mail ,date ,time);
                reference.child(pat_mail.replace(".",",")+"&"+doc_mail.replace(".",",")).setValue(helperClass);
                timeButton.setText(selectedTime);
            }
        }, 12, 00, false);

        dialog.show();

        return selectedTime;
    }

}