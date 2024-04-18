package com.example.eatma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class adminHome extends AppCompatActivity {
Button logout,addVille,addPromo,afficherPromo;
FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        addVille = findViewById(R.id.btnAjouterVille);
        addPromo=findViewById(R.id.btnAjouterPromo);
        afficherPromo=findViewById(R.id.btnAfficherPromo);
        logout=findViewById(R.id.logout);
        setButtonClickListener(addVille, AddVilleAndCartier.class);
        setButtonClickListener(addPromo, AddPromo.class);
        setButtonClickListener(afficherPromo, AfficherPromo.class);
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (auth.getCurrentUser()==null){
                    Intent intent = new Intent(adminHome.this, loginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.signOut();
            }
        });
        
    }
    private void setButtonClickListener(Button button, final Class<?> targetActivity) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adminHome.this, targetActivity);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    }
