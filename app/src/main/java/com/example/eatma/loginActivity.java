package com.example.eatma;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    EditText email,password;
    Button loginBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    email=findViewById(R.id.email);
    password=findViewById(R.id.password);
    loginBtn=findViewById(R.id.loginBtn);
    firebaseAuth=FirebaseAuth.getInstance();
    emailFocusListener();

    loginBtn.setOnClickListener( v->logIn());
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
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Intent intent=new Intent(this,AfficherPromo.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
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
            return "Invalid Email Address";
        }
        return null;
    }
}