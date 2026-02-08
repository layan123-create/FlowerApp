package com.example.flower;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private TextView  tvGoRegister;
    private Button btnLogin, btnGuest;

    private FirebaseAuth auth;

    private static final String PREFS_NAME = "flower_auth_prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "pass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FirebaseAuth instance
        auth = FirebaseAuth.getInstance(); // Firebase Auth start guide :contentReference[oaicite:12]{index=12}

        // Views
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);


        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin = findViewById(R.id.btnLogin);
        btnGuest = findViewById(R.id.btnLoginGuest);



        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(Login.this, Register.class))
        );



        btnLogin.setOnClickListener(v -> {
            clearErrors();

            String email = safeText(etEmail);
            String pass = safeText(etPassword);

            if (!validateLogin(email, pass)) return;


            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {


                            Toast.makeText(this, "Login success 🌸", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(Login.this, MainActivity.class));
                             finish();

                        } else {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        }

        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("Password is required");
            ok = false;
        }

        return ok;
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }


}