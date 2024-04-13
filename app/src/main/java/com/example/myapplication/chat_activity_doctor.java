package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication.adapters.ChatAdapter;
import com.example.myapplication.databinding.ActivityChatDoctorBinding;
import com.example.myapplication.firebase.Constants;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class chat_activity_doctor extends AppCompatActivity implements ChatAdapter.OnImageClickListener{

    private List<ChatMessage> chatMessages;
    String Email_of_pat;
    String Email_of_doct;
    private static final int PERMISSION_REQUIRED_CODE = 100;

    private ChatAdapter chatAdapter;

    private FirebaseFirestore database;
    public static final String SHARED_PREFS="sharedPrefs_doc";


    EditText editText;
    private Uri uri;
    ImageButton sendBtn;
    TextView patient_status;
    private ImageView add_img_btn;

    private ActivityChatDoctorBinding binding;
    TextView patinet_name;
    ImageButton back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        patient_status = findViewById(R.id.patient_status);

        SharedPreferences sharedPreferences_doc = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Email_of_doct = sharedPreferences_doc.getString("doc_email", "");
        Email_of_pat = getIntent().getStringExtra("pat_email");
        init();
        listenMessages();

        //setting the doctor username on chat screen//
        patinet_name = findViewById(R.id.patient_name);
        patinet_name.setText(getIntent().getStringExtra("pat_name"));

        back_btn = findViewById(R.id.back_btn_arrow);

        back_btn.setOnClickListener(v -> {
            OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
            onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Close the application
                    Intent intent = new Intent(chat_activity_doctor.this, doc_landing_page.class);
                    startActivity(intent);
                    finish();
                }
            });

            Intent intent = new Intent(chat_activity_doctor.this, doc_landing_page.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sendBtn = findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(v->{

            sendMessage();
        });


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();//for gallery
                            if (data != null && data.getData() != null) {
                                uri = data.getData();
                                try {
                                    InputStream inputStream = getContentResolver().openInputStream(uri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    Bitmap resizedBitmap = getResizedBitmap(bitmap, 300, 300);
//                                      <-here we have to send to the bubble

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {// for camera
                                Bundle extras = data.getExtras();
                                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                                uplodedImage.setImageBitmap(imageBitmap);
                                uri = getImageUri(imageBitmap);
                            }
                        } else {
                            Toast.makeText(chat_activity_doctor.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                        sendImageMessage(uri);

                    }
                }
        );



        add_img_btn = findViewById(R.id.add_img_btn);
        add_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(chat_activity_doctor.this);
                builder.setTitle("Select Image Source")
                        .setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                                    photoPicker.setType("image/*");
                                    activityResultLauncher.launch(photoPicker);
                                    break;
                                case 1:
                                    if (ActivityCompat.checkSelfPermission(chat_activity_doctor.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(chat_activity_doctor.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUIRED_CODE);
                                    } else {
                                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        activityResultLauncher.launch(cameraIntent);
                                    }
                                    break;
                            }
                        });
                builder.create().show();
            }
        });

    }
    private void init(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages,Email_of_doct,(ChatAdapter.OnImageClickListener) this);//doct->email
        binding.chatRecyclerViewDoc.setAdapter(chatAdapter);

        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        editText = findViewById(R.id.chat_edittext);
        String messageText = editText.getText().toString().trim(); // Get the trimmed message text
        if (!messageText.isEmpty()) { // Check if the message is not empty
            message.put(Constants.KEY_SENDER_ID, Email_of_doct); //doct->email
            message.put(Constants.KEY_RECEIVER_ID, Email_of_pat); //pat-email
            message.put(Constants.KEY_MESSAGE, messageText);
            message.put(Constants.KEY_TIMESTAMP, new Date());
            database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
            editText.setText(""); // Clear the EditText after sending the message
        }
    }

    private void listenMessages(){


        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,Email_of_doct)//doct->email
                .whereEqualTo(Constants.KEY_RECEIVER_ID,Email_of_pat)//patinet->email
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,Email_of_pat)//patient->email
                .whereEqualTo(Constants.KEY_RECEIVER_ID,Email_of_doct )//doc->email
                .addSnapshotListener(eventListener);
    }



    private  final EventListener<QuerySnapshot> eventListener = (value, error)->{
        if(error!= null){
            return;
        }
        if (value != null){
            int count = chatMessages.size();
            for (DocumentChange documentChange: value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage  = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);}

            }
            Collections.sort(chatMessages, (obj1,obj2)->obj1.dateObject.compareTo(obj2.dateObject));
            if (count==0){
                chatAdapter.notifyDataSetChanged();
            }
            else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatRecyclerViewDoc.smoothScrollToPosition(chatMessages.size()-1);

            }
            binding.chatRecyclerViewDoc.setVisibility(View.VISIBLE);
        }


    };

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    public void onImageClick(String imageUrl) {
        openImageViewer(imageUrl);
    }

    private void openImageViewer(String imageUrl) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image_viewer);
        ImageView imageView = dialog.findViewById(R.id.image_view);
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
        dialog.show();
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

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    private void sendImageMessage(Uri imageUri) {
        // Upload the image to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("chat_images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Send the download URL as a chat message
                                HashMap<String, Object> message = new HashMap<>();
                                message.put(Constants.KEY_SENDER_ID, Email_of_doct);
                                message.put(Constants.KEY_RECEIVER_ID, Email_of_pat);
                                message.put(Constants.KEY_MESSAGE, uri.toString());
                                message.put(Constants.KEY_TIMESTAMP, new Date());
                                database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
                });
    }
}