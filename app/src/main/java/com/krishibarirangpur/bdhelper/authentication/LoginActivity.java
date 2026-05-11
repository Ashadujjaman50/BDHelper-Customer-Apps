package com.krishibarirangpur.bdhelper.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityLoginBinding;
import com.krishibarirangpur.bdhelper.utils.authWidget.UserAction;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.network.NotificationPermissionHelper;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.userActivity.partner.DashboardActivity;
import com.krishibarirangpur.bdhelper.userActivity.customer.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.ValidationClass;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private static final String KEY_FIRST_TIME_NOTIFICATION_REQUESTED = "notification_requested";
    private static final String USER_LOGIN_MODE = "user_role";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LoadingDialog loadingDialog;
    private SharedPrefHelper sharedPrefHelper;
    private GoogleSignInHelper googleSignInHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট করব
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        //setContentView(R.layout.activity_login);

        //Post Notification Enable
        sharedPrefHelper = new SharedPrefHelper(this);
        boolean alreadyAsked = sharedPrefHelper.getBoolean(KEY_FIRST_TIME_NOTIFICATION_REQUESTED, false);

        if (!alreadyAsked) {
            NotificationPermissionHelper.requestPermissionAndSubscribe(this);
            sharedPrefHelper.putBoolean(KEY_FIRST_TIME_NOTIFICATION_REQUESTED, true);
        }

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        // Google Sign In Helper
        googleSignInHelper = new GoogleSignInHelper(this, new GoogleSignInHelper.OnGoogleSignInSuccessListener() {
            @Override
            public void onSignInSuccess(FirebaseUser user) {
                // ✅ অ্যাক্টিভিটি চালু আছে কি না তা চেক করুন
                if (!isFinishing() && !isDestroyed()) {
                    loadingDialog.setMessage("লগইন হচ্ছে...");
                    loadingDialog.show();

                    // 🔁 Go to next activity
                    gotoNextActivity(MyUtils.USER_TYPE_GOOGLE);
                    Log.d("GoogleLog", "Google: " + user.getEmail());
                }
            }

            @Override
            public void onSignInFailure(String errorMessage, Exception exception) {
                // ✅ অ্যাক্টিভিটি চালু আছে কি না তা চেক করুন
                if (!isFinishing() && !isDestroyed()) {
                    loadingDialog.dismiss();

                    if (exception instanceof GetCredentialCancellationException) {
                        Log.d("GoogleSignIn", "Sign-in cancelled by user");
                    } else {
                        MyToast.showShort(LoginActivity.this, errorMessage);
                    }
                }
            }
        });

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ✅ Already logged-in user check
        if (mAuth.getCurrentUser() != null) {
            gotoNextActivity("");
            return;
        }


        binding.signUpTV.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.logInBtn.setOnClickListener(v -> {
            String email = binding.emailET.getText().toString().trim();
            String password = binding.passwordET.getText().toString().trim();

            if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ValidationClass.setErrorWatcher(binding.emailET, true);
                MyToast.showShort(this, "সঠিক ইমেইল অ্যাড্রেস দিন");
            }
            else if (TextUtils.isEmpty(password) || password.length() < 6) {
                ValidationClass.setErrorWatcher(binding.passwordET, true);
                MyToast.showShort(this, "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে");
            }
            else {
                loginUserAccount(email, password);
            }
        });

        binding.gmailLogInBtn.setOnClickListener(v -> {
            //Click Google SignIn
            googleSignInHelper.startGoogleSignIn();
        });

    }

    private void loginUserAccount(String email, String password) {
        loadingDialog.setMessage("লগইন হচ্ছে...");
        loadingDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Login successful, check if user exists in Firestore
                    gotoNextActivity(MyUtils.USER_TYPE_EMAIL);
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    // Login failed
                    MyToast.showShort(this, "লগইন ব্যর্থ: " + e.getMessage());
                });
    }

    private void gotoNextActivity(String userSignInWith) {
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
                            String verifyStatus = document.getString("verifyStatus");

                            // 🔴 Blocked / Rejected User
                            if ("rejected".equalsIgnoreCase(verifyStatus)) {
                                loadingDialog.dismiss();
                                UserAction.blockAccountCheck(this);
                                return;
                            }

                            if ("customer".equals(userType)) {
                                sharedPrefHelper.putString(USER_LOGIN_MODE, "customer");
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            else if ("partner".equals(userType)) {
                                sharedPrefHelper.putString(USER_LOGIN_MODE, "partner");
                                intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            }
                            else {
                                loadingDialog.dismiss();
                                MyToast.showShort(LoginActivity.this, "⚠\uFE0F এই ইমেইলটি অনুমোদিত নয়। অন্য ইমেইল ব্যবহার করুন।");
                                FirebaseAuth.getInstance().signOut();
                            }
                        } else {
                            // User does not exist in "users" table, go to UserTypeSelectionActivity
                            intent = new Intent(LoginActivity.this, UserTypeSelectionActivity.class);
                            intent.putExtra(MyUtils.USER_SIGN_IN_WITH, userSignInWith);
                        }
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            loadingDialog.dismiss();
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    } else {
                        loadingDialog.dismiss();
                        MyToast.showShort(this, "ইউজার তথ্য চেক করতে ব্যর্থ: " + task.getException().getMessage());
                    }
                });
    }
}
