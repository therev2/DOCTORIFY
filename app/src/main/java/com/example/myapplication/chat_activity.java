package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ActivityChatBinding;

public class chat_activity extends AppCompatActivity {

    private ActivityChatBinding binding;
    TextView doc_name;
    ImageView docImage;
    ImageButton back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setting doctor image on chat screen
        docImage = findViewById(R.id.doctor_profile);
        Glide.with(this).load(getIntent().getStringExtra("Image")).into(docImage);

        //setting the doctor username on chat screen//
        doc_name = findViewById(R.id.doctor_name);
        doc_name.setText(getIntent().getStringExtra("username"));

        back_btn = findViewById(R.id.back_btn_arrow);

        back_btn.setOnClickListener(v -> {
            getOnBackPressedDispatcher();
        });

        getorCreateChatRoom();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    void getorCreateChatRoom(){
         
    }
}