package com.krishibarirangpur.bdhelper.utils.core;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility class to manage application theme (light, dark, system default).
 */
public class ThemeUtil {

    private static final String PREF_NAME = "theme_pref";
    private static final String KEY_THEME_MODE = "theme_mode";

    /**
     * Apply the saved theme when the app starts.
     */
    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int mode = prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Set the theme and save to preferences.
     *
     * @param context Context
     * @param mode AppCompatDelegate mode: MODE_NIGHT_YES / NO / FOLLOW_SYSTEM
     */
    public static void setTheme(Context context, int mode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Get the currently saved theme mode.
     *
     * @return One of: MODE_NIGHT_YES, MODE_NIGHT_NO, or MODE_NIGHT_FOLLOW_SYSTEM
     */
    public static int getTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }


    public static String getThemeName(Context context) {
        int mode = getTheme(context);
        switch (mode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                return "Light";
            case AppCompatDelegate.MODE_NIGHT_YES:
                return "Dark";
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                return "System";
        }
    }



    /**
     * Check if the current theme is dark.
     *
     * @return true if dark mode is active
     */
    public static boolean isDarkMode(Context context) {
        int mode = getTheme(context);
        return mode == AppCompatDelegate.MODE_NIGHT_YES;
    }

    /**
     * Check if the current theme is light.
     *
     * @return true if light mode is active
     */
    public static boolean isLightMode(Context context) {
        int mode = getTheme(context);
        return mode == AppCompatDelegate.MODE_NIGHT_NO;
    }

    /**
     * Check if the current theme is system default.
     *
     * @return true if system theme is used
     */
    public static boolean isSystemDefault(Context context) {
        int mode = getTheme(context);
        return mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }
}
