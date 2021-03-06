package com.example.chat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.chat.R;
import com.example.chat.Model.User;
import com.example.chat.Adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers = new ArrayList<>();

    EditText searchUsers; // for search users

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_users, container, false);

       recyclerView = view.findViewById(R.id.recycler_view);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

       userAdapter = new UserAdapter(getContext(), mUsers);
       recyclerView.setAdapter(userAdapter);

        // call readsUsers function
       readUsers();

       searchUsers = view.findViewById(R.id.searchUsers);
       searchUsers.addTextChangedListener(new TextWatcher() { // when the text changed -> event occurs
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
       return view;
    }

    // for searching users in the user list
    private void searchUsers(String toString) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // make query
        Query query = FirebaseDatabase.getInstance().getReference("UserAccount").orderByChild("lowercaseUsername").startAt(toString).endAt(toString+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear(); // clear the array
                for (DataSnapshot snapshot1: snapshot.getChildren()){ // loop for all users
                    User user = snapshot1.getValue(User.class);
                    if(!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user); // add to the array
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

    // for reading the users in the firebase
    private void readUsers(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // get the reference under UserAccount
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserAccount");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(searchUsers.getText().toString().equals("")) { // readuser occurs before searchuser occurs
                    mUsers.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        User user = snapshot1.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) { // if the read user is not the user
                            mUsers.add(user); // then add to the array
                        }
                    }

                    // attach adapter
                    userAdapter = new UserAdapter(getContext(), mUsers);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}