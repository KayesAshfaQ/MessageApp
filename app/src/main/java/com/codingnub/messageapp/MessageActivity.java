package com.codingnub.messageapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codingnub.messageapp.adapter.MessageAdapter;
import com.codingnub.messageapp.model.Chat;
import com.codingnub.messageapp.model.User;
import com.codingnub.messageapp.notification.Data;
import com.codingnub.messageapp.notification.MyResponse;
import com.codingnub.messageapp.notification.Sender;
import com.codingnub.messageapp.notification.Token;
import com.codingnub.messageapp.remote.ApiClient;
import com.codingnub.messageapp.remote.ApiInterface;
import com.codingnub.messageapp.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageActivity extends AppCompatActivity {

    private ImageView profile_image;
    private TextView username;
    private RecyclerView recyclerView;
    private EditText edtTextSend;
    private ImageButton btnSend;

    private String receiver;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ArrayList<Chat> chats;
    private MessageAdapter adapter;

    private boolean notify = false;


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

        receiver = getIntent().getStringExtra("userId");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(receiver);

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

                notify = true;

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
        map.put("receiver", receiver);
        map.put("message", message);
        map.put("status", Constant.STATUS_DELIVERED);

        reference.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    DatabaseReference chatListRef = FirebaseDatabase.getInstance().getReference("ChatList");
                    chatListRef.child(firebaseUser.getUid()).child(receiver).child("id").setValue(receiver);
                    chatListRef.child(receiver).child(firebaseUser.getUid()).child("id").setValue(firebaseUser.getUid());

                }

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String currentUserName = snapshot.getValue(User.class).getName();

                sendNotification(receiver, message, currentUserName);
                notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void sendNotification(String receiver, String message, String username) {

        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokenRef.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), username+": "+ message, "New Message", receiver, R.mipmap.ic_launcher_round);
                    Sender sender = new Sender(data, token.getToken());

                    ApiInterface api = ApiClient.getRetrofit().create(ApiInterface.class);
                    api.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200){
                                assert response.body() != null;
                                if (response.body().success != 1){
                                    Toast.makeText(MessageActivity.this, "Notification send failed!", Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(MessageActivity.this, "notif. success", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMsg() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot s : snapshot.getChildren()) {

                    Chat c = s.getValue(Chat.class);

                    assert c != null;
                    if (c.getReceiver().equals(firebaseUser.getUid()) && c.getSender().equals(receiver) ||
                            c.getSender().equals(firebaseUser.getUid()) && c.getReceiver().equals(receiver)) {

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


    private void changeStatus(String status) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);

        reference.updateChildren(map);
    }

    @Override
    protected void onStart() {
        super.onStart();
        changeStatus(Constant.STATUS_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        changeStatus(Constant.STATUS_OFF);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        changeStatus(Constant.STATUS_OFF);
    }


}