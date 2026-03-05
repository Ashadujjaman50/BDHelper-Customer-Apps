package com.krishibarirangpur.bdhelper.adapter.partner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.OrderModel;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.FinanceCache;
import com.krishibarirangpur.bdhelper.utils.FinanceManager;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.partner.DialogAlert;
import com.krishibarirangpur.bdhelper.utils.partner.PartnerUtils;

import java.util.ArrayList;
import java.util.Map;

public class OrderPartnerAdapter extends RecyclerView.Adapter<OrderPartnerAdapter.HolderViewOrderPartner> {
    private static final int TRANSPORT = 0;
    private static final int EQUIPMENT = 1;
    private static final int HOME_SHIFTING = 2;
    private static final int SKILLED_LABOUR = 3;

    @SuppressLint("StaticFieldLeak")
    static Context context;
    ArrayList<OrderModel> orderModelArrayList;

    private OnItemClickListener mListener;

    public OrderPartnerAdapter(Context context, ArrayList<OrderModel> orderModelArrayList) {
        this.context = context;
        this.orderModelArrayList = orderModelArrayList;
    }

    @NonNull
    @Override
    public HolderViewOrderPartner onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TRANSPORT){
            view = LayoutInflater.from(context).inflate(R.layout.row_post_transport, parent, false);
        }
        else if (viewType == EQUIPMENT){
            view = LayoutInflater.from(context).inflate(R.layout.row_post_equipment, parent, false);
        }
        else if (viewType == HOME_SHIFTING){
            view = LayoutInflater.from(context).inflate(R.layout.row_post_homeshifting, parent, false);
        }
        else if (viewType == SKILLED_LABOUR){
            view = LayoutInflater.from(context).inflate(R.layout.row_post_skilled_labour, parent, false);
        }
        assert view != null;
        return new HolderViewOrderPartner(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderViewOrderPartner holder, int position) {
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
        holder.rentTimeTv.setText(CommonClass.millisToTimeWithLocal(context, rentDateAndTime));


        // ============ Common Info ============
        holder.postNameTv.setText(CommonClass.getSubCategoryName(context, subCategoryId));
        holder.orderIdTv.setText(orderId);

        holder.loadLocation.setText(CommonClass.formatAddress(loadLocation).first);
        holder.loadArea.setText(CommonClass.formatAddress(loadLocation).second);

        holder.unLoadLocation.setText(CommonClass.formatAddress(unLoadLocation).first);
        holder.unLoadArea.setText(CommonClass.formatAddress(unLoadLocation).second);

        holder.locationNameTv.setText(CommonClass.formatAddress(rentArea).first);
        holder.locationArea.setText(CommonClass.formatAddress(rentArea).second);

        holder.postDescription.setText(postDescription);
        holder.quantityTv.setText(quantity);
        holder.capacityTv.setText(capacity);
        holder.typesTv.setText(types);
        //holder.durationTv.setText(duration);


        // Set icon
        int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
        holder.postImage.setImageDrawable(ContextCompat.getDrawable(context, iconRes));

        // ============ Handle SubCategory Specific ============
        setServiceInfo(holder, subCategoryId, types, quantity, capacity);

    }

    // 🔹 Service Info based on subCategory
    @SuppressLint("SetTextI18n")
    private void setServiceInfo(HolderViewOrderPartner holder, String subCategoryId, String types, String quantity, String capacity) {
        switch (subCategoryId) {
            case MyUtils.SUB_MICROBUS_ID:
            case MyUtils.SUB_AMBULANCE_ID:
            case MyUtils.SUB_CAR_ID:
                holder.postNameTv.setVisibility(View.GONE);
                holder.typesIv.setImageResource(R.drawable.ic_tool);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
            case MyUtils.SUB_DUMP_TRUCK_ID:
                holder.postNameTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(capacity+", ");
                holder.typesIv.setImageResource(R.drawable.ic_dump_truck);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
            case MyUtils.HOME_SHIFTING_ID:
                holder.typesTv.setText(types + ", ");
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
                holder.typesIv.setImageResource(R.drawable.ic_pickup);
                holder.quantityTv.setText(Replacement.ReplacementPersonInLocal(context, quantity));
                holder.capacityTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(context.getString(R.string.work_experience_dot)+" "+Replacement.ReplacementExperienceInLocal(context, capacity));
                break;
            case MyUtils.SUB_PLUMBER_ID:
            case MyUtils.SUB_STOVE_TECHNICIAN_ID:
            case MyUtils.SUB_ELECTRICIAN_ID:
            case MyUtils.SUB_MECHANIC_ID:
                holder.typesIv.setImageResource(R.drawable.ic_service);
                holder.quantityTv.setText(Replacement.ReplacementPersonInLocal(context, quantity));
                break;
            default:
                holder.postNameTv.setVisibility(View.VISIBLE);
                holder.capacityTv.setText(capacity+", ");
                holder.typesIv.setImageResource(R.drawable.ic_parcel);
                holder.quantityTv.setText(Replacement.ReplacementQtyToLocal(context, quantity));
                break;
        }

    }


    @Override
    public int getItemCount() {
        return orderModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
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

    static class HolderViewOrderPartner extends RecyclerView.ViewHolder{
        TextView postNameTv, quantityTv, loadLocation, unLoadLocation, rentTimeTv, typesTv,
                postDescription,locationNameTv, locationArea, capacityTv, orderIdTv, loadArea,unLoadArea;
        ImageView postImage, typesIv;
        public HolderViewOrderPartner(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            //init views
            postImage = itemView.findViewById(R.id.postImage);
            typesIv = itemView.findViewById(R.id.typesIv);
            postNameTv = itemView.findViewById(R.id.postName);
            quantityTv = itemView.findViewById(R.id.quantity);
            loadLocation = itemView.findViewById(R.id.postLoadLocation);
            unLoadLocation = itemView.findViewById(R.id.postUnLoadLocation);
            rentTimeTv = itemView.findViewById(R.id.postTime);
            typesTv = itemView.findViewById(R.id.typesTv);
            postDescription = itemView.findViewById(R.id.postDescription);
            locationNameTv = itemView.findViewById(R.id.locationNameTv);
            locationArea = itemView.findViewById(R.id.locationArea);
            capacityTv = itemView.findViewById(R.id.capacityTv);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);

            loadArea = itemView.findViewById(R.id.loadArea);
            unLoadArea = itemView.findViewById(R.id.unLoadArea);

            itemView.setOnClickListener(v -> {
                double partnerReceivable = FinanceCache.partnerReceivable;
                double companyReceivable = FinanceCache.companyReceivable;
                Map<String, Double> result = FinanceManager.getNetReceivable(partnerReceivable, companyReceivable);
                double netAmount = result.get("netAmount");
                double owedTo = result.get("owedTo");
                double currentDue = 0;

                if (owedTo == 2.0){
                    currentDue = netAmount;
                }

                double limit = PartnerUtils.PARTNER_DUE_LIMIT;

                if (currentDue >= limit){
                    String message ="আপনার বকেয়া "+currentDue+" টাকা রয়েছে। অনুগ্রহ করে প্রথমে বকেয়া পরিশোধ করুন, তারপর বিড করতে পারবেন।";
                    DialogAlert.dueAlert(context,"",Replacement.ReplacementNumberInLocal(context,message));
                }
                else {
                    if (listener != null){
                        int position  = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(v, position);
                        }
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
