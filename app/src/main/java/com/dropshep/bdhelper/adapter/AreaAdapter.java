package com.dropshep.bdhelper.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.model.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ExampleViewHolder> implements Filterable {
    private final List<Area> areaList;
    private final List<Area> areaListFull;
    private OnItemClickListener mListener;

    class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView cityName;


        ExampleViewHolder(View itemView) {
            super(itemView);

            cityName = itemView.findViewById(R.id.text_view1);

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

    public AreaAdapter(List<Area> areaList) {
        this.areaList = areaList;
        areaListFull = new ArrayList<>(areaList);
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_location_item_layout,
                parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {

        Area currentArea = areaList.get(position);
        holder.cityName.setText(currentArea.getAreaName());
    }

    @Override
    public int getItemCount() {
        return areaList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private final Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Area> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(areaListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();

                for (Area item : areaListFull) {
                    if (item.getCityId().toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            areaList.clear();
            areaList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
