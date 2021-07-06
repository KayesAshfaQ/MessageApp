package com.codingnub.messageapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.codingnub.messageapp.adapter.ViewPagerAdapter;
import com.codingnub.messageapp.fragments.ChatsFragment;
import com.codingnub.messageapp.fragments.ProfileFragment;
import com.codingnub.messageapp.fragments.UsersFragment;
import com.codingnub.messageapp.model.User;
import com.codingnub.messageapp.util.Constant;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private final int PAGER_ADAPTER_BEHAVIOUR = 1;

    FirebaseUser firebaseUser;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setUserInfoInToolbar();


        //set viewPager & tabLayout
        String[] titles = {"Users", "Chats", "Profile"};
        Fragment[] fragments = {new UsersFragment(), new ChatsFragment(), new ProfileFragment()};

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),
                PAGER_ADAPTER_BEHAVIOUR, titles, fragments);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setUserInfoInToolbar() {

        reference = FirebaseDatabase.getInstance().getReference("User")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user != null) {

                    username.setText(user.getName());

                    if (user.getImgUrl().equals("default")) {
                        profile_image.setImageResource(R.drawable.img_placeholder_profile);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImgUrl()).into(profile_image);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {

            logOut();

        }

        return true;
    }

    private void logOut() {

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REMEMBER_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        startActivity(new Intent(MainActivity.this, StartActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        finish();

    }

    private void changeStatus(String status) {

        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

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