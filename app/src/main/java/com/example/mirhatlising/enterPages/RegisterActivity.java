package com.example.mirhatlising.enterPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mirhatlising.MainActivity;
import com.example.mirhatlising.R;
import com.example.mirhatlising.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private static final String linkToDatabase = "https://mirhatlising-de6b2-default-rtdb.europe-west1.firebasedatabase.app";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUpButton.setOnClickListener(v -> {
            String email = binding.registerEmailInputField.getText().toString().trim();
            String password = binding.registerPasswordInputField.getText().toString().trim();
            String username = binding.registerUsernameInputField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(email, password, username);
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        binding.backToLoginButton.setOnClickListener(v-> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

    }

    private void registerUser(String email, String password, String username) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        HashMap<String, String> userInfo = new HashMap<>();
                        userInfo.put("email", email);
                        userInfo.put("username", username);
                        userInfo.put("profileImage", "");
                        userInfo.put("calls", "");
                        FirebaseDatabase.getInstance(linkToDatabase).getReference().child("Users")
                                .child(userId)
                                .setValue(userInfo)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Регистрация успешна", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Ошибка базы данных: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
