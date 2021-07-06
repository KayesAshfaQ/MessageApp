package com.codingnub.messageapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {

    private TextInputEditText eTxtEmail;
    private Button btnReset;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        eTxtEmail = findViewById(R.id.eTxtEmail);
        btnReset = findViewById(R.id.btnReset);
        text = findViewById(R.id.text);
        text.setVisibility(View.VISIBLE);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = eTxtEmail.getText().toString();

                if (email.isEmpty() || !email.contains("@")) {

                    eTxtEmail.setError("Error!");
                    eTxtEmail.requestFocus();

                } else {

                    resetPassword(email);

                }

            }
        });

    }

    private void resetPassword(String email) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    text.setText("Check your mail for change password.");
                } else {
                    text.setText("try again");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                text.setText(e.getMessage() + "\n" + e.getCause());
            }
        });

    }
}