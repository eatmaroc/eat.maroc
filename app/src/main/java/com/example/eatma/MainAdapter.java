package com.example.eatma;

import static android.content.Intent.getIntent;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    Context context;
    ArrayList<PromoCarte> listPromo;
    DatabaseReference databaseReference;

    public MainAdapter(Context context, ArrayList<PromoCarte> listPromo) {
        this.context = context;
        this.listPromo = listPromo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("villes");
        PromoCarte promo=listPromo.get(position);
        holder.title.setText(promo.getTitle());
        holder.type.setText(promo.getType());
        holder.prix.setText(promo.getPrix());
        Picasso.get().load(promo.getImage()).into(holder.img);
        holder.delete.setOnClickListener(v -> {
            String city = promo.getVille();
            String quartier = promo.getQuartier();
            String promoTitle = promo.getTitle();
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("villes")
                    .child(city)
                    .child("quartiers")
                    .child(quartier)
                    .child(String.format("Promo:%s",promoTitle));

            // Remove the promo from the database
            databaseReference.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Promo deleted successfully
                            Toast.makeText(context, "Promo deleted successfully", Toast.LENGTH_SHORT).show();

                            // Remove the item from the RecyclerView
                            removeItem(position);
                            Intent intent = new Intent(context, AfficherPromo.class);
                            context.startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete promo
                            Toast.makeText(context, "Failed to delete promo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        holder.modify.setOnClickListener(v -> {
            // TODO: 4/17/2024 modifier promo on novelle activity
        });



    }

    @Override
    public int getItemCount() {
        return listPromo.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageButton modify, delete;
        TextView title, type, prix;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgCarte);
            modify = itemView.findViewById(R.id.btnModifier);
            delete = itemView.findViewById(R.id.btnSupprimer);
            title = itemView.findViewById(R.id.titleCarte);
            type = itemView.findViewById(R.id.typeCarte);
            prix = itemView.findViewById(R.id.prixCarte);
        }
    }
    public void removeItem(int position) {
        listPromo.remove(position);
        notifyItemRemoved(position);
    }
}
