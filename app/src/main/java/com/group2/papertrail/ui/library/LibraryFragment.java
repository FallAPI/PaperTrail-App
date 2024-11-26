package com.group2.papertrail.ui.library;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayoutMediator;
import com.group2.papertrail.R;
import com.group2.papertrail.databinding.FragmentLibraryBinding;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LibraryViewModel libraryViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);

        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<String> tabNames = libraryViewModel.getTabNames().getValue();

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, tabNames);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {

//            View customTab = inflater.inflate(R.layout.tab_item, null);
//
//            ImageView imgView = customTab.findViewById(R.id.tab_icon);
//            TextView tabTxtView = customTab.findViewById(R.id.tab_title);

//            tab.setCustomView(customTab);

            if (position == tabNames.size() - 1) {
                tab.setText("Add");
//                tabTxtView.setText("Add");
//                tab.setIcon(R.drawable.ic_add);
//                imgView.setImageIcon(Icon.createWithResource(getActivity().getApplicationContext(), R.drawable.ic_add));
            } else {
                tab.setText(tabNames.get(position));
//                tabTxtView.setText(tabNames.get(position));
//                imgView.setVisibility(View.GONE);
            }
        }).attach();



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}