package com.example.flower;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import android.content.Intent;
public class CartFragment extends Fragment {

    private RecyclerView recyclerCart;
    private TextView tvTotalPrice;

    private ArrayList<Flower> cartList;
    private CartAdapter cartAdapter;
    private Button btnPayNow;

    private FirebaseAuth mAuth;
    private DatabaseReference cartRef;
    private DatabaseReference flowersRef;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerCart = view.findViewById(R.id.recyclerCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnPayNow = view.findViewById(R.id.btnPayNow);
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();
        btnPayNow.setOnClickListener(v -> {
            double total = 0.0;

            for (Flower flower : cartList) {
                total += flower.getPrice() * flower.getQuantity();
            }

            if (total <= 0) {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getContext(), PaymentActivity.class);
            intent.putExtra("totalPrice", total);
            startActivity(intent);
        });
        cartAdapter = new CartAdapter(getContext(), cartList, new CartAdapter.OnCartActionListener() {
            @Override
            public void onDeleteCartItem(Flower flower, int position) {
                deleteCartItemFromFirebase(flower);
            }
        });

        recyclerCart.setAdapter(cartAdapter);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
            flowersRef = FirebaseDatabase.getInstance().getReference("Flowers");
            loadCartItems();
        } else {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();

                if (!snapshot.exists()) {
                    return;
                }

                final int[] remaining = {(int) snapshot.getChildrenCount()};

                for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                    String flowerId = cartItemSnapshot.getKey();
                    Integer quantity = cartItemSnapshot.getValue(Integer.class);

                    if (flowerId != null && quantity != null) {
                        flowersRef.child(flowerId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot flowerSnapshot) {
                                Flower flower = flowerSnapshot.getValue(Flower.class);

                                if (flower != null) {
                                    flower.setQuantity(quantity);
                                    cartList.add(flower);
                                }

                                remaining[0]--;
                                if (remaining[0] == 0) {
                                    cartAdapter.notifyDataSetChanged();
                                    updateTotalPrice();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                remaining[0]--;
                                if (remaining[0] == 0) {
                                    cartAdapter.notifyDataSetChanged();
                                    updateTotalPrice();
                                }
                            }
                        });
                    } else {
                        remaining[0]--;
                        if (remaining[0] == 0) {
                            cartAdapter.notifyDataSetChanged();
                            updateTotalPrice();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadFlowerDetails(String flowerId, int quantity) {
        flowersRef.child(flowerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Flower flower = snapshot.getValue(Flower.class);

                if (flower != null) {
                    flower.setQuantity(quantity);
                    cartList.add(flower);
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load flower details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalPrice() {
        double total = 0.0;

        for (Flower flower : cartList) {
            total += flower.getPrice() * flower.getQuantity();
        }

        tvTotalPrice.setText("$" + String.format(Locale.US, "%.2f", total));
    }

    private void deleteCartItemFromFirebase(Flower flower) {
        if (cartRef == null || flower.getId() == null || flower.getId().isEmpty()) {
            Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
            return;
        }

        cartRef.child(flower.getId()).removeValue()
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show());
    }
}