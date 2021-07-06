package com.codingnub.messageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codingnub.messageapp.adapter.MessageAdapter;
import com.codingnub.messageapp.model.Chat;
import com.codingnub.messageapp.model.User;
import com.codingnub.messageapp.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    private ImageView profile_image;
    private TextView username;
    private RecyclerView recyclerView;
    private EditText edtTextSend;
    private ImageButton btnSend;

    private String userId;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ArrayList<Chat> chats;
    private MessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MessageActivity.this, MainActivity.class));

            }
        });

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        edtTextSend = findViewById(R.id.edtTextSend);
        btnSend = findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        chats = new ArrayList<>();

        userId = getIntent().getStringExtra("userId");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                username.setText(user.getName());

                if (user.getImgUrl().equals("default")) {

                    profile_image.setImageResource(R.drawable.img_placeholder_profile);

                } else {

                    Glide.with(getApplicationContext()).load(user.getImgUrl()).into(profile_image);

                }

                readMsg();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = edtTextSend.getText().toString();

                if (!message.isEmpty()) {

                    sendMsg(message);
                    edtTextSend.setText("");

                }

            }
        });

    }

    private void sendMsg(String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");

        HashMap<String, Object> map = new HashMap<>();

        map.put("sender", firebaseUser.getUid());
        map.put("receiver", userId);
        map.put("message", message);
        map.put("status", Constant.STATUS_DELIVERED);

        reference.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    DatabaseReference chatListRef = FirebaseDatabase.getInstance().getReference("ChatList");
                    chatListRef.child(firebaseUser.getUid()).child(userId).child("id").setValue(userId);
                    chatListRef.child(userId).child(firebaseUser.getUid()).child("id").setValue(firebaseUser.getUid());

                }

            }
        });


    }

    private void readMsg() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot s : snapshot.getChildren()){

                    Chat c = s.getValue(Chat.class);

                    if (c.getReceiver().equals(firebaseUser.getUid()) && c.getSender().equals(userId) ||
                            c.getSender().equals(firebaseUser.getUid()) && c.getReceiver().equals(userId)){

                        chats.add(c);

                    }

                }

                adapter = new MessageAdapter(MessageActivity.this, chats, "default");
                recyclerView.setAdapter(adapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}