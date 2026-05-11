package com.krishibarirangpur.bdhelper.utils.core;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {
    private static final String PREF_NAME = "bdhelper_shared_pref";
    private final SharedPreferences preferences;

    public SharedPrefHelper(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    // ✅ Remove specific key
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
