package com.dropshep.bdhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.model.OrderModel;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.Replacement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.HolderViewOrder> {

    private static final int TRANSPORT = 0;
    private static final int EQUIPMENT = 1;
    private static final int HOME_SHIFTING = 2;
    private static final int SKILLED_LABOUR = 3;

    private final Context context;
    private final ArrayList<OrderModel> orderModelArrayList;

    private OnItemClickListener mListener;

    public OrderAdapter(Context context, ArrayList<OrderModel> orderModelArrayList) {
        this.context = context;
        this.orderModelArrayList = orderModelArrayList;
    }

    @NonNull
    @Override
    public HolderViewOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TRANSPORT){
            view = LayoutInflater.from(context).inflate(R.layout.row_single_requirement_transport_layout, parent, false);
        }
        else if (viewType == EQUIPMENT) {
            view = LayoutInflater.from(context).inflate(R.layout.row_single_requirement_equipment_layout, parent, false);
        }
        else if (viewType == HOME_SHIFTING){
            view = LayoutInflater.from(context).inflate(R.layout.row_single_requirement_homeshifting_layout, parent, false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.row_single_requirement_skilled_labor_layout, parent, false);
        }
        assert view != null;
        return new HolderViewOrder(view, mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HolderViewOrder holder, int position) {
        OrderModel post = orderModelArrayList.get(position);

        // ============ Order Info ============
        String orderId = post.getOrderInfo().getOrderId();
        String subCategoryId = post.getOrderInfo().getSubCategoryId();
        String orderStatus = post.getOrderInfo().getStatus();
        long timestamp = post.getOrderInfo().getTimestamp();

        // ============ Route Info ============
        String rentArea = post.getRouteInfo().getRentLocation();
        String loadLocation = post.getRouteInfo().getLoad();
        String unLoadLocation = post.getRouteInfo().getUnload();
        String rentDateAndTime = post.getRouteInfo().getRentTime();

        // ============ Specification ============
        String quantity = post.getSpecInfo().getQuantity();
        String capacity = post.getSpecInfo().getCapacity();
        String duration = post.getSpecInfo().getDuration();
        String types = post.getSpecInfo().getTypes();
        String postDescription = post.getSpecInfo().getDesc();

        // ============ Time Formatting ============
        holder.rentTimeTv.setText(CommonClass.formatTime(rentDateAndTime, "dd MMMM, hh:mm aa"));
        holder.postedDate.setText(CommonClass.formatTime(String.valueOf(timestamp), "dd-MMM-yy  hh:mm aa"));

        // ============ Common Info ============
        holder.postNameTv.setText(CommonClass.getSubCategoryName(subCategoryId));
        holder.orderIdTv.setText(orderId);
        holder.loadLocation.setText(loadLocation);
        holder.unLoadLocation.setText(unLoadLocation);
        holder.locationArea.setText(rentArea);
        holder.capacityTv.setText(capacity + ", ");
        holder.durationTv.setText(duration);
        holder.postDescription.setText(postDescription);

        // Set icon
        int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
        holder.postImage.setImageDrawable(ContextCompat.getDrawable(context, iconRes));

        // ============ Handle SubCategory Specific ============
        setServiceInfo(holder, subCategoryId, types, quantity, capacity);

        // ============ Status ============
        setOrderStatus(holder, post , orderStatus);
    }

    // 🔹 Service Info based on subCategory
    @SuppressLint("SetTextI18n")
    private void setServiceInfo(HolderViewOrder holder, String subCategoryId, String types, String quantity, String capacity) {
        switch (subCategoryId) {
            case MyUtils.SUB_MICROBUS_ID:
            case MyUtils.SUB_AMBULANCE_ID:
                holder.serviceNameTV.setText(CommonClass.getSubCategoryName(subCategoryId));
                holder.sizeCapacityDefTV.setText(context.getString(R.string.category_and_trip));
                holder.typesTv.setText(types);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
            case MyUtils.SUB_EXCAVATOR_ID:
            case MyUtils.SUB_RICE_TRANSPLANTER_ID:
            case MyUtils.SUB_TRACTOR_ID:
                holder.serviceNameTV.setText(CommonClass.getSubCategoryName(subCategoryId));
                holder.sizeCapacityDefTV.setText(context.getString(R.string.size_and_trip));
                holder.typesTv.setText(types);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
            case MyUtils.SUB_CAR_ID:
                holder.serviceNameTV.setText(context.getString(R.string.trip));
                holder.sizeCapacityDefTV.setText(context.getString(R.string.category_and_trip));
                holder.typesTv.setText(types);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
            case MyUtils.HARVESTER_MACHINE_ID:
                holder.serviceNameTV.setText(context.getString(R.string.working));
                holder.sizeCapacityDefTV.setText(context.getString(R.string.category_and_trip));
                holder.typesTv.setText(types);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
            case MyUtils.HOME_SHIFTING_ID:
                holder.typesTv.setText(types + ", ");
                holder.serviceNameTV.setText(context.getString(R.string.home_shift));
                holder.sizeCapacityDefTV.setText(context.getString(R.string.truck_access));
                if ("yes".equals(quantity)) {
                    holder.quantityTv.setText(context.getString(R.string.will_come_in_front_of_the_house));
                }
                else {
                    holder.quantityTv.setText(
                            context.getString(R.string.from_main_road) + " " +
                                    Replacement.getLocalMinutes(context, quantity)
                    );
                }
                break;
            case MyUtils.SUB_DRIVER_ID:
                holder.serviceNameTV.setText(context.getString(R.string.working_type_and_experience));
                holder.sizeCapacityDefTV.setText(context.getString(R.string.need_dot));
                holder.typesTv.setText(types);
                holder.quantityTv.setText(Replacement.ReplacementPersonInLocal(context, quantity));

                break;
            case MyUtils.SUB_PLUMBER_ID:
            case MyUtils.SUB_STOVE_TECHNICIAN_ID:
            case MyUtils.SUB_ELECTRICIAN_ID:
            case MyUtils.SUB_MECHANIC_ID:
                holder.serviceNameTV.setText(context.getString(R.string.working));
                holder.sizeCapacityDefTV.setText(context.getString(R.string.need_dot));
                holder.typesTv.setText(types);
                holder.quantityTv.setText(Replacement.ReplacementPersonInLocal(context, quantity));
                holder.capacityTv.setText(capacity);
                break;
            default:
                holder.serviceNameTV.setText(context.getString(R.string.product));
                holder.sizeCapacityDefTV.setText(context.getString(R.string.size_and_trip));
                holder.typesTv.setText(types);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
        }
    }

    // 🔹 Order Status UI
    @SuppressLint("SetTextI18n")
    private void setOrderStatus(HolderViewOrder holder, OrderModel post, String postStatus) {
        switch (postStatus) {
            case "pending":
            case "process": {
                String rentDateAndTime = post.getRouteInfo().getRentTime();
                long rentMillis = CommonClass.parseDateStringToMillis(rentDateAndTime);

                // আজকের দিন (midnight)
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long startOfToday = cal.getTimeInMillis();

                if (rentMillis > 0 && rentMillis < startOfToday) {
                    // Expired
                    setStatus(holder, "Expired", R.color.text_secondary, R.drawable.ic_expire);
                    setBidAction(holder, R.string.cancel, R.drawable.ic_hammer, R.drawable.ic_clear);
                }
                else {
                    if (postStatus.equals("pending")) {
                        setStatus(holder, "Pending", R.color.warning, R.drawable.ic_pending);
                    } else {
                        setStatus(holder, "Process", R.color.blue, R.drawable.ic_process);
                    }
                }
                break;
            }

            case "confirmed":
                setStatus(holder, "Confirm", R.color.green, R.drawable.ic_confirm);
                setBidAction(holder, R.string.bidding, R.drawable.ic_hammer, R.drawable.ic_arrow_drop_down);
                break;

            case "done":
                setStatus(holder, "Done", R.color.primaryMid, R.drawable.ic_done);
                if ("pending".equals(post.getPaymentInfo().getPayStatus())) {
                    setBidAction(holder, R.string.pay_now, R.drawable.ic_payment, R.drawable.ic_arrow_drop_down);
                }
                break;

            case "complete":
                setStatus(holder, "Complete", R.color.green, R.drawable.ic_done);
                setBidAction(holder, R.string.review, R.drawable.ic_review, R.drawable.ic_arrow_drop_down);
                break;

            case "cancel":
                setStatus(holder, "Cancel", R.color.red, R.drawable.ic_cancel);
                setBidAction(holder, R.string.cancel, R.drawable.ic_hammer, R.drawable.ic_clear);
                break;
        }
    }

    /** 🔹 Helper method for status text, color, icon */
    private void setStatus(HolderViewOrder holder, String text, int colorRes, int iconRes) {
        holder.postStatusTv.setText(text);
        holder.postStatusTv.setTextColor(ContextCompat.getColor(context, colorRes));
        holder.postStatusTv.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
    }

    /** 🔹 Helper method for bid/review text + icons */
    private void setBidAction(HolderViewOrder holder, int textRes, int leftIcon, int rightIcon) {
        holder.bidOrReviewTv.setText(context.getString(textRes));
        holder.bidOrReviewTv.setCompoundDrawablesWithIntrinsicBounds(leftIcon, 0, rightIcon, 0);
    }



    @Override
    public int getItemCount() {
        return orderModelArrayList.size();
    }


    public int getItemViewType(int position){
        String categoryId = orderModelArrayList.get(position).getOrderInfo().getCategoryId();
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

    static class HolderViewOrder extends RecyclerView.ViewHolder{

        private final TextView orderIdTv, postNameTv, quantityTv, loadLocation, unLoadLocation,
                rentTimeTv, postDescription, locationArea,postedDate, sizeCapacityDefTV, confirmVendorName,confirmVendorPrice,
                confirmVendorMobile, postStatusTv, bidOrReviewTv, typesTv, capacityTv, durationTv, serviceNameTV;
        private final LinearLayout confirmationLayout, productTypeLL;
        private final ImageView postImage;

        public HolderViewOrder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            postNameTv = itemView.findViewById(R.id.postNameTv);
            quantityTv = itemView.findViewById(R.id.quantityTv);
            loadLocation = itemView.findViewById(R.id.postLoadLocation);
            unLoadLocation = itemView.findViewById(R.id.postUnLoadLocation);
            rentTimeTv = itemView.findViewById(R.id.rentTimeTv);
            postDescription = itemView.findViewById(R.id.postDescription);
            locationArea = itemView.findViewById(R.id.locationNameTV);
            postedDate = itemView.findViewById(R.id.postedDate);
            sizeCapacityDefTV = itemView.findViewById(R.id.sizeCapacityDefTV);
            confirmationLayout = itemView.findViewById(R.id.confirmationLayout);
            confirmVendorName = itemView.findViewById(R.id.confirmVendorName);
            confirmVendorMobile = itemView.findViewById(R.id.confirmVendorMobile);
            confirmVendorPrice = itemView.findViewById(R.id.confirmVendorPrice);
            postImage = itemView.findViewById(R.id.postImage);
            postStatusTv = itemView.findViewById(R.id.postStatusTv);

            typesTv = itemView.findViewById(R.id.typesTv);
            capacityTv = itemView.findViewById(R.id.capacityTv);
            durationTv = itemView.findViewById(R.id.durationTv);
            bidOrReviewTv = itemView.findViewById(R.id.bidOrReviewTv);
            productTypeLL = itemView.findViewById(R.id.productTypeLL);
            serviceNameTV = itemView.findViewById(R.id.serviceNameTV);

            itemView.setOnClickListener(v -> {
                if (listener != null){
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
        notifyDataSetChanged();
    }
}
