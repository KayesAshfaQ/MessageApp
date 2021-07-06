package com.codingnub.messageapp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codingnub.messageapp.R;
import com.codingnub.messageapp.adapter.UserAdapter;
import com.codingnub.messageapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersFragment extends Fragment {

    private EditText edtTxtSearch;
    private RecyclerView recyclerView;

    private ArrayList<User> userList;
    private UserAdapter adapter;
    private FirebaseUser fUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        edtTxtSearch = view.findViewById(R.id.edtTxtSearch);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        userList = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        readUser();

        edtTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void searchUser(String s) {


        Query query = FirebaseDatabase.getInstance().getReference("User").orderByChild("search")
                .startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {

                    User user = s.getValue(User.class);

                    if (fUser != null && !fUser.getUid().equals(user.getUid())) {

                        userList.add(user);

                    }

                }

                adapter = new UserAdapter(getContext(), userList, false);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void readUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {

                    User user = s.getValue(User.class);

                    if (fUser != null && !fUser.getUid().equals(user.getUid())) {

                        userList.add(user);

                    }

                }

                adapter = new UserAdapter(getContext(), userList, false);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static Fragment newInstance() {

        return new UsersFragment();

    }

}