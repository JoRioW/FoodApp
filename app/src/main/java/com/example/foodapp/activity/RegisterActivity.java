package com.example.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText emailRegisterET, passwordRegisterET, cpasswordRegisterET;
    Button registerRBtn, loginRBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        emailRegisterET = findViewById(R.id.emailRegisterET);
        passwordRegisterET = findViewById(R.id.passwordRegisterET);
        cpasswordRegisterET = findViewById(R.id.cpasswordRegisterET);
        registerRBtn = findViewById(R.id.registerRBtn);
        loginRBtn = findViewById(R.id.loginRBtn);

        registerRBtn.setOnClickListener(v -> registerUser());
        loginRBtn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, MainActivity.class)));
    }

    private void registerUser() {
        String email = emailRegisterET.getText().toString();
        String password = passwordRegisterET.getText().toString();
        String confirmpassword = cpasswordRegisterET.getText().toString();


        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email must ends with @gmail.com", Toast.LENGTH_SHORT).show();
            return;
        }else if(!password.equals(confirmpassword)) {
            Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               FirebaseUser user = mAuth.getCurrentUser();
               startActivity(new Intent(RegisterActivity.this, ProfileRegisterActivity.class));
           }else {
               Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show();
           }
        });
    }
}