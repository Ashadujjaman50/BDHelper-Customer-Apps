package com.krishibarirangpur.bdhelper.myUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationPermissionHelper {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1001;
    private static final String TOPIC_POST_NOTIFICATION = "post_notification";

    private static final String TAG = "NotifPermHelper";

    public static void requestPermissionAndSubscribe(Activity activity) {
        Log.d(TAG, "requestPermissionAndSubscribe called");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "SDK >= 33, checking notification permission");

            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "Notification permission NOT granted");

                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);

            } else {
                Log.d(TAG, "Notification permission already granted");
                subscribeToFCM(activity);
            }

        } else {
            Log.d(TAG, "SDK < 33, no need to ask for notification permission");
            subscribeToFCM(activity);
        }
    }

    private static void subscribeToFCM(Activity activity) {
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to FCM topic: general");
                        Toast.makeText(activity, "Notification Enable", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "FCM subscription failed", task.getException());
                    }
                });
    }

    public static void showPermissionDialog(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            new AlertDialog.Builder(activity)
                    .setTitle("নোটিফিকেশন অনুমতি")
                    .setMessage("আপনি যদি বিড ও অন্যান্য গুরুত্বপূর্ণ আপডেট পেতে চান, অনুগ্রহ করে নোটিফিকেশন চালু করুন।")
                    .setPositiveButton("অনুমতি দিন", (dialog, which) -> {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                REQUEST_CODE_POST_NOTIFICATIONS);
                    })
                    .setNegativeButton("না এখন নয়", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        else {
            Log.d(TAG, "Notification permission already granted");
            subscribeToFCM(activity);
        }

    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                subscribeToFirebaseTopic(activity);
            } else {
                Toast.makeText(activity, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void subscribeToFirebaseTopic(Activity activity) {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_POST_NOTIFICATION).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity, "Notification enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Notification subscription failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
