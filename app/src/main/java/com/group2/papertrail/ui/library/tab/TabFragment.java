package com.group2.papertrail.ui.library.tab;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.group2.papertrail.R;
import com.group2.papertrail.databinding.FragmentLibraryBinding;
import com.group2.papertrail.databinding.FragmentTabBinding;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.ui.PDFComponent;
import com.group2.papertrail.ui.PDFComponentAdapter;
import com.group2.papertrail.ui.PDFViewModel;
import com.group2.papertrail.ui.library.LibraryViewModel;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TabFragment extends Fragment {

    private TabViewModel tabViewModel;
    private FragmentTabBinding binding;
    private PDFViewModel pdfViewModel;

    public static TabFragment newInstance(Category category) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putLong("tabId", category.getId());
        args.putString("tabName", category.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        pdfViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new PDFViewModel(requireActivity().getApplication());
            }
        }).get(PDFViewModel.class);

        binding = FragmentTabBinding.inflate(inflater, container, false);
        tabViewModel = new ViewModelProvider(this).get(TabViewModel.class);

        tabViewModel.setTabName(getArguments().getString("tabName"));

        binding.placeholderTxt.setText(tabViewModel.getTabName().getValue());

        binding.pdfRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        this.pdfViewModel.loadPDF(getArguments().getLong("tabId"), result -> {
            this.pdfViewModel.getPdfFiles().observe(getViewLifecycleOwner(), pdfs -> {
                var pdfComponentList = pdfs.stream().map(PDFComponent::new).collect(Collectors.toList()); // same as return new PDFComponent(pdf)
                var pdfAdapter = new PDFComponentAdapter(pdfComponentList);
                binding.pdfRecyclerView.setAdapter(pdfAdapter);
            });

            switch (result) {
                case ERROR:
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    break;
            }
        });


        return binding.getRoot();
    }

}