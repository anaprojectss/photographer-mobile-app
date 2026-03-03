package com.example.photographyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
            String email = String.valueOf(etEmail.getText()).trim();
            String password = String.valueOf(etPassword.getText()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);

            FirebaseRest.login(email, password, new FirebaseRest.LoginCallback() {
                @Override
                public void onSuccess(String userId, String role) {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        Intent i;

                        if (role.equals(WelcomeActivity.ROLE_ADMIN)) {
                            i = new Intent(LoginActivity.this, AdminActivity.class);
                        } else {
                            i = new Intent(LoginActivity.this, MainActivity.class);
                        }

                        i.putExtra("userId", userId);
                        i.putExtra(WelcomeActivity.EXTRA_ROLE, role);
                        startActivity(i);
                        finish();
                    });
                }

                @Override
                public void onInvalidCredentials() {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }
            });
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