package com.example.myapplication;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the views in ViewHolder
    }

    @Override
    public int getItemCount() {
        // Return the size of your data set
        return 0;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here using findViewById()
        }
    }
}
