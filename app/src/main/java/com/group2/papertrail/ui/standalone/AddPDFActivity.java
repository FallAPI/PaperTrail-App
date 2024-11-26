package com.group2.papertrail.ui.standalone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.group2.papertrail.R;
import com.group2.papertrail.databinding.ActivityAddPdfactivityBinding;
import com.group2.papertrail.util.FilePicker;

public class AddPDFActivity extends AppCompatActivity {

    private ActivityAddPdfactivityBinding binding;
    private FilePicker filePicker;

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

        this.filePicker = new FilePicker(this);

        binding.myToolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.selectFileBtn.setOnClickListener(v -> this.filePicker.openFilePicker());

    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FilePicker.FileMetadata metadata = filePicker.handleActivityResult(requestCode, resultCode, data);

        binding.fileName.setText(metadata.getFileName());
        binding.fileName.setVisibility(View.VISIBLE);
    }
}