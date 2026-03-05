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

public class PhotographerAdapter extends RecyclerView.Adapter<PhotographerAdapter.VH> {

    public interface OnClick {
        void onClick(Photographer p);
    }

    private final List<Photographer> items = new ArrayList<>();
    private final OnClick onClick;

    public PhotographerAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setItems(List<Photographer> list) {
        items.clear();
        if (list != null) items.addAll(list);
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
        Photographer p = items.get(position);

        h.tvStudio.setText(p.studioName == null || p.studioName.isEmpty() ? "Studio" : p.studioName);
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
        return items.size();
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