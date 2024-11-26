package com.group2.papertrail.ui.library.tab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TabViewModel extends ViewModel {
    private final MutableLiveData<String> tabName = new MutableLiveData<>();

    public TabViewModel() {

    }

    public void setTabName(String name) {
        this.tabName.setValue(name);
    }

    public LiveData<String> getTabName() {
        return this.tabName;
    }
}