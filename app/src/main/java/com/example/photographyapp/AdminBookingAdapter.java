package com.example.photographyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.VH> {

    public interface OnAcceptClick {
        void onAccept(Booking booking);
    }

    public interface OnRejectClick {
        void onReject(Booking booking);
    }

    private final List<Booking> items = new ArrayList<>();
    private final OnAcceptClick onAcceptClick;
    private final OnRejectClick onRejectClick;

    public AdminBookingAdapter(OnAcceptClick onAcceptClick, OnRejectClick onRejectClick) {
        this.onAcceptClick = onAcceptClick;
        this.onRejectClick = onRejectClick;
    }

    public void setItems(List<Booking> bookings) {
        items.clear();
        if (bookings != null) items.addAll(bookings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Booking b = items.get(position);

        h.tvDate.setText(b.date);
        h.tvDetails.setText(b.location + " • " + b.shootType + " • " + b.hours + "h");
        h.tvStatus.setText("Status: " + b.status);

        h.btnAccept.setOnClickListener(v -> {
            if (onAcceptClick != null) onAcceptClick.onAccept(b);
        });

        h.btnReject.setOnClickListener(v -> {
            if (onRejectClick != null) onRejectClick.onReject(b);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvDetails, tvStatus;
        MaterialButton btnAccept, btnReject;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvBookingDate);
            tvDetails = itemView.findViewById(R.id.tvBookingDetails);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}