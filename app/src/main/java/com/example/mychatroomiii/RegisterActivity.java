package com.example.mychatroomiii;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    TextView tvSignIn;
    ShapeableImageView profile_image;
    EditText etRegName, etRegEmail, etRegPass, etRegConfPass;
    Button btnSignUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    String imageURI;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createComponents();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        builder = new AlertDialog.Builder(this);
        builder.setMessage("Please wait...");
        builder.setCancelable(false);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = builder.create();
                progressDialog.show();

                String name = etRegName.getText().toString();
                String email = etRegEmail.getText().toString();
                String password = etRegPass.getText().toString();
                String cPassword = etRegConfPass.getText().toString();
                String status = "Aa";

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || 
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)){
                    Toast.makeText(RegisterActivity.this,
                            "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (!email.matches(emailPattern)) {
                    etRegEmail.setError("Invalid Email");
                    Toast.makeText(RegisterActivity.this,
                            "Invalid Email", Toast.LENGTH_SHORT).show();
                    setStringEmpty();
                    progressDialog.dismiss();
                } else if (!password.equals(cPassword)) {
                    Toast.makeText(RegisterActivity.this,
                            "Password not match", Toast.LENGTH_SHORT).show();
                    setStringEmpty();
                    progressDialog.dismiss();
                } else if (password.length() < 6) {
                    etRegPass.setError("At least 6 characters.");
                    Toast.makeText(RegisterActivity.this,
                            "At least 6 characters.", Toast.LENGTH_SHORT).show();
                    setStringEmpty();
                    progressDialog.dismiss();
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();

                                DatabaseReference databaseRef = database.getReference()
                                        .child("user").child(auth.getUid());
                                StorageReference storageRef = storage.getReference()
                                        .child("upload").child(auth.getUid());

                                if (imageUri != null){
                                    storageRef.putFile(imageUri)
                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()){
                                                storageRef.getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        Users users = new Users(auth.getUid(), name, email, imageURI, status);
                                                        databaseRef.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                                                } else {
                                                                    Toast.makeText(RegisterActivity.this,
                                                                            "Create User Failed", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    String status = "Aa";
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/mychatroomiii.appspot.com/o/person_image.xml?alt=media&token=c7a5d1d2-f7d7-46f6-bf64-1321a9f92c2f";
                                    Users users = new Users(auth.getUid(), name, email, imageURI, status);
                                    databaseRef.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                            } else {
                                                Toast.makeText(RegisterActivity.this,
                                                        "Create User Failed", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                            } else {
                                progressDialog.dismiss();

                                Toast.makeText(RegisterActivity.this, "Something wrong.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });
    }

    private void setStringEmpty() {
        etRegName.setText("");
        etRegEmail.setText("");
        etRegPass.setText("");
        etRegConfPass.setText("");
    }

    private void createComponents() {
        tvSignIn = findViewById(R.id.tvSignIn);
        profile_image = findViewById(R.id.profile_image);
        etRegName = findViewById(R.id.etRegName);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPass = findViewById(R.id.etRegPass);
        etRegConfPass = findViewById(R.id.etRegConfPass);
        btnSignUp = findViewById(R.id.btnSignUp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10){
            if (data != null){
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}