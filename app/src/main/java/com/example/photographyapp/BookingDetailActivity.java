package com.example.photographyapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookingDetailActivity extends AppCompatActivity {

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

        TextView tvStudio = findViewById(R.id.tvStudio);
        TextView tvPhotographer = findViewById(R.id.tvPhotographer);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvType = findViewById(R.id.tvType);
        TextView tvHours = findViewById(R.id.tvHours);
        TextView tvStatus = findViewById(R.id.tvStatus);

        String photographerId = getIntent().getStringExtra(EXTRA_PHOTOGRAPHER_ID);
        String date = getIntent().getStringExtra(EXTRA_DATE);
        String location = getIntent().getStringExtra(EXTRA_LOCATION);
        String type = getIntent().getStringExtra(EXTRA_TYPE);
        String hours = getIntent().getStringExtra(EXTRA_HOURS);
        String status = getIntent().getStringExtra(EXTRA_STATUS);

        tvDate.setText("Date: " + date);
        tvLocation.setText("Location: " + location);
        tvType.setText("Type: " + type);
        tvHours.setText("Hours: " + hours);
        tvStatus.setText("Status: " + status);

        if (photographerId != null) {
            FirebaseRest.getUser(photographerId, new FirebaseRest.UserCallback() {
                @Override
                public void onSuccess(String fullName, String studioName, String bio, String avatarUrl) {
                    runOnUiThread(() -> {
                        tvStudio.setText(studioName.isEmpty() ? "Studio" : studioName);
                        tvPhotographer.setText("Photographer: " + fullName);
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
    }
}