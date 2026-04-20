package com.krishibarirangpur.bdhelper.FirebaseMessaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.authentication.LoginActivity;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "bd_helper_notification_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = (remoteMessage.getNotification() != null) ? remoteMessage.getNotification().getTitle() : data.get("title");
        String body = (remoteMessage.getNotification() != null) ? remoteMessage.getNotification().getBody() : data.get("body");

        if (title != null || body != null) {
            handleNotificationFilter(title, body, data);
        }
    }

    private void handleNotificationFilter(String title, String body, Map<String, String> data) {
        String type = data.get("type");
        SharedPrefHelper sharedPreferences = new SharedPrefHelper(getApplicationContext());
        String currentUserRole = sharedPreferences.getString("user_role", "");

        // অ্যাডমিন থেকে আসা অফার বা নোটিশ সবার জন্য (টপিক অনুযায়ী অলরেডি ফিল্টার হয়ে এসেছে)
        if ("OFFER".equals(type) || "NOTICE".equals(type)) {
            sendNotification(title, body, data, currentUserRole);
        }
        else if ("NEW_ORDER".equals(type)) {
            if ("partner".equalsIgnoreCase(currentUserRole)) {
                sendNotification(title, body, data, currentUserRole);
            }
        }
        else if ("NEW_BID".equals(type)) {
            if ("customer".equalsIgnoreCase(currentUserRole)) {
                sendNotification(title, body, data, currentUserRole);
            }
        }
    }

    private void sendNotification(String title, String body, Map<String, String> data, String target) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (data != null) {
            intent.putExtra("orderId", data.get("orderId"));
            intent.putExtra("noti_type", data.get("type"));
            intent.putExtra("target", target);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "BD Helper", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(new Random().nextInt(), builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // আপনার মেথডটি কল করা
        FCMTokenManager.updateTokenToFirestore(token);
    }

}