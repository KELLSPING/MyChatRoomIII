package com.example.mychatroomiii;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    String receiverUid, receiverName, receiverImage;
    ShapeableImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverUid = getIntent().getStringExtra("receiverUid");
        receiverName = getIntent().getStringExtra("receiverName");
        receiverImage = getIntent().getStringExtra("receiverImage");

        profileImage = findViewById(R.id.profile_image);

        Picasso.get().load(receiverImage).into(profileImage);
    }
}