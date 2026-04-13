package com.krishibarirangpur.bdhelper.sharedActivity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityPromoBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;

public class PromoActivity extends BaseActivity {

    private ActivityPromoBinding binding;
    String discountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_promo);

        //init views
        discountType = getIntent().getStringExtra("discountType");


        binding.backBtn.setOnClickListener(v -> finishOnBack());

        if (discountType.equals("promo")){
            //Promo Code
            binding.titleTv.setText(getString(R.string.promo));
            binding.messageTV.setText("No promo available");
        }
        else {
            binding.titleTv.setText(getString(R.string.discount));
            binding.messageTV.setText("Coming soon..");
        }

    }
}