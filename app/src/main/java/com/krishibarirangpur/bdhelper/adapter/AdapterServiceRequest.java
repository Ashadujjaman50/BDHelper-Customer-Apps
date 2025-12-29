package com.krishibarirangpur.bdhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.ModelServiceRequest;
import com.krishibarirangpur.bdhelper.utils.Replacement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterServiceRequest extends RecyclerView.Adapter<AdapterServiceRequest.HolderViewServiceRequest> {

    Context context;
    ArrayList<ModelServiceRequest> serviceRequestArrayList;

    public AdapterServiceRequest(Context context, ArrayList<ModelServiceRequest> serviceRequestArrayList) {
        this.context = context;
        this.serviceRequestArrayList = serviceRequestArrayList;
    }

    @NonNull
    @Override
    public HolderViewServiceRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Layout Inflater
        View view = LayoutInflater.from(context).inflate(R.layout.row_service_request, parent, false);
        return new HolderViewServiceRequest(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HolderViewServiceRequest holder, int position) {
        //get Data
        ModelServiceRequest modelServiceRequest = serviceRequestArrayList.get(position);

        // Serial Number (1-based index)
        @SuppressLint("DefaultLocale")
        String serviceNo = String.format("%03d", position + 1);
        //String serviceId = modelServiceRequest.getServiceId();
        String serviceName = modelServiceRequest.getServiceName();
        String district = modelServiceRequest.getDistrict();
        //String userId = modelServiceRequest.getUserId();
        String note = modelServiceRequest.getNote();
        String status = modelServiceRequest.getStatus();
        String timestamp = modelServiceRequest.getTimestamp();

        //convert Timestamp to dd/mm/yyyy  hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String requestTime = DateFormat.format("dd-MMM, hh:mm aa", calendar).toString();

        //set data
        requestTime = requestTime.replace("AM", "am");
        requestTime = requestTime.replace("PM", "pm");
        holder.sRequestTimeTv.setText(requestTime);

        String statusText;
        if ("pending".equalsIgnoreCase(status)) {
            statusText = "Pending";
        } else if (status != null && !status.isEmpty()) {
            statusText = status.substring(0, 1).toUpperCase(Locale.getDefault()) + status.substring(1).toLowerCase(Locale.getDefault());
        } else {
            statusText = "";
        }

        holder.statusTv.setText(statusText);
        holder.sRequestNoTv.setText("#"+serviceNo);
        holder.serviceNameTv.setText(serviceName);
        String shownDistrict = Replacement.getLocalizedDistrict(context, district);
        holder.serviceLocationTv.setText(shownDistrict);

        if (note.isEmpty()){
            holder.noteTv.setVisibility(View.GONE);
            holder.noteViewBar.setVisibility(View.GONE);
        }
        else {
            holder.noteTv.setVisibility(View.VISIBLE);
            holder.noteViewBar.setVisibility(View.VISIBLE);
        }
        holder.noteTv.setText(note);

    }

    @Override
    public int getItemCount() {
        return serviceRequestArrayList.size();
    }

    static class HolderViewServiceRequest extends RecyclerView.ViewHolder{

        TextView sRequestNoTv, sRequestTimeTv, serviceNameTv, serviceLocationTv, noteTv, statusTv;
        View noteViewBar;

        public HolderViewServiceRequest(@NonNull View itemView) {
            super(itemView);
            //init views

            sRequestNoTv = itemView.findViewById(R.id.sRequestNoTv);
            sRequestTimeTv = itemView.findViewById(R.id.sRequestTimeTv);
            serviceNameTv = itemView.findViewById(R.id.serviceNameTv);
            serviceLocationTv = itemView.findViewById(R.id.serviceLocationTv);
            noteTv = itemView.findViewById(R.id.noteTv);
            statusTv = itemView.findViewById(R.id.statusTv);

            noteViewBar = itemView.findViewById(R.id.noteViewBar);
        }
    }
}
