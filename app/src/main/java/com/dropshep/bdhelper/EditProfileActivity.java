package com.dropshep.bdhelper;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dropshep.bdhelper.databinding.ActivityEditProfileBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partnerFragment.EditPartnerProfileFragment;
import com.dropshep.bdhelper.userFragment.EditUserProfileFragment;

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