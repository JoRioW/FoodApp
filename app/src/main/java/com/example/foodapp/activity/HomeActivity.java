package com.example.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.example.foodapp.adapter.CategoryAdapter;
import com.example.foodapp.adapter.PopularAdapter;
import com.example.foodapp.model.Category;
import com.example.foodapp.model.Foods;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseDatabase db2;
    private PopularAdapter adapter;
    private CategoryAdapter categoryAdapter;

    private RecyclerView homeRV, catRV;
    private LinearLayout cartBtn, profileBtn;
    private TextView nameTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nameTV = findViewById(R.id.nameTV);
        homeRV = findViewById(R.id.homeReView);
        catRV = findViewById(R.id.catReView);
        cartBtn = findViewById(R.id.cartBtn);
        profileBtn = findViewById(R.id.profileBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db2 = FirebaseDatabase.getInstance("https://foodapp-d9b0e-default-rtdb.asia-southeast1.firebasedatabase.app/");

        cartBtn.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        loadUsername();
        loadPopularItem();
        loadCategoryItem();
    }

    private void loadUsername() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            nameTV.setText(username);
                        }else {
                            Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load user data!", Toast.LENGTH_SHORT).show();
                    });
        }
    }



    private void loadPopularItem() {
        homeRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DatabaseReference ref = db2.getReference("Foods");
        Query query = ref.orderByChild("PopularFood").equalTo(true);

        FirebaseRecyclerOptions<Foods> options = new FirebaseRecyclerOptions.Builder<Foods>()
                .setQuery(query, Foods.class)
                .build();

        adapter = new PopularAdapter(options, this);
        homeRV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void loadCategoryItem() {
        catRV.setLayoutManager(new GridLayoutManager(this, 3));

        DatabaseReference ref = db2.getReference("Category");
        Query query = ref.orderByChild("Id");

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();

        categoryAdapter = new CategoryAdapter(options, this);
        catRV.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        categoryAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        categoryAdapter.stopListening();
    }
}