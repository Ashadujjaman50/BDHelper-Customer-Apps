package com.krishibarirangpur.bdhelper;

import android.app.Application;
import android.content.Context;

import com.krishibarirangpur.bdhelper.FirebaseMessaging.FCMTokenManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;

public class BDHelper extends Application {

    private static BDHelper instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Apply Theme globally
        ThemeHelper.applyTheme(this);

        // Enable offline persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Update FCM Token if logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FCMTokenManager.updateFCMToken(this);
        }
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
