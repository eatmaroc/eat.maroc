package com.example.eatma;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddVilleAndCartier extends AppCompatActivity {
    DatabaseReference databaseReference;
    Button btnOk;
    ImageButton btnBack;
    EditText villeText, quartierText;
    RadioButton villeRadio, quartierRadio;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ville_and_cartier);

        btnBack = findViewById(R.id.btnBackAddVille);
        btnOk = findViewById(R.id.btnOkAddVille);
        villeText = findViewById(R.id.villeAddVille);
        quartierText = findViewById(R.id.quartierAddVille);
        villeRadio = findViewById(R.id.radioVille);
        quartierRadio = findViewById(R.id.radioQuartier);
        villeRadio.setChecked(true);

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("villes");
        btnBack.setOnClickListener(v -> finish());

        btnOk.setOnClickListener(v -> {
            if (villeRadio.isChecked()) {
                addVille();
            } else if (quartierRadio.isChecked()) {
                addQuartier();
            }
        });
    }

    private void addVille() {
        String villeName = villeText.getText().toString();
        if (villeName.isEmpty()) {
            villeText.setError("Ville vide!!!");
            return;
        }
        Ville ville=new Ville(villeName);
        databaseReference.push().setValue(ville);
        Toast.makeText(AddVilleAndCartier.this, "Ville added successfully!", Toast.LENGTH_SHORT).show();
        villeText.setText("");
    }


    private void addQuartier() {
        String quartierName = quartierText.getText().toString().trim();
        if (quartierName.isEmpty()) {
            quartierText.setError("Quartier vide!!!");

        }
    }
}
