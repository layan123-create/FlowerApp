package com.example.flower;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FlowersListActivity extends AppCompatActivity {

    private ImageView btnBackFlowers;
    private TextView tvFlowersCount;
    private RecyclerView recyclerAllFlowers;

    private ArrayList<Flower> flowerList;
    private FlowerAdapter flowerAdapter;

    private DatabaseReference flowersRef;
    private DatabaseReference favoritesRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowers_list);

        btnBackFlowers = findViewById(R.id.btnBackFlowers);
        tvFlowersCount = findViewById(R.id.tvFlowersCount);
        recyclerAllFlowers = findViewById(R.id.recyclerAllFlowers);

        flowerList = new ArrayList<>();

        recyclerAllFlowers.setLayoutManager(new GridLayoutManager(this, 2));

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
        }

        flowerAdapter = new FlowerAdapter(this, flowerList, new FlowerAdapter.OnFlowerClickListener() {
            @Override
            public void onFlowerClick(Flower flower) {
                Intent intent = new Intent(FlowersListActivity.this, FlowerDetailsActivity.class);
                intent.putExtra("id", flower.getId());
                intent.putExtra("name", flower.getName());
                intent.putExtra("category", flower.getCategory());
                intent.putExtra("description", flower.getDescription());
                intent.putExtra("price", flower.getPrice());
                intent.putExtra("imageUrl", flower.getImageUrl());
                intent.putExtra("rating", flower.getRating());
                intent.putExtra("stock", flower.getStock());
                intent.putExtra("favorite", flower.isFavorite());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Flower flower, int position) {
                toggleFavorite(flower, position);
            }
        });

        recyclerAllFlowers.setAdapter(flowerAdapter);

        flowersRef = FirebaseDatabase.getInstance().getReference("Flowers");

        btnBackFlowers.setOnClickListener(v -> finish());

        loadAllFlowers();
    }

    private void loadAllFlowers() {
        flowersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flowerList.clear();

                if (mAuth.getCurrentUser() != null && favoritesRef != null) {
                    favoritesRef.get().addOnSuccessListener(favoriteSnapshot -> {
                        ArrayList<String> favoriteIds = new ArrayList<>();

                        for (DataSnapshot favSnap : favoriteSnapshot.getChildren()) {
                            if (favSnap.getKey() != null) {
                                favoriteIds.add(favSnap.getKey());
                            }
                        }

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Flower flower = dataSnapshot.getValue(Flower.class);

                            if (flower != null) {
                                flower.setFavorite(favoriteIds.contains(flower.getId()));
                                flowerList.add(flower);
                            }
                        }

                        flowerAdapter.notifyDataSetChanged();
                        tvFlowersCount.setText(flowerList.size() + " flowers");
                    }).addOnFailureListener(e ->
                            Toast.makeText(FlowersListActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show());

                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Flower flower = dataSnapshot.getValue(Flower.class);

                        if (flower != null) {
                            flower.setFavorite(false);
                            flowerList.add(flower);
                        }
                    }

                    flowerAdapter.notifyDataSetChanged();
                    tvFlowersCount.setText(flowerList.size() + " flowers");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FlowersListActivity.this, "Failed to load flowers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFavorite(Flower flower, int position) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (favoritesRef == null || flower.getId() == null || flower.getId().isEmpty()) {
            Toast.makeText(this, "Favorite failed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (flower.isFavorite()) {
            favoritesRef.child(flower.getId()).removeValue().addOnSuccessListener(unused -> {
                flower.setFavorite(false);
                flowerAdapter.notifyItemChanged(position);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to update favorites", Toast.LENGTH_SHORT).show());
        } else {
            favoritesRef.child(flower.getId()).setValue(true).addOnSuccessListener(unused -> {
                flower.setFavorite(true);
                flowerAdapter.notifyItemChanged(position);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to update favorites", Toast.LENGTH_SHORT).show());
        }
    }
}