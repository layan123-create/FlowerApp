package com.example.flower;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etName, etEmail, etPassword, etConfirm;
    private Button btnRegister;
    private TextView tvGoLogin;

    private FirebaseAuth auth;
    private DatabaseReference usersRef; // Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        auth = FirebaseAuth.getInstance(); // https://firebase.google.com/docs/auth/android/start
        usersRef = FirebaseDatabase.getInstance().getReference("Users"); // https://firebase.google.com/docs/database/android/read-and-write

        // Views
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirm);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);

        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            clearErrors();

            String name = safeText(etName);
            String email = safeText(etEmail);
            String pass = safeText(etPassword);
            String confirm = safeText(etConfirm);

            if (!validateRegister(name, email, pass, confirm)) return;


            // Create Firebase Auth account (Email/Password)
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show();
                            return;
                        }

                        FirebaseUser user = auth.getCurrentUser();

                        String uid = user.getUid();

                        // OPTIONAL: Save profile to Realtime Database
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("uid", uid);
                        map.put("name", name);
                        map.put("email", email);
                        map.put("createdAt", System.currentTimeMillis());

                        usersRef.child(uid).setValue(map)
                                .addOnCompleteListener(saveTask -> {

                                    if (saveTask.isSuccessful()) {
                                        Toast.makeText(this, "Account created 🌷", Toast.LENGTH_SHORT).show();

                                        //  Go back to Login
                                        startActivity(new Intent(Register.this, Login.class));
                                        finish();

                                    } else {
                                        Toast.makeText(this,  "Failed to save user data", Toast.LENGTH_LONG).show();
                                    }
                                });
                    });
        });
    }

    private boolean validateRegister(String name, String email, String pass, String confirm) {
        boolean ok = true;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name is required");
            ok = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            ok = false;
        }

        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Password is required");
            ok = false;
        } else if (pass.length() < 6) {
            tilPassword.setError("Minimum 6 characters");
            ok = false;
        }

        if (TextUtils.isEmpty(confirm)) {
            tilConfirm.setError("Confirm your password");
            ok = false;
        } else if (!confirm.equals(pass)) {
            tilConfirm.setError("Passwords do not match");
            ok = false;
        }

        return ok;
    }

    private void clearErrors() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }


}