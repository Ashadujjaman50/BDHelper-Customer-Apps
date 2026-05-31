package com.krishibarirangpur.bdhelper.utils.partner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.utils.Replacement;

public class PartnerAlertDialog {

    public static class BidSummary {
        private final Context context;
        private final String bidPrice;
        private final String commission;
        private final String totalPayable;

        public BidSummary(Context context, String bidPrice, String totalPayable) {
            this.context = context;
            this.bidPrice = bidPrice;
            this.totalPayable = totalPayable;

            // totalPayable থেকে bidPrice বিয়োগ করে কমিশন বের করা হচ্ছে
            String calculatedCommission;
            try {
                double price = Double.parseDouble(bidPrice);
                double total = Double.parseDouble(totalPayable);
                calculatedCommission = String.valueOf((int) (total - price));
            } catch (Exception e) {
                calculatedCommission = "0";
            }
            this.commission = calculatedCommission;
        }

        public void show() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_bid_summary, null);
            builder.setView(view);

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            TextView tvBidPrice = view.findViewById(R.id.tvBidPrice);
            TextView tvCommission = view.findViewById(R.id.tvCommission);
            TextView tvCustomerPay = view.findViewById(R.id.tvCustomerPay);
            MaterialButton btnOk = view.findViewById(R.id.btnOk);

            if (tvBidPrice != null) {
                tvBidPrice.setText(context.getString(R.string.bd_currency_format,
                        Replacement.ReplacementNumberInLocal(context, bidPrice)));
            }
            if (tvCommission != null) {
                tvCommission.setText(context.getString(R.string.bd_currency_format,
                        Replacement.ReplacementNumberInLocal(context, commission)));
            }
            if (tvCustomerPay != null) {
                tvCustomerPay.setText(context.getString(R.string.bd_currency_format,
                        Replacement.ReplacementNumberInLocal(context, totalPayable)));
            }

            if (btnOk != null) {
                btnOk.setOnClickListener(v -> dialog.dismiss());
            }

            dialog.show();
        }
    }
}
