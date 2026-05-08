package com.krishibarirangpur.bdhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.model.ReviewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.utils.CommonClass;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ReviewModel> reviewList;
    private final FirebaseFirestore db;

    public ReviewAdapter(Context context, ArrayList<ReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_review_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewModel model = reviewList.get(position);

        holder.customerReviewTv.setText(model.getReview());
        holder.customerRatingTv.setText(String.valueOf(model.getRating()));
        holder.ratingDateTv.setText(CommonClass.formatTime(String.valueOf(model.getCreatedAt()), "dd MMM, yyyy") );


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView ratingDateTv, customerRatingTv, customerReviewTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            ratingDateTv = itemView.findViewById(R.id.ratingDateTv);
            customerRatingTv = itemView.findViewById(R.id.customerRatingTv);
            customerReviewTv = itemView.findViewById(R.id.customerReviewTv);
        }
    }
}
