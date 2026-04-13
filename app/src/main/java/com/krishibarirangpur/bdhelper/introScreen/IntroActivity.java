package com.krishibarirangpur.bdhelper.introScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.authentication.LoginActivity;
import com.krishibarirangpur.bdhelper.databinding.ActivityIntroBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.network.NotificationPermissionHelper;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;

public class IntroActivity extends BaseActivity {

    private ActivityIntroBinding binding;
    private static final String KEY_FIRST_TIME_NOTIFICATION_REQUESTED = "notification_requested";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        //Post Notification Enable
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(this);
        boolean alreadyAsked = sharedPrefHelper.getBoolean(KEY_FIRST_TIME_NOTIFICATION_REQUESTED, false);

        if (!alreadyAsked) {
            NotificationPermissionHelper.requestPermissionAndSubscribe(this);
            sharedPrefHelper.putBoolean(KEY_FIRST_TIME_NOTIFICATION_REQUESTED, true);
        }

        binding.banglaBtn.setOnClickListener(v -> {
            LocaleHelper.setLocale(this, "bn");
            new SharedPrefHelper(this).putBoolean("first_time", false);
            goToLogin();
        });

        binding.englishBtn.setOnClickListener(v -> {
            LocaleHelper.setLocale(this, "en");
            new SharedPrefHelper(this).putBoolean("first_time", false);
            goToLogin();
        });
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
