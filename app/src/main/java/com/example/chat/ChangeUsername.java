package com.example.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeUsername extends AppCompatActivity {
    EditText username;
    Button cancelBtn, updateBtn;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference;
    Boolean isChanged = false;


    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        username =  findViewById(R.id.changeUsername);
        cancelBtn = findViewById(R.id.cancel_button3);
        updateBtn = findViewById(R.id.ok_button2);
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(user.getUid());


        // for setting current username on the view
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                isChanged = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // cancel button -> go to main app intent
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangeUsername.this, MainApp.class));
            }
        });



        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                isChanged = true; // for tracking the username is updated
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChanged){ // username update is successfully when the username is updated
                    Toast.makeText(ChangeUsername.this, "Username updated successfully!", Toast.LENGTH_SHORT).show();
                    databaseReference.child("username").setValue(username.getText().toString());
                    //startActivity(new Intent(ChangeUsername.this, MainApp.class));
                    finish();
                } else { // if the username is not updated
                    Toast.makeText(ChangeUsername.this, "Username updates failed!", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
