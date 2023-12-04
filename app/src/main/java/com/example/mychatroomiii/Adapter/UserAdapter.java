package com.example.mychatroomiii.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatroomiii.Activity.ChatActivity;
import com.example.mychatroomiii.Activity.HomeActivity;
import com.example.mychatroomiii.R;
import com.example.mychatroomiii.Model.Users;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context homeActivity;
    ArrayList<Users> usersArrayList;

    public UserAdapter(HomeActivity homeActivity, ArrayList<Users> usersArrayList) {
        this.homeActivity = homeActivity;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.item_user_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);

        holder.user_name.setText(users.getName());
        holder.user_status.setText(users.getStatus());

        Picasso.get().load(users.getImageUri()).into(holder.user_profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity, ChatActivity.class);
                intent.putExtra("receiverUid", users.getUid());
                intent.putExtra("receiverName", users.getName());
                intent.putExtra("receiverImage", users.getImageUri());
                homeActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView user_profile;
        TextView user_name, user_status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.tvUserName);
            user_status = itemView.findViewById(R.id.tvUserStatus);
        }
    }
}
