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
import com.example.myapplication.databinding.ActivityChatBinding;
import com.example.myapplication.firebase.Constants;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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


public class chat_activity extends AppCompatActivity implements ChatAdapter.OnImageClickListener {



    @Override
    public void onImageClick(String imageUrl) {

        Toast.makeText(this,imageUrl,Toast.LENGTH_SHORT).show();
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

    // ...
    private List<ChatMessage> chatMessages;

    private ChatAdapter chatAdapter;
    Uri uri;

    private FirebaseFirestore database;
    public static final String SHARED_PREFS="sharedPrefs";
    private static final int PERMISSION_REQUIRED_CODE = 100;



    EditText editText;
    ImageButton sendBtn;

    String Email_of_pat;

    private ActivityChatBinding binding;
    TextView doc_name;
    ImageView docImage;
    ImageButton back_btn;
    TextView doct_staus;

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private ImageView add_img_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        doct_staus = findViewById(R.id.doctor_status);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Email_of_pat = sharedPreferences.getString("patient_email", "");
        init();
        listenMessages();



        //setting doctor image on chat screen
        docImage = findViewById(R.id.doctor_profile);
        Glide.with(this).load(getIntent().getStringExtra("Image")).into(docImage);

        //setting the doctor username on chat screen//
        doc_name = findViewById(R.id.doctor_name);
        doc_name.setText(getIntent().getStringExtra("username"));

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
                            Toast.makeText(chat_activity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                        sendImageMessage(uri);

                    }
                }
        );


        //add_img_btn

        add_img_btn = findViewById(R.id.add_img_btn);
        add_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(chat_activity.this);
                builder.setTitle("Select Image Source")
                        .setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                                    photoPicker.setType("image/*");
                                    activityResultLauncher.launch(photoPicker);
                                    break;
                                case 1:
                                    if (ActivityCompat.checkSelfPermission(chat_activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(chat_activity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUIRED_CODE);
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

        back_btn = findViewById(R.id.back_btn_arrow);

        back_btn.setOnClickListener(v -> {
            OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
            onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Close the application
                    Intent intent = new Intent(chat_activity.this, HomePage.class);
                    startActivity(intent);
                    finish();
                }
            });

            Intent intent = new Intent(chat_activity.this, HomePage.class);
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

    }



    private void init(){
        chatMessages = new ArrayList<>();
//        chatAdapter = new ChatAdapter(chatMessages,Email_of_pat);
        chatAdapter = new ChatAdapter(chatMessages, Email_of_pat, (ChatAdapter.OnImageClickListener) this);
        binding.chatRecyclerView.setAdapter(chatAdapter);
        
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        editText = findViewById(R.id.chat_edittext);
        String messageText = editText.getText().toString().trim();

        if (!messageText.isEmpty()){
        message.put(Constants.KEY_SENDER_ID,Email_of_pat);
        message.put(Constants.KEY_RECEIVER_ID,getIntent().getStringExtra("email_doc"));
        message.put(Constants.KEY_MESSAGE,editText.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        editText.setText(null);
    }}

    private void listenMessages(){


        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,Email_of_pat)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,getIntent().getStringExtra("email_doc"))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,getIntent().getStringExtra("email_doc"))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, Email_of_pat)
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
              binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);

          }
          binding.chatRecyclerView.setVisibility(View.VISIBLE);
      }


    };

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
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
                                message.put(Constants.KEY_SENDER_ID, Email_of_pat);
                                message.put(Constants.KEY_RECEIVER_ID, getIntent().getStringExtra("email_doc"));
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