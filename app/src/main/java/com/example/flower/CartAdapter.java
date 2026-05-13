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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private ArrayList<Flower> cartList;
    private OnCartActionListener listener;

    public CartAdapter(Context context, ArrayList<Flower> cartList, OnCartActionListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Flower flower = cartList.get(position);

        holder.tvCartFlowerName.setText(flower.getName());
        holder.tvCartFlowerCategory.setText(flower.getCategory());
        holder.tvCartFlowerPrice.setText("$" + flower.getPrice());
        holder.tvCartFlowerQuantity.setText("Quantity: " + flower.getQuantity());

        if (flower.getImageUrl() != null && !flower.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(flower.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivCartFlower);
        } else {
            holder.ivCartFlower.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.cardCartItem.setOnLongClickListener(v -> {
            showDeleteDialog(flower, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private void showDeleteDialog(Flower flower, int position) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_dialog);
        dialog.setCancelable(true);

        Button btnCancelDelete = dialog.findViewById(R.id.btnCancelDelete);
        Button btnConfirmDelete = dialog.findViewById(R.id.btnConfirmDelete);

        btnCancelDelete.setOnClickListener(v -> dialog.dismiss());

        btnConfirmDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteCartItem(flower, position);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        CardView cardCartItem;
        ImageView ivCartFlower;
        TextView tvCartFlowerName, tvCartFlowerCategory, tvCartFlowerPrice, tvCartFlowerQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cardCartItem = itemView.findViewById(R.id.cardView);
            ivCartFlower = itemView.findViewById(R.id.ivCartFlower);
            tvCartFlowerName = itemView.findViewById(R.id.tvCartFlowerName);
            tvCartFlowerCategory = itemView.findViewById(R.id.tvCartFlowerCategory);
            tvCartFlowerPrice = itemView.findViewById(R.id.tvCartFlowerPrice);
            tvCartFlowerQuantity = itemView.findViewById(R.id.tvCartFlowerQuantity);
        }
    }

    public interface OnCartActionListener {
        void onDeleteCartItem(Flower flower, int position);
    }
}