package com.group2.papertrail.ui.standalone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.group2.papertrail.R;
import com.group2.papertrail.databinding.ActivityAddPdfactivityBinding;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.ui.PDFViewModel;
import com.group2.papertrail.ui.library.LibraryViewModel;
import com.group2.papertrail.util.FilePicker;

public class AddPDFActivity extends AppCompatActivity {

    private ActivityAddPdfactivityBinding binding;
    private AddPDFActivityViewModel addPDFActivityViewModel;
    private PDFViewModel pdfViewModel;
    private FilePicker filePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addPDFActivityViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AddPDFActivityViewModel(getApplication());
            }
        }).get(AddPDFActivityViewModel.class);

        this.pdfViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new PDFViewModel(getApplication());
            }
        }).get(PDFViewModel.class);

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

        this.addPDFActivityViewModel.getCategories().observe(this, categories -> {
            var categoriesAdapter = new ArrayAdapter<>(this, R.layout.list_item, categories);
            ((MaterialAutoCompleteTextView) binding.dropdownMenu).setAdapter(categoriesAdapter);
        });

        binding.dropdownMenu.setOnItemClickListener((adapterView, view, i, l) -> {
            this.addPDFActivityViewModel.selectCategoryById(i+1);
        });

        binding.addBtn.setOnClickListener(view -> {
            this.pdfViewModel.addPDF(
                    this.addPDFActivityViewModel.getFileMetadata().getValue(),
                    this.addPDFActivityViewModel.getSelectedCategory().getValue(),
                    result -> {
                        switch (result) {
                            case SUCCESS:
                                Toast.makeText(this, "PDF Successfuly Imported", Toast.LENGTH_SHORT).show();
                                this.finish();
                                break;
                            case ERROR:
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                break;
                            case EMPTY_FILE:
                                binding.menu.setError("Category is required");
                                break;
                        }
                    }
            );
        });
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
        addPDFActivityViewModel.setFileMetadata(metadata);
        binding.fileName.setText(metadata.getFileName());
        binding.fileName.setVisibility(View.VISIBLE);

    }
}