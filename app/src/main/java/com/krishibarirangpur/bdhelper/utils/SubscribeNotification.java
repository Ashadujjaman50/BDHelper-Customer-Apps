package com.krishibarirangpur.bdhelper.utils;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

public class SubscribeNotification {
    private static final String TAG = "FCMTopicManager";

    public static void handleUserSubscribe(String userRole) {
        if (userRole == null) return;

        // সবার জন্য কমন টপিক (যেমন: জেনারেল নোটিশ)
        subscribeToTopic(MyUtils.FCM_TOPIC_ALL);

        if (MyUtils.NOTICE_RECEIVER_PARTNER.equalsIgnoreCase(userRole)) {
            subscribeToTopic(MyUtils.FCM_TOPIC_PARTNERS);
            unsubscribeFromTopic(MyUtils.FCM_TOPIC_CUSTOMERS);
        } else if (MyUtils.NOTICE_RECEIVER_CUSTOMER.equalsIgnoreCase(userRole)) {
            subscribeToTopic(MyUtils.FCM_TOPIC_CUSTOMERS);
            unsubscribeFromTopic(MyUtils.FCM_TOPIC_PARTNERS);
        }
    }

    private static void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d(TAG, "Subscribed to: " + topic);
                });
    }

    private static void unsubscribeFromTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d(TAG, "Unsubscribed from: " + topic);
                });
    }

    public static void unSubscribeFromAll() {
        unsubscribeFromTopic(MyUtils.FCM_TOPIC_ALL);
        unsubscribeFromTopic(MyUtils.FCM_TOPIC_PARTNERS);
        unsubscribeFromTopic(MyUtils.FCM_TOPIC_CUSTOMERS);
    }
}
