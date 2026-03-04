package com.example.photographyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.VH> {

    public interface OnDeleteClick {
        void onDelete(Photo photo);
    }

    private final List<Photo> items = new ArrayList<>();
    private final OnDeleteClick onDeleteClick;

    private final OnItemClick onItemClick;

    public PhotoAdapter(OnDeleteClick onDeleteClick, OnItemClick onItemClick) {
        this.onDeleteClick = onDeleteClick;
        this.onItemClick = onItemClick;
    }
    public void setItems(List<Photo> photos) {
        items.clear();
        if (photos != null) items.addAll(photos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Photo p = items.get(position);

        h.tvTitle.setText(p.title == null ? "" : p.title);

        Glide.with(h.itemView.getContext())
                .load(p.url)
                .centerCrop()
                .into(h.imgPhoto);

        h.btnDelete.setOnClickListener(v -> {
            if (onDeleteClick != null) onDeleteClick.onDelete(p);
        });

        h.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvTitle;
        ImageButton btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnItemClick {
        void onClick(Photo photo);
    }


}