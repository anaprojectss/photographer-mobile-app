package com.example.photographyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    MaterialButton btnRegister;
    TextView tvLogin, tvRoleTitle;

    String role = WelcomeActivity.ROLE_CLIENT; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // role iz WelcomeActivity (client/admin)
        String passedRole = getIntent().getStringExtra(WelcomeActivity.EXTRA_ROLE);
        if (passedRole != null) role = passedRole;

        tvRoleTitle = findViewById(R.id.tvRoleTitle);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        tvRoleTitle.setText(role.equals(WelcomeActivity.ROLE_ADMIN)
                ? "Register as Photographer"
                : "Register as Client");

        btnRegister.setOnClickListener(v -> {
            String name = String.valueOf(etFullName.getText()).trim();
            String email = String.valueOf(etEmail.getText()).trim();
            String pass = String.valueOf(etPassword.getText()).trim();
            String pass2 = String.valueOf(etConfirmPassword.getText()).trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(pass2)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Privremeno: samo prebacimo na MainActivity
            // Kasnije ovde radimo REST poziv ka Firebase Realtime DB
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(WelcomeActivity.EXTRA_ROLE, role);
            startActivity(i);
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}