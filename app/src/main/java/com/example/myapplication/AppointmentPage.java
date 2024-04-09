package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AppointmentPage extends AppCompatActivity {

    String[] specialist_array = {"sp1", "sp2", "sp3"};
    Button login_btn;

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    Button location_search_btn;
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
                Toast.makeText(AppointmentPage.this,"Item" + item, Toast.LENGTH_SHORT).show();
            }
        });

//        autoCompleteTextView = findViewById(R.id.location_complete_txt);
//        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item, location_array);
//
//
//        autoCompleteTextView.setAdapter(adapterItems);
//
//        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String item = adapterView.getItemAtPosition(i).toString().trim();
//                Toast.makeText(AppointmentPage.this, item, Toast.LENGTH_SHORT).show();
//            }
//        });





    }
}