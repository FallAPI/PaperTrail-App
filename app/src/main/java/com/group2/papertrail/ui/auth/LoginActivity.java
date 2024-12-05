package com.group2.papertrail.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.group2.papertrail.util.SharedPreferencesManager;
import com.group2.papertrail.MainActivity;
import com.group2.papertrail.dao.UserDAO;
import com.group2.papertrail.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private UserDAO userDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDAO = new UserDAO(this);


        binding.btnLogin.setOnClickListener(v -> LoginProcess());

        binding.RegisterLink.setOnClickListener(v -> navigateTo(RegisterActivity.class));
    }

    private void LoginProcess(){
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Clear previous errors
        binding.TextInputUsername.setError(null);
        binding.TextInputPassword.setError(null);

        boolean hasError = false;

        // Validate username
        if (username.isEmpty()) {
            binding.TextInputUsername.setError("Username is required");
            hasError = true;
        }

        // Validate password
        if (password.isEmpty()) {
            binding.TextInputPassword.setError("Password is required");
            hasError = true;
        }

        if (hasError) {
            return; // Stop processing if there's an error
        }
        long userId = userDAO.loginUser(username, password);
        if (userId != -1){
            //save to shared preferences
            SharedPreferencesManager.getInstance(this).saveUserId(userId);
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            navigateTo(MainActivity.class);
        }else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateTo(Class<?> targetActivity){
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        finish();
    }

}
