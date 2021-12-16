package com.example.chat.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private static final int RESULT_OK = 2;
    CircleImageView circleImageView;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    private static final int CHANGE_IMAGE = 1;
    private Uri imageuri;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_profile, container, false);

       circleImageView = view.findViewById(R.id.profile_image);
       username = view.findViewById(R.id.username);

       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(firebaseUser.getUid());

       storageReference = FirebaseStorage.getInstance().getReference("uploads");

       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               User user = snapshot.getValue(User.class);
               username.setText(user.getUsername());
               if(user.getImageURL().equals("default")){
                   circleImageView.setImageResource(R.drawable.ic_baseline_person_24);
               } else {
                   Glide.with(getContext()).load(user.getImageURL()).into(circleImageView);
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

       circleImageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               openImage();
           }
       });

       return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHANGE_IMAGE);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private  void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if(imageuri != null){
            final StorageReference storageReference2 = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
            uploadTask = storageReference2.putFile(imageuri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return storageReference2.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String sUri = downloadUri.toString();
                        databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", sUri);
                        databaseReference.updateChildren(hashMap);
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Required to select image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHANGE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "In progressing", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}