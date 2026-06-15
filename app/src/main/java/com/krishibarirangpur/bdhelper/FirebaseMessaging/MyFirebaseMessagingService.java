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
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "bd_helper_notification_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String title = (remoteMessage.getNotification() != null) ? remoteMessage.getNotification().getTitle() : data.get("title");
        String body = (remoteMessage.getNotification() != null) ? remoteMessage.getNotification().getBody() : data.get("body");
        String imageUrl = (remoteMessage.getNotification() != null && remoteMessage.getNotification().getImageUrl() != null) 
                ? remoteMessage.getNotification().getImageUrl().toString() : null;

        if (title != null || body != null) {
            handleNotificationFilter(title, body, imageUrl, data);
        }
    }

    private void handleNotificationFilter(String title, String body, String imageUrl, Map<String, String> data) {
        String type = data.get("type");
        Log.d("FCM_DEBUG", "Received Type: " + type);

        SharedPrefHelper sharedPreferences = new SharedPrefHelper(getApplicationContext());
        String currentUserRole = sharedPreferences.getString(MyUtils.USER_LOGIN_MODE, "");
        Log.d("FCM_DEBUG", "Current User Role: " + currentUserRole);

        // ১. সাধারণ নোটিশ, অফার বা জেনারেল (সবার জন্য)
        if ("OFFER".equals(type) || "NOTICE".equals(type) || "GENERAL".equals(type)) {
            sendNotification(title, body, imageUrl, data);
        }
        // ২. নতুন অর্ডার বা বিড কনফার্মেশন (শুধুমাত্র পার্টনার)
        else if ("NEW_ORDER".equals(type) || "BID_CONFIRMED".equals(type)) {
            if ("partner".equalsIgnoreCase(currentUserRole)) {
                sendNotification(title, body, imageUrl, data);
            }
        }
        // ৩. নতুন বিড অ্যালার্ট (শুধুমাত্র কাস্টমার)
        else if ("NEW_BID".equals(type)) {
            if ("customer".equalsIgnoreCase(currentUserRole)) {
                sendNotification(title, body, imageUrl, data);
            }
        }
        // ৪. অন্যান্য ইনডিভিজুয়াল টাইপ
        /*else {
            sendNotification(title, body, imageUrl, data);
        }*/
    }

    private void sendNotification(String title, String body, String imageUrl, Map<String, String> data) {
        Intent intent = new Intent(this, NotificationRoutingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (data != null) {
            intent.putExtra("orderId", data.get("orderId"));
            intent.putExtra("type", data.get("type"));
            intent.putExtra("categoryId", data.get("categoryId"));
            intent.putExtra("subCategoryId", data.get("subCategoryId"));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent,
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

        // ইমেজ হ্যান্ডলিং (BigPictureStyle)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Bitmap bitmap = getBitmapFromUrl(imageUrl);
            if (bitmap != null) {
                builder.setLargeIcon(bitmap);
                builder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon((Bitmap) null));
            }
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "BD Helper", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(new Random().nextInt(), builder.build());
    }

    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e("FCM_IMAGE", "Error downloading image: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Context (this) সহ মেথডটি কল করা
        FCMTokenManager.updateTokenToFirestore(token);
    }

}