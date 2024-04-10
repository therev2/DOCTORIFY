package com.example.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class doctor_appointment_full_screen extends AppCompatActivity implements View.OnClickListener {

    private TextView doctorName;
    private TextView doctorSpecialist;
    private ImageView docProfile;
    private Button appointmentButton;
    private Button timeButton;
    private String doctorNameString;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String dateForDatabase = "";
    private LinearLayout[] timeSlotsLayout;
    private static final String SHARED_PREFS = "sharedPrefs";
    private String selectedTime = "";
    private String patMail;
    private String docMail;

    @Override
    public void onClick(View v) {
        dateForDatabase = (String) v.getTag();
        Toast.makeText(this, dateForDatabase, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment_full_screen);

        doctorName = findViewById(R.id.doc_namee);
        doctorSpecialist = findViewById(R.id.doc_specialistt);
        docProfile = findViewById(R.id.doc_dp);
        timeButton = findViewById(R.id.time_btn);
        appointmentButton = findViewById(R.id.Appointment_btn);

        timeSlotsLayout = new LinearLayout[]{
                findViewById(R.id.a18), findViewById(R.id.a19), findViewById(R.id.a20),
                findViewById(R.id.a21), findViewById(R.id.a22), findViewById(R.id.a23),
                findViewById(R.id.a24)
        };

        for (LinearLayout layout : timeSlotsLayout) {
            layout.setOnClickListener(this);
        }

        timeButton.setOnClickListener(v -> openDialog());

        doctorNameString = getIntent().getStringExtra("username");
        doctorName.setText(doctorNameString);
        doctorSpecialist.setText(getIntent().getStringExtra("specialist"));
        Glide.with(this).load(getIntent().getStringExtra("Image")).into(docProfile);

        docMail = getIntent().getStringExtra("doc_mail");
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        patMail = sharedPreferences.getString("email", "");

        appointmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, booked_confirm.class);
            intent.putExtra("doctor_name", doctorNameString);
            intent.putExtra("timee", selectedTime);
            intent.putExtra("qr_code_data", patMail + "&" + docMail);
            startActivity(intent);
        });
    }

    private void openDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            int hour = hourOfDay % 12;
            String amPm = (hourOfDay / 12) == 0 ? "AM" : "PM";
            selectedTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, amPm);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference("appointment");
            String date = dateForDatabase;
            HelperClass3 helperClass = new HelperClass3(patMail, docMail, date, selectedTime);
            reference.child(patMail.replace(".", ",") + "&" + docMail.replace(".", ",")).setValue(helperClass);

            timeButton.setText(selectedTime);
        }, 0, 0, false);

        dialog.show();
    }
}