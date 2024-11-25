package com.group2.papertrail.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.papertrail.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup Recently Viewed RecyclerView
        RecyclerView paperView = binding.reclyerViewRecentlyUsed;

        paperView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false  ));

        List<String> recentlyItems = Arrays.asList("item 1", "item 2", "item 3");

        PaperRecylerViewAdapter recentlyAdapter = new PaperRecylerViewAdapter(recentlyItems);
        paperView.setAdapter(recentlyAdapter);

        // Setup Favorites RecyclerView
        RecyclerView favoriteView = binding.recyclerViewFavorites;

        favoriteView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<String> favoritesItems = Arrays.asList("item 1", "item 2", "item 3");

        PaperRecylerViewAdapter favoriteAdapter = new PaperRecylerViewAdapter(favoritesItems);
        favoriteView.setAdapter(favoriteAdapter);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}