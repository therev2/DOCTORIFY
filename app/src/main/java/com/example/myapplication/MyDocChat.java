package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyDocChat extends AppCompatActivity {

    chatDoc_listAdapter myAdapter_doclist;
    ArrayList<HelperClass> list_doc;
    DatabaseReference database;
    RecyclerView recyclerView_doclist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_doc_chat);

        recyclerView_doclist = findViewById(R.id.mainuser);
        database = FirebaseDatabase.getInstance().getReference("doctor");
        recyclerView_doclist.setHasFixedSize(true);
        recyclerView_doclist.setLayoutManager(new LinearLayoutManager(this));

        list_doc = new ArrayList<>();
        myAdapter_doclist = new chatDoc_listAdapter(this,list_doc);
        recyclerView_doclist.setAdapter(myAdapter_doclist);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HelperClass helper = dataSnapshot.getValue(HelperClass.class);
                    list_doc.add(helper);

                }
                myAdapter_doclist.notifyDataSetChanged();
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
}