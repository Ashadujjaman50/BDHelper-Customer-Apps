package com.krishibarirangpur.bdhelper.partner;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivitySettingBinding;
import com.krishibarirangpur.bdhelper.myUtils.BaseActivity;
import com.krishibarirangpur.bdhelper.myUtils.ThemeUtil;
import com.krishibarirangpur.bdhelper.partnerFragment.SettingFragment;

public class SettingActivity extends BaseActivity {

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        String userType = getIntent().getStringExtra("user_type");
        if (userType != null && userType.equals("partner")) {
            loadFragment(new SettingFragment());
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
