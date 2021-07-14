package com.codingnub.messageapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingnub.messageapp.R;
import com.codingnub.messageapp.adapter.UserAdapter;
import com.codingnub.messageapp.model.Chat;
import com.codingnub.messageapp.model.ChatList;
import com.codingnub.messageapp.model.User;
import com.codingnub.messageapp.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter adapter;
    private ArrayList<ChatList> list;
    private ArrayList<User> userList;


    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference();


        //chat list data
        reference.child("ChatList")
                .child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot s : snapshot.getChildren()) {

                    ChatList chatList = s.getValue(ChatList.class);
                    list.add(chatList);

                }
                chatlist();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateToken();


        return view;
    }


    private void updateToken(){

        //getToken
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {

                if (task.isSuccessful()){

                    String s =  task.getResult();

                    if (firebaseUser != null){

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
                        Token token = new Token(s);
                        databaseReference.child(firebaseUser.getUid()).setValue(token);

                    }

                }

            }
        });

    }


    private void chatlist() {

        userList = new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference().child("User");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {

                    User user = s.getValue(User.class);

                    for (ChatList chatList : list) {

                        if (user.getUid().equals(chatList.getId())) {

                            userList.add(user);

                        }

                    }

                }

                adapter = new UserAdapter(getContext(), userList, true);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}