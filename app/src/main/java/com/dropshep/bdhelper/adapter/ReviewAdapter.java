package com.dropshep.bdhelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.model.ReviewModel;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // ✅ Firebase থেকে Customer নাম ও ছবি লোড করা
        db.collection("users")
                .document(model.getCustomerId())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String name = snapshot.getString("name");
                        String profileUrl = snapshot.getString("profileImage");

                        holder.customerNameTv.setText(name != null ? name : "Customer");

                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            Glide.with(context)
                                    .load(profileUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .into(holder.profileImage);
                        } else {
                            holder.profileImage.setImageResource(R.drawable.ic_profile);
                        }
                    }
                })
                .addOnFailureListener(e -> holder.customerNameTv.setText("Customer"));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView customerNameTv, customerRatingTv, customerReviewTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            customerNameTv = itemView.findViewById(R.id.customerNameTv);
            customerRatingTv = itemView.findViewById(R.id.customerRatingTv);
            customerReviewTv = itemView.findViewById(R.id.customerReviewTv);
        }
    }
}
