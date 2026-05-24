package com.krishibarirangpur.bdhelper.utils.partner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;

import java.util.HashMap;
import java.util.Map;

public class PartnerBidEdit {

    private final Context context;
    private final FirebaseFirestore db;
    private final LoadingDialog loadingDialog;

    public PartnerBidEdit(Context context, FirebaseFirestore db, LoadingDialog loadingDialog) {
        this.context = context;
        this.db = db;
        this.loadingDialog = loadingDialog;
    }

    public void startEditProcess(String bidId, String orderId) {
        loadingDialog.show();

        db.collection("orders").document(orderId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                OrderModel order = documentSnapshot.toObject(OrderModel.class);
                if (order != null && "confirmed".equalsIgnoreCase(order.getOrderInfo().getStatus())) {
                    loadingDialog.dismiss();
                    MyToast.showShort(context, "অর্ডার ইতিমধ্যে কনফার্ম করা হয়েছে, আপনি বিড পরিবর্তন করতে পারবেন না।");
                } else {
                    db.collection("bidForOrder").document(bidId).get().addOnSuccessListener(bidSnapshot -> {
                        if (bidSnapshot.exists()) {
                            BidModel bid = bidSnapshot.toObject(BidModel.class);
                            if (bid != null) {
                                int editCount = bid.getBidInfo().getEditCount();
                                if (editCount < 5) {
                                    loadingDialog.dismiss();
                                    showEditBidDialog(bidId, bid.getBidInfo().getBidAmount(), editCount);
                                } else {
                                    loadingDialog.dismiss();
                                    MyToast.showShort(context, "আপনি বিড পরিবর্তনের সর্বোচ্চ সীমা অতিক্রম করেছেন।");
                                }
                            }
                        }
                    }).addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        MyToast.showShort(context, "Error checking bid: " + e.getMessage());
                    });
                }
            } else {
                 loadingDialog.dismiss();
                 MyToast.showShort(context, "অর্ডার পাওয়া যায়নি।");
            }
        }).addOnFailureListener(e -> {
            loadingDialog.dismiss();
            MyToast.showShort(context, "Error checking order: " + e.getMessage());
        });
    }

    private void showEditBidDialog(String bidId, String currentAmount, int editCount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bid_edit_alert, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // বাইরে ক্লিক করলে ডায়ালগ বন্ধ হবে না

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            // ডায়ালগ খোলার সাথে সাথে কিবোর্ড দেখানোর জন্য সফট ইনপুট মোড সেট করা
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        EditText bidAmountEt = view.findViewById(R.id.bidAmountEt);
        TextView currentBidAmountTv = view.findViewById(R.id.currentBidAmountTv);
        TextView cancelBtn = view.findViewById(R.id.cancelBtn);
        Button updateBtn = view.findViewById(R.id.updateBtn);

        currentBidAmountTv.setText(Replacement.ReplacementNumberInLocal(context, currentAmount));
        bidAmountEt.setText(""); // ইনপুট বক্স খালি থাকবে
        bidAmountEt.requestFocus();

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        updateBtn.setOnClickListener(v -> {
            String newAmount = bidAmountEt.getText().toString().trim();
            if (!newAmount.isEmpty() && !newAmount.equals(currentAmount)) {
                updateBid(bidId, newAmount, editCount + 1);
                dialog.dismiss();
            } else if (newAmount.isEmpty()) {
                MyToast.showShort(context, "নতুন পরিমাণ লিখুন।");
            } else {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateBid(String bidId, String newAmount, int newEditCount) {
        loadingDialog.show();
        Map<String, Object> updates = new HashMap<>();
        updates.put("bidInfo.bidAmount", newAmount);
        updates.put("bidInfo.editCount", newEditCount);

        db.collection("bidForOrder").document(bidId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(context, "বিড সফলভাবে আপডেট করা হয়েছে।");
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(context, "আপডেট করতে ব্যর্থ হয়েছে: " + e.getMessage());
                });
    }

}
