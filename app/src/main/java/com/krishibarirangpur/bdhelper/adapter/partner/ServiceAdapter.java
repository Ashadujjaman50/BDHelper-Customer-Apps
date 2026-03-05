package com.krishibarirangpur.bdhelper.adapter.partner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.ServiceModel;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private Context context;
    private List<ServiceModel> serviceList;
    private OnItemClickListener mListener; // static নয়, instance variable

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
        holder.serviceModelNumberTV.setText(service.getServiceModelNumber());
        holder.serviceCategoryAndYearTV.setText(service.getServiceCategoryAndYear());

        String regNumber = service.getServiceRegistrationNumber();
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
                .load(service.getTransportImage())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.transportImageIv);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView transportImageIv;
        TextView subCategoryNameTv, serviceRegistrationNumberTV, serviceModelNumberTV, serviceCategoryAndYearTV;
        TextView serviceStatusTv, verifiedMessageTv, verifiedMessagePendingTv;
        ImageButton uploadBtn;

        public ServiceViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            transportImageIv = itemView.findViewById(R.id.transportImageIv);
            subCategoryNameTv = itemView.findViewById(R.id.subCategoryNameTv);
            serviceRegistrationNumberTV = itemView.findViewById(R.id.serviceRegistrationNumberTV);
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


