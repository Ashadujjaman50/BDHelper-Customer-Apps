package com.dropshep.bdhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.model.WithdrawRequest;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.FinanceManager;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.Replacement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<WithdrawRequest> withdrawList;

    private OnItemClickListener mListener;

    public WithdrawAdapter(Context context, ArrayList<WithdrawRequest> withdrawList) {
        this.context = context;
        this.withdrawList = withdrawList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_withdraw_request, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawRequest request = withdrawList.get(position);

        String amount= String.valueOf(request.getRequestedAmount());
        String time = String.valueOf(request.getRequestedAt());
        String paymentMethod = request.getPaymentMethod();
        holder.amountTv.setText(Replacement.ReplacementNumberInLocal(context, amount));
        holder.accountNumberTv.setText(request.getAccountNumber());

        // Timestamp → formatted date
        holder.dateTv.setText(CommonClass.formatTime(time, "dd MMMM yyyy, hh:mm a"));

        String status = request.getStatus();
        if (status != null && !status.isEmpty()) {
            // 🔹 Format status text
            status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
            holder.statusTv.setText(status);

            // 🔹 Change text color based on status
            switch (status.toLowerCase()) {
                case "pending":
                    holder.statusTv.setTextColor(context.getColor(R.color.warning)); // 🟠
                    break;
                case "done":
                case "approved":
                    holder.statusTv.setTextColor(context.getColor(R.color.green)); // ✅
                    break;
                case "rejected":
                    holder.statusTv.setTextColor(context.getColor(R.color.red)); // ❌
                    break;
                default:
                    holder.statusTv.setTextColor(context.getColor(R.color.text_secondary)); // ⚪ Default
                    break;
            }
        }

        switch (paymentMethod) {
            case "bKash":
                holder.accountNumberTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mfs_bkash, 0, 0, 0);
                break;
            case "Rocket":
                holder.accountNumberTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mfs_rocket, 0, 0, 0);
                break;
            case "Nagad":
                holder.accountNumberTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mfs_nagad, 0, 0, 0);
                break;
            case "Upay":
                holder.accountNumberTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mfs_upay, 0, 0, 0);
                break;
        }



    }

    @Override
    public int getItemCount() {
        return withdrawList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountTv, statusTv, accountNumberTv, dateTv;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            accountNumberTv = itemView.findViewById(R.id.accountNumberTv);
            dateTv = itemView.findViewById(R.id.dateTv);


            itemView.setOnClickListener(v -> {
                if (listener != null){
                    int position  = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(v, position);
                    }
                }
            });
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
        notifyDataSetChanged();
    }
}
