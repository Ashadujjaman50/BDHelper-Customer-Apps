package com.krishibarirangpur.bdhelper.adapter.partner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.BidModel;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.partner.PartnerCommissionUtils;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.UIHelper;

import java.util.ArrayList;

public class AdapterBidDetail extends RecyclerView.Adapter<AdapterBidDetail.HolderViewBidDetail> {

    private static final int TRANSPORT = 0;
    private static final int EQUIPMENT = 1;
    private static final int HOME_SHIFTING = 2;
    private static final int SKILLED_LABOUR = 3;

    private final Context context;
    private final ArrayList<BidModel> bidModelArrayList;
    private final OnBidDetailActionListener listener;

    public AdapterBidDetail(Context context, ArrayList<BidModel> bidModelArrayList, OnBidDetailActionListener listener) {
        this.context = context;
        this.bidModelArrayList = bidModelArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HolderViewBidDetail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = switch (viewType) {
            case TRANSPORT -> R.layout.row_bid_detail_transport;
            case HOME_SHIFTING -> R.layout.row_bid_detail_home_shifting;
            case EQUIPMENT -> R.layout.row_bid_detail_equipment;
            default -> R.layout.row_bid_detail_skilled_labour;
        };
        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        return new HolderViewBidDetail(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HolderViewBidDetail holder, @SuppressLint("RecyclerView") int position) {
        //get data
        BidModel bidModel = bidModelArrayList.get(position);

        String bidAmount = bidModel.getBidInfo().getBidAmount();
        String bidStatus = bidModel.getBidInfo().getStatus();
        String bidTime = bidModel.getBidInfo().getTimestamp();

        //Order Info
        String orderId = bidModel.getOrderInfo().getOrderId();
        String categoryId = bidModel.getOrderInfo().getCategoryId();
        String subCategoryId = bidModel.getOrderInfo().getSubCategoryId();
        String rentTime = bidModel.getOrderInfo().getRentTime();

        // Time Formatting
        holder.rentTimeTv.setText(CommonClass.millisToTimeWithLocal(context, rentTime));
        holder.bidDateTv.setText(CommonClass.formatTime(bidTime, "dd-MMMM-yy  hh:mm aa"));

        holder.orderIdTv.setText(context.getString(R.string.order_no_dot) + " " + orderId);
        holder.postImage.setImageResource(CommonClass.getIconForSubCategory(subCategoryId));
        holder.postNameTv.setText(CommonClass.getSubCategoryName(context, subCategoryId));

        //get order info by OrderID
        CommonClass.getOrderInfoById(orderId, new CommonClass.FirestoreOrderCallback() {
            @Override
            public void onSuccess(ArrayList<OrderModel> orderList) {
                if (orderList.isEmpty()) return;

                OrderModel order = orderList.get(0);
                UIHelper.bindAddress(holder.loadLocationTv, holder.loadAreaTv, order.getRouteInfo().getLoad());
                UIHelper.bindAddress(holder.unLoadLocationTv, holder.unLoadAreaTv, order.getRouteInfo().getUnload());
                UIHelper.bindAddress(holder.locationNameTv, holder.locationAreaTv, order.getRouteInfo().getRentLocation());

                holder.postDescriptionTv.setText(order.getSpecInfo().getDesc());

                String quantity = order.getSpecInfo().getQuantity();
                String capacity = order.getSpecInfo().getCapacity();
                String types = order.getSpecInfo().getTypes();
                String orderStatus = order.getOrderInfo().getStatus();
                String landArea = order.getSpecInfo().getLandArea();

                int bidValue = (int) Double.parseDouble(bidAmount);
                int finalBidPrice, companyAmount;

                if ("pending".equalsIgnoreCase(orderStatus)) {
                    String finalBidAmountStr = switch (categoryId) {
                        case MyUtils.HARVESTER_MACHINE_ID -> CommonClass.getRoundedCommissionValue(true, bidAmount, landArea);
                        case MyUtils.EQUIPMENT_ID -> CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_EQUIPMENT);
                        case MyUtils.ROAD_TRANSPORT_ID, MyUtils.RENT_A_CAR_ID -> CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_TRANSPORT);
                        case MyUtils.SKILLED_LABOR_ID -> CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_SKILLED_LABOUR);
                        case MyUtils.HOME_SHIFTING_ID -> CommonClass.getRoundedTenPercentValue(bidAmount, PartnerCommissionUtils.COMMISSION_HOME_SHIFTING);
                        default -> bidAmount;
                    };
                    finalBidPrice = Integer.parseInt(finalBidAmountStr);
                    companyAmount = finalBidPrice - bidValue;
                } else {
                    finalBidPrice = order.getBidInfo().getVendorPrice();
                    companyAmount = finalBidPrice - bidValue;
                }

                holder.confirmOrderPriceTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(finalBidPrice)));
                holder.bidAmountTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(bidValue)));
                holder.companyAmountTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(companyAmount)));
                holder.typesTv.setText(types);

                updateStatusUI(holder, orderStatus, bidStatus);
                setServiceInfo(holder, subCategoryId, types, quantity, capacity, landArea);

                holder.callBtn.setOnClickListener(v -> {
                    String phone = order.getUserInfo().getUserPhone();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    context.startActivity(intent);
                });

                holder.itemView.setOnClickListener(v -> listener.onItemClick(position, finalBidPrice, bidValue, order));
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("OrderInfo", "Error: " + e.getMessage());
            }
        });
    }

    private void updateStatusUI(HolderViewBidDetail holder, String orderStatus, String bidStatus) {
        if ("pending".equalsIgnoreCase(orderStatus) || "process".equalsIgnoreCase(orderStatus)) {
            setStatusInfo(holder, "Pending", R.color.warning, R.drawable.ic_pending, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
        } else if ("confirmed".equalsIgnoreCase(bidStatus)) {
            setStatusInfo(holder, "Confirmed", R.color.green, R.drawable.ic_confirm, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
        } else if ("done".equalsIgnoreCase(bidStatus)) {
            setStatusInfo(holder, "Done", R.color.green, R.drawable.ic_done_24, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.card_light));
        } else if ("pending".equalsIgnoreCase(bidStatus)) {
            setStatusInfo(holder, "Cancel", R.color.red, R.drawable.ic_cancel, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE);
        } else {
            setStatusInfo(holder, bidStatus == null ? "Unknown" : bidStatus, R.color.text_primary, 0, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
        }
    }

    private void setStatusInfo(HolderViewBidDetail holder, String text, int color, int icon, int more, int call, int row1, int row3, int div) {
        holder.bidStatusTv.setText(text);
        holder.bidStatusTv.setTextColor(context.getColor(color));
        holder.bidStatusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0);
        holder.moreBtn.setVisibility(more);
        holder.callBtn.setVisibility(call);
        holder.row_one.setVisibility(row1);
        holder.row_three.setVisibility(row3);
        holder.dividerOne.setVisibility(div);
    }

    @SuppressLint("SetTextI18n")
    private void setServiceInfo(HolderViewBidDetail holder, String subCategoryId, String types, String quantity, String capacity, String landArea) {
        holder.landAreaLL.setVisibility(View.GONE);
        holder.capacityTv.setVisibility(View.GONE);
        holder.postNameTv.setVisibility(View.VISIBLE);

        switch (subCategoryId) {
            case MyUtils.SUB_MICROBUS_ID, MyUtils.SUB_AMBULANCE_ID, MyUtils.SUB_CAR_ID -> {
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tags, 0, 0);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
            }
            case MyUtils.SUB_DUMP_TRUCK_ID -> {
                holder.capacityTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(capacity + ", ");
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dump_truck, 0, 0);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
            }
            case MyUtils.HOME_SHIFTING_ID -> {
                holder.typesTv.setText(types + ", ");
                holder.quantityTv.setText("yes".equals(quantity) ? context.getString(R.string.will_come_in_front_of_the_house) :
                        context.getString(R.string.from_main_road) + " " + Replacement.getLocalMinutes(context, quantity));
            }
            case MyUtils.SUB_DRIVER_ID -> {
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_pickup, 0, 0);
                holder.quantityTv.setText(Replacement.ReplacementPersonInLocal(context, quantity));
                holder.capacityTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(context.getString(R.string.work_experience_dot) + " " + Replacement.ReplacementExperienceInLocal(context, capacity));
            }
            case MyUtils.SUB_PLUMBER_ID, MyUtils.SUB_STOVE_TECHNICIAN_ID, MyUtils.SUB_ELECTRICIAN_ID, MyUtils.SUB_MECHANIC_ID -> {
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_service, 0, 0);
                holder.quantityTv.setText(Replacement.ReplacementPersonInLocal(context, quantity));
            }
            case MyUtils.HARVESTER_MACHINE_ID, MyUtils.SUB_TRACTOR_ID -> {
                holder.landAreaLL.setVisibility(View.VISIBLE);
                holder.landAreaTv.setText(Replacement.ReplacementNumberInLocal(context, landArea) + " " + context.getString(R.string.acres));
            }
            default -> {
                holder.capacityTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(capacity + ", ");
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_parcel, 0, 0);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
            }
        }
    }

    @Override
    public int getItemCount() {
        return bidModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String categoryId = bidModelArrayList.get(position).getOrderInfo().getCategoryId();
        return switch (categoryId) {
            case MyUtils.ROAD_TRANSPORT_ID, MyUtils.RENT_A_CAR_ID -> TRANSPORT;
            case MyUtils.EQUIPMENT_ID, MyUtils.HARVESTER_MACHINE_ID -> EQUIPMENT;
            case MyUtils.HOME_SHIFTING_ID -> HOME_SHIFTING;
            default -> SKILLED_LABOUR;
        };
    }

    static class HolderViewBidDetail extends RecyclerView.ViewHolder {
        TextView orderIdTv, postNameTv, bidDateTv, bidAmountTv, rentTimeTv, capacityTv, quantityTv, typesTv,
                locationNameTv, locationAreaTv, loadLocationTv, loadAreaTv, unLoadLocationTv, unLoadAreaTv,
                postDescriptionTv, bidStatusTv, confirmOrderPriceTv, companyAmountTv, landAreaTv;
        ImageView postImage, moreBtn;
        ImageButton callBtn;
        LinearLayout row_one, row_three, landAreaLL;
        View dividerOne;
        CardView cardView;

        public HolderViewBidDetail(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            postNameTv = itemView.findViewById(R.id.postNameTv);
            bidDateTv = itemView.findViewById(R.id.bidDateTv);
            bidAmountTv = itemView.findViewById(R.id.bidAmountTv);
            confirmOrderPriceTv = itemView.findViewById(R.id.confirmOrderPriceTv);
            companyAmountTv = itemView.findViewById(R.id.companyAmountTv);
            rentTimeTv = itemView.findViewById(R.id.rentTimeTv);
            callBtn = itemView.findViewById(R.id.callBtn);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            row_one = itemView.findViewById(R.id.row_one);
            row_three = itemView.findViewById(R.id.row_three);
            dividerOne = itemView.findViewById(R.id.dividerOne);
            postImage = itemView.findViewById(R.id.postImage);
            postDescriptionTv = itemView.findViewById(R.id.postDescription);
            capacityTv = itemView.findViewById(R.id.capacityTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            typesTv = itemView.findViewById(R.id.typesTv);
            locationNameTv = itemView.findViewById(R.id.locationNameTv);
            locationAreaTv = itemView.findViewById(R.id.locationArea);
            loadLocationTv = itemView.findViewById(R.id.postLoadLocation);
            loadAreaTv = itemView.findViewById(R.id.loadArea);
            unLoadLocationTv = itemView.findViewById(R.id.postUnLoadLocation);
            unLoadAreaTv = itemView.findViewById(R.id.unLoadArea);
            bidStatusTv = itemView.findViewById(R.id.bidStatusTv);
            landAreaLL = itemView.findViewById(R.id.landAreaLL);
            landAreaTv = itemView.findViewById(R.id.landAreaTv);
        }
    }

    //Interface for handling actions from activity
    public interface OnBidDetailActionListener {
        void onItemClick(int position, int confirmOrderPrice, int bidAmount, OrderModel order);
    }
}
