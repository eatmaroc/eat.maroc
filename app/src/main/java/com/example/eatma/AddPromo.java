package com.example.eatma;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPromo extends AppCompatActivity {
    DatabaseReference databaseReference;

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
        databaseReference = FirebaseDatabase.getInstance().getReference().child("villes");
    }

    private void addpromo(Promo promo) {
        String selectedVille = promo.getVille(); // Hardcoded for testing, you should get this dynamically
        String selectedQuartier = promo.getQuartier(); // Hardcoded for testing, you should get this dynamically
        DatabaseReference villeRef = databaseReference.child(selectedVille).child("quartiers").child(selectedQuartier);
        String promoKey =String.format("Promo:%s",promo.getTitle());
        villeRef.child(promoKey).setValue(promo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddPromo.this, "Promo added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddPromo.this, "Failed to add promo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}