package com.krishibarirangpur.bdhelper.adapter.customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.sharedActivity.RatingReviewActivity;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.partner.PartnerCommissionUtils;

import java.util.ArrayList;
import java.util.Locale;

public class BidCustomerAdapter extends RecyclerView.Adapter<BidCustomerAdapter.HolderViewBid> {

    private final Context context;
    private final ArrayList<BidModel> bidModelArrayList;
    private final OnBidActionListener listener;
    private String landArea;

    public BidCustomerAdapter(Context context, ArrayList<BidModel> bidModelArrayList, String landArea, OnBidActionListener listener) {
        this.context = context;
        this.bidModelArrayList = bidModelArrayList;
        this.landArea = landArea;
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
        String bidAmount = bidModel.getBidInfo().getBidAmount();
        String status = bidModel.getBidInfo().getStatus();

        //Order Info
        String categoryId = bidModel.getOrderInfo().getCategoryId();
        String subCategoryId = bidModel.getOrderInfo().getSubCategoryId();
        String rentTime = bidModel.getOrderInfo().getRentTime();

        //Service Info
        String vehicleModel = bidModel.getServiceInfo().getVehicleModel();
        String vehicleRegNo = bidModel.getServiceInfo().getVehicleRegNo();
        String vehicleCatAndYear = bidModel.getServiceInfo().getVehicleCatAndYear();

        if (categoryId.equals(MyUtils.SKILLED_LABOR_ID)){
            holder.nameTv.setText(CommonClass.getSubCategoryName(context, subCategoryId));
        } else {
            holder.nameTv.setText(vehicleModel);
        }

        handleVehicleUI(holder, subCategoryId, vehicleRegNo, vehicleCatAndYear, vehicleModel);

        String finalBidAmount = "";
        switch (categoryId) {
            case MyUtils.HARVESTER_MACHINE_ID -> finalBidAmount = CommonClass.getRoundedCommissionValue(true, bidAmount, landArea);
            case MyUtils.EQUIPMENT_ID -> finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_EQUIPMENT);
            case MyUtils.ROAD_TRANSPORT_ID, MyUtils.RENT_A_CAR_ID -> finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_TRANSPORT);
            case MyUtils.SKILLED_LABOR_ID -> finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_SKILLED_LABOUR);
            case MyUtils.HOME_SHIFTING_ID -> finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_HOME_SHIFTING);
        }
        holder.amountTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(finalBidAmount)));

        // Handle Status and Dates
        handleStatusAndDates(holder, status, rentTime);


        CommonClass.getUserRatingInfo(MyUtils.vendorId, bidModel.getBidInfo().getVendorId(), MyUtils.CUSTOMER, (averageRating, totalReviews) -> {
            if (totalReviews > 0) {
                holder.ratingTv.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
            } else {
                holder.ratingTv.setText(String.format(Locale.getDefault(), "%.1f", 5.0));
            }
        });
        //click to goto Review and Rating Page
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RatingReviewActivity.class);
            intent.putExtra(MyUtils.userId, bidModel.getBidInfo().getVendorId());
            intent.putExtra(MyUtils.USER_TYPE, MyUtils.CUSTOMER);
            context.startActivity(intent);
            // Animation remove
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(0, 0);
            }
        });


        // Click Listeners
        holder.callBtn.setOnClickListener(v -> listener.onCallClicked(bidModel));
        holder.confirmOrderBtn.setOnClickListener(v -> listener.onConfirmOrderClicked(bidModel));
    }

    private void handleVehicleUI(HolderViewBid holder, String subCategoryId, String vehicleRegNo, String vehicleCatAndYear, String vehicleModel) {
        if (subCategoryId.equals(MyUtils.SUB_TRUCK_ID)||subCategoryId.equals(MyUtils.SUB_PICKUP_ID) ||
                subCategoryId.equals(MyUtils.SUB_COVERED_VAN_ID)||subCategoryId.equals(MyUtils.SUB_FREEZER_VAN_ID)||
                subCategoryId.equals(MyUtils.SUB_DUMP_TRUCK_ID)||subCategoryId.equals(MyUtils.SUB_TRAILER_ID)||
                subCategoryId.equals(MyUtils.SUB_LOW_BED_ID) || subCategoryId.equals(MyUtils.SUB_CAR_ID) ||
                subCategoryId.equals(MyUtils.SUB_MICROBUS_ID) || subCategoryId.equals(MyUtils.SUB_AMBULANCE_ID)){
            holder.vehicleRegNoTv.setText(Replacement.convertVehicleRegByLocale(context, vehicleRegNo));
            holder.modelAndYear.setText(Replacement.ReplacementNumberInLocal(context, vehicleCatAndYear));
        } else if (subCategoryId.equals(MyUtils.SUB_DRIVER_ID) || subCategoryId.equals(MyUtils.SUB_PLUMBER_ID) ||
                subCategoryId.equals(MyUtils.SUB_ELECTRICIAN_ID) || subCategoryId.equals(MyUtils.SUB_STOVE_TECHNICIAN_ID) ||
                subCategoryId.equals(MyUtils.SUB_MECHANIC_ID)){
            holder.registerNameTv.setText(R.string.work_type_dot);
            holder.modelAndTypeTv.setText(R.string.work_area_dot);
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            holder.modelAndYear.setText(vehicleCatAndYear);
        } else if (subCategoryId.equals(MyUtils.SUB_TRACTOR_ID)){
            holder.registerNameTv.setText(R.string.work_type_dot);
            holder.modelAndTypeTv.setText(R.string.work_area_dot);
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            holder.modelAndYear.setText(vehicleCatAndYear);
            holder.transportLl.setVisibility(View.GONE);
            holder.modelAndTypeTv.setText(R.string.tractor_type_dot);
        } else if (subCategoryId.equals(MyUtils.HOME_SHIFTING_ID)){
            holder.serviceNameTv.setText(R.string.team_leader_dot);
            holder.registerNameTv.setText(R.string.team_member_dot);
            holder.modelAndTypeTv.setText(R.string.work_area_dot);
            holder.nameTv.setText(vehicleRegNo);
            holder.vehicleRegNoTv.setText(Replacement.ReplacementPersonInLocal(context, vehicleModel));
            holder.modelAndYear.setText(vehicleCatAndYear);
        } else if (subCategoryId.equals(MyUtils.SUB_EXCAVATOR_ID)){
            holder.serviceNameTv.setText(R.string.size_dot);
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            holder.modelAndYear.setText(vehicleCatAndYear);
            holder.transportLl.setVisibility(vehicleRegNo.isEmpty() ? View.GONE : View.VISIBLE);
        } else {
            holder.serviceNameTv.setText(R.string.team_leader_dot);
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            holder.modelAndYear.setText(vehicleCatAndYear);
            holder.transportLl.setVisibility(vehicleRegNo.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void handleStatusAndDates(HolderViewBid holder, String status, String rentTime) {
        long rentMillis = CommonClass.parseMillis(rentTime);
        long todayMillis = CommonClass.getTodayStartMillis();

        if (status.equals("confirmed") || status.equals("done")) {
            holder.confirmIcon.setVisibility(View.VISIBLE);
            holder.confirmOrderBtn.setText("Confirmed");
            holder.confirmOrderBtn.setEnabled(false);
            holder.confirmOrderBtn.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_button_confirmed));
        } else if (status.equals("pending")) {
            holder.confirmIcon.setVisibility(View.GONE);
            if (rentMillis >= todayMillis) {
                holder.confirmOrderBtn.setText(R.string.confirm);
                holder.confirmOrderBtn.setEnabled(true);
                holder.confirmOrderBtn.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_button_gradient));
            } else {
                holder.confirmOrderBtn.setText("Expired");
                holder.confirmOrderBtn.setEnabled(false);
                holder.confirmOrderBtn.setBackground(AppCompatResources.getDrawable(context, R.drawable.custom_button_disable));
            }
        }
    }

    @Override
    public int getItemCount() {
        return bidModelArrayList.size();
    }

    static class HolderViewBid extends RecyclerView.ViewHolder {
        TextView ratingTv, serviceNameTv, nameTv, mobile, rentTimeTv, amountTv, registerNameTv, modelAndTypeTv, modelAndYear, vehicleRegNoTv;
        TextView confirmOrderBtn, callBtn;
        ImageView confirmIcon;
        CardView cardLayout;
        LinearLayout transportLl;
        public HolderViewBid(@NonNull View itemView) {
            super(itemView);
            ratingTv = itemView.findViewById(R.id.ratingTv);
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

    public interface OnBidActionListener {
        void onCallClicked(BidModel bidModel);
        void onConfirmOrderClicked(BidModel bidModel);
    }
}
