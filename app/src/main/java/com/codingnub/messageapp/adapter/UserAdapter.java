package com.codingnub.messageapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codingnub.messageapp.MessageActivity;
import com.codingnub.messageapp.R;
import com.codingnub.messageapp.model.Chat;
import com.codingnub.messageapp.model.User;
import com.codingnub.messageapp.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> userList;

    private boolean isChatFrag;

    private String lstMsg;

    public UserAdapter(Context context, ArrayList<User> userList, boolean isChatFrag) {
        this.context = context;
        this.userList = userList;
        this.isChatFrag = isChatFrag;
    }

    public UserAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {

        User user = userList.get(position);

        holder.txtUsername.setText(user.getName());

        if (user.getImgUrl().equals("default")) {
            holder.imgProfile.setImageResource(R.drawable.img_placeholder_profile);
        } else {

            Glide.with(context).load(user.getImgUrl()).into(holder.imgProfile);

        }


        //status
        if (isChatFrag) {
            holder.imgStatus.setVisibility(View.VISIBLE);
            if (user.getStatus().equals(Constant.STATUS_OFF)) {
                holder.imgStatus.setImageResource(R.drawable.ic_offline);
            } else {
                holder.imgStatus.setImageResource(R.drawable.ic_online);
            }
        } else {
            holder.imgStatus.setVisibility(View.INVISIBLE);
        }

        //last message
        if (isChatFrag) {

            holder.txtLastMessage.setVisibility(View.VISIBLE);
            lastMessage(user.getUid(), holder.txtLastMessage);

        } else {
            holder.txtLastMessage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userId", user.getUid());
                context.startActivity(intent);

            }
        });

    }

    private void lastMessage(String uid, TextView txtLastMessage) {

        lstMsg = "No Message";

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Chat c = dataSnapshot.getValue(Chat.class);

                    if (c.getReceiver().equals(firebaseUser.getUid()) && c.getSender().equals(uid) ||
                            c.getSender().equals(firebaseUser.getUid()) && c.getReceiver().equals(uid)) {

                        lstMsg = c.getMessage();

                    }

                }
                txtLastMessage.setText(lstMsg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgProfile;
        ImageView imgStatus;
        TextView txtUsername;
        TextView txtLastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);

        }
    }

}
