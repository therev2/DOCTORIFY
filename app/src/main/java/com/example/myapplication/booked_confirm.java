package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class booked_confirm extends AppCompatActivity {

    Button done1;
    TextView doctor_namee;
    TextView timeee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booked_confirm);
        done1 = findViewById(R.id.done);
        done1.setOnClickListener(v -> {
            Intent intent = new Intent(booked_confirm.this,HomePage.class);
            startActivity(intent);
            finish();

        });
        doctor_namee = findViewById(R.id.doctor_name1);
        doctor_namee.setText(getIntent().getStringExtra("doctor_name"));

        timeee = findViewById(R.id.time);
        timeee.setText(getIntent().getStringExtra("timee"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}