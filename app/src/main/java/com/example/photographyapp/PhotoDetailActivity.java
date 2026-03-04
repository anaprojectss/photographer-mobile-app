package com.example.photographyapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PhotoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO_ID = "photoId";
    public static final String EXTRA_PHOTO_URL = "photoUrl";
    public static final String EXTRA_PHOTO_TITLE = "photoTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        ImageView imgFull = findViewById(R.id.imgFull);
        TextInputEditText etTitle = findViewById(R.id.etTitle);
        MaterialButton btnSave = findViewById(R.id.btnSaveTitle);

        String photoId = getIntent().getStringExtra(EXTRA_PHOTO_ID);
        String photoUrl = getIntent().getStringExtra(EXTRA_PHOTO_URL);
        String photoTitle = getIntent().getStringExtra(EXTRA_PHOTO_TITLE);

        if (photoTitle != null) etTitle.setText(photoTitle);

        if (photoUrl != null && !photoUrl.trim().isEmpty()) {
            Glide.with(this).load(photoUrl).centerCrop().into(imgFull);
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
                        finish(); // back to grid
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