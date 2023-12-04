package com.example.mychatroomiii;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    String ReceiverUid, ReceiverName, ReceiverImage;
    ShapeableImageView profileImage;
    TextView tvReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ReceiverUid = getIntent().getStringExtra("receiverUid");
        ReceiverName = getIntent().getStringExtra("receiverName");
        ReceiverImage = getIntent().getStringExtra("receiverImage");

        profileImage = findViewById(R.id.profile_image);
        tvReceiverName = findViewById(R.id.tvReceiverName);

        Picasso.get().load(ReceiverImage).into(profileImage);
        tvReceiverName.setText(""+ReceiverName);
    }
}