package com.group2.papertrail.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.group2.papertrail.dao.UserDAO;
import com.group2.papertrail.databinding.ActivityRegisterBinding;
import com.group2.papertrail.model.User;

import java.util.Objects;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding  binding;
    private UserDAO userDAO;
    private boolean usernameErr = false;
    private boolean emailErr = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDAO = new UserDAO(this);

        binding.btnRegister.setOnClickListener(v -> registerUser());

        binding.LoginLink.setOnClickListener(v -> navigateToLogin());

        binding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.TextInputUsername.setError(null);
                usernameErr = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    String username = s.toString();
                    var user = userDAO.findByUsername(username);
                    if (user != null) {
                        runOnUiThread(() -> {
                            binding.TextInputUsername.setError("Username is taken");
                            usernameErr = true;
                        });
                    }
                });
            }
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.TextInputEmail.setError(null);
                emailErr = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    String email = s.toString();
                    var user = userDAO.findByEmail(email);
                    if (user != null) {
                        runOnUiThread(() -> {
                            binding.TextInputEmail.setError("E-mail is already registered");
                            emailErr = false;
                        });
                    }
                });
            }
        });

    }



    private void registerUser(){

        if (usernameErr || emailErr) {
            Toast.makeText(this, "Please fill the fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

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

        // Clear previous errors
        binding.TextInputUsername.setError(null);
        binding.TextInputEmail.setError(null);
        binding.textInputPassword.setError(null);

        if (username.isEmpty()){
            binding.TextInputUsername.setError("Username is required");
            return false;
        }
        if (email.isEmpty() || !isValidEmail(email) ){
            binding.TextInputEmail.setError("Valid email is required");
            return false;
        }
        if (password.isEmpty() || password.length() < 6){
            binding.textInputPassword.setError("Password must be at least 6 character");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
        return email != null && email.matches(emailRegex);
    }
}
