package com.example.flower;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private ArrayList<Flower> favoriteList;
    private OnFavoriteActionListener listener;

    public FavoriteAdapter(Context context, ArrayList<Flower> favoriteList, OnFavoriteActionListener listener) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Flower flower = favoriteList.get(position);

        holder.tvFavoriteFlowerName.setText(flower.getName());
        holder.tvFavoriteFlowerCategory.setText(flower.getCategory());
        holder.tvFavoriteFlowerPrice.setText("$" + flower.getPrice());

        if (flower.getImageUrl() != null && !flower.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(flower.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivFavoriteFlower);
        } else {
            holder.ivFavoriteFlower.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.cardFavoriteItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(flower);
            }
        });

        holder.cardFavoriteItem.setOnLongClickListener(v -> {
            showDeleteDialog(flower);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    private void showDeleteDialog(Flower flower) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_favorite);
        dialog.setCancelable(true);

        Button btnCancelFavoriteDelete = dialog.findViewById(R.id.btnCancelFavoriteDelete);
        Button btnConfirmFavoriteDelete = dialog.findViewById(R.id.btnConfirmFavoriteDelete);

        btnCancelFavoriteDelete.setOnClickListener(v -> dialog.dismiss());

        btnConfirmFavoriteDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteFavorite(flower);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {

        CardView cardFavoriteItem;
        ImageView ivFavoriteFlower, ivFavoriteIcon;
        TextView tvFavoriteFlowerName, tvFavoriteFlowerCategory, tvFavoriteFlowerPrice;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            cardFavoriteItem = itemView.findViewById(R.id.cardFavoriteItem);
            ivFavoriteFlower = itemView.findViewById(R.id.ivFavoriteFlower);
            ivFavoriteIcon = itemView.findViewById(R.id.ivFavoriteIcon);
            tvFavoriteFlowerName = itemView.findViewById(R.id.tvFavoriteFlowerName);
            tvFavoriteFlowerCategory = itemView.findViewById(R.id.tvFavoriteFlowerCategory);
            tvFavoriteFlowerPrice = itemView.findViewById(R.id.tvFavoriteFlowerPrice);
        }
    }

    public interface OnFavoriteActionListener {
        void onFavoriteClick(Flower flower);
        void onDeleteFavorite(Flower flower);
    }
}