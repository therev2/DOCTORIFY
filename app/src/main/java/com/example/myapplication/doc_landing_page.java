package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class doc_landing_page extends AppCompatActivity {

    RecyclerView recyclerView_doc;
    DatabaseReference database;
    Myadapter_Doc myadapter_doc;
    ArrayList<HelperClass3> list_doc;
    TextView doctor_nam_karan;
    public static final String SHARED_PREFS="sharedPrefs_doc";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doc_landing_page);

        SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String Email_of_doc = sharedPreferences_doc.getString("doc_email", "");


        doctor_nam_karan = findViewById(R.id.doccomo);
        doctor_nam_karan.setText("Dr."+ getIntent().getStringExtra("docu_name"));
        recyclerView_doc = findViewById(R.id.recyclerView_doc);
        database = FirebaseDatabase.getInstance().getReference("appointment");
        recyclerView_doc.setHasFixedSize(true);
        recyclerView_doc.setLayoutManager(new LinearLayoutManager(this));

        list_doc = new ArrayList<>();
        myadapter_doc = new Myadapter_Doc(this,list_doc);
        recyclerView_doc.setAdapter(myadapter_doc);
        ImageView doc_photo = findViewById(R.id.imageView2);
        Glide.with(doc_landing_page.this).load(getIntent().getStringExtra("image_url")).into(doc_photo);


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HelperClass3 helper = dataSnapshot.getValue(HelperClass3.class);
                    list_doc.add(helper);
                    searchList(Email_of_doc);


                }
                myadapter_doc.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void searchList(String text){
        ArrayList<HelperClass3> searchList = new ArrayList<>();
        for (HelperClass3 helperClass: list_doc){
            if (helperClass.getDoc_email().toLowerCase().contains(text.toLowerCase())){
                searchList.add(helperClass);
            }
        }
        myadapter_doc.searchDataList(searchList);
    }
}