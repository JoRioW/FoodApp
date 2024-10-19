package com.example.foodapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.activity.FoodDetailActivity;
import com.example.foodapp.helper.ChangeNumberItemsListener;
import com.example.foodapp.helper.ManagmentCart;
import com.example.foodapp.model.Foods;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    ArrayList<Foods> foods = new ArrayList<>();
    Context context;
    private ManagmentCart managmentCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter (ArrayList<Foods> foods, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.foods = foods;
        this.context = context;
        managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foods food = foods.get(position);
        holder.cartItemTV.setText(food.getFoodName());
        holder.cartPriceItem.setText("Rp" + food.getPrice());
        holder.cartQuantityItem.setText(food.getNumInCart() + "");
        holder.cartItemTotal.setText("Rp" + food.getPrice() * food.getNumInCart());
        Glide.with(holder.cartItemIV.getContext())
                .load(food.getImagePath())
                .into(holder.cartItemIV);

        holder.cartAdd.setOnClickListener(v -> managmentCart.plusNumberItem(foods, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));

        holder.cartRemove.setOnClickListener(v -> managmentCart.minusNumberItem(foods, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cartItemTV, cartPriceItem, cartQuantityItem, cartItemTotal;
        ImageView cartItemIV, cartRemove, cartAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemTV = itemView.findViewById(R.id.cartItemTV);
            cartPriceItem = itemView.findViewById(R.id.cartPriceItem);
            cartQuantityItem = itemView.findViewById(R.id.cartQuantityItem);
            cartItemTotal = itemView.findViewById(R.id.cartItemTotal);
            cartItemIV = itemView.findViewById(R.id.cartItemIV);
            cartRemove = itemView.findViewById(R.id.cartRemove);
            cartAdd = itemView.findViewById(R.id.cartAdd);

        }
    }
}
