package com.krishibarirangpur.bdhelper.sharedActivity;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.ReviewAdapter;
import com.krishibarirangpur.bdhelper.databinding.ActivityRatingReviewBinding;
import com.krishibarirangpur.bdhelper.model.ReviewModel;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class RatingReviewActivity extends BaseActivity {

    private ActivityRatingReviewBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String userId, user_type;
    
    private ArrayList<ReviewModel> allReviewList = new ArrayList<>();
    private ArrayList<ReviewModel> filteredList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    
    private boolean isNewestFirst = true;
    private int currentFilterStar = 0; // 0 means 'All'
    private Double profileRating = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rating_review);

        //init views
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        // প্রথমে Intent থেকে userId নেওয়ার চেষ্টা করবে
        userId = getIntent().getStringExtra(MyUtils.userId);
        user_type = getIntent().getStringExtra(MyUtils.USER_TYPE);

        // যদি Intent এ না থাকে, তাহলে FirebaseAuth থেকে নিবে
        if (TextUtils.isEmpty(userId)) {
            if (firebaseAuth.getCurrentUser() == null) {
                finish();
                return;
            }
            userId = firebaseAuth.getCurrentUser().getUid();
        }
        else {
            if (user_type.equals(MyUtils.PARTNER)){
                binding.appBarTitleTV.setText(R.string.customer_rating_review);
            }
            else {
                binding.appBarTitleTV.setText(R.string.partner_rating_review);
            }
        }

        String userType = getIntent().getStringExtra(MyUtils.USER_TYPE);
        if (userType != null && userType.equals(MyUtils.PARTNER)) {
            binding.takeTripBtn.setVisibility(View.GONE);
            binding.getTripBtn.setVisibility(View.VISIBLE);
        }

        //init views
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        setupRecyclerView();
        setupFilterButtons();
        setupSortButton();
        
        loadProfileInfo(); // প্রোফাইল থেকে রেটিং লোড করা
        loadUserAllReviews();
    }

    private void loadProfileInfo() {
        db.collection("users")
                .document(userId)
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        profileRating = documentSnapshot.getDouble("rating");
                        if (allReviewList.isEmpty()) {
                            calculateAndShowSummary();
                        }
                    }
                });
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(this, filteredList);
        binding.reviewRv.setAdapter(reviewAdapter);
    }

    private void setupFilterButtons() {
        binding.allFilterBtn.setOnClickListener(v -> filterReviews(0));
        binding.fiveStarFilterBtn.setOnClickListener(v -> filterReviews(5));
        binding.fourStarFilterBtn.setOnClickListener(v -> filterReviews(4));
        binding.threeStarFilterBtn.setOnClickListener(v -> filterReviews(3));
        binding.twoStarFilterBtn.setOnClickListener(v -> filterReviews(2));
        binding.oneStarFilterBtn.setOnClickListener(v -> filterReviews(1));
    }

    private void setupSortButton() {
        binding.sortTv.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, binding.sortTv, Gravity.END);
            popupMenu.getMenu().add("নতুন থেকে পুরানো");
            popupMenu.getMenu().add("পুরানো থেকে নতুন");

            popupMenu.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                binding.sortTv.setText(title);
                
                if (title.equals("নতুন থেকে পুরানো")) {
                    isNewestFirst = true;
                } else if (title.equals("পুরানো থেকে নতুন")) {
                    isNewestFirst = false;
                }
                
                sortAndFilter();
                return true;
            });
            popupMenu.show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortAndFilter() {
        // Sort the main list
        Collections.sort(allReviewList, new Comparator<ReviewModel>() {
            @Override
            public int compare(ReviewModel r1, ReviewModel r2) {
                if (isNewestFirst) {
                    return Long.compare(r2.getCreatedAt(), r1.getCreatedAt());
                } else {
                    return Long.compare(r1.getCreatedAt(), r2.getCreatedAt());
                }
            }
        });

        // Re-apply filter
        applyFilter(currentFilterStar);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterReviews(int stars) {
        currentFilterStar = stars;
        applyFilter(stars);
        
        // Update Button UI
        if (stars == 0) updateFilterButtonUI(binding.allFilterBtn);
        else if (stars == 5) updateFilterButtonUI(binding.fiveStarFilterBtn);
        else if (stars == 4) updateFilterButtonUI(binding.fourStarFilterBtn);
        else if (stars == 3) updateFilterButtonUI(binding.threeStarFilterBtn);
        else if (stars == 2) updateFilterButtonUI(binding.twoStarFilterBtn);
        else if (stars == 1) updateFilterButtonUI(binding.oneStarFilterBtn);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void applyFilter(int stars) {
        filteredList.clear();
        if (stars == 0) {
            filteredList.addAll(allReviewList);
        } else {
            for (ReviewModel review : allReviewList) {
                if ((int) review.getRating() == stars) {
                    filteredList.add(review);
                }
            }
        }
        reviewAdapter.notifyDataSetChanged();
        
        if (filteredList.isEmpty()) {
            binding.reviewRv.setVisibility(View.GONE);
            binding.reviewLl.setVisibility(View.VISIBLE);
        } else {
            binding.reviewRv.setVisibility(View.VISIBLE);
            binding.reviewLl.setVisibility(View.GONE);
        }
    }

    private void updateFilterButtonUI(Button selectedBtn) {
        // Reset all buttons
        Button[] buttons = {binding.allFilterBtn, binding.fiveStarFilterBtn, binding.fourStarFilterBtn,
                           binding.threeStarFilterBtn, binding.twoStarFilterBtn, binding.oneStarFilterBtn};
        
        for (Button btn : buttons) {
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            btn.setTextColor(Color.BLACK);
        }
        
        // Highlight selected
        selectedBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryMid)));
        selectedBtn.setTextColor(Color.WHITE);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadUserAllReviews() {
        db.collection("reviews")
                .whereEqualTo("vendorId", userId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        MyToast.showShort(this, "Error: " + error.getMessage());
                        return;
                    }

                    allReviewList.clear();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (var doc : snapshot.getDocuments()) {
                            ReviewModel model = doc.toObject(ReviewModel.class);
                            if (model != null) allReviewList.add(model);
                        }
                    }

                    calculateAndShowSummary();
                    sortAndFilter(); // Sort and filter based on current states
                });
    }

    private void calculateAndShowSummary() {
        int totalReviews = allReviewList.size();
        binding.yourReviewCountTv.setText(String.format(Locale.getDefault(), "আপনার রিভিউ (%d)", totalReviews));

        if (totalReviews == 0) {
            // যদি রিভিউ না থাকে, প্রোফাইল রেটিং দেখাও (ProfileFragment এর মতো)
            float displayRating;
            if (profileRating != null && profileRating > 0) {
                displayRating = profileRating.floatValue();
            } else {
                displayRating = 5.0f; // ডিফল্ট ৫.০
            }

            binding.averageRatingTv.setText(String.format(Locale.getDefault(), "%.1f", displayRating));

            // যদি রেটিং ৪.৫ এর উপরে থাকে (যেমন ৫.০), তবে ৫-স্টার বার ১০০% আপডেট করা
            if (displayRating >= 4.5f) {
                binding.fiveStarProgress.setProgress(100);
                binding.fiveStarPercentTv.setText("১০০%");

                // অন্যদের ০ করে রাখা
                binding.fourStarProgress.setProgress(0);
                binding.threeStarProgress.setProgress(0);
                binding.twoStarProgress.setProgress(0);
                binding.oneStarProgress.setProgress(0);

                binding.fourStarPercentTv.setText("০%");
                binding.threeStarPercentTv.setText("০%");
                binding.twoStarPercentTv.setText("০%");
                binding.oneStarPercentTv.setText("০%");
            } else {
                resetProgressBars();
            }
            return;
        }

        float totalRatingSum = 0;
        int count5 = 0, count4 = 0, count3 = 0, count2 = 0, count1 = 0;

        for (ReviewModel review : allReviewList) {
            float rating = review.getRating();
            totalRatingSum += rating;

            int star = (int) rating;
            switch (star) {
                case 5: count5++; break;
                case 4: count4++; break;
                case 3: count3++; break;
                case 2: count2++; break;
                case 1: count1++; break;
            }
        }

        float average = totalRatingSum / totalReviews;
        binding.averageRatingTv.setText(String.format(Locale.getDefault(), "%.1f", average));

        // Update ProgressBars and Percentages
        updateProgress(binding.fiveStarProgress, binding.fiveStarPercentTv, count5, totalReviews);
        updateProgress(binding.fourStarProgress, binding.fourStarPercentTv, count4, totalReviews);
        updateProgress(binding.threeStarProgress, binding.threeStarPercentTv, count3, totalReviews);
        updateProgress(binding.twoStarProgress, binding.twoStarPercentTv, count2, totalReviews);
        updateProgress(binding.oneStarProgress, binding.oneStarPercentTv, count1, totalReviews);
    }

    private void updateProgress(android.widget.ProgressBar progressBar, android.widget.TextView percentTv, int count, int total) {
        int percent = (count * 100) / total;
        progressBar.setProgress(percent);
        percentTv.setText(String.format(Locale.getDefault(), "%d%%", percent));
    }

    private void resetProgressBars() {
        binding.fiveStarProgress.setProgress(0);
        binding.fourStarProgress.setProgress(0);
        binding.threeStarProgress.setProgress(0);
        binding.twoStarProgress.setProgress(0);
        binding.oneStarProgress.setProgress(0);
        
        binding.fiveStarPercentTv.setText("০%");
        binding.fourStarPercentTv.setText("০%");
        binding.threeStarPercentTv.setText("০%");
        binding.twoStarPercentTv.setText("০%");
        binding.oneStarPercentTv.setText("০%");
    }
}
