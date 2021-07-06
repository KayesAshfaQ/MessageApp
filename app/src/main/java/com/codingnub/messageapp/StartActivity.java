package com.codingnub.messageapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    Button btnLogIn, btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        autoLogin();

        btnLogIn = findViewById(R.id.btnLogIn);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(StartActivity.this, RegisterActivity.class));

            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(StartActivity.this, LoginActivity.class));

            }
        });

    }

    private void autoLogin(){

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REMEMBER_PREF, MODE_PRIVATE);
        boolean b = preferences.getBoolean(LoginActivity.REMEMBER_PREF_BOOL, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (b && firebaseUser != null) {

            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
            );
            finish();

        }
    }

}