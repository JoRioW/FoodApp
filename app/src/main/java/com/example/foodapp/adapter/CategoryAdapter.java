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
import com.example.foodapp.activity.CategoryListActivity;
import com.example.foodapp.model.Category;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CategoryAdapter extends FirebaseRecyclerAdapter<Category, CategoryAdapter.ViewHolder> {

    Context context;

    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<Category> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position, @NonNull Category model) {
        holder.catTV.setText(model.getName());
        Glide.with(holder.catIV.getContext())
                .load(model.getImagePath())
                .into(holder.catIV);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryListActivity.class);
            intent.putExtra("Id", model.getId());
            intent.putExtra("Name", model.getName());
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView catTV;
        ImageView catIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catTV = itemView.findViewById(R.id.catTV);
            catIV = itemView.findViewById(R.id.catIV);
        }
    }
}
