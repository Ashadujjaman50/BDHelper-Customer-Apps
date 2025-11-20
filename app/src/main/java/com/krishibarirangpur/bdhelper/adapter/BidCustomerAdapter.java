package com.krishibarirangpur.bdhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.myUtils.CommonClass;
import com.krishibarirangpur.bdhelper.myUtils.MyUtils;
import com.krishibarirangpur.bdhelper.myUtils.Replacement;

import java.util.ArrayList;

public class BidCustomerAdapter extends RecyclerView.Adapter<BidCustomerAdapter.HolderViewBid> {

    Context context;
    ArrayList<BidModel> bidModelArrayList;

    private OnBidActionListener listener; // 👈 Interface reference

    // Constructor
    public BidCustomerAdapter(Context context, ArrayList<BidModel> bidModelArrayList, OnBidActionListener listener) {
        this.context = context;
        this.bidModelArrayList = bidModelArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HolderViewBid onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_bid_customer_layout, parent, false);
        return new HolderViewBid(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull HolderViewBid holder, int position) {
        BidModel bidModel = bidModelArrayList.get(position);

        //Bid Info
        String bidId = bidModel.getBidInfo().getBidId();
        String bidAmount = bidModel.getBidInfo().getBidAmount();
        String status = bidModel.getBidInfo().getStatus();
        String userId = bidModel.getBidInfo().getUserId();
        String vendorID = bidModel.getBidInfo().getVendorId();
        String bidTime = bidModel.getBidInfo().getTimestamp();

        //Order Info
        String orderId = bidModel.getOrderInfo().getOrderId();
        String categoryId = bidModel.getOrderInfo().getCategoryId();
        String subCategoryId = bidModel.getOrderInfo().getSubCategoryId();
        String rentTime = bidModel.getOrderInfo().getRentTime();

        //Service Info
        String vehicleModel = bidModel.getServiceInfo().getVehicleModel();
        String vehicleRegNo = bidModel.getServiceInfo().getVehicleRegNo();
        String vehicleCatAndYear = bidModel.getServiceInfo().getVehicleCatAndYear();

        if (categoryId.equals(MyUtils.SKILLED_LABOR_ID)){
            holder.nameTv.setText(CommonClass.getSubCategoryName(subCategoryId));
        }
        else {
            holder.nameTv.setText(vehicleModel);
        }

        if (subCategoryId.equals(MyUtils.SUB_TRUCK_ID)||subCategoryId.equals(MyUtils.SUB_PICKUP_ID) ||
                subCategoryId.equals(MyUtils.SUB_COVERED_VAN_ID)||subCategoryId.equals(MyUtils.SUB_FREEZER_VAN_ID)||
                subCategoryId.equals(MyUtils.SUB_DUMP_TRUCK_ID)||subCategoryId.equals(MyUtils.SUB_TRAILER_ID)||
                subCategoryId.equals(MyUtils.SUB_LOW_BED_ID) || subCategoryId.equals(MyUtils.SUB_CAR_ID) ||
                subCategoryId.equals(MyUtils.SUB_MICROBUS_ID) || subCategoryId.equals(MyUtils.SUB_AMBULANCE_ID)){
            holder.vehicleRegNoTv.setText(Replacement.convertVehicleRegByLocale(context, vehicleRegNo));
            holder.modelAndYear.setText(Replacement.ReplacementNumberInLocal(context, vehicleCatAndYear));
        }
        else if (subCategoryId.equals(MyUtils.SUB_TRACTOR_ID)){
            holder.registerNameTv.setText(R.string.work_type_dot);
            holder.modelAndTypeTv.setText(R.string.work_area_dot);
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            holder.modelAndYear.setText(vehicleCatAndYear);
            holder.transportLl.setVisibility(View.GONE);
            holder.modelAndTypeTv.setText(R.string.tractor_type_dot);
        }
        else if (subCategoryId.equals(MyUtils.HOME_SHIFTING_ID)){
            holder.serviceNameTv.setText(R.string.team_leader_dot);
            holder.registerNameTv.setText(R.string.team_member_dot);
            holder.modelAndTypeTv.setText(R.string.work_area_dot);
            holder.nameTv.setText(vehicleRegNo);
            holder.vehicleRegNoTv.setText(Replacement.ReplacementPersonInLocal(context, vehicleModel));
            holder.modelAndYear.setText(vehicleCatAndYear);
        }
        else {
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            holder.modelAndYear.setText(vehicleCatAndYear);
            if (vehicleRegNo.isEmpty()){
                holder.transportLl.setVisibility(View.GONE);
            }
            else {
                holder.transportLl.setVisibility(View.VISIBLE);
            }
        }

        // 🔹 এখন local format এ দেখাও
        String finalBidAmount = CommonClass.getRoundedTenPercentValue( bidAmount, 10);
        holder.amountTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(finalBidAmount)));


        // 🔹 rentTime যেহেতু millisecond string, তাই long এ convert করো
        long rentMillis = CommonClass.parseMillis(rentTime);
        long todayMillis = CommonClass.getTodayStartMillis();


        // 🔹 প্রথমে check করো bid status কী
        if (status.equals("confirmed") || status.equals("done")) {
            // ✅ যদি confirmed হয় → fixed button
            holder.confirmIcon.setVisibility(View.VISIBLE);
            holder.confirmOrderBtn.setText("Confirmed");
            holder.confirmOrderBtn.setEnabled(false);
            holder.confirmOrderBtn.setBackground(
                    context.getDrawable(R.drawable.custom_button_confirmed)
            );
        }
        else if (status.equals("pending")) {
            // 🕓 যদি pending হয় → তারিখ compare করো
            holder.confirmIcon.setVisibility(View.GONE);
            if (rentMillis >= todayMillis) {
                // ✅ Valid (future or today)
                holder.confirmOrderBtn.setText("Confirm");
                holder.confirmOrderBtn.setEnabled(true);
            }
            else {
                // ❌ Expired
                holder.confirmOrderBtn.setText("Expired");
                holder.confirmOrderBtn.setEnabled(false);
                holder.confirmOrderBtn.setBackground(
                        context.getDrawable(R.drawable.custom_button_disable)
                );
            }
        }

        // 👇 Click Listeners call the interface methods
        holder.callBtn.setOnClickListener(v -> listener.onCallClicked(bidModel));
        holder.confirmOrderBtn.setOnClickListener(v -> listener.onConfirmOrderClicked(bidModel));


    }

    @Override
    public int getItemCount() {
        return bidModelArrayList.size();
    }


    static class HolderViewBid extends RecyclerView.ViewHolder {
        TextView serviceNameTv, nameTv, mobile, rentTimeTv, amountTv, registerNameTv, modelAndTypeTv, modelAndYear, vehicleRegNoTv;
        TextView confirmOrderBtn, callBtn;
        ImageView confirmIcon;
        CardView cardLayout;
        LinearLayout transportLl;
        public HolderViewBid(@NonNull View itemView) {
            super(itemView);
            //
            serviceNameTv = itemView.findViewById(R.id.serviceNameTv);
            nameTv = itemView.findViewById(R.id.nameTV);
            mobile = itemView.findViewById(R.id.mobileNumberTv);
            amountTv = itemView.findViewById(R.id.bidAmount);
            modelAndYear = itemView.findViewById(R.id.modelAndYear);
            registerNameTv = itemView.findViewById(R.id.registerNameTv);
            modelAndTypeTv = itemView.findViewById(R.id.modelAndTypeTv);
            rentTimeTv = itemView.findViewById(R.id.rentTimeTv);
            vehicleRegNoTv = itemView.findViewById(R.id.vehicleRegNoTv);
            confirmIcon = itemView.findViewById(R.id.confirmIcon);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            transportLl = itemView.findViewById(R.id.transportLl);

            callBtn = itemView.findViewById(R.id.callBtn);
            confirmOrderBtn = itemView.findViewById(R.id.confirmOrderBtn);
        }
    }

    // 👇 Interface for handling actions from Activity
    public interface OnBidActionListener {
        void onCallClicked(BidModel bidModel);
        void onConfirmOrderClicked(BidModel bidModel);
    }

}
