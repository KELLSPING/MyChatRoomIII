package com.example.mychatroomiii.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mychatroomiii.Model.Users;
import com.example.mychatroomiii.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {

    ShapeableImageView settings_image, save;
    EditText settings_name, settings_status;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImageUri;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        settings_image = findViewById(R.id.settings_image);
        settings_name = findViewById(R.id.settings_name);
        settings_status = findViewById(R.id.settings_status);
        save = findViewById(R.id.save);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                email = snapshot.child("email").getValue().toString();
                String image = snapshot.child("imageUri").getValue().toString();
                String status = snapshot.child("status").getValue().toString();

                settings_name.setText(name);
                settings_status.setText(status);
                Picasso.get().load(image).into(settings_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        settings_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = settings_name.getText().toString();
                String status = settings_status.getText().toString();

                if (selectedImageUri != null){
                    storageReference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();
                                    Users users = new Users(auth.getUid(), name, email, finalImageUri, status);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(SettingsActivity.this, "Data Upload Success", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                            } else {
                                                Toast.makeText(SettingsActivity.this, "Data Upload Fail", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri = uri.toString();
                            Users users = new Users(auth.getUid(), name, email, finalImageUri, status);

                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SettingsActivity.this, "Data Upload Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "Data Upload Fail", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10){
            if (data != null){
                selectedImageUri = data.getData();
                settings_image.setImageURI(selectedImageUri);
            }
        }
    }
}