package com.krishibarirangpur.bdhelper.utils;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.HashMap;

public class NoticeSend {
    public static void sendNotice( String senderType, String noticeType, String sendUserID, String receivedUserId, String orderId, Object msg) {
        //When Successfully rent post submit then Notice Vendor apps
        String timestamp = "" + System.currentTimeMillis();
        long noticeId = System.currentTimeMillis();

        String TAG = "NoticeSend";

        String title;
        switch (noticeType) {
            case MyUtils.NOTICE_TYPE_BID -> title = "একজন বিড করেছেন";
            case MyUtils.NOTICE_TYPE_BID_CONFIRM -> title = "অভিনন্দন বিড কনফার্ম";
            case MyUtils.NOTICE_TYPE_POST -> title = "নতুন রিকয়ারমেন্ট পোস্ট করা হয়েছে";
            case MyUtils.NOTICE_TYPE_WELCOME -> title = "বিডি হেল্পার অ্যাপে আপনাকে স্বাগতম";
            case MyUtils.NOTICE_TYPE_REFERRAL -> title = "নতুন রেফারেল পেয়েছেন";
            default -> title = noticeType;
        }


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("noticeId", String.valueOf(noticeId + 1000));
        hashMap.put("sendUserId", sendUserID);
        hashMap.put("receivedUserId", receivedUserId);
        hashMap.put("senderType", senderType);
        hashMap.put("orderId", orderId);
        hashMap.put("noticeCategory", noticeType);
        hashMap.put("postDistrict", "");
        hashMap.put("noticeTitle", title);
        hashMap.put("noticeDescription", msg);
        hashMap.put("timestamp", timestamp);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notice")
                .document(timestamp)   // timestamp কে documentId হিসেবে ব্যবহার করছ
                .set(hashMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "sendNotice: Notice Send Successful");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "sendNotice: Notice Send Failed: " + e.getMessage());
                });
    }
}
