package com.krishibarirangpur.bdhelper.utils.partner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.NoticeSend;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.HashMap;
import java.util.Map;

public class BidActionManager {

    // ১. ফোন কল হ্যান্ডেল করার কমন ফাংশন
    public static void handleCall(Context context, BidModel bidModel) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + MyUtils.HOTLINE_NUMBER));
        context.startActivity(intent);
    }

    // ২. বিড ডিলিট করার কমন ফাংশন
    public static void deleteBid(Context context, String bidId, LoadingDialog loadingDialog) {
        DueWarningAlertDialog.showDeleteBidDialog(context, () -> {
            loadingDialog.show();
            FirebaseFirestore.getInstance().collection("bidForOrder")
                    .document(bidId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        loadingDialog.dismiss();
                        MyToast.showShort(context, "Bid deleted successfully.");
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        MyToast.showShort(context, "Failed: " + e.getMessage());
                    });
        });
    }

    // ৩. অর্ডার কনফার্ম করার কমন ফাংশন
    public static void confirmOrder(Context context, BidModel bidModel, String userType, String landArea,
                                    double commissionPercent, LoadingDialog loadingDialog, Runnable onSuccess) {
        try {
            long rentMillis = CommonClass.parseMillis(bidModel.getOrderInfo().getRentTime());
            if (rentMillis < CommonClass.getTodayStartMillis()) {
                MyToast.showShort(context, "⚠️ Already Expired this requirement");
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_order_confirmation, null);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
            dialogView.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
                dialog.dismiss();
                loadingDialog.show();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String bidId = bidModel.getBidInfo().getBidId();
                String orderId = bidModel.getOrderInfo().getOrderId();
                String categoryId = bidModel.getOrderInfo().getCategoryId();

                // কমিশন ক্যালকুলেশন
                String finalBidAmount;
                if (categoryId.equals(MyUtils.HARVESTER_MACHINE_ID)) {
                    finalBidAmount = CommonClass.getRoundedCommissionValue(false, bidModel.getBidInfo().getBidAmount(), landArea);
                } else {
                    finalBidAmount = CommonClass.getRoundedTenPercentValue(bidModel.getBidInfo().getBidAmount(), commissionPercent);
                }

                db.collection("bidForOrder").document(bidId)
                        .update("bidInfo.status", "confirmed")
                        .addOnSuccessListener(aVoid -> {
                            Map<String, Object> update = new HashMap<>();
                            update.put("bidInfo.bidId", bidId);
                            update.put("bidInfo.vendorId", bidModel.getBidInfo().getVendorId());
                            update.put("bidInfo.vendorPrice", Double.valueOf(finalBidAmount));
                            update.put("bidInfo.bidStatus", "confirmed");
                            update.put("orderInfo.status", "confirmed");

                            db.collection("orders").document(orderId).update(update)
                                    .addOnSuccessListener(unused -> {
                                        loadingDialog.dismiss();

                                        sendNotice(context, userType, bidModel.getBidInfo().getVendorId(),
                                                bidModel.getBidInfo().getUserId(), orderId,
                                                bidModel.getOrderInfo().getSubCategoryId(), finalBidAmount, MyUtils.NOTICE_TYPE_BID_CONFIRM);

                                        MyToast.showShort(context, "✅ Order confirmed successfully!");
                                        if (onSuccess != null) onSuccess.run();
                                    });
                        });
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.CENTER);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ৪. কাস্টম নোটিশ পাঠানোর কমন ফাংশন
    public static void sendNotice(Context context, String userType, String userId, String currentUserId,
                                  String orderId, String subCategoryId, String bidAmount, String noticeType) {

        String sender = "partner".equals(userType) ? MyUtils.NOTICE_SENDER_PARTNER : MyUtils.NOTICE_SENDER_CUSTOMER;
        String messageForUser, messageForAdmin;

        if ("partner".equals(userType)) {
            String bnAmount = Replacement.ReplacementNumberEnToBn(bidAmount.replace(".0", ""));
            messageForUser = "একজন " + CommonClass.getSubCategoryName(context, subCategoryId) + " পার্টনার " + bnAmount + "/= বিড করেছেন।";
            messageForAdmin = messageForUser;
        } else {
            messageForUser = "কাস্টমার আপনার করা বিড কনফার্ম করেছেন।";
            messageForAdmin = "অর্ডার " + orderId + " এর বিড কনফার্ম হয়েছে।";
        }

        NoticeSend.sendNotice(sender, noticeType, currentUserId, userId, orderId, messageForUser);
        NoticeSend.sendNotice(sender, noticeType, currentUserId, MyUtils.NOTICE_RECEIVER_ADMIN, orderId, messageForAdmin);
    }


}