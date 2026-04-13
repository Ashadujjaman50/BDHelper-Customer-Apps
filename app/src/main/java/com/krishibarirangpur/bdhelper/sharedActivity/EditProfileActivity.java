package com.krishibarirangpur.bdhelper.sharedActivity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityEditProfileBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;
import com.krishibarirangpur.bdhelper.userFragment.partner.profileFragment.EditPartnerProfileFragment;
import com.krishibarirangpur.bdhelper.userFragment.customer.profileFragment.EditUserProfileFragment;

public class EditProfileActivity extends BaseActivity {

    private ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);


        String userType = getIntent().getStringExtra("user_type");
        if (userType != null && userType.equals("customer")) {
            loadFragment(new EditUserProfileFragment());
        }
        else {
            loadFragment(new EditPartnerProfileFragment());
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