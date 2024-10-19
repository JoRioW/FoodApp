package com.example.foodapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodapp.R;
import com.example.foodapp.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImg;
    private TextView profileName, profileDob;
    private Uri imageuri;
    private ImageView profilebackBtn;
    private Button editProfileBtn, signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileImg = findViewById(R.id.profileImg);
        profileName = findViewById(R.id.profileName);
        profileDob = findViewById(R.id.profileDob);
        profilebackBtn = findViewById(R.id.profilebackBtn);
        signOut = findViewById(R.id.signOutBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        editProfileBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
        });

        profilebackBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        signOut.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            auth.signOut();
        });

        loadImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            profileImg.setImageURI(imageuri);
        }
    }

    private void loadImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                            if (profile != null) {
                                profileName.setText(profile.getUsername());
                                profileDob.setText(profile.getDob());
                                imageuri = Uri.parse(profile.getImageUrl());

                                if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
                                    loadImageFromStorage(imageuri);
                                }else {
                                    profileImg.setImageResource(R.drawable.baseline_account_circle_24);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed To Load Image!", Toast.LENGTH_SHORT).show());

        }
    }

    private void loadImageFromStorage(Uri imageuri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(imageuri.toString());

        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.baseline_account_circle_24)
                            .into(profileImg);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show();
                });
    }
}