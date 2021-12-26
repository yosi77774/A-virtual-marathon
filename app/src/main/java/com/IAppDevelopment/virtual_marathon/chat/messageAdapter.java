package com.IAppDevelopment.virtual_marathon.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.IAppDevelopment.virtual_marathon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This class receives the user message data,
 * and feeds the display screen with this data.
 */
public class messageAdapter extends RecyclerView.Adapter<messageAdapter.messageViewHolder> {

    private ArrayList<com.IAppDevelopment.virtual_marathon.chat.message> message;

    public messageAdapter(ArrayList<com.IAppDevelopment.virtual_marathon.chat.message> message) {
        this.message = message;
    }

    @NonNull
    @Override
    public messageAdapter.messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem_message,parent,false);

        return new messageAdapter.messageViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position) {
        com.IAppDevelopment.virtual_marathon.chat.message currentmessage = message.get(position);
        // holder.tv_name.setText(currentmessage.getName());
        holder.tv_time2.setText(currentmessage.getData());
        holder.tv_name2.setText(currentmessage.getMy_name());
        holder.tv_message2.setText(currentmessage.getMessage());
        Picasso.get().load(currentmessage.getPic_url()).into(holder.iv_message);
    }


    @Override
    public int getItemCount() {
        return message.size();
    }

    public static class messageViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_name2;
        public TextView tv_message2;
        public TextView tv_time2;
        public ImageView iv_message;


        public messageViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time2=itemView.findViewById(R.id.Time);
            tv_message2=itemView.findViewById(R.id.tv_message2);
            tv_name2=itemView.findViewById(R.id.tv_name2);
            iv_message=itemView.findViewById(R.id.iv_message1);

        }
    }

}

