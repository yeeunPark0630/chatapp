package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Adapter.MessageAdapter;
import com.example.chat.Model.Chat;
import com.example.chat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;
    TextView username;
    ImageView profile_img;

    ImageButton btn_send, btn_back;
    EditText send_text;

    // Adapter
    MessageAdapter messageAdapter;
    List<Chat> mchat = new ArrayList<>();

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        // recycler view for show the messages in the view
        recyclerView =  (RecyclerView) findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // create chat array
        mchat = new ArrayList<>();
        messageAdapter = new MessageAdapter(MessageActivity.this, mchat, "defualt");
        recyclerView.setAdapter(messageAdapter);


        // show at the top
        // show user name and profile image
        username = findViewById(R.id.username2);
        profile_img = findViewById(R.id.profile_img2);

        intent = getIntent();
        String userid = intent.getStringExtra("userid");


        // get the user from firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // get firebase reference of userid
        reference = FirebaseDatabase.getInstance().getReference("UserAccount").child(userid);

        // set profile image and username
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_img.setImageResource(R.drawable.ic_baseline_person_24);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_img);
                }

                // read message from firebase cloud
                readMessage(firebaseUser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // send msg
        btn_send = findViewById(R.id.btn_send);
        send_text = findViewById(R.id.send_text);

        // send button clicked
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = send_text.getText().toString(); // get the text from send_text edittext
                if(!msg.equals("")){ // message box is not empty
                    sendMessage(firebaseUser.getUid(), userid, msg);
                } else { // if message box is empty
                    Toast.makeText(MessageActivity.this, "You cannot send empty message!", Toast.LENGTH_SHORT).show();
                }
                // initialize send text for next
                send_text.setText("");
            }
        });

        // back button clicked
        btn_back = findViewById(R.id.back_button);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when back button is clicked -> move to previous page
                //startActivity(new Intent(MessageActivity.this, MainApp.class));
                finish();
            }
        });

    }

    // send message
    private void sendMessage(String sender, String receiver, String msg){
        // get the data reference from firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        // crete new hashmap for message
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", msg);

        // add message hashmap to the firebase under Chats
        reference.child("Chats").push().setValue(hashMap);
    }

    // read message
    private void readMessage(String userid, String friendid, String imgurl){  
        // get the data from firebase under Chats for reading messages
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear previous mchat array
                mchat.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    // if the receiver and sender of the data from firebase are equal, add chat to mchat array
                    if(chat.getReceiver().equals(userid) && chat.getSender().equals(friendid) ||
                            chat.getReceiver().equals(friendid) && chat.getSender().equals(userid)){
                        mchat.add(chat);
                    }

                    // attach adapter with mchat array and profile image
                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imgurl);
                    recyclerView.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
