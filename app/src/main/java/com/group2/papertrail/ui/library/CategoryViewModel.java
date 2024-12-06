package com.group2.papertrail.ui.library;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.group2.papertrail.dao.CategoryDAO;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.util.Callback;
import com.group2.papertrail.util.SharedPreferencesManager;

import java.util.List;
import java.util.concurrent.Executors;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isUpdated = new MutableLiveData<>(false);
    private SharedPreferencesManager sharedPreferencesManager;
    private final CategoryDAO categoryDAO;

    public enum AddCategoryResult {
        SUCCESS,
        ERROR,
        EMPTY_NAME
    }

    public CategoryViewModel(Application app) {
        this.categoryDAO = new CategoryDAO(app.getApplicationContext());
        this.sharedPreferencesManager = SharedPreferencesManager.getInstance(app.getApplicationContext());
        loadCategories();
    }

    public void loadCategories() {
        setIsLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Category> categoryList = categoryDAO.findAllByUserId(sharedPreferencesManager.getUserId());
                new Handler(Looper.getMainLooper()).post(() -> {
                    categoryList.add(new Category("Add", 0)); // DO NOT REMOVE
                    categories.setValue(categoryList);
                    setIsLoading(false);
                    setIsUpdated(true);
                });
            } catch (Exception e) {
                Log.e("CATEGORY_VM", "Error loading categories", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    setIsLoading(false);
                });
            }
        });
    }

    public void addCategory(String categoryName, Callback<AddCategoryResult> callback) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            callback.onResult(AddCategoryResult.EMPTY_NAME);
            return;
        }

        setIsLoading(true);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Category category = new Category(categoryName.trim(), sharedPreferencesManager.getUserId());
                categoryDAO.insert(category);
                loadCategories(); // Reload categories after insertion

                new Handler(Looper.getMainLooper()).post(() -> {
                    setIsLoading(false);
                    callback.onResult(AddCategoryResult.SUCCESS);
                });
            } catch (Exception e) {
                Log.e("CATEGORY_VM", "Error adding category", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    setIsLoading(false);
                    callback.onResult(AddCategoryResult.ERROR);
                });
            }
        });
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    private void setIsLoading(boolean loading) {
        new Handler(Looper.getMainLooper()).post(() ->
                isLoading.setValue(loading)
        );
    }

    public MutableLiveData<Boolean> getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(boolean state) {
        isUpdated.setValue(state);
    }
}