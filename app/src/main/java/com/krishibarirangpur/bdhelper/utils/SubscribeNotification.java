package com.krishibarirangpur.bdhelper.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class SubscribeNotification {

    private static final String TAG = "FCMTopicManager";
    private static final String PARTNER_TOPIC = "partners";

    // 🔹 Role অনুযায়ী subscribe/unsubscribe
    public static void handleUserSubscribe(String userRole) {
        if (userRole == null) return;

        if ("partner".equalsIgnoreCase(userRole)) {
            subscribeToPartner();
        } else {
            unsubscribeFromPartner();
        }
    }

    // 🔹 Partner subscribe
    private static void subscribeToPartner() {
        FirebaseMessaging.getInstance().subscribeToTopic(PARTNER_TOPIC)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to partners topic");
                    } else {
                        Log.e(TAG, "Failed to subscribe");
                    }
                });
    }

    // 🔹 Partner unsubscribe
    private static void unsubscribeFromPartner() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(PARTNER_TOPIC)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Unsubscribed from partners topic");
                    } else {
                        Log.e(TAG, "Failed to unsubscribe");
                    }
                });
    }

    // 🔹 Logout হলে সব topic থেকে remove
    public static void unSubscribeFromAll() {
        unsubscribeFromPartner();
        // ভবিষ্যতে অন্য topic থাকলে এখানে add করো
    }
}
