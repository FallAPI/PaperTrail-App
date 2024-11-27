package com.group2.papertrail.ui.library.tab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.group2.papertrail.util.Callback;

public class AddCategoryViewModel extends ViewModel {
    private final MutableLiveData<String> categoryName = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public void setCategoryName(String categoryName) {
        this.categoryName.setValue(categoryName);
    }

    public void setIsLoading(boolean state) {
        this.isLoading.postValue(state);
    }

    public LiveData<String> getCategoryName() {
        return this.categoryName;
    }

    public LiveData<Boolean> getIsLoading() {
        return this.isLoading;
    }
}