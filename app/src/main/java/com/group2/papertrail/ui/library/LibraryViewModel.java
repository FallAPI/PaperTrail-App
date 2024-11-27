package com.group2.papertrail.ui.library;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group2.papertrail.dao.CategoryDAO;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.ui.library.tab.AddCategoryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LibraryViewModel extends ViewModel {

    private final MutableLiveData<List<Category>> categoryTabs;
    private final CategoryDAO categoryDAO;


    public LibraryViewModel(Application app) {
        // Retrieve categories from sqlite
        this.categoryTabs = new MutableLiveData<>(new ArrayList<Category>());
        this.categoryDAO = new CategoryDAO(app.getApplicationContext());

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Ensure callback is called on main thread
                List<Category> categories = this.categoryDAO.findAll();
                new Handler(Looper.getMainLooper()).post(() -> {
                    this.categoryTabs.setValue(categories);
                });
            } catch (Exception e) {
                // Ensure callback is called on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                });
            }
        });


//        addTab("School");
//        addTab("Work");
//        addTab("Personal");
        addTab("Add"); // Add new category placeholder DO NOT REMOVE
    }

    public void addTab(String tabName) {
        List<Category> tabList = this.categoryTabs.getValue();
        if (tabList == null) {
            tabList = new ArrayList<Category>();
        } else {
            tabList = new ArrayList<>(tabList);
        }

        var newTab = new Category(tabName);

        tabList.add(newTab);
        this.categoryTabs.setValue(tabList);
    }

    public LiveData<List<Category>> getCategoryTabs() {
        return categoryTabs;
    }
}