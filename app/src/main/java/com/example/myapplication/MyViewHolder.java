package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView day_view,date_view;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        day_view = itemView.findViewById(R.id.day_);
        date_view = itemView.findViewById(R.id.date_);

    }
}
