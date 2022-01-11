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
import com.example.chat.R;
import com.example.chat.Model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context mContext, List<User> mUsers){
        this.mContext= mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_info, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = mUsers.get(position);
        ViewHolder holder1 = (ViewHolder) holder;
        // set username
        holder1.username.setText(user.getUsername());

        // set profile image
        if(user.getImageURL().equals("default")){
            holder1.profile_image.setImageResource(R.drawable.ic_baseline_person_24);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder1.profile_image);
        }

        // set status message
        if(user.getStatusMsg().equals("default")){
            holder1.statusMsg.setText("");
        } else {
            holder1.statusMsg.setText(user.getStatusMsg());
        }

        // if user profile is clicked, move to chat activity
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        public TextView statusMsg;

        public ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username2);
            profile_image = itemView.findViewById(R.id.profile_image);
            statusMsg = itemView.findViewById(R.id.statusMsg);
        }
    }
}
