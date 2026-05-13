package com.example.flower;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerFavorites;
    private TextView tvEmptyFavorites;

    private ArrayList<Flower> favoriteList;
    private FavoriteAdapter favoriteAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference favoritesRef;
    private DatabaseReference flowersRef;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerFavorites = view.findViewById(R.id.recyclerFavorites);
        tvEmptyFavorites = view.findViewById(R.id.tvEmptyFavorites);

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteList = new ArrayList<>();

        favoriteAdapter = new FavoriteAdapter(getContext(), favoriteList, new FavoriteAdapter.OnFavoriteActionListener() {
            @Override
            public void onFavoriteClick(Flower flower) {
                openFlowerDetails(flower);
            }

            @Override
            public void onDeleteFavorite(Flower flower) {
                removeFavoriteFromFirebase(flower);
            }
        });

        recyclerFavorites.setAdapter(favoriteAdapter);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
            flowersRef = FirebaseDatabase.getInstance().getReference("Flowers");
            loadFavorites();
        } else {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            showEmptyState();
        }

        return view;
    }

    private void loadFavorites() {
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteList.clear();
                favoriteAdapter.notifyDataSetChanged();

                if (!snapshot.exists()) {
                    showEmptyState();
                    return;
                }

                final int[] remaining = {(int) snapshot.getChildrenCount()};

                for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                    String flowerId = favoriteSnapshot.getKey();

                    if (flowerId != null) {
                        flowersRef.child(flowerId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot flowerSnapshot) {
                                Flower flower = flowerSnapshot.getValue(Flower.class);

                                if (flower != null) {
                                    flower.setFavorite(true);
                                    favoriteList.add(flower);
                                }

                                remaining[0]--;
                                if (remaining[0] == 0) {
                                    favoriteAdapter.notifyDataSetChanged();
                                    updateEmptyState();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                remaining[0]--;
                                if (remaining[0] == 0) {
                                    favoriteAdapter.notifyDataSetChanged();
                                    updateEmptyState();
                                }
                            }
                        });
                    } else {
                        remaining[0]--;
                        if (remaining[0] == 0) {
                            favoriteAdapter.notifyDataSetChanged();
                            updateEmptyState();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFavoriteFromFirebase(Flower flower) {
        if (favoritesRef == null || flower.getId() == null || flower.getId().isEmpty()) {
            Toast.makeText(getContext(), "Remove failed", Toast.LENGTH_SHORT).show();
            return;
        }

        favoritesRef.child(flower.getId()).removeValue()
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to remove favorite", Toast.LENGTH_SHORT).show());
    }

    private void openFlowerDetails(Flower flower) {
        Intent intent = new Intent(getContext(), FlowerDetailsActivity.class);
        intent.putExtra("id", flower.getId());
        intent.putExtra("name", flower.getName());
        intent.putExtra("category", flower.getCategory());
        intent.putExtra("description", flower.getDescription());
        intent.putExtra("price", flower.getPrice());
        intent.putExtra("imageUrl", flower.getImageUrl());
        intent.putExtra("rating", flower.getRating());
        intent.putExtra("stock", flower.getStock());
        intent.putExtra("favorite", true);
        startActivity(intent);
    }

    private void updateEmptyState() {
        if (favoriteList.isEmpty()) {
            showEmptyState();
        } else {
            tvEmptyFavorites.setVisibility(View.GONE);
            recyclerFavorites.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyState() {
        tvEmptyFavorites.setVisibility(View.VISIBLE);
        recyclerFavorites.setVisibility(View.GONE);
    }
}