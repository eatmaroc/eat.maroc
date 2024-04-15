package com.example.eatma;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class AddVilleAndCartier extends AppCompatActivity {
    DatabaseReference databaseReference;
    Button btnOk, suprimer;
    ImageButton btnBack;
    EditText villeText, quartierText;
    RadioButton villeRadio, quartierRadio, suprimerville, suprimerquartier;
    ArrayList<String> cityList, quartierList;
    Spinner spinner, spinnerSuprimmeville, spinnerSuprimquertierVILL, spinnerSuprimquertierQuartier;
    ArrayAdapter<String> adapter;
    RadioGroup R1,R2;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ville_and_cartier);

        // Initialize views
//        btnBack = findViewById(R.id.btnBackAddVille);
        btnOk = findViewById(R.id.btnOkAddVille);
        villeText = findViewById(R.id.villeAddVille);
        quartierText = findViewById(R.id.quartierAddVille);
        villeRadio = findViewById(R.id.radioVille);
        quartierRadio = findViewById(R.id.radioQuartier);
        suprimerville = findViewById(R.id.supressville);
        suprimerquartier = findViewById(R.id.supressquartier);
        spinner = findViewById(R.id.villeSpinnerAddVille);
        spinnerSuprimmeville = findViewById(R.id.spinnerVilleDelet);
        spinnerSuprimquertierQuartier = findViewById(R.id.spinnerQuartierDelet);
        spinnerSuprimquertierVILL = findViewById(R.id.spinnervillequartierdelet);
        suprimer = findViewById(R.id.btnSuprimer);

        villeRadio.setChecked(true);
        cityList = new ArrayList<>();
        quartierList = new ArrayList<>();

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("villes");

        // Fetch and populate spinner with cities
        fetchVilles();
        // Button click listeners
        suprimerville.setChecked(true);


        btnBack.setOnClickListener(v -> finish());
        suprimer.setOnClickListener(v -> {
            if (suprimerville.isChecked()) {
                String villeToDelete = spinnerSuprimmeville.getSelectedItem().toString();
                deleteVille(villeToDelete);
            } else if (suprimerquartier.isChecked()) {
                // Delete quartier
                String villeToDelete = spinnerSuprimquertierVILL.getSelectedItem().toString();
                String quartierToDelete = spinnerSuprimquertierQuartier.getSelectedItem().toString();
                deleteQuartier(villeToDelete, quartierToDelete);
            }
        });


        btnOk.setOnClickListener(v -> {
            if (villeRadio.isChecked()) {
                addVille();
            } else if (quartierRadio.isChecked()) {
                addQuartier();
            }
        });


        // Radio button change listener
        suprimerville.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show spinner to select ville for deletion
                spinnerSuprimmeville.setVisibility(View.VISIBLE);
                spinnerSuprimquertierVILL.setVisibility(View.GONE);
                spinnerSuprimquertierQuartier.setVisibility(View.GONE);
            }
        });

        suprimerquartier.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show spinners to select ville and quartier for deletion
                spinnerSuprimmeville.setVisibility(View.GONE);
                spinnerSuprimquertierVILL.setVisibility(View.VISIBLE);
                spinnerSuprimquertierQuartier.setVisibility(View.VISIBLE);
                // Populate spinnerSuprimquertierVILL with cities
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSuprimquertierVILL.setAdapter(adapter);


            }
        });
        spinnerSuprimquertierVILL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchQuartier(spinnerSuprimquertierVILL.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    private void fetchQuartier(String ville) {
        DatabaseReference villeRef = databaseReference.child(ville).child("quartiers");
        villeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quartierList.clear();
                for (DataSnapshot quartierSnapshot : snapshot.getChildren()) {
                    String quartierName = quartierSnapshot.getKey();
                    quartierList.add(quartierName);
                }
                updateQuartierSpinnerAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddVilleAndCartier.this, "Failed to fetch quartiers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinnerSuprimmeville.setAdapter(adapter);
    }

    private void updateQuartierSpinnerAdapter() {
        ArrayAdapter<String> quartierAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quartierList);
        quartierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSuprimquertierQuartier.setAdapter(quartierAdapter);
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

    private void deleteVille(String villeToDelete) {
        DatabaseReference villeRef = databaseReference.child(villeToDelete);
        villeRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddVilleAndCartier.this, "Ville supprimée avec succès!", Toast.LENGTH_SHORT).show();
                    // Clear the selected item in the spinner after deletion
                    spinnerSuprimmeville.setSelection(0);
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
        // Removed push() to prevent generating random key
        Quartier cartier = new Quartier(quartierName);
        // Use the quartier name as the key
        DatabaseReference quartierRef = villeRef.child("quartiers").child(quartierName);
        quartierRef.setValue(cartier);

        Toast.makeText(AddVilleAndCartier.this, "Quartier added successfully!", Toast.LENGTH_SHORT).show();
        quartierText.setText("");
    }

    private void deleteQuartier(String villeToDelete, String quartierToDelete) {
        DatabaseReference quartierRef = databaseReference.child(villeToDelete).child("quartiers").child(quartierToDelete);
        quartierRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddVilleAndCartier.this, "Quartier supprimé avec succès!", Toast.LENGTH_SHORT).show();
                    // Clear the selected items in the spinners after deletion
                    spinnerSuprimquertierVILL.setSelection(0);
                    spinnerSuprimquertierQuartier.setSelection(0);
                })
                .addOnFailureListener(e -> Toast.makeText(AddVilleAndCartier.this, "Échec de la suppression du quartier: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
