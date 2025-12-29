package com.krishibarirangpur.bdhelper.authentication;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityRegistrationBinding;
import com.krishibarirangpur.bdhelper.userFragment.CustomerRegisterFragment;
import com.krishibarirangpur.bdhelper.partnerFragment.PartnerRegisterFragment;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;

public class RegistrationActivity extends BaseActivity {

    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট করব
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        //setContentView(R.layout.activity_sign_up);

        String userType = getIntent().getStringExtra("user_type");
        String userSignWith = getIntent().getStringExtra("userSignWith");

        Fragment targetFragment;
        if (userType != null && userType.equals("partner")) {
            targetFragment = new PartnerRegisterFragment();
        } else {
            targetFragment = new CustomerRegisterFragment();
        }
        // 🟡 userSignWith data Fragment-এ পাঠানো হচ্ছে
        Bundle bundle = new Bundle();
        bundle.putString("userSignWith", userSignWith);
        targetFragment.setArguments(bundle);

        loadFragment(targetFragment);

        binding.backBtn.setOnClickListener(v -> {
            finishOnBack();
        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}