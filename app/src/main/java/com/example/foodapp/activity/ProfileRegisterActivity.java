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

import com.example.foodapp.R;
import com.example.foodapp.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class ProfileRegisterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView profileIV;
    EditText nameProfileET, dobProfileET;
    Button saveProfileBtn;

    private Uri imageuri;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        profileIV = findViewById(R.id.profileIV);
        nameProfileET = findViewById(R.id.nameProfileET);
        dobProfileET = findViewById(R.id.dobProfileET);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        dobProfileET.setOnClickListener(v -> showDatePicker());

        profileIV.setOnClickListener(v -> openFileChooser());

        loadImage();

        saveProfileBtn.setOnClickListener(v -> saveProfile());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ProfileRegisterActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedMonth += 1;
                    selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                    dobProfileET.setText(selectedDate);
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
            profileIV.setImageURI(imageuri);
        }
    }

    private void saveProfile() {
        String username = nameProfileET.getText().toString();
        String dob = selectedDate;

        if (imageuri != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("users/" + user.getUid());

            storageReference.putFile(imageuri).addOnSuccessListener(taskSnapshot -> {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    UserProfile profile = new UserProfile(username, dob, uri.toString());
                    db.collection("users").document(user.getUid()).set(profile)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ProfileRegisterActivity.this, HomeActivity.class));
                                    finish();
                                }else {
                                    Toast.makeText(this, "Save failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            });
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
                               nameProfileET.setText(profile.getUsername());
                               dobProfileET.setText(profile.getDob());
                               imageuri = Uri.parse(profile.getImageUrl());

                               if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
                                   loadImageFromStorage(imageuri);
                               }else {
                                   profileIV.setImageResource(R.drawable.baseline_account_circle_24);
                               }
                           }
                       }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed To Load Image!", Toast.LENGTH_SHORT).show());

        }
    }

    private void loadImageFromStorage(Uri imageuri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(String.valueOf(this.imageuri));

        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    // Load the image from the Firebase Storage URL and display it in the ImageView
                    profileIV.setImageURI(imageuri);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show();
                });
    }
}