package com.example.eatma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class adminHome extends AppCompatActivity {
Button logout,addVille,addPromo,afficherPromo;
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

}
