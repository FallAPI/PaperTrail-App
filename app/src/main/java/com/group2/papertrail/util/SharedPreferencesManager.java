package com.group2.papertrail.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "PaperTrailPrefs";
    private static SharedPreferencesManager instance;
    private final SharedPreferences preferences;

    // Preference Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_RECENTLY_VIEWED_STRING_ARRAY = "recently_viewed_ids";

    private SharedPreferencesManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    // User Methods
    public void saveUserId(long userId) {
        preferences.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public long getUserId() {
        return preferences.getLong(KEY_USER_ID, -1);
    }

    public void setKeyRecentlyViewedStringArray(String recentlyViewedStringArray) {
        preferences.edit().putString(KEY_RECENTLY_VIEWED_STRING_ARRAY, recentlyViewedStringArray).apply();
    }

    public String getKeyRecentlyViewedStringArray() {
        return preferences.getString(KEY_RECENTLY_VIEWED_STRING_ARRAY, "");
    }

    public void clearAllPreferences() {
        preferences.edit().clear().apply();
    }
} 