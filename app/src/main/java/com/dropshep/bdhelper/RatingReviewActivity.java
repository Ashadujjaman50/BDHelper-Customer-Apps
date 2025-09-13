package com.dropshep.bdhelper;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.dropshep.bdhelper.databinding.ActivityRatingReviewBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partnerFragment.SettingFragment;

public class RatingReviewActivity extends BaseActivity {

    private ActivityRatingReviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rating_review);

        String userType = getIntent().getStringExtra("user_type");
        if (userType != null && userType.equals("partner")) {
            binding.takeTripBtn.setVisibility(View.GONE);
            binding.getTripBtn.setVisibility(View.VISIBLE);
        }

        //init views
        binding.backBtn.setOnClickListener(v -> finishOnBack());


    }
}