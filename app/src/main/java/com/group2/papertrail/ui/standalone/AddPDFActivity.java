package com.group2.papertrail.ui.standalone;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.group2.papertrail.MainActivity;
import com.group2.papertrail.R;
import com.group2.papertrail.databinding.ActivityAddPdfactivityBinding;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.ui.PDFDataManager;
import com.group2.papertrail.util.FilePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class AddPDFActivity extends AppCompatActivity {

    private ActivityAddPdfactivityBinding binding;
    private AddPDFActivityViewModel viewModel;
    private final String TAG = "AddPDFActivity";
    private PDFDataManager pdfDataManager;
    private FilePicker filePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewBinding();
        initializeViewModel();
        setupToolbar();
        setupViews();
        observeViewModelChanges();

        var intent = getIntent();
        var action = intent.getAction();
        var type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if("application/pdf".equals(type)) {
                handleSendPdf(intent);
            } else {
                showToast("File type is not supported");
            }
        }

    }

    private void handleSendPdf(Intent intent) {
        Uri pdfUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (pdfUri != null) {
            takePersistableUriPermission(pdfUri);

            var metadata = filePicker.getFileMetadata(pdfUri);
            viewModel.setHasFileContent(true);
            handleMetadata(metadata);

        } else {
            showToast("URI not valid");
        }
    }

    private void initializeViewBinding() {
        binding = ActivityAddPdfactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AddPDFActivityViewModel(getApplication());
            }
        }).get(AddPDFActivityViewModel.class);
        pdfDataManager = PDFDataManager.getInstance(getApplicationContext());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.myToolbar.setNavigationOnClickListener(v -> 
            getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupViews() {
        filePicker = new FilePicker(this);
        binding.selectFileBtn.setOnClickListener(v -> filePicker.openFilePicker());
        binding.addBtn.setOnClickListener(view -> handleAddPdfClick());
        setupCategoryDropdown();
    }

    private void setupCategoryDropdown() {
        binding.dropdownMenu.setOnItemClickListener((adapterView, view, i, l) -> {
            Category category = (Category) binding.dropdownMenu.getAdapter().getItem(i);
            viewModel.setSelectedCategory(category);
        });
    }

    private void observeViewModelChanges() {
        viewModel.getCategories().observe(this, this::updateCategoriesAdapter);
        viewModel.getIsLoading().observe(this, this::updateLoadingState);
    }

    private void updateCategoriesAdapter(List<Category> categories) {
        ArrayAdapter<Category> categoriesAdapter = new ArrayAdapter<>(
            this, R.layout.list_item, categories);
        ((MaterialAutoCompleteTextView) binding.dropdownMenu).setAdapter(categoriesAdapter);
    }

    private void updateLoadingState(boolean isLoading) {
        binding.addPdfProgress.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    private void handleAddPdfClick() {
        viewModel.setIsLoading(true);
        toggleInput(false);
        
        pdfDataManager.addPDF(
            viewModel.getFileMetadata().getValue(),
            viewModel.getSelectedCategory().getValue(),
            viewModel.hasFileContent(),
            this::handlePdfAddResult
        );
    }

    private void handlePdfAddResult(PDFDataManager.PDFOperationResult result) {
        switch (result) {
            case SUCCESS:
                showToast("PDF Successfully Imported");
                if (isTaskRoot()) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case ERROR:
                showToast("Something went wrong");
                break;
            case EMPTY_FILE:
                binding.menu.setError("Category is required");
                break;
            case DUPLICATE:
                handleDuplicatePdf();
                break;
        }
        viewModel.setIsLoading(false);
        toggleInput(true);
    }

    private void handleDuplicatePdf() {
        binding.fileName.setTextColor(Color.RED);
        String categoryName = viewModel.getSelectedCategory().getValue().getName();
        showToast("PDF already exists in " + categoryName);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
            handleMetadata(metadata);
        }

    }

    private void handleMetadata(FilePicker.FileMetadata metadata) {
        viewModel.setFileMetadata(metadata);
        binding.fileName.setText(metadata.getFileName());
        binding.fileName.setVisibility(View.VISIBLE);
    }

    private void toggleInput(boolean state) {
        binding.addBtn.setEnabled(state);
        binding.selectFileBtn.setEnabled(state);
        binding.menu.setEnabled(state);
        binding.dropdownMenu.setEnabled(state);
    }

    private void takePersistableUriPermission(Uri uri) {
        try {
            int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

            // Take persistable permission if the URI allows it
            if (getIntent() != null && (getIntent().getFlags() & Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION) != 0) {
                getContentResolver().takePersistableUriPermission(uri, flags);
                Log.d(TAG, "Persistable URI permission taken for: " + uri);
            } else {
                Log.d(TAG, "URI does not support persistable permissions: " + uri);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to take persistable URI permission for: " + uri, e);
        }
    }
}