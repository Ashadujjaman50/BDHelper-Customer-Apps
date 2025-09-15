package com.dropshep.bdhelper.FirebaseMessaging;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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



}