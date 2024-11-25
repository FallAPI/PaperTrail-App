package com.group2.papertrail.ui.standalone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.group2.papertrail.R;
import com.group2.papertrail.databinding.ActivityAddPdfactivityBinding;

public class AddPDFActivity extends AppCompatActivity {

    ActivityAddPdfactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddPdfactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.myToolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}