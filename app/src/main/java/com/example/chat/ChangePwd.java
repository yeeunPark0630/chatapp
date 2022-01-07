package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePwd extends AppCompatActivity {
    EditText currentPwd, newPwd, confirmPwd;
    Button cancelBtn, confirmBtn;
    String curPwd, newPwdString, confirmPwdString;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Passwords
        currentPwd = findViewById(R.id.currentPwd);
        newPwd = findViewById(R.id.newPwd1);
        confirmPwd = findViewById(R.id.newPwd2);


        // Buttons
        cancelBtn = findViewById(R.id.cancel_button2);
        confirmBtn = findViewById(R.id.ok_button);

        String newPwd2 = confirmPwd.toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String emailAddress = user.getEmail();

        currentPwd.addTextChangedListener(new TextWatcher() { // update text
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                curPwd = currentPwd.getText().toString();
            }
        });
        newPwd.addTextChangedListener(new TextWatcher() { // update text
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                newPwdString =  newPwd.getText().toString();
            }
        });
        confirmPwd.addTextChangedListener(new TextWatcher() { // update text
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmPwdString = confirmPwd.getText().toString();
            }
        });




        // Prompt the user to re-provide their sign-in credentials
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // all fields required
                if(confirmPwdString == null || newPwdString == null || curPwd == null){
                    Toast.makeText(ChangePwd.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (!newPwdString.equals(confirmPwdString)) { // if new password and confirm password does not match, cannot update
                    Toast.makeText(ChangePwd.this, "Confirm password does not match.", Toast.LENGTH_SHORT).show();
                } else if (confirmPwdString.length() < 6){
                    Toast.makeText(ChangePwd.this, "Password should longer than 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(emailAddress, curPwd);
                    user.reauthenticate(credential) // firebase check the email and address
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(newPwd2).addOnCompleteListener(new OnCompleteListener<Void>() { // update the password
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Success", "Password updated");
                                                    Toast.makeText(ChangePwd.this, "Password Update Successful!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ChangePwd.this, MainApp.class));
                                                } else {
                                                    Toast.makeText(ChangePwd.this, "Password Update Fail! Try one more time.", Toast.LENGTH_SHORT).show();
                                                    Log.d("Fail", "Error password not updated");
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ChangePwd.this, "Current password does not match.", Toast.LENGTH_SHORT).show();
                                        Log.d("Error", "Error auth failed");
                                    }
                                }
                            });
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePwd.this, MainApp.class));
            }
        });

    }
}
