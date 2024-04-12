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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.adapters.ChatAdapter;
import com.example.myapplication.databinding.ActivityChatBinding;
import com.example.myapplication.databinding.ActivityChatDoctorBinding;
import com.example.myapplication.firebase.Constants;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class chat_activity_doctor extends AppCompatActivity {
    private User receiverUser;
    private List<ChatMessage> chatMessages;

    private ChatAdapter chatAdapter;

    private FirebaseFirestore database;
    public static final String SHARED_PREFS="sharedPrefs";


    EditText editText;
    ImageButton sendBtn;

    private ActivityChatDoctorBinding binding;
    TextView doc_name;
    ImageView docImage;
    ImageButton back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        Email_of_pat = sharedPreferences.getString("patient_email", "");
        init();
        listenMessages();

        //setting the doctor username on chat screen//
        doc_name = findViewById(R.id.patient_name);

        back_btn = findViewById(R.id.back_btn_arrow);

        back_btn.setOnClickListener(v -> {
            OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
            onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Close the application
                    Intent intent = new Intent(chat_activity_doctor.this, MyDocChat.class);
                    startActivity(intent);
                    finish();
                }
            });
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
        chatAdapter = new ChatAdapter(chatMessages,"rajeev@gmail.com");
        binding.chatRecyclerViewDoc.setAdapter(chatAdapter);

        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        editText = findViewById(R.id.chat_edittext);
        message.put(Constants.KEY_SENDER_ID,"rajeev@gmail.com");
        message.put(Constants.KEY_RECEIVER_ID,"x@gmail.com");
        message.put(Constants.KEY_MESSAGE,editText.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        editText.setText(null);
    }

    private void listenMessages(){


        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,"rajeev@gmail.com")
                .whereEqualTo(Constants.KEY_RECEIVER_ID,"x@gmail.com")
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,"x@gmail.com")
                .whereEqualTo(Constants.KEY_RECEIVER_ID,"rajeev@gmail.com" )
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

}