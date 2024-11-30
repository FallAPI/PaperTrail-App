package com.group2.papertrail.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.group2.papertrail.dao.UserDAO;
import com.group2.papertrail.databinding.ActivityRegisterBinding;
import com.group2.papertrail.model.User;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding  binding;
    private UserDAO userDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDAO = new UserDAO(this);

        binding.btnRegister.setOnClickListener(v -> registerUser());

    }


    private void registerUser(){
        String username = Objects.requireNonNull(binding.etUsername.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();


        if (validateInput(username, email, password)){
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);

            long result = userDAO.insert(newUser);
            if (result != -1){
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                navigateToLogin();
            }else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void navigateToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateInput(String username, String email, String password){
        if (username.isEmpty()){
            binding.etUsername.setError("Username is required");
            return false;
        }
        if (email.isEmpty() || !isValidEmail(email) ){
            binding.etEmail.setError("Valid email is required");
            return false;
        }
        if (password.isEmpty() || password.length() < 6){
            binding.etPassword.setError("Password must be at least 6 character");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
        return email != null && email.matches(emailRegex);
    }
}
