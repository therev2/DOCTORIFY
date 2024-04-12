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

public class MyAppointmentsPat_Adapter extends RecyclerView.Adapter<MyAppointmentsPat_Adapter.MyViewHolder_Doc>{

    Context context;

    ArrayList<HelperClass3> appointment_doc;

    public MyAppointmentsPat_Adapter(Context context, ArrayList<HelperClass3> list_doc) {
        this.context = context;
        this.appointment_doc = list_doc;
    }

    @NonNull
    @Override
    public MyViewHolder_Doc onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_app,parent, false);
        return new MyViewHolder_Doc(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder_Doc holder, int position) {

        HelperClass3 helperclass = appointment_doc.get(position);
        holder.doc_name.setText(helperclass.getDoc_name());
        holder.app_date.setText(helperclass.getDate());
        holder.app_time.setText(helperclass.getTime());
        Glide.with(context).load(appointment_doc.get(position).getDoc_image()).into(holder.doc_image);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, booked_confirm.class);
            intent.putExtra("doctor_name", helperclass.getDoc_name());
            intent.putExtra("timee", helperclass.getTime());
            intent.putExtra("qr_code_data", helperclass.getPat_email() + "&" + helperclass.getDoc_email());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return appointment_doc.size();
    }



    public static class MyViewHolder_Doc extends RecyclerView.ViewHolder{

        TextView doc_name, app_date, app_time;
        ImageView doc_image;

        public MyViewHolder_Doc(@NonNull View itemView) {
            super(itemView);

            doc_name = itemView.findViewById(R.id.docName);
            app_time = itemView.findViewById(R.id.time);
            app_date = itemView.findViewById(R.id.date);
            doc_image = itemView.findViewById(R.id.doc_img);


        }
    }

}
