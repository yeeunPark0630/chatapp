package com.example.chat.Adapter;

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
import com.example.chat.MessageActivity;
import com.example.chat.Model.Chat;
import com.example.chat.R;
import com.example.chat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Chat> mChats;
    private String imgurl;
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChats, String imgurl){
        this.mContext= mContext;
        this.mChats = mChats;
        this.imgurl = imgurl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_RIGHT){ // sender
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else { // receiver
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // show message and profile image
       Chat chat = mChats.get(position);
       MessageAdapter.ViewHolder holder1 = (MessageAdapter.ViewHolder) holder;
       holder1.message.setText(chat.getMessage());
       if(imgurl.equals("default")){
           holder1.profile_image.setImageResource(R.drawable.ic_baseline_person_24);
       } else {
           Glide.with(mContext).load(imgurl).into(holder1.profile_image);
       }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView message;
        public ImageView profile_image;

        public ViewHolder(View itemView){
            super(itemView);
            message = itemView.findViewById(R.id.msg_show);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }
}

