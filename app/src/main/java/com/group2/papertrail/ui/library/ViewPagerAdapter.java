package com.group2.papertrail.ui.library;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group2.papertrail.ui.library.tab.AddCategoryFragment;
import com.group2.papertrail.ui.library.tab.TabFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<String> tabNames;

    public ViewPagerAdapter(@NonNull Fragment fragment, List<String> tabNames) {
        super(fragment);
        this.tabNames = tabNames;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == tabNames.size() - 1) {
            return AddCategoryFragment.newInstance();
        } else {
            return TabFragment.newInstance(tabNames.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return tabNames.size();
    }


}
