package com.group2.papertrail.ui.standalone;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group2.papertrail.dao.CategoryDAO;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.util.FilePicker.FileMetadata;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddPDFActivityViewModel extends ViewModel {
    private MutableLiveData<FileMetadata> fileMetadata;
    private CategoryDAO categoryDAO;
    private MutableLiveData<List<Category>> categories;
    private MutableLiveData<Category> selectedCategory;
    private MutableLiveData<Boolean> isLoading;
    public AddPDFActivityViewModel(Application app) {
        this.fileMetadata = new MutableLiveData<>();
        this.categoryDAO = new CategoryDAO(app.getApplicationContext());
        this.selectedCategory = new MutableLiveData<>();
        this.categories = new MutableLiveData<>(new ArrayList<>());
        this.isLoading = new MutableLiveData<>(false);
        loadCategories();
    }

    public MutableLiveData<FileMetadata> getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata.setValue(fileMetadata);
    }

    public MutableLiveData<Category> getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory.setValue(selectedCategory);
    }

    public void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Category> categoryList = categoryDAO.findAll();
                new Handler(Looper.getMainLooper()).post(() -> {
                    this.categories.setValue(categoryList);
                });
            } catch (Exception e) {
                Log.e("CATEGORY_VM", "Error loading categories", e);
            }
        });
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void selectCategoryById(long id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Category targetCategory = categoryDAO.findById(id);
                new Handler(Looper.getMainLooper()).post(() -> {
                    this.selectedCategory.setValue(targetCategory);
                });
            } catch (Exception e) {
                Log.e("CATEGORY_VM", "Error selecting category", e);
            }
        });
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean state) {
        this.isLoading.setValue(state);
    }
}
