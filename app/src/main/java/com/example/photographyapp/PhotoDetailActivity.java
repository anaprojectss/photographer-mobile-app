package com.example.photographyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class PhotoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO_ID = "photoId";
    public static final String EXTRA_PHOTO_URL = "photoUrl";
    public static final String EXTRA_PHOTO_TITLE = "photoTitle";
    public static final String EXTRA_READ_ONLY = "readOnly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        ImageView imgFull = findViewById(R.id.imgFull);
        TextInputEditText etTitle = findViewById(R.id.etTitle);
        MaterialButton btnSave = findViewById(R.id.btnSaveTitle);
        MaterialCardView cardEditTitle = findViewById(R.id.cardEditTitle);

        String photoId = getIntent().getStringExtra(EXTRA_PHOTO_ID);
        String photoUrl = getIntent().getStringExtra(EXTRA_PHOTO_URL);
        String photoTitle = getIntent().getStringExtra(EXTRA_PHOTO_TITLE);
        boolean readOnly = getIntent().getBooleanExtra(EXTRA_READ_ONLY, false);

        if (photoTitle != null) {
            etTitle.setText(photoTitle);
        }

        if (photoUrl != null && !photoUrl.trim().isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .centerCrop()
                    .into(imgFull);
        }

        // Ako je klijent otvorio sliku, sakrij edit title deo
        if (readOnly) {
            cardEditTitle.setVisibility(View.GONE);
            return;
        }

        btnSave.setOnClickListener(v -> {
            if (photoId == null) {
                Toast.makeText(this, "Missing photoId", Toast.LENGTH_LONG).show();
                return;
            }

            String newTitle = String.valueOf(etTitle.getText()).trim();
            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSave.setEnabled(false);

            FirebaseRest.updatePhotoTitle(photoId, newTitle, new FirebaseRest.ResultCallback() {
                @Override
                public void onSuccess(String responseBody) {
                    runOnUiThread(() -> {
                        btnSave.setEnabled(true);
                        Toast.makeText(PhotoDetailActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        btnSave.setEnabled(true);
                        Toast.makeText(PhotoDetailActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }
}