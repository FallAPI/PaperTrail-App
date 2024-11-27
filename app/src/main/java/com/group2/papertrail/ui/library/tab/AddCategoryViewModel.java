package com.group2.papertrail.ui.library.tab;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group2.papertrail.dao.CategoryDAO;
import com.group2.papertrail.model.Category;
import com.group2.papertrail.ui.standalone.AddPDFActivityViewModel;
import com.group2.papertrail.util.Callback;

import java.util.concurrent.Executors;

public class AddCategoryViewModel extends ViewModel {
    private final MutableLiveData<String> categoryName = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final CategoryDAO categoryDAO;
    private final MutableLiveData<AddCategoryResult> addCategoryResult = new MutableLiveData<>();

    public enum AddCategoryResult {
        SUCCESS,
        ERROR,
        EMPTY_NAME
    }

    public AddCategoryViewModel(Context ctx) {
        this.categoryDAO = new CategoryDAO(ctx);
    }

    public void setCategoryName(String categoryName) {
        this.categoryName.setValue(categoryName);
    }
    public void setIsLoading(boolean state) {
        this.isLoading.postValue(state);
    }

    public LiveData<String> getCategoryName() {
        return this.categoryName;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return this.isLoading;
    }

    public MutableLiveData<AddCategoryResult> getAddCategoryResult() {
        return this.addCategoryResult;
    }

    public void addCategory(Callback<AddCategoryResult> callback) {
        String name = categoryName.getValue();

        // Validate category name
        if (name == null || name.trim().isEmpty()) {
            callback.onResult(AddCategoryResult.EMPTY_NAME);
            return;
        }

        setIsLoading(true);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Insert new category
                Category category = new Category(name);
                categoryDAO.insert(category);

                // Ensure callback is called on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    setIsLoading(false);
                    callback.onResult(AddCategoryResult.SUCCESS);
                });
            } catch (Exception e) {
                Log.e("ADD_CATEGORY", "Error adding category", e);

                // Ensure callback is called on main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    setIsLoading(false);
                    callback.onResult(AddCategoryResult.ERROR);
                });
            }
        });
    }
}