package com.example.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.helper.ManagmentCart;
import com.example.foodapp.model.Foods;

public class FoodDetailActivity extends AppCompatActivity {
    private Foods foods;
    private int quantity = 1;
    private ManagmentCart managmentCart;

    private ImageView backDetailsBtn, detailIV, detailAddBtn, detailRemoveBtn;
    private TextView detailNameTV, detailPriceTV, detailTotalPrice, detailQtTV;
    private Button detailAddCartBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backDetailsBtn = findViewById(R.id.backDetailsBtn);
        detailIV = findViewById(R.id.detailIV);
        detailNameTV = findViewById(R.id.detailNameTV);
        detailPriceTV = findViewById(R.id.detailPriceTV);
        detailTotalPrice = findViewById(R.id.detailTotalPrice);
        detailAddBtn = findViewById(R.id.detailAddBtn);
        detailRemoveBtn = findViewById(R.id.detailRemoveBtn);
        detailQtTV = findViewById(R.id.detailQtTV);
        detailAddCartBtn = findViewById(R.id.detailAddCartBtn);



        getIntentDetail();
        setVariable();

        backDetailsBtn.setOnClickListener(v -> {
            startActivity(new Intent(FoodDetailActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void setVariable() {
        managmentCart = new ManagmentCart(this);
        Glide.with(FoodDetailActivity.this)
                .load(foods.getImagePath())
                .into(detailIV);

        detailNameTV.setText(foods.getFoodName());
        detailPriceTV.setText("Rp" + foods.getPrice());
        String total = "Rp" + (quantity * foods.getPrice());
        detailTotalPrice.setText("Rp" + (quantity * foods.getPrice()));
        String qt = "" + quantity;
        detailQtTV.setText(qt);

        detailAddBtn.setOnClickListener(v -> {
            quantity = quantity + 1;
            detailQtTV.setText(quantity + " ");;
            detailTotalPrice.setText("Rp"+(quantity*foods.getPrice()) + "");
        });

        detailRemoveBtn.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity = quantity - 1;
                detailQtTV.setText(quantity + " ");;
                detailTotalPrice.setText("Rp"+(quantity*foods.getPrice()) + "");
            }
        });

        detailAddCartBtn.setOnClickListener(v -> {
            foods.setNumInCart(quantity);
            managmentCart.insertFood(foods);
        });


    }

    private void getIntentDetail() {
        foods = (Foods) getIntent().getSerializableExtra("foods");
    }
}