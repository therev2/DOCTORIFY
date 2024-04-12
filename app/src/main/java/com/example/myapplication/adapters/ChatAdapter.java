package com.example.myapplication.adapters;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.ChatMessage;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemContainerRecievedMessageBinding;
import com.example.myapplication.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {




    private final List<ChatMessage> chatMessages;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    private final OnImageClickListener onImageClickListener;

    public ChatAdapter(List<ChatMessage> chatMessages, String senderId,OnImageClickListener onImageClickListener) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.onImageClickListener = onImageClickListener;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new ReceivedMessageViewHolder(
                    ItemContainerRecievedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            SentMessageViewHolder sentMessageViewHolder = (SentMessageViewHolder) holder;
            handleMessage(sentMessageViewHolder, chatMessage);
        } else {
            ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
            handleMessage(receivedMessageViewHolder, chatMessage);
        }
    }

    private void handleMessage(SentMessageViewHolder holder, ChatMessage chatMessage) {
        if (isImageMessage(chatMessage.message)) {
            holder.binding.textMessage.setVisibility(View.GONE);
            holder.binding.imageMessage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(chatMessage.message)
                    .into(holder.binding.imageMessage);
            holder.binding.imageMessage.setOnClickListener(v -> onImageClickListener.onImageClick(chatMessage.message));
        } else {
            holder.binding.textMessage.setVisibility(View.VISIBLE);
            holder.binding.imageMessage.setVisibility(View.GONE);
            holder.binding.textMessage.setText(chatMessage.message);
        }

        holder.binding.textDateTime.setText(chatMessage.dateTime);
    }

    private void handleMessage(ReceivedMessageViewHolder holder, ChatMessage chatMessage) {
        if (isImageMessage(chatMessage.message)) {
            holder.binding.textMessage.setVisibility(View.GONE);
            holder.binding.imageMessage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(chatMessage.message)
                    .into(holder.binding.imageMessage);
            holder.binding.imageMessage.setOnClickListener(v -> onImageClickListener.onImageClick(chatMessage.message));

        } else {
            holder.binding.textMessage.setVisibility(View.VISIBLE);
            holder.binding.imageMessage.setVisibility(View.GONE);
            holder.binding.textMessage.setText(chatMessage.message);
        }

        holder.binding.textDateTime.setText(chatMessage.dateTime);
    }
    private boolean isImageMessage(String message) {
        return message.startsWith("https://") || message.startsWith("http://");
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
//        private  final ItemContainerSentMessageBinding binding;
//
//        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
//            super(itemContainerSentMessageBinding.getRoot());
//            binding = itemContainerSentMessageBinding;
//        }
        private final ItemContainerSentMessageBinding binding;
        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void  setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

//        private final ItemContainerRecievedMessageBinding binding;
//
//        ReceivedMessageViewHolder(ItemContainerRecievedMessageBinding itemContainerRecievedMessageBinding) {
//            super(itemContainerRecievedMessageBinding.getRoot());
//            binding = itemContainerRecievedMessageBinding;
//        }

        private final ItemContainerRecievedMessageBinding binding;
        ReceivedMessageViewHolder(ItemContainerRecievedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }



}


