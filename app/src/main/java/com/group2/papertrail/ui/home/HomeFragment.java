package com.group2.papertrail.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.group2.papertrail.ui.PDFDataManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private PDFDataManager pdfDataManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new HomeViewModel(requireActivity().getApplication());
            }
        }).get(HomeViewModel.class);

        this.pdfDataManager = PDFDataManager.getInstance(requireActivity().getApplicationContext());

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        pdfDataManager.loadPDF(result -> {
            if (Objects.requireNonNull(result) == PDFDataManager.PDFOperationResult.ERROR || Objects.requireNonNull(result) == PDFDataManager.PDFOperationResult.EMPTY_FILE) {
                Toast.makeText(requireContext(), "Something went wrong when loading recently viewed", Toast.LENGTH_SHORT).show();
            }
        });

        homeViewModel.getPdfIds().observe(getViewLifecycleOwner(), pdfIds -> {
            if (pdfIds != null && !pdfIds.isEmpty()) {
                pdfDataManager.getPdfFiles().observe(getViewLifecycleOwner(), pdfs -> {
                    var pdfComponentList = pdfs.stream()
                        .filter(pdf -> pdfIds.contains(pdf.getId()))
                        .map(PDFComponent::new)
                        .collect(Collectors.toList());
                    var shallowList = pdfComponentList.subList(0, pdfComponentList.size());
                    Collections.reverse(shallowList);
                    var pdfAdapter = new PDFComponentAdapter(pdfComponentList);
                    binding.reclyerViewRecentlyUsed.setAdapter(pdfAdapter);
                });
            }
        });

        binding.reclyerViewRecentlyUsed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Setup Favorites RecyclerView
        RecyclerView favoriteView = binding.recyclerViewFavorites;

        favoriteView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<String> favoritesItems = Arrays.asList("item 1", "item 2", "item 3");

        PaperRecylerViewAdapter favoriteAdapter = new PaperRecylerViewAdapter(favoritesItems, requireActivity().getApplicationContext());
        favoriteView.setAdapter(favoriteAdapter);


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        var isChanged = pdfDataManager.isDataChanged().getValue();
        if (isChanged) {
            pdfDataManager.loadPDF(result -> {
                if (Objects.requireNonNull(result) == PDFDataManager.PDFOperationResult.ERROR || Objects.requireNonNull(result) == PDFDataManager.PDFOperationResult.EMPTY_FILE) {
                    Toast.makeText(requireContext(), "Something went wrong when loading recently viewed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}