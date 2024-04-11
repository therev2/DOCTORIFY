package com.example.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class doctor_appointment_full_screen extends AppCompatActivity {
    private TextView doctorName;
    private TextView doctorSpecialist;
    private ImageView docProfile;
    private Button appointmentButton;
    private Button timeButton;
    private String doctorNameString;
    private DatabaseReference reference;
    private String dateForDatabase = "";
    private static final String SHARED_PREFS = "sharedPrefs";
    private String selectedTime = "";
    private String patMail;
    private String docMail;
    private ItemDate selectedDateItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment_full_screen);
        RecyclerView recyclerView = findViewById(R.id.recyclerDate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<ItemDate> items = getDateItems();
        MyAdapterDate adapter = new MyAdapterDate(this, items);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, itemDate) -> {
            selectedDateItem = itemDate;

            // Update the selected position
            int clickedPosition = recyclerView.getChildAdapterPosition(view);
            adapter.setSelectedPosition(clickedPosition);
        });

        doctorName = findViewById(R.id.doc_namee);
        doctorSpecialist = findViewById(R.id.doc_specialistt);
        docProfile = findViewById(R.id.doc_dp);
        timeButton = findViewById(R.id.time_btn);
        appointmentButton = findViewById(R.id.Appointment_btn);
        appointmentButton.setEnabled(false); // Initially disable the button

        timeButton.setOnClickListener(v -> openDialog());

        doctorNameString = getIntent().getStringExtra("username");
        doctorName.setText(doctorNameString);
        doctorSpecialist.setText(getIntent().getStringExtra("specialist"));
        Glide.with(this).load(getIntent().getStringExtra("Image")).into(docProfile);

        docMail = getIntent().getStringExtra("doc_mail");
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        patMail = sharedPreferences.getString("patient_email", "");

        appointmentButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, booked_confirm.class);
            intent.putExtra("doctor_name", doctorNameString);
            intent.putExtra("timee", selectedTime);
            intent.putExtra("qr_code_data", patMail + "&" + docMail);

            // Check if selectedDateItem and selectedTime are not empty or null
            if (selectedDateItem != null && !selectedTime.isEmpty()) {
                dateForDatabase = selectedDateItem.getDate() + " " + selectedDateItem.getDay();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                reference = database.getReference("appointment");

                HelperClass3 helperClass = new HelperClass3(patMail, docMail, dateForDatabase, selectedTime);
                reference.child(patMail.replace(".", ",") + "&" + docMail.replace(".", ",")).setValue(helperClass);
            }

            startActivity(intent);
        });
    }

    private List<ItemDate> getDateItems() {
        List<ItemDate> items = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = currentDay; day <= daysInMonth; day++) {
            calendar.set(currentYear, currentMonth, day);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String dayName = getDayName(dayOfWeek);
            String dayNumber = String.valueOf(day);
            items.add(new ItemDate(dayName, dayNumber));
        }

        return items;
    }

    private void openDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            int hour = hourOfDay % 12;
            String amPm = (hourOfDay / 12) == 0 ? "AM" : "PM";
            selectedTime = String.format("%02d:%02d %s", hour == 0 ? 12 : hour, minute, amPm);
            if (!selectedTime.isEmpty() && selectedDateItem != null) {
                appointmentButton.setEnabled(true);
            } else {
                appointmentButton.setEnabled(false);
            }

            timeButton.setText(selectedTime);
        }, 0, 0, false);

        dialog.show();
    }

    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "Sun";
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            default:
                return "";
        }
    }
}