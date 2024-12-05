package com.group2.papertrail.util;

import android.content.Context;
import android.util.Log;

import com.group2.papertrail.model.PDF;
import com.group2.papertrail.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
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
        LinkedHashSet<Long> pdfIds = new LinkedHashSet<>(decodeStringArray(
                this.preferencesManager.getKeyRecentlyViewedStringArray()));
        
        if (pdfIds.contains(id)) {
            pdfIds.remove(id);
        } else if (pdfIds.size() >= MAX_ITEMS) {
            Iterator<Long> iterator = pdfIds.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        
        pdfIds.add(id);
        this.preferencesManager.setKeyRecentlyViewedStringArray(encodeLongList(new ArrayList<>(pdfIds)));
    }

    public Set<Long> getPdfIds() {
        return new HashSet<>(new LinkedHashSet<>(decodeStringArray(
                this.preferencesManager.getKeyRecentlyViewedStringArray())));
    }

    private Set<Long> decodeStringArray(String arrayString) {
        if (arrayString == null || arrayString.trim().isEmpty()) {
            return new HashSet<>();
        }

        try {
            return Arrays.stream(arrayString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
        } catch (NumberFormatException e) {
            Log.d("RECENTLY_VIEWED_UTIL", "Parsing Failed");
            return new HashSet<>();
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
