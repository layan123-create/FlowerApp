package com.example.flower;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import android.widget.TextView;

public class PaymentActivity extends AppCompatActivity {

    private ImageView btnBackPayment;
    private TextView tvPaymentTotal;
    private EditText etCardName, etCardNumber, etExpiry, etCvv;
    private Button btnConfirmPayment;

    private FirebaseAuth mAuth;
    private DatabaseReference cartRef;

    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        btnBackPayment = findViewById(R.id.btnBackPayment);
        tvPaymentTotal = findViewById(R.id.tvPaymentTotal);
        etCardName = findViewById(R.id.etCardName);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCvv = findViewById(R.id.etCvv);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        }

        totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);
        tvPaymentTotal.setText("$" + String.format(java.util.Locale.US, "%.2f", totalPrice));

        btnBackPayment.setOnClickListener(v -> finish());

        btnConfirmPayment.setOnClickListener(v -> validateAndPay());
    }

    private void validateAndPay() {
        String cardName = etCardName.getText().toString().trim();
        String cardNumber = etCardNumber.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (TextUtils.isEmpty(cardName)) {
            etCardName.setError("Enter cardholder name");
            etCardName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
            etCardNumber.setError("Enter valid 16-digit card number");
            etCardNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(expiry) || expiry.length() != 5 || !expiry.contains("/")) {
            etExpiry.setError("Enter valid expiry like MM/YY");
            etExpiry.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cvv) || cvv.length() != 3) {
            etCvv.setError("Enter valid 3-digit CVV");
            etCvv.requestFocus();
            return;
        }

        completePayment();
    }

    private void completePayment() {
        if (cartRef == null) {
            Toast.makeText(this, "Cart not found", Toast.LENGTH_SHORT).show();
            return;
        }

        cartRef.removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Payment successful", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show());
    }
}