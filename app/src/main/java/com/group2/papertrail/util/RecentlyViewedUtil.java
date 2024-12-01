package com.group2.papertrail.util;

import android.content.Context;
import android.util.Log;

import com.group2.papertrail.model.PDF;
import com.group2.papertrail.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class RecentlyViewedUtil {
    private static final int MAX_ITEMS = 5;
    private static RecentlyViewedUtil instance;
    private final SharedPreferencesManager preferencesManager;

    public RecentlyViewedUtil(SharedPreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    public static RecentlyViewedUtil getInstance(SharedPreferencesManager preferencesManager) {
        if (instance == null) {
            instance = new RecentlyViewedUtil(preferencesManager);
        }
        return instance;
    }

    public void addPDF(PDF pdf) {
        addPDF(pdf.getId());
    }

    public void addPDF(long id) {
        Queue<Long> pdfIds = new LinkedList<>(decodeStringArray(
                this.preferencesManager.getKeyRecentlyViewedStringArray()));
        
        if (pdfIds.size() >= MAX_ITEMS) {
            pdfIds.poll();
        }
        
        pdfIds.offer(id);
        this.preferencesManager.setKeyRecentlyViewedStringArray(encodeLongList(new ArrayList<>(pdfIds)));
    }

    public List<Long> getPdfIds() {
        return new ArrayList<>(new LinkedList<>(decodeStringArray(
                this.preferencesManager.getKeyRecentlyViewedStringArray())));
    }

    private List<Long> decodeStringArray(String arrayString) {
        if (arrayString == null || arrayString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return Arrays.stream(arrayString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            Log.d("RECENTLY_VIEWED_UTIL", "Parsing Failed");
            return new ArrayList<>();
        }
    }

    private String encodeLongList(List<Long> pdfIds) {
        if (pdfIds.isEmpty()) {
            return "";
        }
        return pdfIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
