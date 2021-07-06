package com.codingnub.messageapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codingnub.messageapp.R;
import com.codingnub.messageapp.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_LEFT = 0;
    private static final int MSG_RIGHT = 1;

    private Context context;
    private ArrayList<Chat> chats;
    private String imgUrl;

    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context, ArrayList<Chat> chats, String imgUrl) {
        this.context = context;
        this.chats = chats;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;

        if (viewType == MSG_RIGHT){

            v = LayoutInflater.from(context).inflate(R.layout.char_right_item, parent, false);

        }else {
            v = LayoutInflater.from(context).inflate(R.layout.chat_left_item, parent, false);
        }

        return new ViewHolder(v) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat c = chats.get(position);

        holder.message.setText(c.getMessage());

        if (imgUrl.equals("default")) {

            holder.profile_image.setImageResource(R.drawable.img_placeholder_profile);

        } else {

            Glide.with(context).load(imgUrl).into(holder.profile_image);

        }

        if (position == (chats.size()-1)){

            holder.messageStatus.setVisibility(View.VISIBLE);
            holder.messageStatus.setText(c.getStatus());

        }else {
            holder.messageStatus.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_RIGHT;
        }else {
            return MSG_LEFT;
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profile_image;
        TextView message;
        TextView messageStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            message = itemView.findViewById(R.id.message);
            messageStatus = itemView.findViewById(R.id.messageStatus);

        }
    }

}
