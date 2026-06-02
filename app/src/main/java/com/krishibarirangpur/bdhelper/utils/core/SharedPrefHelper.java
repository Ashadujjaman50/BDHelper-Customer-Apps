package com.krishibarirangpur.bdhelper.utils.core;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {
    private static final String PREF_NAME = "bdhelper_shared_pref";
    private SharedPreferences preferences;

    public SharedPrefHelper(Context context) {
        if (context != null) {
            preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public void putBoolean(String key, boolean value) {
        if (preferences != null) {
            preferences.edit().putBoolean(key, value).apply();
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences != null ? preferences.getBoolean(key, defaultValue) : defaultValue;
    }

    public void putString(String key, String value) {
        if (preferences != null) {
            preferences.edit().putString(key, value).apply();
        }
    }

    public String getString(String key, String defaultValue) {
        return preferences != null ? preferences.getString(key, defaultValue) : defaultValue;
    }

    public void putInt(String key, int value) {
        if (preferences != null) {
            preferences.edit().putInt(key, value).apply();
        }
    }

    public int getInt(String key, int defaultValue) {
        return preferences != null ? preferences.getInt(key, defaultValue) : defaultValue;
    }

    // ✅ Remove specific key
    public void remove(String key) {
        if (preferences != null) {
            preferences.edit().remove(key).apply();
        }
    }

    public void clear() {
        if (preferences != null) {
            preferences.edit().clear().apply();
        }
    }
}
