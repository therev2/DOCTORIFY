package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityMyAppListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class my_app_list extends AppCompatActivity {
    RecyclerView recyclerView_appointment;
    DatabaseReference database;
    MyAppointmentsPat_Adapter myadapter_appointments;
    ArrayList<HelperClass3> appointment_list;
    public static final String SHARED_PREFS="sharedPrefs";


    private AppBarConfiguration appBarConfiguration;
    private ActivityMyAppListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_app_list);

        //getting doc email from shared preference and storing it in variable
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String Email_of_pat = sharedPreferences.getString("patient_email","");


        //recycler to get all appointment list
        recyclerView_appointment = findViewById(R.id.mainuser);
        database = FirebaseDatabase.getInstance().getReference("appointment");
        recyclerView_appointment.setHasFixedSize(true);
        recyclerView_appointment.setLayoutManager(new LinearLayoutManager(this));

        appointment_list = new ArrayList<>();
        myadapter_appointments = new MyAppointmentsPat_Adapter(this,appointment_list);
        recyclerView_appointment.setAdapter(myadapter_appointments);

        //getting all appointments from database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointment_list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HelperClass3 helper = dataSnapshot.getValue(HelperClass3.class);
                    assert helper != null;
                    if (helper.getPat_email().toLowerCase().contains(Email_of_pat.toLowerCase())) {
                        appointment_list.add(helper);
                    }
                }
                //refreshing for data change
                myadapter_appointments.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_my_app_list);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}