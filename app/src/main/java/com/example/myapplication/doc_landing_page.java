package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Emitter;

public class doc_landing_page extends AppCompatActivity {

    RecyclerView recyclerView_doc;
    DatabaseReference database;
    Myadapter_Doc myadapter_doc;
    ArrayList<HelperClass3> list_doc;
    TextView doctorName;
    String doc_name,image_url,scanned_patEmail,Email_of_doc;
    Button logoutDoc, camera_btn, resetButton;

    //initialised shared storage for doc
    public static final String SHARED_PREFS="sharedPrefs_doc";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doc_landing_page);

        logoutDoc = findViewById(R.id.logout_doc);
        camera_btn = findViewById(R.id.camera_btn);
        resetButton = findViewById(R.id.reset_btn);

        logoutDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(doc_landing_page.this,"Log out Successful",Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences_doc.edit();
                editor.putString("doc_email", "null");
                editor.putString("remember", "false");
                editor.apply();
                Intent intent = new Intent(doc_landing_page.this, MainActivity.class);
                startActivity(intent);

            }
        });

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(doc_landing_page.this);
                intentIntegrator.setPrompt("Scan a QR Code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
                intentIntegrator.initiateScan();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList(Email_of_doc);
                Toast.makeText(doc_landing_page.this,"reset successful",Toast.LENGTH_SHORT).show();

            }
        });



        //getting doc email from shared preference and storing it in variable
        SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Email_of_doc = sharedPreferences_doc.getString("doc_email", "");

        //referencing database for parent "doctor"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doctor");

        //matching input email with database email
        Query checkUserDatabase = reference.orderByChild("email").equalTo(Email_of_doc);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    doc_name = snapshot.child(Email_of_doc.replace(".",",")).child("name").getValue(String.class);
                    image_url = snapshot.child(Email_of_doc.replace(".",",")).child("image").getValue(String.class);

                    //getting doc name form intent and setting it up
                    doctorName = findViewById(R.id.doccomo);
                    doctorName.setText("Dr."+doc_name);

                    //setting doc image from database
                    ImageView doc_photo = findViewById(R.id.imageView2);
                    Glide.with(doc_landing_page.this).load(image_url).into(doc_photo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        //recycler to get all appointment list
        recyclerView_doc = findViewById(R.id.recyclerView_doc);
        database = FirebaseDatabase.getInstance().getReference("appointment");
        recyclerView_doc.setHasFixedSize(true);
        recyclerView_doc.setLayoutManager(new LinearLayoutManager(this));

        list_doc = new ArrayList<>();
        myadapter_doc = new Myadapter_Doc(this,list_doc);
        recyclerView_doc.setAdapter(myadapter_doc);



        // getting all appointments from database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HelperClass3 helper = dataSnapshot.getValue(HelperClass3.class);
                    list_doc.add(helper);

                    //filtering all appointments list based on doc email
                    searchList(Email_of_doc);

                }
                //refreshing for data change
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


    //function to filter all appointment list based upon whatever text is passed in the fucntion
    //this one is for email based filtering
    public void searchList(String text){
        ArrayList<HelperClass3> searchList = new ArrayList<>();
        for (HelperClass3 helperClass: list_doc){
            if (helperClass.getDoc_email().toLowerCase().contains(text.toLowerCase())){
                searchList.add(helperClass);
            }
        }
        myadapter_doc.searchDataList(searchList);
    }

    public void scannedList(String text){
        ArrayList<HelperClass3> scannedList = new ArrayList<>();
        for (HelperClass3 helperClass: list_doc){
            if (helperClass.getPat_email().toLowerCase().contains((text.toLowerCase())) &&
                    helperClass.getDoc_email().toLowerCase().contains(Email_of_doc.toLowerCase())){
                scannedList.add(helperClass);
            }

        }
        myadapter_doc.searchScannedList(scannedList);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            String content = intentResult.getContents();
            if(content != null){
                System.out.println(content);
                scanned_patEmail = content.substring(0,content.indexOf("&"));
                System.out.println(scanned_patEmail);
                scannedList(scanned_patEmail);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}