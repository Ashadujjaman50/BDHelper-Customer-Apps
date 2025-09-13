package com.dropshep.bdhelper;

import android.app.Application;
import android.content.Context;

import com.dropshep.bdhelper.FirebaseMessaging.FCMTokenManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class BDHelper extends Application {

    private static BDHelper instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Enable offline persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Update FCM Token if logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FCMTokenManager.updateFCMToken();
        }
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
