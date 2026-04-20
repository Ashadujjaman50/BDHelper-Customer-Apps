package com.krishibarirangpur.bdhelper.utils.partner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.userActivity.partner.PaymentActivity;

public class DueWarningAlertDialog {

    public static void dueAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_due_warning, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        TextView tvDueDetails = view.findViewById(R.id.tvDueDetails);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnPayNow = view.findViewById(R.id.btnPayNow);

        tvDueDetails.setText(message);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnPayNow.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra("type","payment_history");
            context.startActivity(intent);
            dialog.dismiss();
            ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    public interface OnConfirmListener {
        void onConfirm();
    }

    public static void showDeleteBidDialog(Context context, OnConfirmListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Bid")
                .setMessage("Are you sure you want to delete this bid?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (listener != null) {
                        listener.onConfirm();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show();
    }

    public static void showBidEditDialog(Context context, OnConfirmListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Bid Edit")
                .setMessage("You are allowed to edit this bid only one time.")
                .setPositiveButton("Edit", (dialog, which) -> {
                    if (listener != null) {
                        listener.onConfirm();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show();
    }


}
