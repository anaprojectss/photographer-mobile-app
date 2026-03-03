package com.example.photographyapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

public class AdminActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        TextView tvName = findViewById(R.id.tvName);
        TextView tvStudio = findViewById(R.id.tvStudio);
        TextView tvBio = findViewById(R.id.tvBio);
        ImageView imgAvatar = findViewById(R.id.imgAvatar);

        String userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            FirebaseRest.getUser(userId, new FirebaseRest.UserCallback() {
                @Override
                public void onSuccess(String fullName, String studioName, String bio, String avatarUrl) {
                    runOnUiThread(() -> {
                        tvName.setText(fullName);
                        tvStudio.setText(studioName);
                        tvBio.setText(bio);

                        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                            Glide.with(AdminActivity.this)
                                    .load(avatarUrl.trim())
                                    .circleCrop()
                                    .into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() ->
                            Toast.makeText(AdminActivity.this, message, Toast.LENGTH_LONG).show()
                    );
                }
            });
        }

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Photos"));
        tabLayout.addTab(tabLayout.newTab().setText("Bookings"));

        // default content
        showPlaceholder("Photos will be shown here (grid).");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showPlaceholder("Photos will be shown here (grid).");
                } else {
                    showPlaceholder("Bookings will be shown here (list).");
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        MaterialButton btnEdit = findViewById(R.id.btnEditProfile);
        View cardEdit = findViewById(R.id.cardEdit);


        TextInputEditText etEditName = findViewById(R.id.etEditName);
        TextInputEditText etEditStudio = findViewById(R.id.etEditStudio);
        TextInputEditText etEditAvatarUrl = findViewById(R.id.etEditAvatarUrl);

        MaterialButton btnSave = findViewById(R.id.btnSaveEdit);
        MaterialButton btnCancel = findViewById(R.id.btnCancelEdit);


// 1) Klik na Edit: prikaži formu i popuni trenutnim vrednostima
        btnEdit.setOnClickListener(v -> {
            if (cardEdit.getVisibility() == View.GONE) {
                etEditName.setText(tvName.getText());
                etEditStudio.setText(tvStudio.getText());
                cardEdit.setVisibility(View.VISIBLE);
            } else {
                cardEdit.setVisibility(View.GONE);
            }
        });

// 2) Cancel
        btnCancel.setOnClickListener(v -> cardEdit.setVisibility(View.GONE));

// 3) Save -> PATCH u bazu -> update UI
        btnSave.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(this, "Missing userId", Toast.LENGTH_LONG).show();
                return;
            }

            String newName = String.valueOf(etEditName.getText()).trim();
            String newStudio = String.valueOf(etEditStudio.getText()).trim();
            String newAvatarUrl = String.valueOf(etEditAvatarUrl.getText()).trim();

            if (newName.isEmpty() || newStudio.isEmpty()) {
                Toast.makeText(this, "Name and studio are required", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSave.setEnabled(false);

            FirebaseRest.updateProfile(userId, newName, newStudio, newAvatarUrl, new FirebaseRest.ResultCallback() {
                @Override
                public void onSuccess(String responseBody) {
                    runOnUiThread(() -> {
                        btnSave.setEnabled(true);
                        tvName.setText(newName);
                        tvStudio.setText(newStudio);
                        cardEdit.setVisibility(View.GONE);
                        Toast.makeText(AdminActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();

                        if (!newAvatarUrl.isEmpty()) {
                            Glide.with(AdminActivity.this)
                                    .load(newAvatarUrl)
                                    .circleCrop()
                                    .into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        btnSave.setEnabled(true);
                        Toast.makeText(AdminActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }

    private void showPlaceholder(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16f);
        tv.setPadding(20, 20, 20, 20);

        ((android.widget.FrameLayout) findViewById(R.id.contentContainer)).removeAllViews();
        ((android.widget.FrameLayout) findViewById(R.id.contentContainer)).addView(tv);
    }
}