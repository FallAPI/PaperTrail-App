package com.group2.papertrail.ui.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class LibraryViewModel extends ViewModel {

    private final MutableLiveData<List<String>> tabNames;

    public LibraryViewModel() {
        // Retrieve categories from sqlite
        this.tabNames = new MutableLiveData<>(new ArrayList<String>());
        tabNames.setValue(new ArrayList<>());

        addTabNames("School");
        addTabNames("Work");
        addTabNames("Personal");
        addTabNames("Add"); // Add new category placeholder DO NOT REMOVE
    }

    public void addTabNames(String tabName) {
        List<String> tabList = tabNames.getValue();
        if (tabList == null) {
            tabList = new ArrayList<String>();
        } else {
            tabList = new ArrayList<>(tabList);
        }

        tabList.add(tabName);
        tabNames.setValue(tabList);
    }

    public LiveData<List<String>> getTabNames() {
        return tabNames;
    }
}