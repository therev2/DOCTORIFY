package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class chat_activity extends AppCompatActivity {
    private List<ChatMessage> chatMessages;

    private ChatAdapter chatAdapter;

    private FirebaseFirestore database;
    public static final String SHARED_PREFS="sharedPrefs";


    EditText editText;
    ImageButton sendBtn;

    String Email_of_pat;

    private ActivityChatBinding binding;
    TextView doc_name;
    ImageView docImage;
    ImageButton back_btn;
    TextView doct_staus;
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
        chatAdapter = new ChatAdapter(chatMessages,Email_of_pat);

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

}