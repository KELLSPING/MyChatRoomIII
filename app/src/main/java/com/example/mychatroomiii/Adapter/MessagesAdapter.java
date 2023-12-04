package com.example.mychatroomiii.Adapter;

import static com.example.mychatroomiii.Activity.ChatActivity.rImage;
import static com.example.mychatroomiii.Activity.ChatActivity.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatroomiii.Model.Messages;
import com.example.mychatroomiii.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sender_layout, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receiver_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesArrayList.get(position);

        if (holder.getClass() == SenderViewHolder.class){
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.txtMessage.setText(messages.getMessage());

            Picasso.get().load(sImage).into(senderViewHolder.shapeableImageView);
        } else {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.txtMessage.setText(messages.getMessage());

            Picasso.get().load(rImage).into(receiverViewHolder.shapeableImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView shapeableImageView;
        TextView txtMessage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            shapeableImageView = itemView.findViewById(R.id.profile_image);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView shapeableImageView;
        TextView txtMessage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            shapeableImageView = itemView.findViewById(R.id.profile_image);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }
}
