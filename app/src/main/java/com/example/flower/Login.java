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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private TextView tvGoRegister;
    private Button btnLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // Views (match your XML)
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        btnLogin = findViewById(R.id.btnLogin);

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
        });

        btnLogin.setOnClickListener(v -> {
            clearErrors();

            String email = safeText(etEmail);
            String pass = safeText(etPassword);

            if (!validateLogin(email, pass)) return;

            btnLogin.setEnabled(false);

            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        btnLogin.setEnabled(true);

                        if (!task.isSuccessful()) {
                            handleLoginError(task.getException());
                            return;
                        }

                        Toast.makeText(this, "Login success 🌸", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Auto-skip login if already signed in
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private boolean validateLogin(String email, String pass) {
        boolean ok = true;

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            ok = false;
        }

        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Password is required");
            ok = false;
        } else if (pass.length() < 6) {
            // Not strictly required for login, but helps catch obvious mistakes
            tilPassword.setError("Password looks too short");
            ok = false;
        }

        return ok;
    }

    private void handleLoginError(Exception e) {
        // Common Firebase Auth errors:
        // - Invalid user / email not found
        // - Wrong password / invalid credentials
        if (e instanceof FirebaseAuthInvalidUserException) {
            tilEmail.setError("No account found with this email");
            return;
        }

        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            tilPassword.setError("Wrong password (or invalid email)");
            return;
        }

        Toast.makeText(this,
                e != null ? e.getMessage() : "Login failed",
                Toast.LENGTH_LONG).show();
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    private String safeText(TextInputEditText et) {
        return (et.getText() == null) ? "" : et.getText().toString().trim();
    }
}
