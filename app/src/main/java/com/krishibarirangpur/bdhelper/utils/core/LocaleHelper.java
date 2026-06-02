package com.krishibarirangpur.bdhelper.utils.core;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {
    public static final String LANGUAGE_KEY = "app_language";

    public static void setLocale(Context context, String languageCode) {
        if (context == null) return;
        new SharedPrefHelper(context).putString(LANGUAGE_KEY, languageCode);
        updateResources(context, languageCode);
    }

    public static String getLanguage(Context context) {
        if (context == null) return "en";
        return new SharedPrefHelper(context).getString(LANGUAGE_KEY, "en"); // default English
    }

    public static ContextWrapper updateResources(Context context, String language) {
        if (context == null) return null;

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return new ContextWrapper(context.createConfigurationContext(config));
    }
}
