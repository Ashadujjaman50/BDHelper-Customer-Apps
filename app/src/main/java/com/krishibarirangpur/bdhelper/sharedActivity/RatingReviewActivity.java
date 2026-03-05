package com.krishibarirangpur.bdhelper.sharedActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.ReviewAdapter;
import com.krishibarirangpur.bdhelper.databinding.ActivityRatingReviewBinding;
import com.krishibarirangpur.bdhelper.model.ReviewModel;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class RatingReviewActivity extends BaseActivity {

    private ActivityRatingReviewBinding binding;

    private FirebaseAuth firebaseAuth;
    private String vendorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rating_review);

        //init views
        firebaseAuth = FirebaseAuth.getInstance();
        assert firebaseAuth.getCurrentUser() != null;
        vendorId = firebaseAuth.getCurrentUser().getUid();


        String userType = getIntent().getStringExtra("user_type");
        if (userType != null && userType.equals("partner")) {
            binding.takeTripBtn.setVisibility(View.GONE);
            binding.getTripBtn.setVisibility(View.VISIBLE);
        }

        //init views
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        loadAllVendorReviews();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAllVendorReviews() {
        ArrayList<ReviewModel> reviewList = new ArrayList<>();
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviewList);
        binding.reviewRv.setAdapter(reviewAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews")
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        MyToast.showShort(this, "Error: " + error.getMessage());
                        return;
                    }

                    reviewList.clear();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (var doc : snapshot.getDocuments()) {
                            ReviewModel model = doc.toObject(ReviewModel.class);
                            if (model != null) reviewList.add(model);
                        }
                    }

                    reviewAdapter.notifyDataSetChanged();

                    if (reviewList.isEmpty()) {
                        binding.reviewRv.setVisibility(View.GONE);
                        binding.reviewLl.setVisibility(View.VISIBLE);
                    } else {
                        binding.reviewRv.setVisibility(View.VISIBLE);
                        binding.reviewLl.setVisibility(View.GONE);
                    }
                });
    }

}