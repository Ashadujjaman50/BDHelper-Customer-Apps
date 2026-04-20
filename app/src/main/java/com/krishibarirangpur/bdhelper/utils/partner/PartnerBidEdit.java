package com.krishibarirangpur.bdhelper.utils.partner;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.model.OrderModel;
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
                    MyToast.showShort(context, "Order already confirmed, you cannot edit bid.");
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
                                    MyToast.showShort(context, "You have reached the maximum edit limit.");
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
                 MyToast.showShort(context, "Order not found.");
            }
        }).addOnFailureListener(e -> {
            loadingDialog.dismiss();
            MyToast.showShort(context, "Error checking order: " + e.getMessage());
        });
    }

    private void showEditBidDialog(String bidId, String currentAmount, int editCount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Bid");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(currentAmount);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newAmount = input.getText().toString();
            if (!newAmount.isEmpty() && !newAmount.equals(currentAmount)) {
                updateBid(bidId, newAmount, editCount + 1);
            } else if (newAmount.isEmpty()) {
                 MyToast.showShort(context, "Amount cannot be empty.");
            } else {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateBid(String bidId, String newAmount, int newEditCount) {
        loadingDialog.show();
        Map<String, Object> updates = new HashMap<>();
        updates.put("bidInfo.bidAmount", newAmount);
        updates.put("bidInfo.editCount", newEditCount);

        db.collection("bidForOrder").document(bidId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(context, "Bid updated successfully.");
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(context, "Failed to update bid: " + e.getMessage());
                });
    }

}
