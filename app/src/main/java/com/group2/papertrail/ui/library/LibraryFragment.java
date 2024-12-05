package com.group2.papertrail.ui.library;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.group2.papertrail.R;
import com.group2.papertrail.databinding.FragmentLibraryBinding;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;
    private CategoryViewModel categoryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LibraryViewModel libraryViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new LibraryViewModel(requireActivity().getApplication());
            }
        }).get(LibraryViewModel.class);

        categoryViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new CategoryViewModel(requireActivity().getApplication());
            }
        }).get(CategoryViewModel.class);

        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categoryTabs -> {
            Log.d("CATEGORY_TAB", "tabs updated");
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, categoryTabs);
            binding.viewPager.setAdapter(adapter);
            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                if (position == categoryTabs.size() - 1) {
                    tab.setText("Add");
                } else {
                    tab.setText(categoryTabs.get(position).getName());
                }
            }).attach();

            if (categoryTabs.size() <= 4) {
                binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
            } else {
                binding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }

        });

        categoryViewModel.getIsUpdated().observe(getViewLifecycleOwner(), isUpdated -> {
            if (isUpdated) {
                var tab = binding.tabLayout.getTabAt(categoryViewModel.getCategories().getValue().size() - 2);
                if (tab != null) {
                    tab.select();
                }
                categoryViewModel.setIsUpdated(false);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}