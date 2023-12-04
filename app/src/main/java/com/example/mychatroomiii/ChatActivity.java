package com.example.mychatroomiii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    String ReceiverUid, ReceiverName, ReceiverImage;
    ShapeableImageView profileImage;
    TextView tvReceiverName;
    FirebaseDatabase database;
    FirebaseAuth auth;
    public static String sImage;
    public static String rImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        ReceiverUid = getIntent().getStringExtra("receiverUid");
        ReceiverName = getIntent().getStringExtra("receiverName");
        ReceiverImage = getIntent().getStringExtra("receiverImage");

        profileImage = findViewById(R.id.profile_image);
        tvReceiverName = findViewById(R.id.tvReceiverName);

        Picasso.get().load(ReceiverImage).into(profileImage);
        tvReceiverName.setText(""+ReceiverName);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("imageUri").getValue().toString();
                rImage = ReceiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}