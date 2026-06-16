package com.krishibarirangpur.bdhelper.FirebaseMessaging;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;

import java.util.HashMap;
import java.util.Map;

public class FCMTokenManager {

    private static final String TAG = "FCMTokenManager";
    private static final String PREF_LAST_FCM_TOKEN = "last_fcm_token";

    public static void updateFCMToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "❌ Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    updateTokenToFirestore(context, token);
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

    public static void updateTokenToFirestore(Context context, String token) {
        if (token == null || token.isEmpty()) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection(FirebaseCollectionTable.USERS).document(uid);

            SharedPrefHelper prefs = new SharedPrefHelper(context);
            String oldToken = prefs.getString(PREF_LAST_FCM_TOKEN, "");

            Map<String, Object> tokenUpdate = new HashMap<>();
            tokenUpdate.put("device_token", FieldValue.delete()); // String ফিল্ড ডিলিট
            tokenUpdate.put("device_tokens", FieldValue.arrayUnion(token)); // নতুন টোকেন অ্যাড

            // যদি আগে কোনো টোকেন সেভ থাকে এবং তা নতুনটার থেকে আলাদা হয়, তবে পুরনোটা রিমুভ করব
            if (!oldToken.isEmpty() && !oldToken.equals(token)) {
                userRef.update("device_tokens", FieldValue.arrayRemove(oldToken))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Old token removed from array"));
            }

            userRef.set(tokenUpdate, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "✅ টোকেন সফলভাবে আপডেট হয়েছে (Array updated, String deleted)");
                        prefs.putString(PREF_LAST_FCM_TOKEN, token);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "❌ টোকেন আপডেট করতে সমস্যা হয়েছে: " + e.getMessage());
                    });
        }
    }
}
