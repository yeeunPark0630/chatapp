package com.example.chat.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.example.chat.chatApp;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    CircleImageView circleImageView;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    private static final int CHANGE_IMAGE = 1;
    private Uri imageuri;
    private StorageTask uploadTask;
    ActivityResultLauncher<Intent> activityResultLauncher, cropActivityResultLauncher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_profile, container, false);

       circleImageView = view.findViewById(R.id.profile_image);
       username = view.findViewById(R.id.username);

       storageReference = FirebaseStorage.getInstance().getReference("uploads");
       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       databaseReference = FirebaseDatabase.getInstance().getReference("UserAccount").child(firebaseUser.getUid());



       circleImageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) { openImage(); }
       });

       activityResultLauncher = registerForActivityResult
                (new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                Intent data = result.getData();
                                if(result.getResultCode() == RESULT_OK && data != null && data.getData() != null ){
                                    imageuri = data.getData();
                                    if(uploadTask != null && uploadTask.isInProgress()){
                                        Toast.makeText(getContext(), "In progressing", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).start(getActivity());
                                        Intent intent = CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                                                .getIntent(getContext());
                                        cropActivityResultLauncher.launch(intent);

                                    }
                                }
                            }
                        });
       cropActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
           @Override
           public void onActivityResult(ActivityResult result) {
               Intent data = result.getData();
               if( result.getResultCode() == RESULT_OK){
                    CropImage.ActivityResult cropresult = CropImage.getActivityResult(data);
                    imageuri = cropresult.getUri();
                    uploadImage();
               }
           }
       });

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



       return view;
    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
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
        // upload to firebase
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
                public void onComplete(@NonNull Task<Uri> task) { // if it successful
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String sUri = downloadUri.toString();
                        HashMap<String, Object> hashMap = new HashMap<>(); // upload to firebase by using hashmap
                        hashMap.put("imageURL", sUri);
                        databaseReference.updateChildren(hashMap);
                        progressDialog.dismiss(); // after upload to firebase, dismiss the progress dialog
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


}