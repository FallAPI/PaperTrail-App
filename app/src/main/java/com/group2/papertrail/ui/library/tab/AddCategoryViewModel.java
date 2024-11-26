package com.group2.papertrail.ui.library.tab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddCategoryViewModel extends ViewModel {
    private final MutableLiveData<String> categoryName = new MutableLiveData<>();

    public void setCategoryName(String categoryName) {
        this.categoryName.setValue(categoryName);
    }

    public LiveData<String> getCategoryName() {
        return this.categoryName;
    }
}