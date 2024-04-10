package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class doctor_register extends AppCompatActivity {

    String[] specialist_array = {"Diabetes Management", "Diet and Nutrition", "Physiotherapist", "ENT Specialist",  "Eyes specialist", "Pulmonologist", "Dentist", "Sexual Health",  "Women's Health ",  "Gastroenterologist", "Cardiologist", "Skin and Hair",  "Child Specialist", "General physician"};


    EditText signupEmail, signupPassword, signupName, signupExp, signupCharge, signupTime, signupDegree;
    Button signupButton, browseBtn;
    ImageView uplodedImage;
    String imageURL;
    Uri uri;
    FirebaseDatabase database;
    DatabaseReference reference;

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    String item;
    Button b;
    String per[]={"android.permission.READ_MEDIA_IMAGES"};
    public static final String SHARED_PREFS="sharedPrefs_doc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_register);
        b=findViewById(R.id.browse_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(per,80);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        autoCompleteTextView = findViewById(R.id.specilist_doc);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, specialist_array);


        autoCompleteTextView.setAdapter(adapterItems);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString().trim();
                Toast.makeText(doctor_register.this,item, Toast.LENGTH_SHORT).show();
            }
        });


        signupName = findViewById(R.id.name_doc);
        signupEmail = findViewById(R.id.email_doc);
        signupPassword = findViewById(R.id.pass_doc);
        signupButton = findViewById(R.id.signupdoc);
        signupExp = findViewById(R.id.exp_doc);
        signupCharge = findViewById(R.id.charge_doc);
        signupTime = findViewById(R.id.time_doc);
        signupDegree = findViewById(R.id.degree_doc);
        browseBtn = findViewById(R.id.browse_btn);
        uplodedImage = findViewById(R.id.uploaded_img);

//for compressing image
////        uplodedImage.buildDrawingCache();
//        Bitmap bmap = uplodedImage.getDrawingCache();
//        Bitmap bitmap = Bitmap.createScaledBitmap(bmap, 300, 300, true);


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uplodedImage.setImageURI(uri);
                        } else {
                            Toast.makeText(doctor_register.this,"No Image Selected", Toast.LENGTH_SHORT);
                        }

                    }
                }
        );

        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });




        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Doctor Images")
                        .child(uri.getLastPathSegment());

                AlertDialog.Builder builder = new AlertDialog.Builder(doctor_register.this);
                builder.setCancelable(false);
//                builder.setView(R.layout.progress_layout)
                AlertDialog dialog = builder.create();
                dialog.show();

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri urlImage = uriTask.getResult();
                        imageURL = urlImage.toString();

                        database = FirebaseDatabase.getInstance();
                        reference = database.getReference("doctor");

                        String email = signupEmail.getText().toString();
                        String password = signupPassword.getText().toString();
                        String name = signupName.getText().toString();
                        String exp = signupExp.getText().toString();
                        String charge = signupCharge.getText().toString();
                        String time = signupTime.getText().toString();
                        String degree = signupDegree.getText().toString();
                        String speacilist = item;



                        if(validateEmail() && validatePassword()){
                            HelperClass helperClass = new HelperClass(email, password, name, exp, charge, time, degree, speacilist, imageURL);
                            reference.child(email.replace(".",",")).setValue(helperClass);

                            SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences_doc.edit();
                            editor.putString("remember", "true");
                            editor.putString("doc_email", email);
                            editor.apply();

                            Toast.makeText(doctor_register.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(doctor_register.this, doc_landing_page.class);
                            startActivity(intent);


                        }
                        dialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });





            }
        });



    }

    boolean isAlphanumeric(final int codePoint) {
        return (codePoint >= 64 && codePoint <= 90) ||
                (codePoint >= 97 && codePoint <= 122) ||
                (codePoint >= 32 && codePoint <= 57);
    }



    public Boolean validateEmail() {
        String val = signupEmail.getText().toString().trim();
        if (val.isEmpty()) {
            signupEmail.setError("Email cannot be empty");
            return false;
        }else if (!properformat()) {
            signupEmail.setError("Invalid Format");
            return false;}
        else {
            signupEmail.setError(null);
            boolean result = true;
            for (int i = 0; i < val.length(); i++) {
                int codePoint = val.codePointAt(i);
                if (!isAlphanumeric(codePoint)) {
                    result = false;
                    break;
                }
            }
            if (result) {
                return true;
            } else {
                signupEmail.setError("Email can only be alphanumberic");
                return false;
            }
        }
    }


    public Boolean validatePassword() {
        String val = signupPassword.getText().toString();
        if (val.isEmpty()) {
            signupPassword.setError("Password cannot be empty");
            return false;
        }   else {
            signupPassword.setError(null);
            if (val.isEmpty()) {
                signupPassword.setError("Password cannot be empty");
                return false;
            } else {
                signupPassword.setError(null);
                boolean result = true;
                for (int i = 0; i < val.length(); i++) {
                    int codePoint = val.codePointAt(i);
                    if (!isAlphanumeric(codePoint)) {
                        result = false;
                        break;
                    }
                }
                if (result) {
                    return true;
                } else {
                    signupPassword.setError("Password can only be alphanumberic");
                    return false;
                }
            }


        }

    }

    public boolean properformat() {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


        Pattern pattern = Pattern.compile(emailRegex);
        String val = signupEmail.getText().toString().trim();

        Matcher matcher = pattern.matcher(val);

        return matcher.matches();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==80)
        {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT).show();
        }
    }



}