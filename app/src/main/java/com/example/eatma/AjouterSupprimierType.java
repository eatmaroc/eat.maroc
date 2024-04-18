package com.example.eatma;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AjouterSupprimierType extends AppCompatActivity {
    Button supprimierTypeBtn,ajouterTypeBtn;
    Spinner spinnerTypeAjouter;
    EditText inputAjouterType;
    RadioButton RBsupprimierType,RBajouterType;
    RadioGroup groupR,groupAjouterType,groupSupprimierType;
    DatabaseReference mDatabase;
    ArrayAdapter<String> spinnerAdapter;
    Menu frame1;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajouter_supprimier_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        supprimierTypeBtn=findViewById(R.id.supprimierTypeBtn);
        ajouterTypeBtn=findViewById(R.id.ajouterTypeBtn);
        spinnerTypeAjouter=findViewById(R.id.spinnerTypeAjouter);
        inputAjouterType=findViewById(R.id.inputAjouterType);
        RBsupprimierType=findViewById(R.id.RBsupprimierType);
        RBajouterType=findViewById(R.id.RBajouterType);
        groupR=findViewById(R.id.groupR);
        groupAjouterType=findViewById(R.id.groupAjouterType);
        groupSupprimierType=findViewById(R.id.groupSupprimierType);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("list_type");

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeAjouter.setAdapter(spinnerAdapter);
        populateSpinner();
        frame1 = new Menu();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentType, frame1).commit();


        RBajouterType.setChecked(true);
        groupSupprimierType.setVisibility(View.GONE);
        groupAjouterType.setVisibility(View.VISIBLE);
        groupR.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==RBajouterType.getId()){
                    groupSupprimierType.setVisibility(View.GONE);
                    groupAjouterType.setVisibility(View.VISIBLE);
                }else {
                    groupSupprimierType.setVisibility(View.VISIBLE);
                    groupAjouterType.setVisibility(View.GONE);
                }

            }
        });



        ajouterTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                // Get the type from the input field
                String newType = inputAjouterType.getText().toString().trim();

                // Check if the type is not empty
                if (!newType.isEmpty()) {
                    // Create a new TypePromo instance
                    TypePromo typePromo = new TypePromo(newType);
                    String typeKey=typePromo.getType();

                    // Push the new type to the database under "list_type" child
                    mDatabase.child(typeKey).setValue(typePromo).addOnSuccessListener(aVoid -> {
                            v.setEnabled(true);
                            inputAjouterType.setText("");
                            Toast.makeText(AjouterSupprimierType.this,"Type added successfully!",Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                        Toast.makeText(AjouterSupprimierType.this, "Failed to add Type:", Toast.LENGTH_SHORT).show();
                        v.setEnabled(true);

                    });
                } else {
                    inputAjouterType.setError("Type cannot be empty!");
                }
            }
        });

        supprimierTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected type from the spinner
                supprimierTypeBtn.setEnabled(false);
                String selectedType = spinnerTypeAjouter.getSelectedItem().toString();


                // Check if a type is selected
                if (selectedType != null) {
                    // Remove the selected type from the database
                    mDatabase.child(selectedType).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    supprimierTypeBtn.setEnabled(true);
                                    Toast.makeText(AjouterSupprimierType.this, "Type deleted successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    supprimierTypeBtn.setEnabled(true);
                                    Toast.makeText(AjouterSupprimierType.this, "Failed to delete type: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Inform the user to select a type first
                    Toast.makeText(AjouterSupprimierType.this, "Please select a type to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public void populateSpinner() {
        // Add a ValueEventListener to retrieve data from Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear existing data in the spinner
                spinnerAdapter.clear();

                // Iterate through the dataSnapshot to get each type
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the type value from the snapshot
                    String type = snapshot.getValue(TypePromo.class).getType();

                    // Add the type to the spinner adapter
                    spinnerAdapter.add(type);
                }

                // Notify the spinner adapter of changes
                spinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(AjouterSupprimierType.this, "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}