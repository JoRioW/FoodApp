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
import com.example.foodapp.model.Foods;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.io.Serializable;

public class PopularAdapter extends FirebaseRecyclerAdapter<Foods, PopularAdapter.ViewHolder> {

    Context context;
    public PopularAdapter(@NonNull FirebaseRecyclerOptions<Foods> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PopularAdapter.ViewHolder holder, int position, @NonNull Foods model) {
        holder.foodName.setText(model.getFoodName());
        holder.foodPrice.setText("Rp" + model.getPrice());

        Glide.with(holder.foodImage.getContext())
                .load(model.getImagePath())
                .into(holder.foodImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("foods", model);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public PopularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice;
        ImageView foodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodTV);
            foodImage = itemView.findViewById(R.id.foodIV);
            foodPrice = itemView.findViewById(R.id.foodPrice);
        }
    }
}
