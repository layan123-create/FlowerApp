package com.example.flower;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        // Using a Handler to delay the transition to the login activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the login activity after the splash time out
                Intent i = new Intent(Splash.this, Login.class);
                startActivity(i);

                // Close this activity
                finish();
            }
        }, 2000);
    }
}
