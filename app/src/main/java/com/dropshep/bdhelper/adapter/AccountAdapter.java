package com.dropshep.bdhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.model.AccountModel;
import com.dropshep.bdhelper.myUtils.Replacement;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter <AccountAdapter.ViewHolder> {


    private List<AccountModel> accountList;

    private static OnItemClickListener mListener;

    public AccountAdapter(List<AccountModel> accountList) {
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_payment_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // এখানে context নিতে হলে
        Context context = holder.itemView.getContext();

        AccountModel accountModel = accountList.get(position);
        holder.nameTv.setText(Replacement.getLocalizedMFS(context,accountModel.getAccountName()));  //How to get context
        holder.numberTv.setText(accountModel.getAccountNumber());
        holder.contactTv.setText(accountModel.getContactName());
        holder.primaryTv.setText(accountModel.getIsPrimary());

        //Log.d("Tag-primary", "Data: "+accountModel.getIsPrimary());
        if (accountModel.getIsPrimary().equals("No")){
            holder.primaryTv.setVisibility(View.GONE);
        }
        else {
            holder.primaryTv.setVisibility(View.VISIBLE);
        }

        switch (accountModel.getAccountName()) {
            case "bKash":
                holder.mfsImageIv.setImageResource(R.drawable.ic_mfs_bkash);
                break;
            case "Nagad":
                holder.mfsImageIv.setImageResource(R.drawable.ic_mfs_nagad);
                break;
            case "Rocket":
                holder.mfsImageIv.setImageResource(R.drawable.ic_mfs_rocket);
                break;
            case "uPay":
                holder.mfsImageIv.setImageResource(R.drawable.ic_mfs_upay);
                break;
            default:
                holder.mfsImageIv.setImageResource(R.drawable.ic_mfs_bkash);
                break;
        }

        holder.moreBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenu().add("Edit");
            popupMenu.getMenu().add("Delete");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Edit")) {
                    if (mListener != null) mListener.onShowItemClick(holder.getAdapterPosition());
                    return true;
                } else if (item.getTitle().equals("Delete")) {
                    if (mListener != null) mListener.onDeleteItemClick(holder.getAdapterPosition());
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTv, numberTv, contactTv, primaryTv;
        ImageView mfsImageIv;
        ImageButton moreBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mfsImageIv = itemView.findViewById(R.id.mfsImageIv);
            nameTv = itemView.findViewById(R.id.accountNameTV);
            numberTv = itemView.findViewById(R.id.accountNumberTv);
            contactTv = itemView.findViewById(R.id.contactNameTV);
            primaryTv = itemView.findViewById(R.id.accountStatusTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);

            moreBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(v, position);
                }
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mListener = onItemClickListener;
    }
}
