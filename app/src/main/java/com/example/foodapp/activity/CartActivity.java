package com.example.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.example.foodapp.adapter.CartAdapter;
import com.example.foodapp.helper.ChangeNumberItemsListener;
import com.example.foodapp.helper.ManagmentCart;

import org.checkerframework.checker.units.qual.C;

public class CartActivity extends AppCompatActivity {

    private int tax;

    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;

    private RecyclerView cartRV;
    private ImageView cartBackBtn;
    private TextView cartSubtotal, cartTax, cartTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cartRV = findViewById(R.id.cartRV);
        cartBackBtn = findViewById(R.id.cartBackBtn);
        cartSubtotal = findViewById(R.id.cartSubtotal);
        cartTax = findViewById(R.id.cartTax);
        cartTotal = findViewById(R.id.cartTotal);


        managmentCart = new ManagmentCart(this);

        setVariable();
        calculateTotal();
        loadList();
    }

    private void loadList() {
        if (managmentCart.getListCart().isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
        }

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cartRV.setLayoutManager(manager);
        adapter = new CartAdapter(managmentCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                calculateTotal();
            }
        });
        cartRV.setAdapter(adapter);


    }

    private void calculateTotal() {
        tax = (int) (managmentCart.getTotalFee() / 10);

        int total = (int) (managmentCart.getTotalFee() + tax);
        int subtotal = (int) (managmentCart.getTotalFee() + 0);

        cartSubtotal.setText("Rp" + subtotal);
        cartTax.setText("Rp" + tax);
        cartTotal.setText("Rp" + total);


    }

    private void setVariable() {
        cartBackBtn.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, HomeActivity.class));
            finish();
        });
    }
}