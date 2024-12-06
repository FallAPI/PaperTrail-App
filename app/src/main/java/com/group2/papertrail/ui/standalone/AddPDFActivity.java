package com.group2.papertrail.ui.standalone;

import android.content.Intent;
import android.os.Bundle;
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
import com.group2.papertrail.ui.PDFDataManager;
import com.group2.papertrail.util.FilePicker;

public class AddPDFActivity extends AppCompatActivity {

    private ActivityAddPdfactivityBinding binding;
    private AddPDFActivityViewModel addPDFActivityViewModel;
    private PDFDataManager pdfDataManager;
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

        this.pdfDataManager = PDFDataManager.getInstance(getApplicationContext());

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
            var category = (Category) binding.dropdownMenu.getAdapter().getItem(i);
            this.addPDFActivityViewModel.setSelectedCategory(category);
        });

        this.addPDFActivityViewModel.getIsLoading().observe(this, isLoading -> {
            if(isLoading) {
                binding.addPdfProgress.setVisibility(View.VISIBLE);
            } else {
                binding.addPdfProgress.setVisibility(View.INVISIBLE);
            }
        });

        // TODO: handle duplicate pdf
        binding.addBtn.setOnClickListener(view -> {
            this.addPDFActivityViewModel.setIsLoading(true);
            toggleInput(false); // disable input
            this.pdfDataManager.addPDF(
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
                        this.addPDFActivityViewModel.setIsLoading(false);
                        toggleInput(true); // re-enable input
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

        if (resultCode < 0) {
            FilePicker.FileMetadata metadata = filePicker.handleActivityResult(requestCode, resultCode, data);
            addPDFActivityViewModel.setFileMetadata(metadata);
            binding.fileName.setText(metadata.getFileName());
            binding.fileName.setVisibility(View.VISIBLE);
        }

    }

    private void toggleInput(boolean state) {
        binding.addBtn.setEnabled(state);
        binding.selectFileBtn.setEnabled(state);
        binding.menu.setEnabled(state);
        binding.dropdownMenu.setEnabled(state);
    }
}