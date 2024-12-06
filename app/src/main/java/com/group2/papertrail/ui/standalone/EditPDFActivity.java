package com.group2.papertrail.ui.standalone;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.group2.papertrail.R;
import com.group2.papertrail.databinding.ActivityEditPdfactivityBinding;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.model.PDF;
import com.group2.papertrail.ui.PDFDataManager;

public class EditPDFActivity extends AppCompatActivity {

    private ActivityEditPdfactivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPdfactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.myToolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        var intent = getIntent();
        var pdf = (PDF) intent.getParcelableExtra("pdf");

        var editPDFViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new EditPDFViewModel(pdf, getApplication());
            }
        }).get(EditPDFViewModel.class);

        editPDFViewModel.getCategories().observe(this, categories -> {
            var categoriesAdapter = new ArrayAdapter<>(this, R.layout.list_item, categories);
            ((MaterialAutoCompleteTextView) binding.dropdownMenu).setAdapter(categoriesAdapter);
        });

        binding.dropdownMenu.setOnItemClickListener((adapterView, view, i, l) -> {
            var category = (Category) adapterView.getAdapter().getItem(i);
            editPDFViewModel.setSelectedCategory(category);

        });

        editPDFViewModel.getIsLoading().observe(this, isLoading -> {
            if(isLoading) {
                binding.addPdfProgress.setVisibility(View.VISIBLE);
            } else {
                binding.addPdfProgress.setVisibility(View.INVISIBLE);
            }
        });

        binding.pdfTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editPDFViewModel.setPdfTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.pdfDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editPDFViewModel.setPdfDesc(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.pdfTitleField.setText(pdf.getTitle() != null ? pdf.getTitle() : "");
        binding.pdfDescriptionField.setText(pdf.getDescription() != null ? pdf.getDescription() : "");
        binding.dropdownMenu.setText(pdf.getCategory().getName());

        binding.saveBtn.setOnClickListener(v -> {
            editPDFViewModel.editPDF(pdf, result -> {
                switch (result) {
                    case SUCCESS:
                        Toast.makeText(this, "Edit successful", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case ERROR:
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}