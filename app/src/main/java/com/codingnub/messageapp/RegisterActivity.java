package com.codingnub.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etxtEmail, eTxtUsername, eTxtPass;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etxtEmail = findViewById(R.id.email);
        eTxtUsername = findViewById(R.id.username);
        eTxtPass = findViewById(R.id.pass);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String email, username, pass;
               email = etxtEmail.getText().toString();
               username = eTxtUsername.getText().toString();
               pass = eTxtPass.getText().toString();

               if (email.isEmpty()){
                   etxtEmail.setError("this field can't be empty!");
                   etxtEmail.requestFocus();
               }else if (username.isEmpty()){
                   eTxtUsername.setError("this field can't be empty!");
                   eTxtUsername.requestFocus();
               }else if (pass.isEmpty() || pass.length()<6){
                   eTxtPass.setError("please enter proper password!");
                   eTxtPass.requestFocus();
               }else {

                   registerNewUser(email, username, pass);

               }

           }
       });

    }

    private void registerNewUser(String email, String username, String pass) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            
                            assert firebaseUser != null;
                            firebaseUser.sendEmailVerification();

                            final String uid = firebaseUser.getUid();

                            DatabaseReference reference = FirebaseDatabase.getInstance()
                                    .getReference("User")
                                    .child(uid);

                            HashMap<String, Object> map = new HashMap<>();

                            map.put("uid", uid);
                            map.put("name", username);
                            map.put("email", email);
                            map.put("imgUrl", "default");
                            map.put("status", "offline");
                            map.put("search", username.toLowerCase());

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Toasty.success(RegisterActivity.this, "User Registration Successfully Done!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();

                                    }else {
                                        Toasty.error(RegisterActivity.this, "User Registration failed!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                        }else {
                            Toasty.error(RegisterActivity.this, "User Registration failed!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}