package com.krishibarirangpur.bdhelper.adapter.partner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.ServiceModel;
import com.krishibarirangpur.bdhelper.userActivity.partner.UpdateServiceActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final Context context;
    private final List<ServiceModel> serviceList;
    private OnItemClickListener mListener;

    public ServiceAdapter(Context context, List<ServiceModel> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_rent_service, parent, false);
        return new ServiceViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceModel service = serviceList.get(position);

        // Bind data
        holder.subCategoryNameTv.setText(service.getSubCategoryName());

        String mfgYear = service.getSafeManufacturingYear();
        if (mfgYear == null || mfgYear.trim().isEmpty()) {
            holder.serviceCategoryAndYearTV.setVisibility(View.GONE);
        } else {
            holder.serviceCategoryAndYearTV.setVisibility(View.VISIBLE);
            holder.serviceCategoryAndYearTV.setText(mfgYear);
        }

        String brandModel = service.getSafeBrandOrModel();
        if (brandModel == null || brandModel.trim().isEmpty()) {
            holder.serviceModelNumberTV.setVisibility(View.GONE);
        } else {
            holder.serviceModelNumberTV.setVisibility(View.VISIBLE);
            holder.serviceModelNumberTV.setText(brandModel);
        }

        String sizeCap = service.getSafeSizeAndCapacity();
        if (sizeCap == null || sizeCap.trim().isEmpty()) {
            holder.sizeAndCapacityTV.setVisibility(View.GONE);
        } else {
            holder.sizeAndCapacityTV.setVisibility(View.VISIBLE);
            if (MyUtils.HOME_SHIFTING_ID.equals(service.getCategoryId())) {
                holder.sizeAndCapacityTV.setText(Replacement.ReplacementPersonInLocal(context, sizeCap));
            } else if (MyUtils.SKILLED_LABOR_ID.equals(service.getCategoryId())) {
                holder.sizeAndCapacityTV.setText(Replacement.ReplacementExperienceInLocal(context, sizeCap));
            } else {
                holder.sizeAndCapacityTV.setText(sizeCap);
            }
        }

        String regNumber = service.getSafeRegistrationNumber();
        if (regNumber == null || regNumber.trim().isEmpty()) {
            holder.serviceRegistrationNumberTV.setVisibility(View.GONE);
        } else {
            holder.serviceRegistrationNumberTV.setVisibility(View.VISIBLE);
            if (service.getCategoryId().equals(MyUtils.ROAD_TRANSPORT_ID) || service.getCategoryId().equals(MyUtils.RENT_A_CAR_ID)){
                holder.serviceRegistrationNumberTV.setText(Replacement.convertVehicleRegByLocale(context, regNumber));
            }
            else {
                holder.serviceRegistrationNumberTV.setText(regNumber);
            }
        }

        // Verified status
        holder.verifiedMessageTv.setVisibility(View.GONE);
        holder.verifiedMessagePendingTv.setVisibility(View.GONE);
        holder.serviceStatusTv.setVisibility(View.GONE);
        holder.uploadBtn.setVisibility(View.GONE);

        switch (service.getServiceVerified().toLowerCase()) {
            case "verified":
                holder.serviceStatusTv.setVisibility(View.VISIBLE);
                holder.serviceStatusTv.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case "process":
                holder.verifiedMessageTv.setVisibility(View.VISIBLE);
                break;
            case "pending":
                holder.verifiedMessagePendingTv.setVisibility(View.VISIBLE);
                holder.uploadBtn.setVisibility(View.VISIBLE);
                break;
        }

        // Load transport image
        Glide.with(context)
                .load(service.getSafeTransportImage())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.transportImageIv);
        
        /*holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateServiceActivity.class);
            intent.putExtra("serviceId", service.getServiceId());
            context.startActivity(intent);
        });*/
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView transportImageIv;
        TextView subCategoryNameTv, serviceRegistrationNumberTV, sizeAndCapacityTV, serviceModelNumberTV, serviceCategoryAndYearTV;
        TextView serviceStatusTv, verifiedMessageTv, verifiedMessagePendingTv;
        ImageButton uploadBtn;

        public ServiceViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            transportImageIv = itemView.findViewById(R.id.transportImageIv);
            subCategoryNameTv = itemView.findViewById(R.id.subCategoryNameTv);
            serviceRegistrationNumberTV = itemView.findViewById(R.id.serviceRegistrationNumberTV);
            sizeAndCapacityTV = itemView.findViewById(R.id.sizeAndCapacityTV);
            serviceModelNumberTV = itemView.findViewById(R.id.serviceModelNumberTV);
            serviceCategoryAndYearTV = itemView.findViewById(R.id.serviceCategoryAndYearTV);
            serviceStatusTv = itemView.findViewById(R.id.serviceStatusTv);
            verifiedMessageTv = itemView.findViewById(R.id.verifiedMessageTv);
            verifiedMessagePendingTv = itemView.findViewById(R.id.verifiedMessagePendingTv);
            uploadBtn = itemView.findViewById(R.id.uploadBtn);

            uploadBtn.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
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
