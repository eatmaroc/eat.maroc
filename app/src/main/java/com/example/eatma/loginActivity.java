package com.example.eatma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    EditText email,password;
    Button loginBtn;
    FirebaseAuth firebaseAuth;

    ProgressBar progressBarLogIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    email=findViewById(R.id.email);
    password=findViewById(R.id.password);
    loginBtn=findViewById(R.id.loginBtn);
    progressBarLogIn = findViewById(R.id.progressBarLogIn);
    firebaseAuth=FirebaseAuth.getInstance();


    loginBtn.setOnClickListener(v->{
        loginBtn.setEnabled(false);
        progressBarLogIn.setVisibility(View.VISIBLE);
        logIn();
    });
    }


    void logIn(){
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        if (userEmail.isEmpty()) {
            email.setError("Email est vide");
            email.requestFocus();
            return;
        }

        if (userPassword.isEmpty()) {
            password.setError("Password est vide");
            password.requestFocus();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginBtn.setEnabled(true);
                        progressBarLogIn.setVisibility(View.GONE);
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Intent intent=new Intent(this,adminHome.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        loginBtn.setEnabled(true);
                        progressBarLogIn.setVisibility(View.GONE);
                        Toast.makeText(loginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            startActivity(new Intent(this, adminHome.class));
        }
    }
}