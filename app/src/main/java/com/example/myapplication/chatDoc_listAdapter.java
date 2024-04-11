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

public class chatDoc_listAdapter extends RecyclerView.Adapter<chatDoc_listAdapter.MyViewHolder_Doc>{

    Context context;

    ArrayList<HelperClass> list_doc;

    public chatDoc_listAdapter(Context context, ArrayList<HelperClass> list_doc) {
        this.context = context;
        this.list_doc = list_doc;
    }

    @NonNull
    @Override
    public MyViewHolder_Doc onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item_doc,parent, false);
        return new MyViewHolder_Doc(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder_Doc holder, int position) {

        HelperClass helperclass = list_doc.get(position);
        holder.DocName.setText(helperclass.getName());
        Glide.with(context).load(list_doc.get(position).getImage()).into(holder.DocImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, chat_activity.class);
            intent.putExtra("username","Dr."+helperclass.getName());
            intent.putExtra("Image",list_doc.get(position).getImage());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return list_doc.size();
    }



    public static class MyViewHolder_Doc extends RecyclerView.ViewHolder{

        TextView DocName;
        ImageView DocImage;

        public MyViewHolder_Doc(@NonNull View itemView) {
            super(itemView);

            DocName = itemView.findViewById(R.id.docName);
            DocImage = itemView.findViewById(R.id.doc_img);

        }
    }

}
