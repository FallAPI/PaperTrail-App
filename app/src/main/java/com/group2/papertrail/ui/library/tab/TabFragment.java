package com.group2.papertrail.ui.library.tab;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.group2.papertrail.R;
import com.group2.papertrail.databinding.FragmentLibraryBinding;
import com.group2.papertrail.databinding.FragmentTabBinding;
import com.group2.papertrail.ui.library.LibraryViewModel;

public class TabFragment extends Fragment {

    private TabViewModel mViewModel;
    private FragmentTabBinding binding;

    public static TabFragment newInstance(String tabName) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putString("tabName", tabName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTabBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TabViewModel.class);
        // TODO: Use the ViewModel
    }

}