package com.krishibarirangpur.bdhelper.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityUserTypeSelectionBinding;
import com.krishibarirangpur.bdhelper.myUtils.BaseActivity;
import com.krishibarirangpur.bdhelper.myUtils.MyUtils;
import com.krishibarirangpur.bdhelper.myUtils.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.myUtils.ThemeUtil;

public class UserTypeSelectionActivity extends BaseActivity {

    private ActivityUserTypeSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_type_selection);

        //init views
        SharedPrefHelper prefHelper = new SharedPrefHelper(this);
        String signInWith = getIntent().getStringExtra(MyUtils.USER_SIGN_IN_WITH);

        // fallback from shared pref if null
        if (signInWith == null) {
            signInWith = prefHelper.getString("userSignWith", "");
        }

        MyUtils.USER_SIGN_IN_WITH = signInWith;
        // Save to SharedPreferences
        prefHelper.putString("userSignWith", signInWith); // always save lates


        binding.customerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserTypeSelectionActivity.this, RegistrationActivity.class);
            intent.putExtra("user_type", "customer");
            intent.putExtra("userSignWith", MyUtils.USER_SIGN_IN_WITH);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.partnerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserTypeSelectionActivity.this, RegistrationActivity.class);
            intent.putExtra("user_type", "partner");
            intent.putExtra("userSignWith", MyUtils.USER_SIGN_IN_WITH);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }
}