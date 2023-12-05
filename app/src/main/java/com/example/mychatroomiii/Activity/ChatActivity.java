package com.example.mychatroomiii.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatroomiii.Adapter.MessagesAdapter;
import com.example.mychatroomiii.Model.Messages;
import com.example.mychatroomiii.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    String ReceiverUid, ReceiverName, ReceiverImage, SenderUid;
    ShapeableImageView profileImage;
    TextView tvReceiverName;
    FirebaseDatabase database;
    FirebaseAuth auth;
    public static String sImage;
    public static String rImage;
    MaterialCardView btnSend;
    EditText etMessage;
    String senderRoom, receiverRoom;
    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;
    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        ReceiverUid = getIntent().getStringExtra("receiverUid");
        ReceiverName = getIntent().getStringExtra("receiverName");
        ReceiverImage = getIntent().getStringExtra("receiverImage");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 顯示 menu 中的返回按鍵
        getSupportActionBar().setTitle(ReceiverName);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        messagesArrayList = new ArrayList<>();

        profileImage = findViewById(R.id.profile_image);
        tvReceiverName = findViewById(R.id.tvReceiverName);

        messageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); // 從底部開始堆疊
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatActivity.this, messagesArrayList);
        messageAdapter.setAdapter(adapter);

        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);

        Picasso.get().load(ReceiverImage).into(profileImage);
        tvReceiverName.setText(""+ReceiverName);

        SenderUid = auth.getUid();

        senderRoom = SenderUid + ReceiverUid;
        receiverRoom = ReceiverUid + SenderUid;

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();
                messageAdapter.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "", Toast.LENGTH_SHORT).show();
                    return;
                }
                etMessage.setText("");
                Date date = new Date();

                Messages messages = new Messages(message, SenderUid, date.getTime());

                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_chat_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}