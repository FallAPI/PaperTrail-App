package com.group2.papertrail.ui.library.tab;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.group2.papertrail.databinding.FragmentAddCategoryBinding;

public class AddCategoryFragment extends Fragment {

    private AddCategoryViewModel addCategoryViewModel;
    private FragmentAddCategoryBinding binding;

    public static AddCategoryFragment newInstance() {
        return new AddCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addCategoryViewModel = new ViewModelProvider(this).get(AddCategoryViewModel.class);

        binding = FragmentAddCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }



}