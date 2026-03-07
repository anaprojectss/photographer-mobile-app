package com.example.photographyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    public interface OnBookingClick {
        void onClick(Booking booking);
    }

    private final List<Booking> items = new ArrayList<>();
    private final OnBookingClick onBookingClick;

    public BookingAdapter(OnBookingClick onBookingClick) {
        this.onBookingClick = onBookingClick;
    }

    public void setItems(List<Booking> bookings) {
        items.clear();
        if (bookings != null) items.addAll(bookings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Booking b = items.get(position);

        h.tvDate.setText(b.date);
        h.tvDetails.setText(b.location + " • " + b.shootType + " • " + b.hours + "h");
        h.tvStatus.setText("Status: " + b.status);

        h.itemView.setOnClickListener(v -> {
            if (onBookingClick != null) onBookingClick.onClick(b);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvDetails, tvStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvBookingDate);
            tvDetails = itemView.findViewById(R.id.tvBookingDetails);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
        }
    }
}