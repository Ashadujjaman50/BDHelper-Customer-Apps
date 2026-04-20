package com.krishibarirangpur.bdhelper.FirebaseMessaging;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMTokenManager {

    private static final String TAG = "FCMTokenManager";

    public static void updateFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "❌ Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "✅ Current FCM Token: " + token);

                    // Update token in Firestore
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if (auth.getCurrentUser() != null) {
                        String userId = auth.getCurrentUser().getUid();
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId)
                                .update("device_token", token)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "✅ Token updated in Firestore"))
                                .addOnFailureListener(e -> Log.e(TAG, "❌ Failed to update token in Firestore", e));
                    } else {
                        Log.w(TAG, "⚠️ No user logged in. Token not updated.");
                    }
                });
    }

    // Callback Interface
    public interface TokenCallback {
        void onSuccess(String token);
    }

    // Lite Token Fetcher
    public static void getToken(TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    Log.d(TAG, "Token: " + token);
                    callback.onSuccess(token);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Token fetch failed", e);
                });
    }

    public static void updateTokenToFirestore(String token) {
        // ১. বর্তমান লগইন থাকা ইউজারের আইডি নেওয়া
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // ২. Firestore রেফারেন্স (users -> {userId})
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uid);

            // ৩. 'device_token' ফিল্ডটি আপডেট করা
            userRef.update("device_token", token)
                    .addOnSuccessListener(aVoid ->
                            Log.d("FCM_TOKEN", "টোকেন সফলভাবে আপডেট হয়েছে"))
                    .addOnFailureListener(e -> {
                        Log.e("FCM_TOKEN", "টোকেন আপডেট করতে সমস্যা হয়েছে: " + e.getMessage());

                        // যদি ডকুমেন্টটি আগে থেকে তৈরি না থাকে, তবে সেট করতে পারেন
                        // db.collection("users").document(uid).set(data, SetOptions.merge());
                    });
        }
    }



}