package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapterDate extends RecyclerView.Adapter<MyAdapterDate.MyViewHolder> {
    private Context context;
    private List<ItemDate> items;
    private int selectedPosition = -1;
    OnItemClickListener onItemClickListener;

    public MyAdapterDate(Context context, List<ItemDate> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_date, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.dateView.setText(items.get(position).getDate());
        holder.dayView.setText(items.get(position).getDay());
        // Update the background color based on the selected position
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.my_primary));
            Toast.makeText(context.getApplicationContext(),items.get(position).getDay() ,Toast.LENGTH_SHORT).show();

        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public void setSelectedPosition(int position) {
        notifyItemChanged(selectedPosition); // Update the previously selected item
        selectedPosition = position;
        notifyItemChanged(selectedPosition); // Update the newly selected item
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ItemDate itemDate);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dateView;
        public TextView dayView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.date_);
            dayView = itemView.findViewById(R.id.day_);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedPosition); // Update the previously selected item
                selectedPosition = position;
                notifyItemChanged(selectedPosition); // Update the newly selected item
            }
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, items.get(position));
            }
        }
    }
}