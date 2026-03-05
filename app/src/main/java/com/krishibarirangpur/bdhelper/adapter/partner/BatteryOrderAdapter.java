package com.krishibarirangpur.bdhelper.adapter.partner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.BatteryOrderModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BatteryOrderAdapter extends RecyclerView.Adapter<BatteryOrderAdapter.OrderViewHolder> {

    private final Context context;
    private final List<BatteryOrderModel> orderList;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(BatteryOrderModel order);
    }

    public BatteryOrderAdapter(Context context, List<BatteryOrderModel> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_battery_item, parent, false);
        return new OrderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        BatteryOrderModel order = orderList.get(position);

        holder.orderIdTv.setText("Order: " + order.getOrderId());
        holder.batteryNameTv.setText("Battery: " + order.getBatteryDetails().getBatteryName());
        holder.orderStatusTv.setText("Status: " + order.getOrderStatus());

        holder.batteryPriceTv.setText("Price: "+order.getBatteryDetails().getBatteryPrice());

        holder.exchangeDiscountPriceTv.setText("Exchange Price: "+order.getExchangeDetails().getExchangeDiscount());

        if (order.getExchangeType().equals("exchange")){
            holder.exchangeDiscountPriceTv.setVisibility(View.VISIBLE);
        }
        else {
            holder.exchangeDiscountPriceTv.setVisibility(View.GONE);
        }

        String timestamp = order.getTimestamp();

        //convert Timestamp to dd/mm/yyyy  hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String orderDate = DateFormat.format("dd/MM/yyyy  hh:mm aa", calendar).toString();
        holder.timestampTv.setText(orderDate);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    static class OrderViewHolder extends RecyclerView.ViewHolder{
        TextView orderIdTv, batteryNameTv, batteryPriceTv, orderStatusTv, timestampTv, exchangeDiscountPriceTv;
        ImageView batteryIv;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            batteryIv = itemView.findViewById(R.id.batteryIv);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            batteryNameTv = itemView.findViewById(R.id.batteryNameTv);
            batteryPriceTv = itemView.findViewById(R.id.batteryPriceTv);
            orderStatusTv = itemView.findViewById(R.id.orderStatusTv);
            exchangeDiscountPriceTv = itemView.findViewById(R.id.exchangeDiscountPriceTv);
            timestampTv = itemView.findViewById(R.id.orderTimeTv);
        }
    }
}
