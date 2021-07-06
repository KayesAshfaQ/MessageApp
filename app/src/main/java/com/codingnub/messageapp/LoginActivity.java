package com.codingnub.messageapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    public static String REMEMBER_PREF = "remember_me_pref";
    public static String REMEMBER_PREF_BOOL = "remember_me_pref_bool";
    private TextInputEditText editTextEmail, editTextPass;
    private TextView txtForget;
    private CheckBox checkBox;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.email);
        editTextPass = findViewById(R.id.pass);
        btnLogin = findViewById(R.id.btnLogIn);
        checkBox = findViewById(R.id.checkbox);
        txtForget = findViewById(R.id.txtForget);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString();
                String pass = editTextPass.getText().toString();


                if (checkBox.isChecked()) {

                    SharedPreferences preferences = getSharedPreferences(REMEMBER_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    Log.d("CHECK", checkBox.isChecked() + "");

                    editor.putBoolean(REMEMBER_PREF_BOOL, true);
                    editor.apply();

                }

                if (email.isEmpty()) {
                    editTextEmail.setError("this field can't be empty!");
                    editTextPass.requestFocus();
                } else if (pass.isEmpty() || pass.length() < 6) {
                    editTextPass.setError("password can't be empty!");
                    editTextPass.requestFocus();
                } else {

                    loginUser(email, pass);

                }

            }
        });

        txtForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, ResetPassActivity.class));

            }
        });

    }

    private void loginUser(String email, String pass) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            if (firebaseUser != null && firebaseUser.isEmailVerified()) {

                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                                LoginActivity.this.finish();

                            } else {
                                Toasty.error(LoginActivity.this, "Please, verify tour mail.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toasty.error(LoginActivity.this, "User Login failed!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}