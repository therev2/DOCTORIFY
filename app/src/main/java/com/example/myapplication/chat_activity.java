package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class chat_activity extends AppCompatActivity {
    TextView doc_name;
    TextView status;
    String chatroomId;
    ImageButton back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);



        //setting the doctor username on chat screen//
        doc_name = findViewById(R.id.doctor_name);
        doc_name.setText(getIntent().getStringExtra("username"));

        back_btn = findViewById(R.id.back_btn_arrow);

        back_btn.setOnClickListener(v -> {
            getOnBackPressedDispatcher();
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}