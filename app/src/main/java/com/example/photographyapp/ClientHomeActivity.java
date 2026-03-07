package com.example.photographyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class ClientHomeActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        TextView tvName = findViewById(R.id.tvClientName);
        TextView tvEmail = findViewById(R.id.tvClientEmail);

        userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            FirebaseRest.getUserBasic(userId, new FirebaseRest.UserBasicCallback() {
                @Override
                public void onSuccess(String fullName, String email) {
                    runOnUiThread(() -> {
                        tvName.setText(fullName);
                        tvEmail.setText(email.isEmpty() ? " " : email);
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() ->
                            Toast.makeText(ClientHomeActivity.this, message, Toast.LENGTH_LONG).show()
                    );
                }
            });
        }

        MaterialButton btnEditName = findViewById(R.id.btnEditName);
        View cardEdit = findViewById(R.id.cardEditClient);

        TextInputEditText etEditName = findViewById(R.id.etEditClientName);
        MaterialButton btnSave = findViewById(R.id.btnSaveClient);
        MaterialButton btnCancel = findViewById(R.id.btnCancelClient);

//        RecyclerView rv = findViewById(R.id.rvPhotographers);
//        rv.setLayoutManager(new LinearLayoutManager(this));

        // Dummy photographers (kasnije ćemo povući iz /users gde role=admin)
        List<String> demo = Arrays.asList("AM Studio", "Sunset Weddings", "Portrait Pro");


//        rv.setAdapter(adapter);

        btnEditName.setOnClickListener(v -> {

            if (cardEdit.getVisibility() == View.GONE) {

                etEditName.setText(tvName.getText());
                cardEdit.setVisibility(View.VISIBLE);

            } else {
                cardEdit.setVisibility(View.GONE);
            }

        });

        btnCancel.setOnClickListener(v ->
                cardEdit.setVisibility(View.GONE)
        );

        btnSave.setOnClickListener(v -> {

            if (userId == null) return;

            String newName = String.valueOf(etEditName.getText()).trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseRest.updateProfile(userId, newName, "", "", new FirebaseRest.ResultCallback() {

                @Override
                public void onSuccess(String responseBody) {

                    runOnUiThread(() -> {

                        tvName.setText(newName);
                        cardEdit.setVisibility(View.GONE);

                        Toast.makeText(ClientHomeActivity.this,
                                "Name updated!", Toast.LENGTH_SHORT).show();

                    });

                }

                @Override
                public void onError(String message) {

                    runOnUiThread(() ->
                            Toast.makeText(ClientHomeActivity.this,
                                    message, Toast.LENGTH_LONG).show()
                    );

                }
            });

        });

        RecyclerView rv = findViewById(R.id.rvPhotographers);
        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        PhotographerAdapter adapter = new PhotographerAdapter(p -> {
            android.content.Intent i = new android.content.Intent(this, PhotographerProfileActivity.class);
            i.putExtra(PhotographerProfileActivity.EXTRA_PHOTOGRAPHER_ID, p.id);
            i.putExtra("userId", userId);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        FirebaseRest.getPhotographers(new FirebaseRest.PhotographersCallback() {
            @Override
            public void onSuccess(List<Photographer> photographers) {
                runOnUiThread(() -> adapter.setItems(photographers));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(ClientHomeActivity.this, message, Toast.LENGTH_LONG).show());
            }
        });


        RecyclerView rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        BookingAdapter bookingAdapter = new BookingAdapter(booking -> {
            Intent i = new Intent(ClientHomeActivity.this, BookingDetailActivity.class);
            i.putExtra(BookingDetailActivity.EXTRA_PHOTOGRAPHER_ID, booking.photographerId);
            i.putExtra(BookingDetailActivity.EXTRA_DATE, booking.date);
            i.putExtra(BookingDetailActivity.EXTRA_LOCATION, booking.location);
            i.putExtra(BookingDetailActivity.EXTRA_TYPE, booking.shootType);
            i.putExtra(BookingDetailActivity.EXTRA_HOURS, booking.hours);
            i.putExtra(BookingDetailActivity.EXTRA_STATUS, booking.status);
            startActivity(i);
        });
        rvBookings.setAdapter(bookingAdapter);

        if (userId != null) {
            FirebaseRest.getBookingsForClient(userId, new FirebaseRest.BookingListCallback() {
                @Override
                public void onSuccess(java.util.List<Booking> bookings) {
                    runOnUiThread(() -> bookingAdapter.setItems(bookings));
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() ->
                            Toast.makeText(ClientHomeActivity.this, message, Toast.LENGTH_LONG).show()
                    );
                }
            });
        }
    }
}