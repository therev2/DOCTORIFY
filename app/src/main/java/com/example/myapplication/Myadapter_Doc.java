package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Myadapter_Doc extends RecyclerView.Adapter<Myadapter_Doc.MyViewHolder_Doc>{

    Context context;

    ArrayList<HelperClass3> list_doc;

    public Myadapter_Doc(Context context, ArrayList<HelperClass3> list_doc) {
        this.context = context;
        this.list_doc = list_doc;
    }

    @NonNull
    @Override
    public MyViewHolder_Doc onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_doc_appointment,parent, false);
        return new MyViewHolder_Doc(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder_Doc holder, int position) {

        HelperClass3 helperclass = list_doc.get(position);
        holder.pat_name.setText(helperclass.getPat_name());
        holder.app_date.setText(helperclass.getDate());
        holder.app_time.setText(helperclass.getTime());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, chat_activity_doctor.class);
            intent.putExtra("pat_email",helperclass.getPat_email());
            intent.putExtra("pat_name",helperclass.getPat_name());
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return list_doc.size();
    }



    public static class MyViewHolder_Doc extends RecyclerView.ViewHolder{

        TextView pat_name, app_date, app_time;

        public MyViewHolder_Doc(@NonNull View itemView) {
            super(itemView);

            pat_name = itemView.findViewById(R.id.patEmail);
            app_time = itemView.findViewById(R.id.app_time);
            app_date = itemView.findViewById(R.id.app_date);


        }
    }

}
