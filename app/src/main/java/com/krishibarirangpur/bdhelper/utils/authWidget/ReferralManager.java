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

    public ReferralManager() {
        db = FirebaseFirestore.getInstance();
    }

    // ✅ Public method (entry point)
    public void createUserWithReferral(
            String userId,
            Map<String, Object> userMap,
            String inputReferralCode,
            Callback callback
    ) {
        ensureUniqueReferralCode(userId, userMap, inputReferralCode, callback);
    }

    // 🔁 Ensure unique referral code
    private void ensureUniqueReferralCode(
            String userId,
            Map<String, Object> userMap,
            String inputReferralCode,
            Callback callback
    ) {
        String code = (String) userMap.get("referralCode");

        db.collection("users")
                .whereEqualTo("referralCode", code)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                        // ✅ Unique
                        saveUserWithReferral(userId, userMap, inputReferralCode, callback);

                    } else {
                        // ❌ Not unique → regenerate
                        userMap.put("referralCode", CommonClass.generateReferralCode());
                        ensureUniqueReferralCode(userId, userMap, inputReferralCode, callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    // 💾 Save user + handle referral
    private void saveUserWithReferral(
            String newUserId,
            Map<String, Object> userMap,
            String inputReferralCode,
            Callback callback
    ) {

        db.collection("users")
                .document(newUserId)
                .set(userMap)
                .addOnSuccessListener(unused -> {

                    // ✅ Handle referral যদি user code দেয়
                    if (!TextUtils.isEmpty(inputReferralCode)) {

                        db.collection("users")
                                .whereEqualTo("referralCode", inputReferralCode)
                                .get()
                                .addOnSuccessListener(query -> {

                                    if (query != null && !query.isEmpty()) {

                                        DocumentSnapshot referrer = query.getDocuments().get(0);
                                        String referrerId = referrer.getId();

                                        Double refBonus = referrer.getDouble("bonusBalance");
                                        double safeBonus = refBonus != null ? refBonus : 0;

                                        // 🎁 Update new user
                                        db.collection("users").document(newUserId)
                                                .update(
                                                        "referredBy", inputReferralCode,
                                                        "bonusBalance", 10
                                                );

                                        // 🎁 Update referrer
                                        db.collection("users").document(referrerId)
                                                .update("bonusBalance", safeBonus + 10);

                                        // 📄 Save referral log
                                        Map<String, Object> referralMap = new HashMap<>();
                                        referralMap.put("referrerId", referrerId);
                                        referralMap.put("refereeId", newUserId);
                                        referralMap.put("bonusGiven", 10);
                                        referralMap.put("createdAt", FieldValue.serverTimestamp());

                                        db.collection("referrals").add(referralMap);
                                    }

                                    if (callback != null) callback.onSuccess();
                                })
                                .addOnFailureListener(callback::onFailure);

                    } else {
                        if (callback != null) callback.onSuccess();
                    }

                })
                .addOnFailureListener(callback::onFailure);
    }
}
