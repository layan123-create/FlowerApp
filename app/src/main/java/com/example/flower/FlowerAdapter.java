package com.example.flower;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FlowerAdapter extends RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder> {

    private Context context;
    private ArrayList<Flower> flowerList;
    private OnFlowerClickListener listener;

    public FlowerAdapter(Context context, ArrayList<Flower> flowerList, OnFlowerClickListener listener) {
        this.context = context;
        this.flowerList = flowerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FlowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flower, parent, false);
        return new FlowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlowerViewHolder holder, int position) {
        Flower flower = flowerList.get(position);

        holder.tvFlowerName.setText(flower.getName());
        holder.tvFlowerCategory.setText(flower.getCategory());
        holder.tvFlowerPrice.setText("$" + flower.getPrice());

        if (flower.isFavorite()) {
            holder.ivFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.ivFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }

        String imageUrl = flower.getImageUrl();
        Log.d("IMG_TEST", "URL = " + imageUrl);

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Glide.with(context)
                    .load(imageUrl.trim())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.ivFlowerImage);
        } else {
            holder.ivFlowerImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.cardFlower.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFlowerClick(flower);
            }
        });

        holder.ivFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(flower, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return flowerList.size();
    }

    public static class FlowerViewHolder extends RecyclerView.ViewHolder {

        CardView cardFlower;
        ImageView ivFlowerImage, ivFavorite;
        TextView tvFlowerName, tvFlowerCategory, tvFlowerPrice;

        public FlowerViewHolder(@NonNull View itemView) {
            super(itemView);

            cardFlower = itemView.findViewById(R.id.cardFlower);
            ivFlowerImage = itemView.findViewById(R.id.ivFlowerImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvFlowerName = itemView.findViewById(R.id.tvFlowerName);
            tvFlowerCategory = itemView.findViewById(R.id.tvFlowerCategory);
            tvFlowerPrice = itemView.findViewById(R.id.tvFlowerPrice);
        }
    }

    public interface OnFlowerClickListener {
        void onFlowerClick(Flower flower);
        void onFavoriteClick(Flower flower, int position);
    }
}