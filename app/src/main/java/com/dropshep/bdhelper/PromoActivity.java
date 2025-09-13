package com.dropshep.bdhelper;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.dropshep.bdhelper.databinding.ActivityPromoBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.ThemeUtil;

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