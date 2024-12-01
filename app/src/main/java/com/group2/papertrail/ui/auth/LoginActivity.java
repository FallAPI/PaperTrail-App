package com.group2.papertrail.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        if (username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isLoginSuccessful = userDAO.loginUser(username, password);
        if (isLoginSuccessful){
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
