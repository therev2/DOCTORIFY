package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ActivityMyprofDocBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Myprof_doc extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMyprofDocBinding binding;

    String Email_of_doc;

    public static final String SHARED_PREFS = "sharedPrefs_doc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_myprof_doc);

        TextView doc_name,doc_email,specialist,charges,timing;
        ImageView doc_image;

        doc_name = findViewById(R.id.docname_profile);
        doc_email = findViewById(R.id.mail_doc);
        specialist = findViewById(R.id.specialist_mp);
        charges = findViewById(R.id.charges);
        timing = findViewById(R.id.time);
        doc_image = findViewById(R.id.doc_dp);

        //getting doc email from shared preference and storing it in variable
        SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Email_of_doc = sharedPreferences_doc.getString("doc_email", "");

        //referencing database for parent "patient"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doctor");

        //matching input email with database email
        Query checkUserDatabase = reference.orderByChild("email").equalTo(Email_of_doc);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String Docname = snapshot.child(Email_of_doc.replace(".", ",")).child("name").getValue(String.class);
                    String docemail = snapshot.child(Email_of_doc.replace(".", ",")).child("email").getValue(String.class);
                    String specialist_doc = snapshot.child(Email_of_doc.replace(".", ",")).child("speacilist").getValue(String.class);
                    String charges_doc = snapshot.child(Email_of_doc.replace(".", ",")).child("charge").getValue(String.class);
                    String timing_doc = snapshot.child(Email_of_doc.replace(".", ",")).child("time").getValue(String.class);
                    String image_doc = snapshot.child(Email_of_doc.replace(".", ",")).child("image").getValue(String.class);

                    doc_name.setText("Dr." + Docname);
                    doc_email.setText(docemail);
                    specialist.setText(specialist_doc);
                    timing.setText(timing_doc);
                    charges.setText("Rs "+charges_doc);

                    Glide.with(Myprof_doc.this).load(image_doc).into(doc_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
