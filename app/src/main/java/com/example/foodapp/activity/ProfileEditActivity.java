package com.example.foodapp.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.Calendar;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView profileEditImg;
    EditText profileEditName, profileDOB;
    Button save;

    private Uri imageuri;
    private String selectedDate;

    private boolean isUpdatingImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        profileEditImg = findViewById(R.id.profileEditImg);
        profileEditName = findViewById(R.id.profileEditName);
        profileDOB = findViewById(R.id.profileDOB);
        save = findViewById(R.id.save);
        profileDOB.setOnClickListener(v -> showDatePicker());

        profileEditImg.setOnClickListener(v -> openFileChooser());

        loadImage();

        save.setOnClickListener(v -> saveProfile());
    }
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ProfileEditActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedMonth += 1;
                    selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                    profileDOB.setText(selectedDate);
                },
                year,month,day
        );
        datePickerDialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageuri = data.getData();
            profileEditImg.setImageURI(imageuri);
            isUpdatingImage = true;
        }
    }

    private void saveProfile() {
        String username = profileEditName.getText().toString();
        String dob = selectedDate;

        if (username.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Name and Date of Birth cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            if (imageuri != null && isUpdatingImage) {
                // If user updated the image, save it to Firebase Storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("users/" + user.getUid());

                storageReference.putFile(imageuri).addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateUserProfile(user, db, username, dob, uri.toString());
                    });
                });
            } else {
                // If user did not update the image, retain the old image URL
                db.collection("users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserProfile profile = documentSnapshot.toObject(UserProfile.class);
                        if (profile != null) {
                            String imageUrl = profile.getImageUrl();
                            updateUserProfile(user, db, username, dob, imageUrl); // Use existing image URL
                        }
                    }
                });
            }
        }
    }

    private void updateUserProfile(FirebaseUser user, FirebaseFirestore db, String username, String dob, String imageUrl) {
        UserProfile profile = new UserProfile(username, dob, imageUrl);
        db.collection("users").document(user.getUid()).set(profile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileEditActivity.this, ProfileActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
                    }
                });
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
                                profileEditName.setText(profile.getUsername());
                                profileDOB.setText(profile.getDob());
                                imageuri = Uri.parse(profile.getImageUrl());

                                if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
                                    loadImageFromStorage(imageuri);
                                } else {
                                    profileEditImg.setImageResource(R.drawable.baseline_account_circle_24);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed To Load Profile!", Toast.LENGTH_SHORT).show());
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
                            .into(profileEditImg);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show();
                });
    }
}