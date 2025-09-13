package com.dropshep.bdhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.model.ModelSubCategory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSubCategory extends RecyclerView.Adapter<AdapterSubCategory.ViewHolder> {

    List<ModelSubCategory> modelSubCategoryList;
    Context context;

    private OnItemClickListener mListener;

    public AdapterSubCategory(List<ModelSubCategory> modelSubCategoryList, Context context) {
        this.modelSubCategoryList = modelSubCategoryList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_category_card_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ModelSubCategory modelSubCategory = modelSubCategoryList.get(position);
        holder.subCategoryName.setText(modelSubCategory.getSubCategoryName());

        if (modelSubCategory.getSubCategoryImage() != null ) {
            Glide.with(context)
                    .load(modelSubCategory.getSubCategoryImage())
                    .into(holder.subCategoryImage);
            //Picasso.get().load(modelSubCategory.getSubCategoryImage()).into(holder.subCategoryImage);
        }
        else {
            holder.subCategoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_transport));
        }
    }

    @Override
    public int getItemCount() {
        return modelSubCategoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView subCategoryImage;
        TextView subCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subCategoryName = itemView.findViewById(R.id.categoryName);
            subCategoryImage = itemView.findViewById(R.id.categoryImage);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(v, position);
                }
            }

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }
}
