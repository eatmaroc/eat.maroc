package com.example.eatma;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddVilleAndCartier extends AppCompatActivity {
    DatabaseReference databaseReference;
    Button btnOk ,suprimer;
    ImageButton btnBack;
    EditText villeText, quartierText,editville,editquartier;
    RadioButton villeRadio, quartierRadio,suprimerville,suprimerquartier;
    ArrayList<String> cityList;
    Spinner spinner;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ville_and_cartier);

        btnBack = findViewById(R.id.btnBackAddVille);
        btnOk = findViewById(R.id.btnOkAddVille);
        villeText = findViewById(R.id.villeAddVille);
        quartierText = findViewById(R.id.quartierAddVille);
        editville=findViewById(R.id.editville);
        editquartier=findViewById(R.id.editquartier);
        villeRadio = findViewById(R.id.radioVille);
        quartierRadio = findViewById(R.id.radioQuartier);
        suprimerville= findViewById(R.id.supressville);
        suprimerquartier = findViewById(R.id.supressquartier);
        spinner=findViewById(R.id.villeSpinnerAddVille);
        suprimer=findViewById(R.id.btnSuprimer);
        villeRadio.setChecked(true);
        cityList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("villes");
        fetchVilles();
        btnBack.setOnClickListener(v -> finish());
        suprimer.setOnClickListener(v -> {
            supprimer();
        });

        btnOk.setOnClickListener(v -> {
            if (villeRadio.isChecked()) {
                addVille();
            } else if (quartierRadio.isChecked()) {
                addQuartier();
            }
        });
    }
    private void fetchVilles() {
      databaseReference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              cityList.clear();
              for (DataSnapshot snap : snapshot.getChildren()) {
                  String villeName = snap.getKey();
                  cityList.add(villeName);
              }
              updateSpinnerAdapter();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
              Toast.makeText(AddVilleAndCartier.this, "Failed to fetch villes: " + error.getMessage(), Toast.LENGTH_SHORT).show();


          }
      });
    }

    private void updateSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void addVille() {
        String villeName = villeText.getText().toString();
        if (villeName.isEmpty()) {
            villeText.setError("Ville vide!!!");
            return;
        }
        DatabaseReference villeRef = databaseReference.child(villeName);
        Ville ville = new Ville(villeName);
        villeRef.setValue(ville);

        Toast.makeText(AddVilleAndCartier.this, "Ville added successfully!", Toast.LENGTH_SHORT).show();
        villeText.setText("");
    }

    private void supprimer() {
        String villeToDelete = editville.getText().toString();
        if (!villeToDelete.isEmpty()) {
            deleteVille(villeToDelete);
        } else {
            Toast.makeText(AddVilleAndCartier.this, "Entrez un nom de ville à supprimer", Toast.LENGTH_SHORT).show();
        }
    }

    // Méthode pour supprimer une ville de Firebase
    private void deleteVille(String villeToDelete) {
        DatabaseReference villeRef = databaseReference.child(villeToDelete);
        villeRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddVilleAndCartier.this, "Ville supprimée avec succès!", Toast.LENGTH_SHORT).show();
                    editville.setText(""); // Efface le texte après la suppression
                })
                .addOnFailureListener(e -> Toast.makeText(AddVilleAndCartier.this, "Échec de la suppression de la ville: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }




    private void addQuartier() {
        String selectedVille = spinner.getSelectedItem().toString();
        String quartierName = quartierText.getText().toString().trim();

        if (quartierName.isEmpty()) {
            quartierText.setError("Quartier vide!!!");
            return;
        }

        DatabaseReference villeRef = databaseReference.child(selectedVille);
        Cartier cartier = new Cartier(quartierName);
        DatabaseReference quartierRef = villeRef.child("quartiers").push();
        quartierRef.setValue(cartier);

        Toast.makeText(AddVilleAndCartier.this, "Quartier added successfully!", Toast.LENGTH_SHORT).show();
        quartierText.setText("");
    }


}
