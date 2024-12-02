package com.group2.papertrail;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group2.papertrail.util.RecentlyViewedUtil;
import com.group2.papertrail.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private SharedPreferencesManager sharedPreferencesManager;
    private RecentlyViewedUtil recentlyViewedUtil;
    private MutableLiveData<List<Long>> pdfIds;

    public MainActivityViewModel(Application app) {
        this.sharedPreferencesManager = SharedPreferencesManager.getInstance(app.getApplicationContext());
        this.recentlyViewedUtil = new RecentlyViewedUtil(this.sharedPreferencesManager);
        this.pdfIds = new MutableLiveData<>(new ArrayList<>());
    }
}
