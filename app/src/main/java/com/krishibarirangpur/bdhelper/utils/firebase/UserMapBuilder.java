package com.krishibarirangpur.bdhelper.utils.firebase;

import com.krishibarirangpur.bdhelper.utils.CommonClass;

import java.util.HashMap;
import java.util.Map;

public class UserMapBuilder {
    public static Map<String, Object> createUserMap(
            String userId,
            String userType,
            String name,
            String email,
            String mobile,
            String district,
            String location,
            String businessName,
            String paymentReceiver,
            String deviceToken,
            String userSignWith,
            String timestamp,
            double rating
    ) {

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("userId", userId);
        userMap.put("userType", userType);          // "customer" or "partner"

        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phone", mobile);

        userMap.put("nidNo", "");
        userMap.put("nidVerify", "false");
        userMap.put("userDob", "");

        userMap.put("district", district);
        userMap.put("location", location);

        // Partner ক্ষেত্র (empty default)
        userMap.put("businessName", businessName);          // only for partner
        userMap.put("rentService", "");                     // only for partner
        userMap.put("paymentReceiver", paymentReceiver);    // only for partner

        userMap.put("verifyStatus", "pending");

        userMap.put("device_token", deviceToken);
        userMap.put("userSignWith", userSignWith);

        userMap.put("userJoinTime", timestamp);
        userMap.put("userLastLogin", timestamp);

        userMap.put("rating", rating);

        // 🎁 Referral fields
        userMap.put("referralCode", CommonClass.generateReferralCode());// Your unique refer code
        userMap.put("referredBy", "");                                  // set later if used
        userMap.put("bonusBalance", 0);                                 // initial bonus


        return userMap;
    }
}
