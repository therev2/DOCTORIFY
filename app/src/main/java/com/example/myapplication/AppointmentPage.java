package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class AppointmentPage extends AppCompatActivity {

    String[] specialist_array = {"Diabetes Management", "Diet and Nutrition", "Physiotherapist", "ENT Specialist",  "Eyes specialist", "Pulmonologist", "Dentist", "Sexual Health",  "Women's Health ",  "Gastroenterologist", "Cardiologist", "Skin and Hair",  "Child Specialist", "General physician"};
    Button login_btn;

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    Button location_search_btn;
    Button sel_day;

    @SuppressLint({"WrongViewCast", "CutPasteId", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        location_search_btn =findViewById(R.id.Search_location_button);
        location_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppointmentPage.this, Doctor_list_patient_main_screen.class);
                startActivity(intent);
            }
        });


        autoCompleteTextView = findViewById(R.id.specilist_complete_txt);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item, specialist_array);


        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString().trim();
                Toast.makeText(AppointmentPage.this,item, Toast.LENGTH_SHORT).show();
            }
        });


        sel_day = findViewById(R.id.Day_select);

        sel_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DayselectPopup();
            }
        });





    }

    private void DayselectPopup(){
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog instance with the appropriate constructor
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set the selected date to the TextView or any other UI component
                sel_day.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
            }
        }, year, month, dayOfMonth); // Provide the initial year, month, and day of the month

        // Show the DatePickerDialog
        dialog.show();
    }


}