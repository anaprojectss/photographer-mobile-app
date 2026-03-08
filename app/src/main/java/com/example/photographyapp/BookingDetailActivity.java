package com.example.photographyapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class BookingDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "bookingId";
    public static final String EXTRA_PHOTOGRAPHER_ID = "photographerId";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_TYPE = "shootType";
    public static final String EXTRA_HOURS = "hours";
    public static final String EXTRA_STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        TextInputEditText etStudio = findViewById(R.id.etStudio);
        TextInputEditText etPhotographer = findViewById(R.id.etPhotographer);
        TextInputEditText etDate = findViewById(R.id.etDate);
        TextInputEditText etLocation = findViewById(R.id.etLocation);
        TextInputEditText etType = findViewById(R.id.etType);
        TextInputEditText etHours = findViewById(R.id.etHours);
        TextInputEditText etStatus = findViewById(R.id.etStatus);

        MaterialButton btnSave = findViewById(R.id.btnSaveBooking);
        MaterialButton btnDelete = findViewById(R.id.btnDeleteBooking);

        String bookingId = getIntent().getStringExtra(EXTRA_BOOKING_ID);
        String photographerId = getIntent().getStringExtra(EXTRA_PHOTOGRAPHER_ID);
        String date = getIntent().getStringExtra(EXTRA_DATE);
        String location = getIntent().getStringExtra(EXTRA_LOCATION);
        String type = getIntent().getStringExtra(EXTRA_TYPE);
        String hours = getIntent().getStringExtra(EXTRA_HOURS);
        String status = getIntent().getStringExtra(EXTRA_STATUS);

        etDate.setText(date);
        etLocation.setText(location);
        etType.setText(type);
        etHours.setText(hours);
        etStatus.setText(status);

        // studio i photographer prikazujemo, ali ne menjamo
        etStudio.setEnabled(false);
        etPhotographer.setEnabled(false);
        etStatus.setEnabled(false);

        if (photographerId != null) {
            FirebaseRest.getUser(photographerId, new FirebaseRest.UserCallback() {
                @Override
                public void onSuccess(String fullName, String studioName, String bio, String avatarUrl) {
                    runOnUiThread(() -> {
                        etStudio.setText(studioName.isEmpty() ? "Studio" : studioName);
                        etPhotographer.setText(fullName);
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() ->
                            Toast.makeText(BookingDetailActivity.this, message, Toast.LENGTH_LONG).show()
                    );
                }
            });
        }

        btnSave.setOnClickListener(v -> {
            if (bookingId == null) {
                Toast.makeText(this, "Missing bookingId", Toast.LENGTH_LONG).show();
                return;
            }

            String newDate = String.valueOf(etDate.getText()).trim();
            String newLocation = String.valueOf(etLocation.getText()).trim();
            String newType = String.valueOf(etType.getText()).trim();
            String newHours = String.valueOf(etHours.getText()).trim();

            if (newDate.isEmpty() || newLocation.isEmpty() || newType.isEmpty() || newHours.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSave.setEnabled(false);

            FirebaseRest.updateBooking(
                    bookingId,
                    newDate,
                    newLocation,
                    newType,
                    newHours,
                    new FirebaseRest.ResultCallback() {
                        @Override
                        public void onSuccess(String responseBody) {
                            runOnUiThread(() -> {
                                btnSave.setEnabled(true);
                                etStatus.setText("pending");
                                Toast.makeText(
                                        BookingDetailActivity.this,
                                        "Booking updated. Status reset to pending.",
                                        Toast.LENGTH_LONG
                                ).show();
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            runOnUiThread(() -> {
                                btnSave.setEnabled(true);
                                Toast.makeText(BookingDetailActivity.this, message, Toast.LENGTH_LONG).show();
                            });
                        }
                    }
            );
        });

        btnDelete.setOnClickListener(v -> {
            if (bookingId == null) {
                Toast.makeText(this, "Missing bookingId", Toast.LENGTH_LONG).show();
                return;
            }

            btnDelete.setEnabled(false);

            FirebaseRest.deleteBooking(bookingId, new FirebaseRest.ResultCallback() {
                @Override
                public void onSuccess(String responseBody) {
                    runOnUiThread(() -> {
                        btnDelete.setEnabled(true);
                        Toast.makeText(BookingDetailActivity.this, "Booking deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        btnDelete.setEnabled(true);
                        Toast.makeText(BookingDetailActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }
}