package com.example.photographyapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PhotographerProfileActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTOGRAPHER_ID = "photographerId";

    private String photographerId;
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_profile);

        ImageView imgAvatar = findViewById(R.id.imgAvatar);
        TextView tvStudio = findViewById(R.id.tvStudio);
        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvBio = findViewById(R.id.tvBio);
        MaterialButton btnReserve = findViewById(R.id.btnReserve);
        RecyclerView rvPhotos = findViewById(R.id.rvPhotos);

        photographerId = getIntent().getStringExtra(EXTRA_PHOTOGRAPHER_ID);

        rvPhotos.setLayoutManager(new GridLayoutManager(this, 2));

        photoAdapter = new PhotoAdapter(
                photo -> {},
                photo -> {}
        );
        rvPhotos.setAdapter(photoAdapter);

        if (photographerId != null) {
            FirebaseRest.getUser(photographerId, new FirebaseRest.UserCallback() {
                @Override
                public void onSuccess(String fullName, String studioName, String bio, String avatarUrl) {
                    runOnUiThread(() -> {
                        tvStudio.setText(studioName);
                        tvFullName.setText(fullName);
                        tvBio.setText(bio);

                        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                            Glide.with(PhotographerProfileActivity.this)
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
                            Toast.makeText(PhotographerProfileActivity.this, message, Toast.LENGTH_LONG).show()
                    );
                }
            });

            FirebaseRest.getPhotos(photographerId, new FirebaseRest.PhotoListCallback() {
                @Override
                public void onSuccess(List<Photo> photos) {
                    runOnUiThread(() -> photoAdapter.setItems(photos));
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() ->
                            Toast.makeText(PhotographerProfileActivity.this, message, Toast.LENGTH_LONG).show()
                    );
                }
            });
        }

        btnReserve.setOnClickListener(v -> {
            Toast.makeText(this, "Reservation screen coming next", Toast.LENGTH_SHORT).show();
        });
    }
}