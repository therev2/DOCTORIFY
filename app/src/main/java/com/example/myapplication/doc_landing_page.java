package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
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
    NavigationView navigationViewDoc;
    DrawerLayout drawerLayoutDoc;
    LottieAnimationView menu_toggle_btn_doc;

    RecyclerView recyclerView_doc;
    DatabaseReference database;
    Myadapter_Doc myadapter_doc;
    ArrayList<HelperClass3> list_doc;
    TextView doctorName;
    String doc_name,image_url,scanned_patEmail,scanned_docEmail,Email_of_doc;
    Button logoutDoc;
    ImageView camera_btn;

    //initialised shared storage for doc
    public static final String SHARED_PREFS="sharedPrefs_doc";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.nav_doc);

        logoutDoc = findViewById(R.id.logout_doc);
        camera_btn = findViewById(R.id.camera_btn);
        logoutDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(doc_landing_page.this,"Log out Successful",Toast.LENGTH_SHORT).show();


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

        drawerLayoutDoc = findViewById(R.id.drawer_layout_doc);
        menu_toggle_btn_doc  = findViewById(R.id.menu_btn_doc);


        menu_toggle_btn_doc.setOnClickListener(v -> {
            menu_toggle_btn_doc.playAnimation();
            drawerLayoutDoc.open();

        });

        //navigation item selection code part
        navigationViewDoc = findViewById(R.id.nav_view_doc);

        navigationViewDoc.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemID = menuItem.getItemId();

                if (itemID ==R.id.My_app_doc)
                {
                    Toast.makeText(doc_landing_page.this, "MY appointments ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(doc_landing_page.this, my_app_list.class);
                    startActivity(intent);
                }


                if (itemID == R.id.My_profile_doc){
                    Toast.makeText(doc_landing_page.this, "My Profile ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(doc_landing_page.this, Myprof_doc.class);
                    startActivity(intent);
                }
//

                if (itemID == R.id.Logout_profile_doc){
                    Toast.makeText(doc_landing_page.this,"Log out Successful",Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences_doc.edit();
                    editor.putString("doc_email", "null");
                    editor.putString("remember", "false");
                    editor.apply();
                    Intent intent = new Intent(doc_landing_page.this, MainActivity.class);
                    startActivity(intent);
                }

                drawerLayoutDoc.close();
                return false;
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
                // Clear the list before adding new data
                list_doc.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HelperClass3 helper = dataSnapshot.getValue(HelperClass3.class);
                    if (helper.getDoc_email().toLowerCase().contains(Email_of_doc.toLowerCase())){
                        list_doc.add(helper);
                    }

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


    //function to filter all appointment list based upon whatever text is passed in the function
    //this one is for email based filtering
//    public void searchList(String text){
//        ArrayList<HelperClass3> searchList = new ArrayList<>();
//        for (HelperClass3 helperClass: list_doc){
//            if (helperClass.getDoc_email().toLowerCase().contains(text.toLowerCase())){
//                searchList.add(helperClass);
//            }
//        }
//        myadapter_doc.searchDataList(searchList);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            String content = intentResult.getContents();
            if(content != null){
                scanned_patEmail = content.substring(0,content.indexOf("&"));
                scanned_docEmail = content.substring(content.indexOf("&")+1);
                System.out.println(scanned_docEmail);
                System.out.println(Email_of_doc);
                String parent = scanned_patEmail.replace(".",",") + "&" + Email_of_doc.replace(".",",");
                System.out.println(parent);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                if (scanned_docEmail.equals(Email_of_doc)){
                    reference.child("appointment").orderByKey().equalTo(parent).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(doc_landing_page.this,"Appointment Exists",Toast.LENGTH_SHORT).show();

                                //harshit tick here

                            } else {

                                //if does not exists in database
                                Toast.makeText(doc_landing_page.this,"Appointment does not exists",Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    Toast.makeText(doc_landing_page.this,"Appointment does not exists",Toast.LENGTH_SHORT).show();

                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}