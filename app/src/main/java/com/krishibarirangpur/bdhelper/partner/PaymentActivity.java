package com.krishibarirangpur.bdhelper.partner;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityPaymentBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.krishibarirangpur.bdhelper.partnerFragment.PaymentAccountFragment;
import com.krishibarirangpur.bdhelper.partnerFragment.PaymentHistoryFragment;

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