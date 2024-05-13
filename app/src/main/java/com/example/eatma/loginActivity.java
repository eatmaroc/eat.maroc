package com.example.eatma;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
    ProgressBar progressBarLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        loginBtn=findViewById(R.id.loginBtn);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        firebaseAuth=FirebaseAuth.getInstance();
        emailFocusListener();


        loginBtn.setOnClickListener(v->{
            progressBarLogin.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);
            logIn();
        });
    }


    void logIn(){
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        if (userEmail.isEmpty()) {
            email.setError("Email est vide");
            email.requestFocus();
            progressBarLogin.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
            return;
        }

        if (userPassword.isEmpty()) {
            password.setError("Password est vide");
            password.requestFocus();
            progressBarLogin.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBarLogin.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Intent intent=new Intent(this,AfficherPromo.class);
                        startActivity(intent);
                        finish();
                    } else {
                        loginBtn.setEnabled(true);
                        progressBarLogin.setVisibility(View.GONE);

                        // If sign in fails, display a message to the user.
                        Toast.makeText(loginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            startActivity(new Intent(this, AfficherPromo.class));
        }
    }

    private void emailFocusListener() {
        email.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                String emailError = validEmail();
                if (emailError != null) {
                    email.setError(emailError);
                }
            }
        });
    }


    private String validEmail() {
        String emailText = email.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            progressBarLogin.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
            return "Invalid Email Address";
        }
        return null;
    }

}