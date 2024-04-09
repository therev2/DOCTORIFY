package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Myadapter extends RecyclerView.Adapter<Myadapter.MyViewHolder> {

    Context context;

    ArrayList<HelperClass> list;

    public Myadapter(Context context, ArrayList<HelperClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        HelperClass helperclass = list.get(position);
        holder.Name.setText("Dr."+helperclass.getName());
        holder.Spec.setText(helperclass.getSpeacilist());
        Glide.with(context).load(list.get(position).getImage()).into(holder.doc_photo);


        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity;
            Intent intent = new Intent(context, doctor_appointment_full_screen.class);
            intent.putExtra("username","Dr."+helperclass.getName());
            intent.putExtra("specialist",helperclass.getSpeacilist());
            intent.putExtra("doc_mail",helperclass.getEmail());
            intent.putExtra("Image",list.get(position).getImage());
//            intent.putExtra("status",helperclass.status):
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public void searchDataList(ArrayList<HelperClass> filterlist){
        list = filterlist;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name, Spec;
        ImageView doc_photo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.docName);
            Spec = itemView.findViewById(R.id.docSpec);
            doc_photo = itemView.findViewById(R.id.recImage);


        }
    }
}
