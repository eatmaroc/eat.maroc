package com.example.eatma;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddPromo extends AppCompatActivity {
        EditText title, prix, whatsapp, description, adress,location;
        DatabaseReference databaseReference,mDatabase;
        Spinner spinnerVille, spinnerQuartier,spinnerTypeAddPromo;
        Button Ajouter;
        ImageButton addpic;
        StorageReference storageReference;

        ArrayList<String> cityList,quartierList;
        ArrayAdapter<String> typeList;
        ProgressBar progressBar;


        boolean isImageChosen = false; // Flag to indicate whether an image is chosen
        Uri chosenImageUri; // Store the chosen image URI
        Promo promo=new Promo();

        @SuppressLint({"MissingInflatedId", "WrongViewCast"})
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                EdgeToEdge.enable(this);
                setContentView(R.layout.activity_add_promo);
                progressBar = findViewById(R.id.progressBar);
                title = findViewById(R.id.titreInpute);
                spinnerTypeAddPromo = findViewById(R.id.spinnerTypeAddPromo);
                prix = findViewById(R.id.prixInput);
                whatsapp = findViewById(R.id.numeroInput);
                description = findViewById(R.id.descriptionInput);
                adress = findViewById(R.id.adressInput);
                spinnerVille = findViewById(R.id.villeAdd);
                spinnerQuartier = findViewById(R.id.quartierAdd);
                Ajouter = findViewById(R.id.btnAjouterProme);
                addpic = findViewById(R.id.imgInput);
                location=findViewById(R.id.inputLocation);
                mDatabase = FirebaseDatabase.getInstance().getReference().child("list_type");
                cityList=new ArrayList<>();
                quartierList=new ArrayList<>();


                typeList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
                typeList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTypeAddPromo.setAdapter(typeList);
                populateSpinner();


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
                } else {
                        Toast.makeText(this, "Erreur lors de la récupération du Spinner Ville", Toast.LENGTH_SHORT).show();
                }


                storageReference = FirebaseStorage.getInstance().getReference();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("villes");

                fetchVilles();





                addpic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                openFileChooser();
                        }
                });

                Ajouter.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                                if (isImageChosen) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        Ajouter.setEnabled(false);


                                        promo.setTitle(title.getText().toString());
                                        promo.setType(spinnerTypeAddPromo.getSelectedItem().toString());
                                        promo.setPrix(prix.getText().toString());
                                        promo.setWhatsapp(whatsapp.getText().toString());
                                        promo.setDescription(description.getText().toString());
                                        promo.setAdress(adress.getText().toString());
                                        promo.setVille(spinnerVille.getSelectedItem().toString());
                                        promo.setQuartier(spinnerQuartier.getSelectedItem().toString());
                                        promo.setLocation(location.getText().toString());
                                        uploadImage(chosenImageUri);

                                } else {
                                        Toast.makeText(AddPromo.this, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show();
                                }
                        }
                });
        }

        private void openFileChooser() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                        chosenImageUri = data.getData();
                        addpic.setImageURI(chosenImageUri); // Set ImageButton image to chosen image
                        isImageChosen = true; // Set flag to true indicating image is chosen
                }
        }
        private void uploadImage(Uri imageUri) {
                StorageReference fileReference = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {

//                                Toast.makeText(AddPromo.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                // You can get the URL of the uploaded image if needed
                                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        promo.setImage(uri.toString());
                                        addpromo(promo);

                                });
                        })
                        .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Ajouter.setEnabled(true);
                                Toast.makeText(AddPromo.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });}







        private void addpromo(Promo promo) {
                String selectedVille = promo.getVille();
                String selectedQuartier = promo.getQuartier();

                DatabaseReference villeRef = databaseReference.child(selectedVille).child("quartiers").child(selectedQuartier);

                String promoKey = String.format("Promo:%s",promo.getTitle());

                villeRef.child(promoKey).setValue(promo)
                        .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Ajouter.setEnabled(true);
                                // Promo added successfully
                                Toast.makeText(AddPromo.this, "Promo added successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddPromo.this,AddPromo.class);
                                startActivity(intent);
                                finish();
                        })
                        .addOnFailureListener(e -> {
                                // Failed to add promo
                                Toast.makeText(AddPromo.this, "Failed to add promo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AddPromo.this, "Failed to fetch villes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(AddPromo.this, "Failed to fetch quartiers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        public void populateSpinner() {
                // Add a ValueEventListener to retrieve data from Firebase
                mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Clear existing data in the spinner
                                typeList.clear();

                                // Iterate through the dataSnapshot to get each type
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        // Get the type value from the snapshot
                                        String type = snapshot.getValue(TypePromo.class).getType();

                                        // Add the type to the spinner adapter
                                        typeList.add(type);
                                }

                                // Notify the spinner adapter of changes
                                typeList.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle errors
                                Toast.makeText(AddPromo.this, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
                        }
                });
        }
}