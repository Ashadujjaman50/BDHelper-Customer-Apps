package com.krishibarirangpur.bdhelper.FirebaseMessaging;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    public static void updateFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "❌ Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    updateTokenToFirestore(token);
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
        if (token == null || token.isEmpty()) return;

        // ১. বর্তমান লগইন থাকা ইউজারের আইডি নেওয়া
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // ২. Firestore রেফারেন্স (users -> {userId})
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection(FirebaseCollectionTable.USERS).document(uid);


            // ৩. 'device_token' ফিল্ড ডিলিট করা (রেডান্ডেন্সি কমাতে) এবং অ্যারে আপডেট করা
            Map<String, Object> tokenUpdate = new HashMap<>();
            tokenUpdate.put("device_token", FieldValue.delete()); // String ফিল্ডটি রিমুভ করা হচ্ছে
            tokenUpdate.put("device_tokens", FieldValue.arrayUnion(token));

            userRef.set(tokenUpdate, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "✅ টোকেন সফলভাবে আপডেট হয়েছে (Array updated, String deleted)");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "❌ টোকেন আপডেট করতে সমস্যা হয়েছে: " + e.getMessage());
                    });
        } else {
            Log.w(TAG, "⚠️ No user logged in. Token not updated to Firestore.");
        }
    }



}