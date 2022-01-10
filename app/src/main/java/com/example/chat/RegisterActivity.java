package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mFirebaseAuth; // for auth firebase
    private DatabaseReference mDatabaseRef; // realtime database
    private EditText mEtEmail, mEtPwd, mUsername;
    private Button mBtnRegister;
    private TextView SigninButton;



    private static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("chatApp");

        mEtEmail = findViewById(R.id.emailText);
        mEtPwd = findViewById(R.id.passwordText);
        mUsername = findViewById(R.id.username2);

        mBtnRegister = findViewById(R.id.registerButton);
        SigninButton = (TextView) findViewById(R.id.SigninBtn);

        // Press sign up button
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start register
                String strEmail = mEtEmail.getText().toString(); // get email in the email text
                String strPwd = mEtPwd.getText().toString(); // get password
                String strUsername = mUsername.getText().toString(); // get username

                if(TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPwd) || TextUtils.isEmpty(strUsername)){
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }


                // process firebase (when success)
                signup(strEmail, strPwd, strUsername);

            }
        });

        // back to log in page
        SigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        }); // back to log in page



    }

    public void signup(String strEmail, String strPwd, String strUsername){

        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                    String userID = firebaseUser.getUid();


                    // constructor for User
                    User user = new User();
                    user.setImageURL("default");
                    user.setUsername(strUsername);
                    user.setId(strEmail);
                    user.setEmailAddress(strEmail);
                    user.setLowercaseUsername(strUsername.toLowerCase());
                    user.setStatusMsg("default");


                    mDatabaseRef = FirebaseDatabase.getInstance().getReference("UserAccount").child(userID);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userID);
                    hashMap.put("emailAddress", strEmail);
                    hashMap.put("username", strUsername);
                    hashMap.put("imageURL", "default");
                    // for searching username regardless lower/capital
                    hashMap.put("lowercaseUsername", strUsername.toLowerCase());
                    hashMap.put("statusMsg", "default");
                    mDatabaseRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Register Successfully!", Toast.LENGTH_SHORT).show();

                                // back to log in page
                                new Handler().postDelayed(new Runnable() { // wait for 1 sec to back to login activity

                                    @Override
                                    public void run() {
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }, 1000 );//time in milisecond
                            }
                        }
                    });

                    // mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);





                } else if (strPwd.length() < 6) { // has length less than 6
                    Toast.makeText(RegisterActivity.this, "More than 6 characters for password is required", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(strEmail)) { // does not have a valid email address
                    Toast.makeText(RegisterActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e){ // if email is already taken
                        Toast.makeText(RegisterActivity.this, "Email address already exist", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}