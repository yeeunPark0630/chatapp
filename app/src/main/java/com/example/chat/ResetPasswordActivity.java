package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText emailAddress;
    private Button reset_btn, back_login_btn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailAddress = findViewById(R.id.reset_email);
        reset_btn = findViewById(R.id.reset_btn);
        back_login_btn = findViewById(R.id.back_login_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        back_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
            }
        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAddress.getText().toString(); // get email address from edit text

                if(email.equals("")){ // if email empty, show the message
                    Toast.makeText(ResetPasswordActivity.this, "Email address is required for reset your password!", Toast.LENGTH_SHORT).show();
                } else {
                    // send password reset email
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){ // if successful, show the message
                                Toast.makeText(ResetPasswordActivity.this, "Please check your email for reset your password!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            } else {
                                String e = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
