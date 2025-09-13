package com.dropshep.bdhelper.partner;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.ActivityPaymentBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partnerFragment.MoreFragment;
import com.dropshep.bdhelper.partnerFragment.PaymentAccountFragment;
import com.dropshep.bdhelper.partnerFragment.PaymentHistoryFragment;
import com.dropshep.bdhelper.partnerFragment.RentFragment;

public class PaymentActivity extends BaseActivity {

    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);

        String type = getIntent().getStringExtra("type");
        if (type != null && type.equals("payment_method")){
            binding.subCategoryNameTv.setText(getText(R.string.payment_account));
            loadFragment(new PaymentAccountFragment());
        }
        else if (type != null && type.equals("payment_history")){
            binding.subCategoryNameTv.setText(getText(R.string.payment_history));
            loadFragment(new PaymentHistoryFragment());
        }


        binding.backBtn.setOnClickListener(v -> finishOnBack());

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}