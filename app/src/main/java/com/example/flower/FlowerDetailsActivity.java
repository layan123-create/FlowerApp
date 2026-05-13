package com.example.flower;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FlowerDetailsActivity extends AppCompatActivity {

    private ImageView ivFlowerDetails, btnBack, btnFavoriteDetails;
    private TextView tvFlowerNameDetails, tvFlowerCategoryDetails, tvFlowerPriceDetails,
            tvFlowerRatingDetails, tvFlowerDescriptionDetails, tvFlowerStockDetails;
    private Button btnAddToCart;

    private boolean isFavorite = false;
    private String flowerId = "";

    private FirebaseAuth mAuth;
    private DatabaseReference favoritesRef, cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_details);

        ivFlowerDetails = findViewById(R.id.ivFlowerDetails);
        btnBack = findViewById(R.id.btnBack);
        btnFavoriteDetails = findViewById(R.id.btnFavoriteDetails);
        tvFlowerNameDetails = findViewById(R.id.tvFlowerNameDetails);
        tvFlowerCategoryDetails = findViewById(R.id.tvFlowerCategoryDetails);
        tvFlowerPriceDetails = findViewById(R.id.tvFlowerPriceDetails);
        tvFlowerRatingDetails = findViewById(R.id.tvFlowerRatingDetails);
        tvFlowerDescriptionDetails = findViewById(R.id.tvFlowerDescriptionDetails);
        tvFlowerStockDetails = findViewById(R.id.tvFlowerStockDetails);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
            cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        }

        flowerId = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        double price = getIntent().getDoubleExtra("price", 0.0);
        double rating = getIntent().getDoubleExtra("rating", 0.0);
        int stock = getIntent().getIntExtra("stock", 0);
        isFavorite = getIntent().getBooleanExtra("favorite", false);

        tvFlowerNameDetails.setText(name);
        tvFlowerCategoryDetails.setText(category);
        tvFlowerPriceDetails.setText("$" + price);
        tvFlowerRatingDetails.setText("★ " + rating);
        tvFlowerDescriptionDetails.setText(description);
        tvFlowerStockDetails.setText("In stock: " + stock);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivFlowerDetails);
        } else {
            ivFlowerDetails.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        updateFavoriteIcon();
        checkIfFavoriteInFirebase();

        btnBack.setOnClickListener(v -> finish());

        btnFavoriteDetails.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            toggleFavorite();
        });

        btnAddToCart.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            addToCart();
        });
    }

    private void checkIfFavoriteInFirebase() {
        if (favoritesRef == null || flowerId == null || flowerId.isEmpty()) return;

        favoritesRef.child(flowerId).get().addOnSuccessListener(snapshot -> {
            isFavorite = snapshot.exists();
            updateFavoriteIcon();
        });
    }

    private void toggleFavorite() {
        if (favoritesRef == null || flowerId == null || flowerId.isEmpty()) return;

        if (isFavorite) {
            favoritesRef.child(flowerId).removeValue().addOnSuccessListener(unused -> {
                isFavorite = false;
                updateFavoriteIcon();
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to update favorites", Toast.LENGTH_SHORT).show());
        } else {
            favoritesRef.child(flowerId).setValue(true).addOnSuccessListener(unused -> {
                isFavorite = true;
                updateFavoriteIcon();
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to update favorites", Toast.LENGTH_SHORT).show());
        }
    }

    private void addToCart() {
        if (cartRef == null || flowerId == null || flowerId.isEmpty()) return;

        cartRef.child(flowerId).get().addOnSuccessListener(snapshot -> {
            int currentQuantity = 0;

            if (snapshot.exists()) {
                Integer value = snapshot.getValue(Integer.class);
                if (value != null) {
                    currentQuantity = value;
                }
            }

            int newQuantity = currentQuantity + 1;

            cartRef.child(flowerId).setValue(newQuantity)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show());

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to read cart", Toast.LENGTH_SHORT).show());
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            btnFavoriteDetails.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavoriteDetails.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }
}