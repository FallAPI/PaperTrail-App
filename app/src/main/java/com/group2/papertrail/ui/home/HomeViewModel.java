package com.group2.papertrail.ui.home;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group2.papertrail.util.RecentlyViewedUtil;
import com.group2.papertrail.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeViewModel extends ViewModel {

    private static MutableLiveData<Set<Long>> globalPdfIds;
    private SharedPreferencesManager sharedPreferencesManager;
    private RecentlyViewedUtil recentlyViewedUtil;
    private MutableLiveData<Set<Long>> pdfIds;

    public HomeViewModel(Application app) {
        this.sharedPreferencesManager = SharedPreferencesManager.getInstance(app.getApplicationContext());
        this.recentlyViewedUtil = RecentlyViewedUtil.getInstance(this.sharedPreferencesManager);
        if (globalPdfIds == null) {
            globalPdfIds = new MutableLiveData<>(new HashSet<>());
        }
        this.pdfIds = globalPdfIds;
        setPdfIds(this.recentlyViewedUtil.getPdfIds());
    }

    public static void updateRecentlyViewedPDF(Context context, long pdfID) {
        SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(context);
        RecentlyViewedUtil recentlyViewedUtil = RecentlyViewedUtil.getInstance(preferencesManager);
        recentlyViewedUtil.addPDF(pdfID);
        
        if (globalPdfIds != null) {
            globalPdfIds.setValue(recentlyViewedUtil.getPdfIds());
        }
    }

    public void setPdfIds(Set<Long> pdfIds) {
        this.pdfIds.setValue(pdfIds);
    }

    public MutableLiveData<Set<Long>> getPdfIds() {
        return pdfIds;
    }
}