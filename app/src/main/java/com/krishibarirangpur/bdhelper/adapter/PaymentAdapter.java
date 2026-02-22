package com.krishibarirangpur.bdhelper.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.PaymentModel;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private final Context context;
    private final List<PaymentModel> paymentList;

    public PaymentAdapter(Context context, List<PaymentModel> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_partner_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentModel payment = paymentList.get(position);

        holder.tvPaymentMethod.setText(payment.getPaymentMethod());
        holder.tvAmount.setText("৳ " + String.format("%.2f", payment.getAmount()));
        holder.tvTrxId.setText(payment.getTrxId().toUpperCase());
        holder.tvAccountNumber.setText(payment.getAccountNumber());
        holder.tvVendorId.setText(payment.getVendorId());

        String status = payment.getStatus();
        // প্রথম অক্ষর বড় হাতের এবং বাকিগুলো ছোট হাতের করার লজিক
        String formattedStatus = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
        holder.tvStatus.setText(formattedStatus);

        // Milliseconds থেকে সুন্দর ডেট ফরম্যাটে রূপান্তর
        holder.tvDate.setText(getFormattedDate(payment.getCreatedAt()));

        // স্ট্যাটাস অনুযায়ী কালার পরিবর্তন (ঐচ্ছিক)
        switch (payment.getStatus()) {
            case "pending":
                holder.tvStatus.setText("PENDING");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
                break;
            case "approved":
                holder.tvStatus.setText("APPROVED");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_approved);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_approved_text));
                break;
            case "rejected":
                holder.tvStatus.setText("REJECTED");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_rejected_text));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (payment.getStatus().equals("pending")) {
                //approvePartnerPayment(payment.getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }








    // টাইমস্ট্যাম্প ফরম্যাট করার মেথড
    private String getFormattedDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaymentMethod, tvAmount, tvTrxId, tvAccountNumber, tvVendorId, tvStatus, tvDate;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTrxId = itemView.findViewById(R.id.tvTrxId);
            tvAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            tvVendorId = itemView.findViewById(R.id.tvVendorId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
