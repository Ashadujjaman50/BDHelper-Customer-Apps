package com.dropshep.bdhelper.introScreen;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.authentication.LoginActivity;
import com.dropshep.bdhelper.databinding.ActivityIntroBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.LocaleHelper;
import com.dropshep.bdhelper.myUtils.NotificationPermissionHelper;
import com.dropshep.bdhelper.myUtils.SharedPrefHelper;
import com.dropshep.bdhelper.myUtils.ThemeUtil;

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
