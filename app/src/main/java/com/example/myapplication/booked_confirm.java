package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class booked_confirm extends AppCompatActivity {

    Button done1;
    TextView doctor_namee;
    TextView timeee;
    ImageView qrcode;

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

        qrcode = findViewById(R.id.qrCode);


        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix = mWriter.encode(getIntent().getStringExtra("qr_code_data"), BarcodeFormat.QR_CODE, 200,200);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            qrcode.setImageBitmap(mBitmap);

        } catch (WriterException e) {
            e.printStackTrace();}



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}