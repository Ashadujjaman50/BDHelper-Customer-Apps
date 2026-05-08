package com.krishibarirangpur.bdhelper.sharedActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.FirebaseMessaging.FCMTokenManager;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.authentication.LoginActivity;
import com.krishibarirangpur.bdhelper.authentication.UserTypeSelectionActivity;
import com.krishibarirangpur.bdhelper.databinding.ActivitySplashScreenBinding;
import com.krishibarirangpur.bdhelper.introScreen.IntroActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.DashboardActivity;
import com.krishibarirangpur.bdhelper.userActivity.customer.MainActivity;
import com.krishibarirangpur.bdhelper.utils.authWidget.UserAction;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.core.AppUpdateChecker;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AppUpdateChecker appUpdateChecker;
    private SharedPrefHelper sharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        //init views
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        sharedPrefHelper = new SharedPrefHelper(this);

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        binding.logoIv.setAnimation(topAnim);
        binding.formTv.setAnimation(bottomAnim);

        Typeface customFont = ResourcesCompat.getFont(this, R.font.amaranth);
        TextView textView = findViewById(R.id.textView);
        textView.setTypeface(customFont);

        //Token update
        FCMTokenManager.updateFCMToken();

        // অ্যাপ আপডেট চেক করা হচ্ছে
        appUpdateChecker = new AppUpdateChecker(this);
        appUpdateChecker.checkForUpdate(this::proceedToNextScreen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUpdateChecker.REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                MyToast.showShort(this, "You must update the app to continue using it.");
                finish();
            } else if (resultCode != RESULT_OK) {
                // আপডেট ব্যর্থ হলে আবার চেক করো
                appUpdateChecker.checkForUpdate(this::proceedToNextScreen);
            }
        }
    }

    private void proceedToNextScreen() {
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
                startNextActivity(intent);
            }, 3000);
        }
    }

    private void gotoNextActivity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            goToLoginWithError("লগইন অবস্থা পাওয়া যায়নি!");
            return;
        }

        db.collection("users").document(currentUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String userType = document.getString("userType");
                            String verifyStatus = document.getString("verifyStatus");
                            Class<?> targetClass;

                            // 🔴 Blocked / Rejected User
                            if ("rejected".equalsIgnoreCase(verifyStatus)) {
                                UserAction.blockAccountCheck(this);
                                return;
                            }


                            if ("customer".equals(userType)) {
                                sharedPrefHelper.putString(MyUtils.USER_LOGIN_MODE, "customer");
                                targetClass = MainActivity.class;
                            } else if ("partner".equals(userType)) {
                                sharedPrefHelper.putString(MyUtils.USER_LOGIN_MODE, "partner");
                                targetClass = DashboardActivity.class;
                            } else {
                                mAuth.signOut();
                                goToLoginWithError("অন্য email দিয়ে চেষ্টা করুন");
                                return;
                            }
                            startNextActivity(new Intent(this, targetClass));
                        } else {
                            startNextActivity(new Intent(this, UserTypeSelectionActivity.class));
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        MyToast.showShort(this, "ইউজার তথ্য চেক করতে ব্যর্থ: " + error);
                    }
                });
    }


    private void goToLoginWithError(String errorMessage) {
        if (errorMessage != null) MyToast.showShort(this, errorMessage);
        startNextActivity(new Intent(this, LoginActivity.class));
    }

    private void startNextActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
