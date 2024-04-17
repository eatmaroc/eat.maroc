package com.example.eatma;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AfficherPromo extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    Spinner spinnerVille, spinnerQuartier;
    ArrayList<String> cityList,quartierList;
    ArrayList<PromoCarte> listPromo;
    MainAdapter myAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_afficher_promo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView=findViewById(R.id.RVAffichier);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinnerVille = findViewById(R.id.villeAfficher);
        spinnerQuartier = findViewById(R.id.quartierAfficher);
        cityList=new ArrayList<>();
        quartierList=new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("villes");
        recyclerView.setHasFixedSize(true);
        listPromo=new ArrayList<>();
        myAdapter=new MainAdapter(this,listPromo);
        recyclerView.setAdapter(myAdapter);

        if (spinnerVille != null) {
            spinnerVille.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedVille = cityList.get(position);
                    fetchQuartiers(selectedVille);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Logique en cas de sélection vide
                }
            });
        }
        else {
            Toast.makeText(this, "Erreur lors de la récupération du Spinner Ville", Toast.LENGTH_SHORT).show();
        }

        fetchVilles();

        spinnerVille.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cityList != null && cityList.size() > position) {
                    String selectedVille = cityList.get(position);
                    fetchQuartiers(selectedVille);
                } else {
                    Toast.makeText(AfficherPromo.this, "Error: Invalid selection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle empty selection
            }
        });
        spinnerQuartier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                afficher(spinnerVille.getSelectedItem().toString(),spinnerQuartier.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });














    }

    private void afficher(String villeSelected,String quartierSelected) {
        // Clear the existing list of promotions
        listPromo.clear();

        // Get a reference to the promotions for the selected city and neighborhood
        DatabaseReference promotionsRef = databaseReference.child(villeSelected).child("quartiers").child(quartierSelected);

        // Attach a listener to fetch the promotions
        promotionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot promoSnapshot : snapshot.getChildren()) {
                    // Get the promotion details
                    String title = promoSnapshot.child("title").getValue(String.class);
                    String type = promoSnapshot.child("type").getValue(String.class);
                    String prix = promoSnapshot.child("prix").getValue(String.class);
                    String image = promoSnapshot.child("image").getValue(String.class);
                    String ville = promoSnapshot.child("ville").getValue(String.class);
                    String quartier = promoSnapshot.child("quartier").getValue(String.class);
                    String whatsapp = promoSnapshot.child("whatsapp").getValue(String.class);
                    String adress = promoSnapshot.child("adress").getValue(String.class);
                    String description = promoSnapshot.child("description").getValue(String.class);



                    // Create a PromoCarte object
                    PromoCarte promo = new PromoCarte(title,type,prix,image,whatsapp,description,adress,ville,quartier);

                    // Add the promotion to the list
                    listPromo.add(promo);
                }

                // Notify the adapter that the data set has changed
                if (!listPromo.isEmpty()) {
                    // Remove the last item in the ArrayList
                    listPromo.remove(listPromo.size() - 1);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AfficherPromo.this, "Failed to fetch promotions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchQuartiers(String selectedVille) {
        DatabaseReference villeRef = databaseReference.child(selectedVille).child("quartiers");
        villeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quartierList.clear();
                for (DataSnapshot quartierSnapshot : snapshot.getChildren()) {
                    String quartierName = quartierSnapshot.getKey();
                    quartierList.add(quartierName);
                }
                updateQuartierSpinnerAdapter(); // Rafraîchir l'adaptateur du Spinner Quartier
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AfficherPromo.this, "Failed to fetch quartiers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AfficherPromo.this, "Failed to fetch villes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVille.setAdapter(adapter);

        // Appel pour mettre à jour le spinner du quartier après avoir mis à jour le spinner de la ville
        updateQuartierSpinnerAdapter();
    }

    private void updateQuartierSpinnerAdapter() {
        ArrayAdapter<String> quartierAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quartierList);
        quartierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuartier.setAdapter(quartierAdapter);
    }
}