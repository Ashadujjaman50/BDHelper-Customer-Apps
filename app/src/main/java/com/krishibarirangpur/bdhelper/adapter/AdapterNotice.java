package com.krishibarirangpur.bdhelper.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.ModelNotice;
import com.krishibarirangpur.bdhelper.utils.MyToast;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterNotice extends RecyclerView.Adapter<AdapterNotice.HolderViewNotice> {

    Context context;
    ArrayList<ModelNotice> noticeArrayList;

    private OnItemClickListener mListener; // 🔹 OnItemClickListener

    public AdapterNotice(Context context, ArrayList<ModelNotice> noticeArrayList) {
        this.context = context;
        this.noticeArrayList = noticeArrayList;
    }

    @NonNull
    @Override
    public HolderViewNotice onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate
        View view = LayoutInflater.from(context).inflate(R.layout.row_notice_item, parent, false);
        return new HolderViewNotice(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderViewNotice holder, int position) {
        //get data
        ModelNotice modelNotice = noticeArrayList.get(position);
        String noticeId = modelNotice.getNoticeId();
        String orderId = modelNotice.getOrderId();   //If notice with image Then This Is Used Image Url
        String noticeTitle = modelNotice.getNoticeTitle();;
        String noticeCategory = modelNotice.getNoticeCategory();
        String noticeDesc = modelNotice.getNoticeDescription();
        String senderType = modelNotice.getSenderType();
        String timestamp = modelNotice.getTimestamp();

        //replace
        //convert Timestamp to dd/mm/yyyy  hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String noticeDate = DateFormat.format("dd-MMM-yy\n hh:MM aa", calendar).toString();

        noticeDate = noticeDate.replace("AM", "am");
        noticeDate = noticeDate.replace("PM", "pm");

        //set data
        holder.noticeDescriptionTv.setText(noticeDesc);
        holder.noticeTimeTv.setText(noticeDate);

        //If Notice With Image Then Order Id Used Image Url
        // 🔹 default state (VERY IMPORTANT)
        holder.noticeImageIV.setVisibility(View.GONE);
        holder.noticeCard.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.bg_color)
        );
        if (MyUtils.NOTICE_TYPE_NOTICE.equals(noticeCategory)) {

            if (!TextUtils.isEmpty(orderId) && !"null".equalsIgnoreCase(orderId)) {
                holder.noticeImageIV.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(orderId)
                        .placeholder(R.drawable.ic_notice)
                        .error(R.drawable.ic_notice)
                        .into(holder.noticeImageIV);
            }
        }
        else if (MyUtils.NOTICE_TYPE_BID_CONFIRM.equals(noticeCategory)) {
            holder.noticeCard.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.radio_card_selected_bg)
            );
        }

        //senderType
        if (senderType.equals(MyUtils.NOTICE_SENDER_PARTNER) ||
                senderType.equals(MyUtils.NOTICE_SENDER_CUSTOMER)){
            holder.noticeTitleTv.setText(noticeTitle);
            //
            switch (noticeTitle){
                case "ট্রাক":
                case "Truck":
                    holder.noticeIv.setImageResource(R.drawable.ic_truck);
                    break;
                case "পিকাপ":
                case "Pickup":
                    holder.noticeIv.setImageResource(R.drawable.ic_pickup);
                    break;
                case "কাভার্ড ভ্যান":
                case "Covered Van":
                    holder.noticeIv.setImageResource(R.drawable.ic_covered_van);
                    break;
                case "ফ্রিজার ভ্যান":
                case "Freezer Van":
                    holder.noticeIv.setImageResource(R.drawable.ic_freezer_van);
                    break;
                case "ট্রেইলর":
                case "Trailer":
                case "লো বেড":
                case "Lo bet":
                    holder.noticeIv.setImageResource(R.drawable.ic_trailer);
                    break;
                case "কার":
                case "Car":
                    holder.noticeIv.setImageResource(R.drawable.ic_car);
                    break;
                case "মাইক্রোবাস":
                case "Microbus":
                    holder.noticeIv.setImageResource(R.drawable.ic_microbus);
                    break;
                case "অ্যাম্বুলেন্স":
                case "Ambulance":
                    holder.noticeIv.setImageResource(R.drawable.ic_ambulance);
                    break;
                case "ড্রাম্প ট্রাক":
                case "Dump Truck":
                    holder.noticeIv.setImageResource(R.drawable.ic_dump_truck);
                    break;
                    //Equipment
                case "এক্সকাভেটর":
                case "Excavator":
                    holder.noticeIv.setImageResource(R.drawable.ic_excavator);
                    break;
                case "রাইস ট্রান্সপ্লান্টার":
                case "Rice Transplanter":
                    holder.noticeIv.setImageResource(R.drawable.ic_rice_transplanter);
                    break;
                case "ট্রাক্টর":
                case "Tractor":
                    holder.noticeIv.setImageResource(R.drawable.ic_tractor);
                    break;
                case "হারভেস্টার":
                case "Harvester":
                    holder.noticeIv.setImageResource(R.drawable.ic_harvester);
                    break;
                case "বাসা শিফটিং":
                case "Home Shifting":
                    holder.noticeIv.setImageResource(R.drawable.ic_home_shift);
                    break;
                case "ড্রাইভার":
                case "Driver":
                    holder.noticeIv.setImageResource(R.drawable.ic_driver);
                    break;
                case "মেকানিক":
                case "Mechanic":
                    holder.noticeIv.setImageResource(R.drawable.ic_mechanic);
                    break;
                case "ইলেক্ট্রিশিয়ান":
                case "Electrician":
                    holder.noticeIv.setImageResource(R.drawable.ic_electrician);
                    break;
                case "পানির লাইনের মিস্ত্রি":
                case "Plumber":
                    holder.noticeIv.setImageResource(R.drawable.ic_plumbing);
                    break;
                case "চুলার মিস্ত্রি":
                case "Stove mechanic":
                    holder.noticeIv.setImageResource(R.drawable.ic_stove_technician);
                    break;
            }
        }
        else {
            holder.noticeTitleTv.setText(noticeTitle);
            holder.noticeIv.setImageResource(R.drawable.ic_notification);
        }


        //Click to Next
        /*holder.itemView.setOnClickListener(v -> {
            if (noticeCategory.equals(MyUtils.NOTICE_TYPE_POST) ||
                    noticeCategory.equals(MyUtils.NOTICE_TYPE_BID)){
                //
                MyToast.showShort(context, noticeDesc);
            }
        }); */

    }

    @Override
    public int getItemCount() {
        return noticeArrayList.size();
    }

    static class HolderViewNotice extends RecyclerView.ViewHolder{

        ImageView noticeIv, noticeImageIV;
        TextView noticeTitleTv, noticeDescriptionTv, noticeTimeTv;
        RelativeLayout noticeCard;

        public HolderViewNotice(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            //init views
            noticeCard = itemView.findViewById(R.id.noticeCard);
            noticeIv = itemView.findViewById(R.id.noticeIv);
            noticeImageIV = itemView.findViewById(R.id.noticeImageIV);
            noticeTitleTv = itemView.findViewById(R.id.noticeTitleTv);
            noticeDescriptionTv = itemView.findViewById(R.id.noticeDescriptionTv);
            noticeTimeTv = itemView.findViewById(R.id.noticeTimeTv);

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

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
        notifyDataSetChanged();
    }

}
