package com.example.eatma;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddPromo extends AppCompatActivity {
    EditText title, type, prix, whatsapp, description, adress;
    DatabaseReference databaseReference;
    Spinner spinnerVille, spinnerQuartier;
            Button Ajouter;
            ImageButton addpic;
            Uri imgUri;
            String imgUrl ="";
            StorageReference storageReference;

            boolean isImageChosen = false; // Flag to indicate whether an image is chosen
            Uri chosenImageUri; // Store the chosen image URI

@SuppressLint({"MissingInflatedId", "WrongViewCast"})
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

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("villes");
        title = findViewById(R.id.titreInpute);
        type = findViewById(R.id.typeInput);
        prix = findViewById(R.id.prixInput);
        whatsapp = findViewById(R.id.numeroInput);
        description = findViewById(R.id.descriptionInput);
        adress = findViewById(R.id.adressInput);
        spinnerVille = findViewById(R.id.spinner);
        spinnerQuartier = findViewById(R.id.spinner2);
        Ajouter = findViewById(R.id.btnAjouterProme);
        addpic = findViewById(R.id.imgInput);



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
        uploadImage(chosenImageUri);
                Promo promo=new Promo();
                promo.setTitle("pizza");
                promo.setType("type");
                promo.setPrix("prix");
                promo.setWhatsapp("numero");
                promo.setDescription("description");
                promo.setAdress("adresse");
                promo.setVille("Marrakech");
                promo.setQuartier("Mhamid");
                promo.setImage(imgUrl);
                addpromo(promo);
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
        Toast.makeText(AddPromo.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
        // You can get the URL of the uploaded image if needed
        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
        imgUrl = uri.toString();

        });
        })
        .addOnFailureListener(e -> {
        Toast.makeText(AddPromo.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });}


private void addpromo(Promo promo) {
                String selectedVille = promo.getVille();
                String selectedQuartier = promo.getQuartier();

                DatabaseReference villeRef = databaseReference.child(selectedVille).child("quartiers").child(selectedQuartier);

                String promoKey = String.format("Promo:%s",promo.getTitle());

                villeRef.child(promoKey).setValue(promo)
                        .addOnSuccessListener(aVoid -> {
                                // Promo added successfully
                                Toast.makeText(AddPromo.this, "Promo added successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                                // Failed to add promo
                                Toast.makeText(AddPromo.this, "Failed to add promo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
        }


}

