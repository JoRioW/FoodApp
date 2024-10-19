    package com.example.foodapp.activity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.recyclerview.widget.GridLayoutManager;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.foodapp.R;
    import com.example.foodapp.adapter.FoodListAdapter;
    import com.example.foodapp.adapter.PopularAdapter;
    import com.example.foodapp.model.Foods;
    import com.firebase.ui.database.FirebaseRecyclerOptions;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.Query;

    public class CategoryListActivity extends AppCompatActivity {

        private FirebaseDatabase db;

        private TextView titleCatTV;
        private ImageView backBtn;
        private RecyclerView listReView;
        private FoodListAdapter adapter;

        private int Id;
        private String Name;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_category_list);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            titleCatTV = findViewById(R.id.titleCatTV);
            backBtn = findViewById(R.id.backBtn);
            listReView = findViewById(R.id.listReView);
            db = FirebaseDatabase.getInstance("https://foodapp-d9b0e-default-rtdb.asia-southeast1.firebasedatabase.app/");

            backBtn.setOnClickListener(v -> {
                startActivity(new Intent(CategoryListActivity.this, HomeActivity.class));
                finish();
            });

            getIntentList();
            getList();
        }

        private void getList() {
            listReView.setLayoutManager(new GridLayoutManager(this, 2));

            DatabaseReference ref = db.getReference("Foods");
            Query query = ref.orderByChild("CategoryId").equalTo(Id);

            FirebaseRecyclerOptions<Foods> options = new FirebaseRecyclerOptions.Builder<Foods>()
                    .setQuery(query, Foods.class)
                    .build();

            adapter = new FoodListAdapter(options, CategoryListActivity.this);
            listReView.setAdapter(adapter);
            adapter.startListening();
            adapter.notifyDataSetChanged();
        }

        private void getIntentList() {
            Id = getIntent().getIntExtra("Id", 0);
            Name = getIntent().getStringExtra("Name");
            titleCatTV.setText(Name);
        }
    }