package com.example.photographyapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    public static final String EXTRA_ROLE = "role";
    public static final String ROLE_CLIENT = "client";
    public static final String ROLE_ADMIN = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        MaterialCardView cardClient = findViewById(R.id.cardClient);
        MaterialCardView cardAdmin = findViewById(R.id.cardAdmin);

        cardClient.setOnClickListener(v -> openNext(ROLE_CLIENT));
        cardAdmin.setOnClickListener(v -> openNext(ROLE_ADMIN));

        TextView tvLoginHint = findViewById(R.id.tvLoginHint);

        tvLoginHint.setOnClickListener(v -> {
            // privremeno vodi na MainActivity dok ne napravimo LoginActivity
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra(EXTRA_ROLE, ROLE_CLIENT); // ili bez role, kako želiš
            startActivity(i);
        });
    }

    private void openNext(String role) {
        // Za sada vodi na MainActivity kao placeholder.
        // Kasnije ćemo ovde voditi na LoginActivity i proslediti role.
        Intent i = new Intent(this, RegisterActivity.class);
        i.putExtra(EXTRA_ROLE, role);
        startActivity(i);
    }


}

