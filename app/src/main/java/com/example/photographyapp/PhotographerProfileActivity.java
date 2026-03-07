package com.example.photographyapp;

import android.content.Intent;
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
    private String clientId;
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

        clientId = getIntent().getStringExtra("userId");

        rvPhotos.setLayoutManager(new GridLayoutManager(this, 2));

        photoAdapter = new PhotoAdapter(
                // delete callback - klijent ne briše
                photo -> {
                },

                // klik na fotografiju -> uvećana slika
                photo -> {
                    Intent i = new Intent(
                            PhotographerProfileActivity.this,
                            PhotoDetailActivity.class
                    );
                    i.putExtra(PhotoDetailActivity.EXTRA_PHOTO_ID, photo.id);
                    i.putExtra(PhotoDetailActivity.EXTRA_PHOTO_URL, photo.url);
                    i.putExtra(PhotoDetailActivity.EXTRA_PHOTO_TITLE, photo.title);
                    i.putExtra(PhotoDetailActivity.EXTRA_READ_ONLY, true);
                    startActivity(i);
                },

                // klijent ne vidi delete dugme
                false
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

        btnReserve.setOnClickListener(v -> openReserveDialog());
    }

    private void openReserveDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_reserve_date, null);

        com.google.android.material.textfield.TextInputEditText etDate =
                view.findViewById(R.id.etBookingDate);
        com.google.android.material.textfield.TextInputEditText etLocation =
                view.findViewById(R.id.etBookingLocation);
        com.google.android.material.textfield.TextInputEditText etType =
                view.findViewById(R.id.etBookingType);
        com.google.android.material.textfield.TextInputEditText etHours =
                view.findViewById(R.id.etBookingHours);

        builder.setView(view)
                .setTitle("Reserve date")
                .setPositiveButton("Send request", (dialog, which) -> {
                    String date = String.valueOf(etDate.getText()).trim();
                    String location = String.valueOf(etLocation.getText()).trim();
                    String type = String.valueOf(etType.getText()).trim();
                    String hours = String.valueOf(etHours.getText()).trim();

                    if (date.isEmpty() || location.isEmpty() || type.isEmpty() || hours.isEmpty()) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (clientId == null || photographerId == null) {
                        Toast.makeText(this, "Missing user data", Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseRest.createBooking(
                            clientId,
                            photographerId,
                            date,
                            location,
                            type,
                            hours,
                            new FirebaseRest.ResultCallback() {
                                @Override
                                public void onSuccess(String responseBody) {
                                    runOnUiThread(() ->
                                            Toast.makeText(PhotographerProfileActivity.this,
                                                    "Booking request sent!",
                                                    Toast.LENGTH_SHORT).show()
                                    );
                                }

                                @Override
                                public void onError(String message) {
                                    runOnUiThread(() ->
                                            Toast.makeText(PhotographerProfileActivity.this,
                                                    message,
                                                    Toast.LENGTH_LONG).show()
                                    );
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}