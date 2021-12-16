package com.example.chat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.Adapter.UserAdapter;
import com.example.chat.Model.Chat;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class chatFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers = new ArrayList<>();;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<String> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_chat, container, false);
       recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       userAdapter = new UserAdapter(getContext(), mUsers);
       recyclerView.setAdapter(userAdapter);
       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       userList = new ArrayList<>();

       reference = FirebaseDatabase.getInstance().getReference("Chats");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               userList.clear();

               for(DataSnapshot snapshot1: snapshot.getChildren()){
                   Chat chat = snapshot1.getValue(Chat.class);
                   if(chat.getSender().equals(firebaseUser.getUid())){
                       userList.add(chat.getReceiver());
                   }

                   if(chat.getReceiver().equals(firebaseUser.getUid())){
                       userList.add(chat.getSender());
                   }
               }
               readChats();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
        return view;
    }

    // Display user at chat
    private void readChats(){
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);

                    for(String id: userList){
                        if(user.getId().equals(id)){
                            if(mUsers.size() != 0){
                                for(User user1: mUsers){
                                    if(!user.getId().equals(user1.getId())){
                                        mUsers.add(user);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}