package com.dropshep.bdhelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.dropshep.bdhelper.authentication.LoginActivity;
import com.dropshep.bdhelper.authentication.UserTypeSelectionActivity;
import com.dropshep.bdhelper.databinding.ActivitySplashScreenBinding;
import com.dropshep.bdhelper.introScreen.IntroActivity;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.SharedPrefHelper;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partner.DashboardActivity;
import com.dropshep.bdhelper.user.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        //init views
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        binding.logoIv.setAnimation(topAnim);
        binding.formTv.setAnimation(bottomAnim);

        Typeface customFont = ResourcesCompat.getFont(this, R.font.amaranth);
        TextView textView = findViewById(R.id.textView);
        textView.setTypeface(customFont);

        // ✅ Already logged-in user check
        if (mAuth.getCurrentUser() != null) {
            gotoNextActivity();
        }
        else {
            new Handler().postDelayed(() -> {
                SharedPrefHelper prefHelper = new SharedPrefHelper(this);
                boolean isFirstTime = prefHelper.getBoolean("first_time", true);

                Intent intent;
                if (isFirstTime) {
                    intent = new Intent(this, IntroActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity.class); // অথবা MainActivity
                }

                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 3500);
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
                            // User exists in "users" table, go to MainActivity or Dashboard
                            String userType = document.getString("userType");

                            if ("customer".equals(userType)) {
                                intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            } 
                            else if ("partner".equals(userType)) {
                                intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                            } 

                        } 
                        else {
                            // New user, go to user type selection
                            intent = new Intent(SplashScreenActivity.this, UserTypeSelectionActivity.class);
                        }

                        if (intent != null) {
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } 
                        else {
                            MyToast.showShort(this, "ইউজারের ধরণ নির্ধারণ করা যায়নি!");
                        }

                    } else {
                        MyToast.showShort(this, "ইউজার তথ্য চেক করতে ব্যর্থ: " + task.getException().getMessage());
                    }
                });
    }


    private void goToLoginWithError(String errorMessage) {
        MyToast.showShort(this,errorMessage);
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


}
