package com.krishibarirangpur.bdhelper.utils.authWidget;

import android.text.TextUtils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.utils.CommonClass;

import java.util.HashMap;
import java.util.Map;

public class ReferralManager {

    public interface Callback {
        void onSuccess();
        void onFailure(Exception e);
    }

    private final FirebaseFirestore db;

    private static final double REF_BONUS = 10;

    public ReferralManager() {
        db = FirebaseFirestore.getInstance();
    }

    // 🚀 Entry point
    public void createUserWithReferral(
            String userId,
            Map<String, Object> userMap,
            String inputReferralCode,
            Callback callback
    ) {
        generateUniqueReferralCode(userMap, () ->
                saveUser(userId, userMap, inputReferralCode, callback)
        );
    }

    // 🔁 Ensure unique referral code (no recursion headache)
    private void generateUniqueReferralCode(Map<String, Object> userMap, Runnable onSuccess) {
        String code = (String) userMap.get("referralCode");

        db.collection("users")
                .whereEqualTo("referralCode", code)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {
                        onSuccess.run();
                    } else {
                        userMap.put("referralCode", CommonClass.generateReferralCode());
                        generateUniqueReferralCode(userMap, onSuccess);
                    }

                });
    }

    // 💾 Save user
    private void saveUser(String newUserId, Map<String, Object> userMap, String inputReferralCode, Callback callback) {

        db.collection("users")
                .document(newUserId)
                .set(userMap)
                .addOnSuccessListener(unused -> {

                    if (!TextUtils.isEmpty(inputReferralCode)) {
                        handleReferralBonus(newUserId, inputReferralCode, callback);
                    } else {
                        callback.onSuccess();
                    }

                })
                .addOnFailureListener(callback::onFailure);
    }

    // 🎁 Referral logic separated
    private void handleReferralBonus(String newUserId, String referralCode, Callback callback) {

        db.collection("users")
                .whereEqualTo("referralCode", referralCode)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        callback.onSuccess();
                        return;
                    }

                    DocumentSnapshot referrer = query.getDocuments().get(0);
                    String referrerId = referrer.getId();

                    double currentBonus = getSafeDouble(referrer, "bonusBalance");

                    // 🎁 Update new user
                    db.collection("users").document(newUserId)
                            .update(
                                    "referredBy", referralCode,
                                    "bonusBalance", REF_BONUS
                            );

                    // 🎁 Update referrer
                    db.collection("users").document(referrerId)
                            .update("bonusBalance", currentBonus + REF_BONUS);

                    // 📄 Log referral
                    saveReferralLog(referrerId, newUserId);

                    callback.onSuccess();

                })
                .addOnFailureListener(callback::onFailure);
    }

    // 📄 Referral log
    private void saveReferralLog(String referrerId, String refereeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("referrerId", referrerId);
        map.put("refereeId", refereeId);
        map.put("bonusGiven", REF_BONUS);
        map.put("createdAt", FieldValue.serverTimestamp());

        db.collection("referrals").add(map);
    }

    // 🛡 Safe double
    private double getSafeDouble(DocumentSnapshot doc, String key) {
        Double value = doc.getDouble(key);
        return value != null ? value : 0;
    }
}