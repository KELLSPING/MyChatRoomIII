package com.example.mychatroomiii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView tvSignUp;
    EditText etLoginEmail, etLoginPassword;
    Button btnSignIn;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Boolean validEmail = false;
    Boolean validPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createComponents();

        auth = FirebaseAuth.getInstance();

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etLoginEmail.getText().toString();
                String password = etLoginPassword.getText().toString();

                checkEmailAndPassword(email, password);

                if (validEmail && validPassword){
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkEmailAndPassword(String email, String password) {
        if (TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Empty Email", Toast.LENGTH_SHORT).show();
            validEmail = false;
        } else if (!email.matches(emailPattern)){
            etLoginEmail.setError("Invalid Email");
            Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
            etLoginEmail.setText("");
            validEmail = false;
        } else {
            validEmail = true;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Empty Password", Toast.LENGTH_SHORT).show();
            validPassword = false;
        } else if (password.length() < 6) {
//            etLoginPassword.setError("At least 6 characters.");
//            Toast.makeText(LoginActivity.this, "At least 6 characters.", Toast.LENGTH_SHORT).show();
            etLoginPassword.setText("");
            validPassword = false;
        } else {
            validPassword = true;
        }
    }

    private void createComponents() {
        tvSignUp = findViewById(R.id.tvSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
    }
}