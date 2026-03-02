package com.example.photographyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    MaterialButton btnLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            // privremeno - kasnije ide Firebase login
            if(!email.isEmpty() && !password.isEmpty()) {

                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);

            }

        });



        tvRegister.setOnClickListener(v -> {

            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);

        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // vraća na prethodni ekran (WelcomeActivity)
        return true;
    }
}