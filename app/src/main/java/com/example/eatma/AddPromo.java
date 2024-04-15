package com.example.eatma;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddPromo extends AppCompatActivity {
    EditText title,type,prix,whatsapp,description,adress;

    Spinner spinnerVille,spinnerQuartier;

    Button Ajouter;
    String imgUrl;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_promo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title=findViewById(R.id.titreInpute);
        type=findViewById(R.id.typeInput);
        prix=findViewById(R.id.prixInput);
        whatsapp=findViewById(R.id.numeroInput);
        description=findViewById(R.id.descriptionInput);
        adress=findViewById(R.id.adressInput);
        spinnerVille=findViewById(R.id.spinner);
        spinnerQuartier=findViewById(R.id.spinner2);
        Ajouter=findViewById(R.id.btnAjouterPromo);

    }
}