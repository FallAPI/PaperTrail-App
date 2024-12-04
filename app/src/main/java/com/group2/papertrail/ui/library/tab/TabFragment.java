package com.group2.papertrail.ui.library.tab;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.group2.papertrail.databinding.FragmentTabBinding;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.model.PDF;
import com.group2.papertrail.ui.PDFComponent;
import com.group2.papertrail.ui.PDFComponentAdapter;
import com.group2.papertrail.ui.PDFDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabFragment extends Fragment {

    private static String TAG = "";

    private TabViewModel tabViewModel;
    private FragmentTabBinding binding;
    private PDFDataManager pdfDataManager;
    private PDFComponentAdapter pdfAdapter;

    public static TabFragment newInstance(Category category) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putLong("tabId", category.getId());
        args.putString("tabName", category.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        TAG = "TabFragment " + getArguments().getString("tabName");
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        initializeViewModels();
        setupBinding(inflater, container);
        setupRecyclerView();
        setupObserver();
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        if (pdfDataManager.isDataChanged().getValue()) {
            Log.d(TAG, "Data has changed. Refetching...");
            this.pdfDataManager.loadPDF(result -> {
                switch (result) {
                    case SUCCESS:
                        handlePdfUpdate(pdfDataManager.getPdfFiles().getValue());
                        break;
                    case ERROR:
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
     }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        // Destroy observer
        pdfDataManager.getPdfFiles().removeObserver(this::handlePdfUpdate);
    }

    private void initializeViewModels() {

        this.pdfDataManager = PDFDataManager.getInstance(requireActivity().getApplicationContext());

        tabViewModel = new ViewModelProvider(this).get(TabViewModel.class);
        tabViewModel.setTabName(getArguments().getString("tabName"));
    }

    private void setupBinding(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentTabBinding.inflate(inflater, container, false);
    }

    private void setupRecyclerView() {
        binding.pdfRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        pdfAdapter = new PDFComponentAdapter(new ArrayList<>());
        binding.pdfRecyclerView.setAdapter(pdfAdapter);
    }

    private void setupObserver() {
        pdfDataManager.getPdfFiles().observe(getViewLifecycleOwner(), this::handlePdfUpdate);
    }

    private void handlePdfUpdate(List<PDF> pdfs) {
        Log.d("TabFragment", "GET THE PDF FILES");
        updatePlaceholderVisibility(pdfs.isEmpty());
        
        List<PDFComponent> pdfComponentList = pdfs.stream()
            .filter(pdf -> pdf.getCategory().getId() == getArguments().getLong("tabId"))
            .map(PDFComponent::new)
            .collect(Collectors.toList());
            
        updateAdapter(pdfComponentList);
    }

    private void updatePlaceholderVisibility(boolean isEmpty) {
        binding.placeholderTxt.setText("No PDF in this category");
        binding.placeholderTxt.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void updateAdapter(List<PDFComponent> pdfs) {
        pdfAdapter.updateData(pdfs);
    }
}
