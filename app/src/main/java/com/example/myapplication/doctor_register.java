package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.firebase.PrefrenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class doctor_register extends AppCompatActivity {
    private static final String PERMISSION_USE_CAMERA = Manifest.permission.CAMERA;
    private static final int PERMISSION_REQUIRED_CODE = 100;


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
    private ActivityResultLauncher<Intent> activityResultLauncher;
    String item;

    String per[]={"android.permission.READ_MEDIA_IMAGES"};
    String permission[]={"android.permission.WRITE_EXTERNAL_STORAG"};
    public static final String SHARED_PREFS="sharedPrefs_doc";
    private static final int CAMERA_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_register);
        requestRunTimePermissions();
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

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                uri = data.getData();
                                try {
                                    InputStream inputStream = getContentResolver().openInputStream(uri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    Bitmap resizedBitmap = getResizedBitmap(bitmap, 300, 300);
                                    uplodedImage.setImageBitmap(resizedBitmap);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Bundle extras = data.getExtras();
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
                                uplodedImage.setImageBitmap(imageBitmap);
                                uri = getImageUri(imageBitmap);
                            }
                        } else {
                            Toast.makeText(doctor_register.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );



        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(doctor_register.this);
                builder.setTitle("Select Image Source")
                        .setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                                    photoPicker.setType("image/*");
                                    activityResultLauncher.launch(photoPicker);
                                    break;
                                case 1:
                                    if (ActivityCompat.checkSelfPermission(doctor_register.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(doctor_register.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUIRED_CODE);
                                    } else {
                                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        activityResultLauncher.launch(cameraIntent);
                                    }
                                    break;
                            }
                        });
                builder.create().show();


//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
            }
        });



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Doctor Images")
                        .child(uri.getLastPathSegment());

                AlertDialog.Builder builder = new AlertDialog.Builder(doctor_register.this);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!((Task<?>) uriTask).isComplete());
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
                        String visibility = "1";

                        if(validateEmail() && validatePassword()){
                            HelperClass helperClass = new HelperClass(email, password, name, exp, charge, time, degree, speacilist, imageURL,visibility);
                            reference.child(email.replace(".",",")).setValue(helperClass);

                            SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences_doc.edit();
                            editor.putString("remember", "true");
                            editor.apply();
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
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    private void requestRunTimePermissions() {
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_USE_CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, open the camera intent
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_USE_CAMERA)) {
            // Show a rationale dialog to explain why the permission is needed
            openCamera();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app requires camera permission")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        ActivityCompat.requestPermissions(doctor_register.this, new String[]{PERMISSION_USE_CAMERA}, PERMISSION_REQUIRED_CODE);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_USE_CAMERA}, PERMISSION_REQUIRED_CODE);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(cameraIntent);
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
        }   }
//     }

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


    public Bitmap getResizedBitmap(Bitmap image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;
        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float)maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float)maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
    }
}
