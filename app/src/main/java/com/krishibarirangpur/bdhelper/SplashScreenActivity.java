package com.krishibarirangpur.bdhelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.authentication.LoginActivity;
import com.krishibarirangpur.bdhelper.authentication.UserTypeSelectionActivity;
import com.krishibarirangpur.bdhelper.databinding.ActivitySplashScreenBinding;
import com.krishibarirangpur.bdhelper.introScreen.IntroActivity;
import com.krishibarirangpur.bdhelper.partner.DashboardActivity;
import com.krishibarirangpur.bdhelper.user.MainActivity;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private AppUpdateManager appUpdateManager;
    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        binding.logoIv.setAnimation(topAnim);
        binding.formTv.setAnimation(bottomAnim);

        Typeface customFont = ResourcesCompat.getFont(this, R.font.amaranth);
        TextView textView = findViewById(R.id.textView);
        textView.setTypeface(customFont);

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        checkInAppUpdate();
    }

    private void checkInAppUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // An immediate update is available and allowed.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            IMMEDIATE_APP_UPDATE_REQ_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    // If the update flow fails, proceed to the app.
                    proceedToNextScreen();
                }
            } else {
                // No update is available or an immediate update is not allowed.
                proceedToNextScreen();
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            // If checking for update fails, proceed to the app.
            proceedToNextScreen();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                // The user has canceled the update. Since this is a forced update,
                // we can show a message and close the app.
                MyToast.showShort(this, "You must update the app to continue using it.");
                finish();
            } else if (resultCode != RESULT_OK) {
                // The update failed or was otherwise interrupted.
                // We can try to request the update again or let the user proceed.
                // For a forced update, it's better to try again or close.
                checkInAppUpdate();
            }
            // If resultCode is RESULT_OK, the app will be restarted by the Play Store.
            // No need to call proceedToNextScreen().
        }
    }

    private void proceedToNextScreen() {
        // ✅ Already logged-in user check
        if (mAuth.getCurrentUser() != null) {
            new Handler().postDelayed(this::gotoNextActivity, 2500);
        } else {
            new Handler().postDelayed(() -> {
                SharedPrefHelper prefHelper = new SharedPrefHelper(this);
                boolean isFirstTime = prefHelper.getBoolean("first_time", true);

                Intent intent;
                if (isFirstTime) {
                    intent = new Intent(this, IntroActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 3000);
        }
    }

    private void gotoNextActivity() {
        if (mAuth.getCurrentUser() == null) {
            // কোনো কারণে null হলে Login-এ পাঠিয়ে দিন
            goToLoginWithError("লগইন অবস্থা পাওয়া যায়নি!");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Intent intent = null;

                        if (document.exists()) {
                            String userType = document.getString("userType");

                            if ("customer".equals(userType)) {
                                intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            } else if ("partner".equals(userType)) {
                                intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                            }

                        } else {
                            intent = new Intent(SplashScreenActivity.this, UserTypeSelectionActivity.class);
                        }

                        if (intent != null) {
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            MyToast.showShort(this, "ইউজারের ধরণ নির্ধারণ করা যায়নি!");
                        }

                    } else {
                        MyToast.showShort(this, "ইউজার তথ্য চেক করতে ব্যর্থ: " + task.getException().getMessage());
                    }
                });
    }

    private void goToLoginWithError(String errorMessage) {
        MyToast.showShort(this, errorMessage);
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
