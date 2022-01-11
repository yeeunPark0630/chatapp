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
import com.example.chat.Adapter.UserAdapterForChat;
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

// CHATS fragment
public class chatFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapterForChat userAdapter;
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

       // set user adapter for chat
       userAdapter = new UserAdapterForChat(getContext(), mUsers);
       recyclerView.setAdapter(userAdapter);

       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       userList = new ArrayList<>();

       reference = FirebaseDatabase.getInstance().getReference("Chats");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               // clear previous user list
               userList.clear();

               for(DataSnapshot snapshot1: snapshot.getChildren()){
                   Chat chat = snapshot1.getValue(Chat.class);

                   // if I'm the one who is sender, add receiver to the list
                   if(chat.getSender().equals(firebaseUser.getUid())){
                       userList.add(chat.getReceiver());
                   }

                   // if I'm the one who is receiver, add sender to the list
                   if(chat.getReceiver().equals(firebaseUser.getUid())){
                       userList.add(chat.getSender());
                   }
               }
               // read user
               readUsers();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
        return view;
    }

    // Display user at chat
    private void readUsers(){
        reference = FirebaseDatabase.getInstance().getReference("UserAccount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear user array
                mUsers.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    for(String id: userList){ // check for every user list
                        if(user.getId().equals(id)){ // id in userlist is me (user)
                            if(mUsers.size() != 0){
                                for(User user1: mUsers){
                                    if(!user.getId().equals(user1.getId())){ // user id is not equal to id in mUser array
                                        mUsers.add(user); // add user in the muser array
                                    }
                                }
                            } else { // id in userlist is friends
                                mUsers.add(user); // add user in the muser array
                            }
                        }
                    }
                }
               // userAdapter = new UserAdapterForChat(getContext(), mUsers);
                userAdapter = new UserAdapterForChat(getContext(), mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}