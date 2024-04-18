package com.example.eatma;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifierPromo extends AppCompatActivity {
    EditText title, prix, whatsapp, description, adress;
    DatabaseReference databaseReference,mDatabase;
    Spinner spinnerVille, spinnerQuartier,spinnerTypeModifierPromo;
    Button btnModifier;
    ImageButton addpic;
    StorageReference storageReference;
    ArrayList<String> cityList,quartierList;
    PromoCarte promo;
    String imageUrl;
    boolean isImageChosen=false;
    Uri chosenImageUri;
    ProgressBar progressBar;
    ArrayAdapter<String> typeList;
    Menu frame1;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modifier_promo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar = findViewById(R.id.progressBarModifier);
        title = findViewById(R.id.ModifierTitreInpute);
        spinnerTypeModifierPromo = findViewById(R.id.spinnerTypeModifierPromo);
        prix = findViewById(R.id.modifierPrixInput);
        whatsapp = findViewById(R.id.modifierNumeroInput);
        description = findViewById(R.id.modifierDescriptionInput);
        adress = findViewById(R.id.modifierAdressInput);
        spinnerVille = findViewById(R.id.villeModifier);
        spinnerQuartier = findViewById(R.id.quartierModifier);
        btnModifier = findViewById(R.id.btnModifierProme);
        addpic = findViewById(R.id.modifierImgInput);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("list_type");
        cityList=new ArrayList<>();
        quartierList=new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("villes");
        frame1 = new Menu();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentmodifierpromo, frame1).commit();

        typeList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        typeList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeModifierPromo.setAdapter(typeList);
        populateSpinner();

        fetchVilles();
        title.setEnabled(false);
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
        promo = (PromoCarte) getIntent().getSerializableExtra("promo");
        if (promo != null){
            title.setText(promo.getTitle());
            String typeSelected = promo.getType();
            int typeIndex = -1;
            for (int i = 0; i < typeList.getCount(); i++) {
                if (typeList.getItem(i).equals(typeSelected)) {
                    typeIndex = i;
                    break;
                }
            }
            spinnerTypeModifierPromo.setSelection(typeIndex);
            prix.setText(promo.getPrix());
            whatsapp.setText(promo.getWhatsapp());
            description.setText(promo.getDescription());
            adress.setText(promo.getAdress());
            int cityIndex = cityList.indexOf(promo.getVille());
            spinnerVille.setSelection(cityIndex);
            int quartierIndex = quartierList.indexOf(promo.getQuartier());
            spinnerQuartier.setSelection(quartierIndex);
            updateSpinnerAdapter();
            imageUrl=promo.getImage();
            Picasso.get().load(imageUrl).into(addpic);
        }else {
            Toast.makeText(this, "Error: Promo object is null", Toast.LENGTH_SHORT).show();
        }

        btnModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                btnModifier.setEnabled(true);
                // Get the updated values from EditText fields
                String updatedTitle = title.getText().toString().trim();
                String updatedType = spinnerTypeModifierPromo.getSelectedItem().toString();
                String updatedPrix = prix.getText().toString().trim();
                String updatedWhatsapp = whatsapp.getText().toString().trim();
                String updatedDescription = description.getText().toString().trim();
                String updatedAdress = adress.getText().toString().trim();
                String updatedVille = spinnerVille.getSelectedItem().toString().trim();
                String updatedQuartier = spinnerQuartier.getSelectedItem().toString().trim();

                // Get the reference to the promo node in the database
                DatabaseReference promoRef = FirebaseDatabase.getInstance().getReference()
                        .child("villes").child(updatedVille)
                        .child("quartiers").child(updatedQuartier);

                // Create a map to hold the updated promo data
                Map<String, Object> promoUpdates = new HashMap<>();
                promoUpdates.put("title", updatedTitle);
                promoUpdates.put("type", updatedType);
                promoUpdates.put("prix", updatedPrix);
                promoUpdates.put("whatsapp", updatedWhatsapp);
                promoUpdates.put("description", updatedDescription);
                promoUpdates.put("adress", updatedAdress);

                if (isImageChosen) {
                    // Upload the new image and update the promo with the new image URL
                    uploadImage(chosenImageUri, promoRef, updatedTitle, promoUpdates);
                } else {
                    // Update the promo with the existing image URL
                    promoUpdates.put("image", imageUrl);
                    // Update the promo in the database
                    updatePromo(promoRef, updatedTitle, promoUpdates);
                }

                // Update the promo in the database
                promoRef.child("Promo:" + updatedTitle).updateChildren(promoUpdates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Display a success message
                                Toast.makeText(ModifierPromo.this, "Promo modified successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Toast.makeText(ModifierPromo.this, "Failed to modify promo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
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


    private void uploadImage(Uri imageUri, DatabaseReference promoRef, String updatedTitle, Map<String, Object> promoUpdates) {
        // Here you can implement the logic to upload the image to Firebase Storage
        // For example:
        StorageReference fileReference = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(ModifierPromo.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    // You can get the URL of the uploaded image if needed
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the promo data with the new image URL
                        promoUpdates.put("image", uri.toString());
                        // Update the promo in the database
                        updatePromo(promoRef, updatedTitle, promoUpdates);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ModifierPromo.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnModifier.setEnabled(true);
                });
    }

    private void updatePromo(DatabaseReference promoRef, String updatedTitle, Map<String, Object> promoUpdates) {
        // Update the promo in the database
        promoRef.child("Promo:" + updatedTitle).updateChildren(promoUpdates)
                .addOnSuccessListener(aVoid -> {
                    // Display a success message
                    Toast.makeText(ModifierPromo.this, "Promo updated successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnModifier.setEnabled(true);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnModifier.setEnabled(true);
                    // Handle failure
                    Toast.makeText(ModifierPromo.this, "Failed to update promo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                // Set selection after cityList has been populated
                if (promo != null) {
                    int cityIndex = cityList.indexOf(promo.getVille());
                    if (cityIndex != -1) {
                        spinnerVille.setSelection(cityIndex);
                    } else {
                        // Handle the case where the city is not found in cityList
                        // You can set a default selection or display an error message
                        Toast.makeText(ModifierPromo.this, "Selected city not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ModifierPromo.this, "Failed to fetch villes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                updateQuartierSpinnerAdapter(); // Refresh the spinner after updating the list

                // Set selection after quartierList has been populated
                if (promo != null) {
                    int quartierIndex = quartierList.indexOf(promo.getQuartier());
                    if (quartierIndex != -1) {
                        spinnerQuartier.setSelection(quartierIndex);
                    } else {
                        // Handle the case where the quartier is not found in quartierList
                        // You can set a default selection or display an error message
                        Toast.makeText(ModifierPromo.this, "Selected quartier not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ModifierPromo.this, "Failed to fetch quartiers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

                // Set selection after typeList has been populated
                if (promo != null) {
                    int typeIndex = typeList.getPosition(promo.getType());
                    if (typeIndex != -1) {
                        spinnerTypeModifierPromo.setSelection(typeIndex);
                    } else {
                        // Handle the case where the type is not found in typeList
                        // You can set a default selection or display an error message
                        Toast.makeText(ModifierPromo.this, "Selected type not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(ModifierPromo.this, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

}