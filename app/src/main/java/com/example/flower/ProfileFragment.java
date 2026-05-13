package com.example.flower;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogoutProfile;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);

        btnLogoutProfile = view.findViewById(R.id.btnLogoutProfile);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            tvUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "No email");

            loadUserData();
        } else {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
        }

        btnLogoutProfile.setOnClickListener(v -> {
            mAuth.signOut();

            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserData() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    Long createdAt = snapshot.child("createdAt").getValue(Long.class);

                    tvUserName.setText(name != null ? name : "No name");
                    tvUserEmail.setText(email != null ? email : "No email");


                } else {
                    tvUserName.setText("No name");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(long timestamp) {
        Date date = new Date(timestamp);
        return DateFormat.format("dd/MM/yyyy hh:mm a", date).toString();
    }
}