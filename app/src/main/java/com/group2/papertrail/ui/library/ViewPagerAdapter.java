package com.group2.papertrail.ui.library;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.group2.papertrail.model.Category;
import com.group2.papertrail.ui.library.tab.AddCategoryFragment;
import com.group2.papertrail.ui.library.tab.TabFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<Category> tabNames;

    public ViewPagerAdapter(@NonNull Fragment fragment, List<Category> tabNames) {
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

    public void updateTabs(List<Category> newTabsNames){
        this.tabNames.clear();
        this.tabNames.addAll(newTabsNames);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        // Use a unique identifier for each tab
        if (position < tabNames.size() - 1) {
            return tabNames.get(position).getId();
        } else {
            return -1;
        }
    }

    @Override
    public boolean containsItem(long itemId) {
        // Check if the ID exists in the list
        for (Category category : tabNames) {
            if (category.getId() == itemId) {
                return true;
            }
        }
        return itemId == -1;
    }



}
