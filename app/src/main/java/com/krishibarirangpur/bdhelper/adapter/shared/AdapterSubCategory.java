package com.krishibarirangpur.bdhelper.adapter.shared;

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
import com.krishibarirangpur.bdhelper.model.SubCategoryModel;

import java.util.List;

public class AdapterSubCategory extends RecyclerView.Adapter<AdapterSubCategory.ViewHolder> {

    List<SubCategoryModel> subCategoryModelList;
    Context context;

    private OnItemClickListener mListener;

    public AdapterSubCategory(List<SubCategoryModel> subCategoryModelList, Context context) {
        this.subCategoryModelList = subCategoryModelList;
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
        final SubCategoryModel subCategoryModel = subCategoryModelList.get(position);
        holder.subCategoryName.setText(subCategoryModel.getSubCategoryName());

        if (subCategoryModel.getIconResId() != 0) {
            holder.subCategoryImage.setImageResource(subCategoryModel.getIconResId());
        } else {
            holder.subCategoryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_transport));
        }

    }

    @Override
    public int getItemCount() {
        return subCategoryModelList.size();
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
