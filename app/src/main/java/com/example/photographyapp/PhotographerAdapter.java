package com.example.photographyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhotographerAdapter extends RecyclerView.Adapter<PhotographerAdapter.VH> {

    public interface OnClick {
        void onClick(Photographer p);
    }

    private final List<Photographer> allItems = new ArrayList<>();
    private final List<Photographer> visibleItems = new ArrayList<>();
    private final OnClick onClick;

    public PhotographerAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setItems(List<Photographer> list) {
        allItems.clear();
        visibleItems.clear();

        if (list != null) {
            allItems.addAll(list);
            visibleItems.addAll(list);
        }

        notifyDataSetChanged();
    }

    public void filter(String query) {
        visibleItems.clear();

        if (query == null || query.trim().isEmpty()) {
            visibleItems.addAll(allItems);
        } else {
            String q = query.toLowerCase(Locale.ROOT).trim();

            for (Photographer p : allItems) {
                String fullName = p.fullName == null ? "" : p.fullName.toLowerCase(Locale.ROOT);
                String studioName = p.studioName == null ? "" : p.studioName.toLowerCase(Locale.ROOT);

                if (fullName.contains(q) || studioName.contains(q)) {
                    visibleItems.add(p);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photographer, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Photographer p = visibleItems.get(position);

        h.tvStudio.setText(
                p.studioName == null || p.studioName.isEmpty() ? "Studio" : p.studioName
        );
        h.tvName.setText(p.fullName == null ? "" : p.fullName);

        if (p.avatarUrl != null && !p.avatarUrl.trim().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(p.avatarUrl.trim())
                    .circleCrop()
                    .into(h.imgAvatar);
        } else {
            h.imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvStudio, tvName;

        VH(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvStudio = itemView.findViewById(R.id.tvStudio);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}