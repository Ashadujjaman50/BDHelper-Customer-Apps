package com.krishibarirangpur.bdhelper.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityUserTypeSelectionBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;

public class UserTypeSelectionActivity extends BaseActivity {

    private ActivityUserTypeSelectionBinding binding;
    private SharedPrefHelper prefHelper;
    private String signInWith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_type_selection);
        prefHelper = new SharedPrefHelper(this);

        initSignInMethod();
        initClickListeners();
    }

    private void initSignInMethod() {
        signInWith = getIntent().getStringExtra(MyUtils.USER_SIGN_IN_WITH);

        if (signInWith == null || signInWith.isEmpty()) {
            signInWith = prefHelper.getString("userSignWith", "");
        }

        // Save latest
        MyUtils.USER_SIGN_IN_WITH = signInWith;
        prefHelper.putString("userSignWith", signInWith);
    }

    private void initClickListeners() {
        binding.customerBtn.setOnClickListener(v -> openRegistration("customer"));
        binding.partnerBtn.setOnClickListener(v -> openRegistration("partner"));
    }

    private void openRegistration(String userType) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("user_type", userType);
        intent.putExtra("userSignWith", signInWith);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}