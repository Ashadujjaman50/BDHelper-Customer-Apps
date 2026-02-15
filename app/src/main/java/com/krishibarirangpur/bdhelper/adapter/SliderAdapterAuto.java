package com.krishibarirangpur.bdhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.SlideImage;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapterAuto extends SliderViewAdapter<SliderAdapterAuto.SliderAdapterVH> {

    Context context;
    ArrayList<SlideImage> slideImageArrayList;

    public SliderAdapterAuto(Context context, ArrayList<SlideImage> slideImageArrayList) {
        this.context = context;
        this.slideImageArrayList = slideImageArrayList;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, parent, false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH holder, int position) {
        SlideImage slideImage = slideImageArrayList.get(position);

        holder.textViewDescription.setText(slideImage.getTitle());

        if (slideImage.getImageUrl() != null && !slideImage.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(slideImage.getImageUrl())
                    .placeholder(R.drawable.demo_help_slide1)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.imageViewBackground, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(slideImage.getImageUrl()).into(holder.imageViewBackground);
                        }
                    });
        }


    }

    @Override
    public int getCount() {
        return slideImageArrayList.size();
    }

    public void updateList(List<SlideImage> newList) {
        slideImageArrayList.clear();
        slideImageArrayList.addAll(newList);
        notifyDataSetChanged(); // smooth refresh
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder{

        ImageView imageViewBackground;
        TextView textViewDescription;
        public SliderAdapterVH(View itemView) {
            super(itemView);

            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
        }
    }

}
