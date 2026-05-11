package com.krishibarirangpur.bdhelper.userActivity.partner;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityPaymentBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.userFragment.partner.paymentFragment.PaymentAccountFragment;
import com.krishibarirangpur.bdhelper.userFragment.partner.paymentFragment.PaymentHistoryFragment;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

public class PaymentActivity extends BaseActivity {

    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);

        String type = getIntent().getStringExtra(MyUtils.ACTIVITY_TYPE);
        if (type != null && type.equals(MyUtils.PAYMENT_METHOD_FRAG)){
            binding.subCategoryNameTv.setText(getText(R.string.payment_account));
            loadFragment(new PaymentAccountFragment());
        }
        else if (type != null && type.equals(MyUtils.PAYMENT_HISTORY_FRAG)){
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