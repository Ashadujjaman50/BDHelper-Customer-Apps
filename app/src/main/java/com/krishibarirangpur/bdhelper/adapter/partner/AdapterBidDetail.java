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
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.UIHelper;

import java.util.ArrayList;

public class AdapterBidDetail extends RecyclerView.Adapter<AdapterBidDetail.HolderViewBidDetail> {

    private static final int TRANSPORT = 0;
    private static final int EQUIPMENT = 1;
    private static final int HOME_SHIFTING = 2;
    private static final int SKILLED_LABOUR = 3;

    Context context;
    ArrayList<BidModel> bidModelArrayList;

    private OnBidDetailActionListener listener;

    public AdapterBidDetail(Context context, ArrayList<BidModel> bidModelArrayList,OnBidDetailActionListener listener) {
        this.context = context;
        this.bidModelArrayList = bidModelArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HolderViewBidDetail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TRANSPORT){
            view = LayoutInflater.from(context).inflate(R.layout.row_bid_detail_transport, parent, false);
        }
        else if (viewType == HOME_SHIFTING){
            view = LayoutInflater.from(context).inflate(R.layout.row_bid_detail_home_shifting, parent, false);
        }
        else if (viewType == EQUIPMENT){
            view = LayoutInflater.from(context).inflate(R.layout.row_bid_detail_equipment, parent, false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.row_bid_detail_skilled_labour, parent, false);
        }
        return new HolderViewBidDetail(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HolderViewBidDetail holder, @SuppressLint("RecyclerView") int position) {
        //get data
        BidModel bidModel = bidModelArrayList.get(position);

        //Bid Info
        String bidId = bidModel.getBidInfo().getBidId();
        String bidAmount = bidModel.getBidInfo().getBidAmount();
        String bidStatus = bidModel.getBidInfo().getStatus();
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
        holder.rentTimeTv.setText(CommonClass.millisToTimeWithLocal(context, rentTime));
        holder.bidDateTv.setText(CommonClass.formatTime(bidTime, "dd-MMMM-yy  hh:mm aa"));


        holder.orderIdTv.setText(context.getString(R.string.order_no_dot) + " " + orderId);

        holder.postImage.setImageResource(CommonClass.getIconForSubCategory(subCategoryId));
        holder.postNameTv.setText(CommonClass.getSubCategoryName(context, subCategoryId));


        //get order info by OrderID
        CommonClass.getOrderInfoById(orderId, new CommonClass.FirestoreOrderCallback() {
            @Override
            public void onSuccess(ArrayList<OrderModel> orderList) {
                if (!orderList.isEmpty()) {
                    OrderModel order = orderList.get(0);
                    Log.d("OrderInfo", "Status: " + order.getOrderInfo().getStatus());

                    // UIHelper update in: 06-05-2026
                    UIHelper.bindAddress(holder.loadLocationTv,    holder.loadAreaTv,    order.getRouteInfo().getLoad());
                    UIHelper.bindAddress(holder.unLoadLocationTv,  holder.unLoadAreaTv,  order.getRouteInfo().getUnload());
                    UIHelper.bindAddress(holder.locationNameTv,    holder.locationAreaTv, order.getRouteInfo().getRentLocation());

                    holder.postDescriptionTv.setText(order.getSpecInfo().getDesc());



                    // Calculate the company amount
                    int vendorPrice = order.getBidInfo().getVendorPrice();
                    int bidValue = (int) Double.parseDouble(bidAmount);
                    int companyAmount = vendorPrice - bidValue;

                    // এখন TextView গুলোতে সেট করো
                    holder.confirmOrderPriceTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(vendorPrice)));
                    holder.bidAmountTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(bidValue)));
                    holder.companyAmountTv.setText(Replacement.ReplacementNumberInLocal(context, String.valueOf(companyAmount)));



                    // ============ Specification ============
                    String quantity = order.getSpecInfo().getQuantity();
                    String capacity = order.getSpecInfo().getCapacity();
                    String types = order.getSpecInfo().getTypes();
                    String orderStatus = order.getOrderInfo().getStatus();
                    String landArea = order.getSpecInfo().getLandArea();

                    holder.typesTv.setText(types);


                    // assume orderStatus ও bidStatus string হিসেবে আছে, এবং context আছে
                    if ("pending".equalsIgnoreCase(orderStatus) || "process".equalsIgnoreCase(orderStatus)) {
                        // Order still active -> always show Pending (ignore bidStatus)
                        holder.bidStatusTv.setText("Pending");
                        holder.bidStatusTv.setTextColor(context.getColor(R.color.warning));
                        holder.bidStatusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pending, 0, 0, 0);

                        holder.moreBtn.setVisibility(View.GONE);
                        holder.callBtn.setVisibility(View.GONE);
                        holder.row_one.setVisibility(View.GONE);
                        holder.row_three.setVisibility(View.GONE);
                        holder.dividerOne.setVisibility(View.VISIBLE);
                    }
                    else {
                        // Order is not pending/process -> check bid status
                        if ("pending".equalsIgnoreCase(bidStatus)) {
                            // Bid still pending but order not active -> expired
                            holder.bidStatusTv.setText("Cancel");
                            holder.bidStatusTv.setTextColor(context.getColor(R.color.red));
                            holder.bidStatusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);

                            holder.moreBtn.setVisibility(View.GONE);
                            holder.callBtn.setVisibility(View.GONE);
                            holder.row_one.setVisibility(View.GONE);
                            holder.row_three.setVisibility(View.GONE);
                            holder.dividerOne.setVisibility(View.VISIBLE);
                        }
                        else if ("confirmed".equalsIgnoreCase(bidStatus)) {
                            holder.bidStatusTv.setText("Confirmed");
                            holder.bidStatusTv.setTextColor(context.getColor(R.color.green));
                            holder.bidStatusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_confirm, 0, 0, 0);

                            holder.moreBtn.setVisibility(View.VISIBLE);
                            holder.callBtn.setVisibility(View.VISIBLE);
                            holder.row_one.setVisibility(View.VISIBLE);
                            holder.row_three.setVisibility(View.VISIBLE);
                            holder.dividerOne.setVisibility(View.GONE);
                        }
                        else if ("done".equalsIgnoreCase(bidStatus)) {
                            holder.bidStatusTv.setText("Done");
                            holder.bidStatusTv.setTextColor(context.getColor(R.color.green));
                            holder.bidStatusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_done_24, 0, 0, 0);

                            holder.moreBtn.setVisibility(View.GONE);
                            holder.callBtn.setVisibility(View.GONE);
                            holder.row_one.setVisibility(View.VISIBLE);
                            holder.row_three.setVisibility(View.VISIBLE);
                            holder.dividerOne.setVisibility(View.GONE);

                            // 🔹 CardView background পরিবর্তন
                            holder.cardView.setCardBackgroundColor(context.getColor(R.color.card_light)); // অথবা চাইলে অন্য রঙ
                        }
                        else {
                            // fallback for unknown statuses
                            holder.bidStatusTv.setText(bidStatus == null ? "Unknown" : bidStatus);
                            holder.bidStatusTv.setTextColor(context.getColor(R.color.text_primary));
                            holder.bidStatusTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

                            holder.moreBtn.setVisibility(View.GONE);
                            holder.callBtn.setVisibility(View.GONE);
                            holder.row_one.setVisibility(View.GONE);
                            holder.row_three.setVisibility(View.GONE);
                            holder.dividerOne.setVisibility(View.GONE);
                        }
                    }


                    // ============ Handle SubCategory Specific ============
                    setServiceInfo(holder, subCategoryId, types, quantity, capacity, landArea);



                    holder.callBtn.setOnClickListener(v -> {
                        String phone = order.getUserInfo().getUserPhone();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phone));
                        context.startActivity(intent);
                    });

                    holder.itemView.setOnClickListener(v -> {
                        listener.onItemClick(position, vendorPrice, bidValue);
                    });

                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("OrderInfo", "Error: " + e.getMessage());
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void setServiceInfo(HolderViewBidDetail holder, String subCategoryId, String types, String quantity, String capacity, String landArea) {
        switch (subCategoryId) {
            case MyUtils.SUB_MICROBUS_ID:
            case MyUtils.SUB_AMBULANCE_ID:
            case MyUtils.SUB_CAR_ID:
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tags,0,0);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
            case MyUtils.SUB_DUMP_TRUCK_ID:
                holder.postNameTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(capacity+", ");
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dump_truck,0,0);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
            case MyUtils.HOME_SHIFTING_ID:
                holder.typesTv.setText(types + ", ");
                if ("yes".equals(quantity)) {
                    holder.quantityTv.setText(context.getString(R.string.will_come_in_front_of_the_house));
                }
                else {
                    holder.quantityTv.setText(
                            context.getString(R.string.from_main_road) + " " + Replacement.getLocalMinutes(context, quantity)
                    );
                }
                break;
            case MyUtils.SUB_DRIVER_ID:
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_pickup,0,0);
                holder.quantityTv.setText(Replacement.ReplacementPersonInLocal(context, quantity));
                holder.capacityTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(context.getString(R.string.work_experience_dot)+" "+Replacement.ReplacementExperienceInLocal(context, capacity));
                break;
            case MyUtils.SUB_PLUMBER_ID:
            case MyUtils.SUB_STOVE_TECHNICIAN_ID:
            case MyUtils.SUB_ELECTRICIAN_ID:
            case MyUtils.SUB_MECHANIC_ID:
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_service,0,0);
                holder.quantityTv.setText(Replacement.ReplacementPersonInLocal(context, quantity));
                break;
            case MyUtils.HARVESTER_MACHINE_ID:
            case MyUtils.SUB_TRACTOR_ID:
                holder.landAreaLL.setVisibility(View.VISIBLE);
                holder.landAreaTv.setText(Replacement.ReplacementNumberInLocal(context, landArea) +" "+context.getString(R.string.acres));
                break;
            default:
                holder.postNameTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(capacity+", ");
                holder.typesTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_parcel,0,0);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
        }

    }


    @Override
    public int getItemCount() {
        return bidModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String categoryId = bidModelArrayList.get(position).getOrderInfo().getCategoryId();
        switch (categoryId) {
            case MyUtils.ROAD_TRANSPORT_ID:
            case MyUtils.RENT_A_CAR_ID:
                return TRANSPORT;
            case MyUtils.EQUIPMENT_ID:
            case MyUtils.HARVESTER_MACHINE_ID:
                return EQUIPMENT;
            case MyUtils.HOME_SHIFTING_ID:
                return HOME_SHIFTING;
            default:
                return SKILLED_LABOUR;
        }
    }

    static class HolderViewBidDetail extends RecyclerView.ViewHolder{

        TextView orderIdTv, postNameTv, bidDateTv, bidAmountTv, rentTimeTv, capacityTv, quantityTv, typesTv,
                locationNameTv, locationAreaTv, loadLocationTv, loadAreaTv, unLoadLocationTv, unLoadAreaTv,
                postDescriptionTv, bidStatusTv,  confirmOrderPriceTv, companyAmountTv, landAreaTv;

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
            //
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
        void onItemClick(int position, int confirmOrderPrice, int bidAmount);

    }

}
