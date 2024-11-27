package com.group2.papertrail.ui.library.tab;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.group2.papertrail.dao.CategoryDAO;
import com.group2.papertrail.database.DatabaseManager;
import com.group2.papertrail.databinding.FragmentAddCategoryBinding;

public class AddCategoryFragment extends Fragment {

    private AddCategoryViewModel addCategoryViewModel;
    private FragmentAddCategoryBinding binding;

    public static AddCategoryFragment newInstance() {
        return new AddCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addCategoryViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new AddCategoryViewModel(getActivity().getApplicationContext());
            }
        }).get(AddCategoryViewModel.class);

        binding = FragmentAddCategoryBinding.inflate(inflater, container, false);

        binding.categoryValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addCategoryViewModel.setCategoryName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        addCategoryViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progress.setVisibility(View.VISIBLE);
            } else {
                binding.progress.setVisibility(View.INVISIBLE);
            }
        });

        addCategoryViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progress.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        });

        // Observe add category result
        addCategoryViewModel.getAddCategoryResult().observe(getViewLifecycleOwner(), result -> {
            switch (result) {
                case SUCCESS:
                    Toast.makeText(requireContext(), "Category successfully added", Toast.LENGTH_SHORT).show();
                    binding.categoryValue.setText(""); // Clear input
                    break;
                case ERROR:
                    Toast.makeText(requireContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
                    break;
                case EMPTY_NAME:
                    Toast.makeText(requireContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            addCategoryViewModel.addCategory(result -> {
                switch (result) {
                    case SUCCESS:
                        Toast.makeText(requireContext(), "Category successfully added", Toast.LENGTH_SHORT).show();
                        binding.categoryValue.setText(""); // Clear input
                        binding.categoryTxtLayout.setError(null);
                        break;
                    case ERROR:
                        Toast.makeText(requireContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
                        break;
                    case EMPTY_NAME:
                        binding.categoryTxtLayout.setError("Category name cannot be empty");
                        break;
                }
            });
        });

        return binding.getRoot();
    }


}