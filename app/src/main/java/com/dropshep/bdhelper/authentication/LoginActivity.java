package com.dropshep.bdhelper.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.ActivityLoginBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.NotificationPermissionHelper;
import com.dropshep.bdhelper.myUtils.SharedPrefHelper;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partner.DashboardActivity;
import com.dropshep.bdhelper.user.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private static final String KEY_FIRST_TIME_NOTIFICATION_REQUESTED = "notification_requested";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LoadingDialog loadingDialog;

    private GoogleSignInHelper googleSignInHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট করব
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        //setContentView(R.layout.activity_login);

        //Post Notification Enable
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(this);
        boolean alreadyAsked = sharedPrefHelper.getBoolean(KEY_FIRST_TIME_NOTIFICATION_REQUESTED, false);

        if (!alreadyAsked) {
            NotificationPermissionHelper.requestPermissionAndSubscribe(this);
            sharedPrefHelper.putBoolean(KEY_FIRST_TIME_NOTIFICATION_REQUESTED, true);
        }

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        //Google Sign In Helper
        googleSignInHelper = new GoogleSignInHelper(this, new GoogleSignInHelper.OnGoogleSignInSuccessListener() {
            @Override
            public void onSignInSuccess(FirebaseUser user) {
                loadingDialog.setMessage("লগইন হচ্ছে...");
                loadingDialog.show();
                // 🔁 Go to next activity or dashboard
                gotoNextActivity(MyUtils.USER_TYPE_GOOGLE);
                Log.d("GoogleLog", "Google: "+ user.getEmail());
                //Toast.makeText(LoginActivity.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSignInFailure(String errorMessage) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                setErrorWatcher(binding.emailET, true);
                Toast.makeText(this, "সঠিক ইমেইল অ্যাড্রেস দিন", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(password) || password.length() < 6) {
                setErrorWatcher(binding.passwordET, true);
                Toast.makeText(this, "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "লগইন ব্যর্থ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setErrorWatcher(EditText editText, boolean hasError) {
        if (hasError) {
            editText.setBackgroundResource(R.drawable.bg_edit_text_error);
            editText.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    editText.setBackgroundResource(R.drawable.bg_edit_text);
                }
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void gotoNextActivity(String userSignInWith) {
        Log.d("GoogleLog", "Google: "+userSignInWith);
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
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            }
                            else if ("partner".equals(userType)) {
                                intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            }
                        } else {
                            // User does not exist in "users" table, go to UserTypeSelectionActivity
                            intent = new Intent(LoginActivity.this, UserTypeSelectionActivity.class);
                            intent.putExtra(MyUtils.USER_SIGN_IN_WITH, userSignInWith);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        loadingDialog.dismiss();
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish(); // Close LoginActivity to prevent going back
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(this, "ইউজার তথ্য চেক করতে ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleSignInHelper.handleActivityResult(requestCode, resultCode, data);
    }
}