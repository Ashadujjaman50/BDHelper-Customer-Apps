package com.krishibarirangpur.bdhelper.adapter.partner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.partner.PartnerCommissionUtils;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;

import java.util.ArrayList;

public class BidPartnerAdapter extends RecyclerView.Adapter<BidPartnerAdapter.HolderViewBid> {

    Context context;
    ArrayList<BidModel> bidModelArrayList;
    private BidPartnerListener listener;
    private String landArea;

    public interface BidPartnerListener {
        void onEditClicked(String bidId, String orderId);
        void onDeleteClicked(String bidId, String orderId);
    }

    public void setListener(BidPartnerListener listener) {
        this.listener = listener;
    }

    public BidPartnerAdapter(Context context, ArrayList<BidModel> bidModelArrayList, String landArea) {
        this.context = context;
        this.bidModelArrayList = bidModelArrayList;
        this.landArea = landArea;
    }

    @NonNull
    @Override
    public HolderViewBid onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_bid_order_item, parent, false);
        return new HolderViewBid(view);
    }

    @SuppressLint("SetTextI18n")
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

        // ============ Time Formatting ============
        holder.rentTimeTv.setText(CommonClass.formatTime(rentTime, "dd-MMM-yy  hh:mm aa"));
        holder.bidDateTv.setText(CommonClass.formatTime(bidTime, "dd-MMM-yy  hh:mm aa"));

        //set data
        String finalBidAmount = "";
        switch (categoryId) {
            case MyUtils.HARVESTER_MACHINE_ID -> finalBidAmount = CommonClass.getRoundedCommissionValue(true, bidAmount, landArea);
            case MyUtils.EQUIPMENT_ID -> finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_EQUIPMENT);
            case MyUtils.ROAD_TRANSPORT_ID, MyUtils.RENT_A_CAR_ID -> finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_TRANSPORT);
            case MyUtils.SKILLED_LABOR_ID -> finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_SKILLED_LABOUR);
            case MyUtils.HOME_SHIFTING_ID -> finalBidAmount = CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_HOME_SHIFTING);
        }
        holder.amountTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(finalBidAmount)));

        holder.vehicleModel.setText(vehicleModel);
        holder.vehicleCatAndYearTv.setText(vehicleCatAndYear);
        if (subCategoryId.equals(MyUtils.SUB_TRUCK_ID)||subCategoryId.equals(MyUtils.SUB_PICKUP_ID) ||
                subCategoryId.equals(MyUtils.SUB_COVERED_VAN_ID)|| subCategoryId.equals(MyUtils.SUB_FREEZER_VAN_ID)||
                subCategoryId.equals(MyUtils.SUB_DUMP_TRUCK_ID)||subCategoryId.equals(MyUtils.SUB_TRAILER_ID)||
                subCategoryId.equals(MyUtils.SUB_LOW_BED_ID) || subCategoryId.equals(MyUtils.SUB_CAR_ID) ||
                subCategoryId.equals(MyUtils.SUB_MICROBUS_ID) || subCategoryId.equals(MyUtils.SUB_AMBULANCE_ID)){
            holder.vehicleRegNoTv.setText(Replacement.convertVehicleRegByLocale(context, vehicleRegNo));
        }
        else if (subCategoryId.equals(MyUtils.SUB_DRIVER_ID) || subCategoryId.equals(MyUtils.SUB_PLUMBER_ID) ||
                subCategoryId.equals(MyUtils.SUB_ELECTRICIAN_ID) || subCategoryId.equals(MyUtils.SUB_STOVE_TECHNICIAN_ID) ||
                subCategoryId.equals(MyUtils.SUB_MECHANIC_ID)){
            holder.serviceNameTv.setText(R.string.work_type_dot);
            holder.modelAndTypeTv.setText(R.string.work_area_dot);
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            holder.modelAndTypeTv.setText(vehicleCatAndYear);
        }
        else if (subCategoryId.equals(MyUtils.SUB_TRACTOR_ID)){
            holder.vehicleRegNoTv.setText(vehicleModel);
            holder.serviceNameTv.setText(R.string.tractor_brand_dot);
            holder.vehicleModel.setText(vehicleCatAndYear);
            holder.vehicleCatAndYearTv.setVisibility(View.GONE);
            holder.modelAndTypeTv.setText(R.string.tractor_type_dot);
        }
        else if (subCategoryId.equals(MyUtils.HOME_SHIFTING_ID)){
            holder.serviceNameTv.setText(R.string.team_leader_dot);
            holder.modelAndTypeTv.setText(R.string.team_member_and_area_dot);
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            holder.vehicleModel.setText(Replacement.ReplacementPersonInLocal(context, vehicleModel)+", ");
            holder.vehicleCatAndYearTv.setText(vehicleCatAndYear);
        }
        else {
            holder.vehicleRegNoTv.setText(vehicleRegNo);
            if (vehicleRegNo.isEmpty()){
                holder.vehicleRegisterLL.setVisibility(View.GONE);
            }
            else {
                holder.vehicleRegisterLL.setVisibility(View.VISIBLE);
            }
        }

        //Info Message Hide/Show
        if (position == 0){
            holder.infoMessageTv.setVisibility(View.VISIBLE);
        }
        else {
            holder.infoMessageTv.setVisibility(View.GONE);
        }

        int editCount = bidModel.getBidInfo().getEditCount();
        if (editCount < 5) {
            holder.bidEditLl.setVisibility(View.VISIBLE);
        }
        else {
            holder.bidEditLl.setVisibility(View.GONE);
        }


        holder.bidEditLl.setOnClickListener(v -> {
            if (bidModel.getBidInfo().getStatus().equals("pending")){
                if (listener != null) {
                    listener.onEditClicked(bidId, orderId);
                }
                /*PopupMenu popupMenu = new PopupMenu(context, holder.bidEditLl, Gravity.BOTTOM);
                popupMenu.getMenu().add(0,1, 1,"Edit");
                popupMenu.getMenu().add(0,2, 1,"Delete");

                popupMenu.setOnMenuItemClickListener(item -> {
                    int id =item.getItemId();
                    if (id==1){

                    }
                    else if (id==2){
                        if (listener != null) {
                            listener.onDeleteClicked(bidId, orderId);
                        }
                    }
                    return false;
                });
                popupMenu.show();*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return bidModelArrayList.size();
    }


    static class HolderViewBid extends RecyclerView.ViewHolder {
        TextView serviceNameTv,  bidDateTv, vendorNameTV, mobileNumberTv, amountTv, infoMessageTv, checkedConfirmTv,
                vehicleRegNoTv,modelAndTypeTv, vehicleModel, vehicleCatAndYearTv, rentTimeTv;
        ImageView call;

        LinearLayout vehicleRegisterLL, modelYearLL, bidEditLl;
        public HolderViewBid(@NonNull View itemView) {
            super(itemView);
            //init views
            vehicleRegisterLL = itemView.findViewById(R.id.vehicleRegisterLL);

            modelYearLL = itemView.findViewById(R.id.modelYearLL);
            serviceNameTv = itemView.findViewById(R.id.serviceNameTv);
            bidDateTv = itemView.findViewById(R.id.bidDateTv);
            vendorNameTV = itemView.findViewById(R.id.vendorNameTV);
            mobileNumberTv = itemView.findViewById(R.id.mobileNumberTv);
            amountTv = itemView.findViewById(R.id.bidAmountTv);
            call = itemView.findViewById(R.id.call);
            bidEditLl = itemView.findViewById(R.id.bidEditLl);

            checkedConfirmTv = itemView.findViewById(R.id.checkedConfirmTv);
            vehicleRegNoTv = itemView.findViewById(R.id.vehicleRegNoTv);
            modelAndTypeTv = itemView.findViewById(R.id.modelAndTypeTv);
            vehicleModel = itemView.findViewById(R.id.vehicleModel);
            vehicleCatAndYearTv = itemView.findViewById(R.id.vehicleCatAndYearTv);
            rentTimeTv = itemView.findViewById(R.id.rentTimeTv);

            infoMessageTv = itemView.findViewById(R.id.infoMessageTv);
        }
    }

}
