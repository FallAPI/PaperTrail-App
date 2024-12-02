package com.group2.papertrail.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.papertrail.databinding.FragmentHomeBinding;
import com.group2.papertrail.ui.PDFComponent;
import com.group2.papertrail.ui.PDFComponentAdapter;
import com.group2.papertrail.ui.PDFViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private PDFViewModel pdfViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new HomeViewModel(requireActivity().getApplication());
            }
        }).get(HomeViewModel.class);

        pdfViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new PDFViewModel(requireActivity().getApplication());
            }
        }).get(PDFViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        homeViewModel.getPdfIds().observe(getViewLifecycleOwner(), pdfIds -> {
            pdfViewModel.loadPDF(pdfIds.toArray(new Long[0]), result -> {
                if (Objects.requireNonNull(result) == PDFViewModel.PDFOperationResult.ERROR) {
                    Toast.makeText(requireContext(), "Something went wrong when loading recently viewed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.reclyerViewRecentlyUsed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false  ));

        pdfViewModel.getPdfFiles().observe(getViewLifecycleOwner(), pdfs -> {
            var pdfComponentList = pdfs.stream().map(PDFComponent::new).collect(Collectors.toList()); // same as return new PDFComponent(pdf)
            var pdfAdapter = new PDFComponentAdapter(pdfComponentList);
            binding.reclyerViewRecentlyUsed.setAdapter(pdfAdapter);
        });

        // Setup Favorites RecyclerView
        RecyclerView favoriteView = binding.recyclerViewFavorites;

        favoriteView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<String> favoritesItems = Arrays.asList("item 1", "item 2", "item 3");

        PaperRecylerViewAdapter favoriteAdapter = new PaperRecylerViewAdapter(favoritesItems, requireActivity().getApplicationContext());
        favoriteView.setAdapter(favoriteAdapter);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}