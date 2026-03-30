package com.krishibarirangpur.bdhelper.authentication;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivitySignUpBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.network.NetworkUtils;
import com.krishibarirangpur.bdhelper.utils.network.NoInternetDialog;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.List;

public class SignUpActivity extends BaseActivity {

    private ActivitySignUpBinding binding;

    FirebaseAuth firebaseAuth;
    private LoadingDialog loadingDialog;
    private GoogleSignInHelper googleSignInHelper;

    // 🔥 global variable for linking
    private String tempEmail = null;
    private String tempPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        //init views

        // Show no internet dialog if offline
        if (!NetworkUtils.isNetworkAvailable(this)) {
            NoInternetDialog internetDialog = new NoInternetDialog();
            internetDialog.show(getSupportFragmentManager(), "NoInternetDialog");
        }

        firebaseAuth = FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        // Google Sign In Helper setup
        googleSignInHelper = new GoogleSignInHelper(this, new GoogleSignInHelper.OnGoogleSignInSuccessListener() {
            @Override
            public void onSignInSuccess(FirebaseUser user) {
                if (tempEmail != null && tempPassword != null) {
                    // যদি Email Sign up থেকে এসে থাকে, তবে link করো
                    linkEmailAccount(user);
                } else {
                    gotoNextActivity();
                }
            }

            @Override
            public void onSignInFailure(String errorMessage) {
                MyToast.showShort(SignUpActivity.this, errorMessage);
            }
        });

        binding.signInTV.setOnClickListener(v -> finishOnBack());

        binding.signUpBtn.setOnClickListener(v -> {
            String email = binding.emailET.getText().toString().trim();
            String password = binding.passwordET.getText().toString().trim();
            String confirmPass = binding.confirmPasswordET.getText().toString().trim();

            if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                setErrorWatcher(binding.emailET, true);
                Toast.makeText(this, "সঠিক ইমেইল অ্যাড্রেস দিন", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(password) || password.length() < 6) {
                setErrorWatcher(binding.passwordET, true);
                Toast.makeText(this, "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে", Toast.LENGTH_SHORT).show();
            }
            else if (!confirmPass.equals(password)) {
                setErrorWatcher(binding.confirmPasswordET, true);
                Toast.makeText(this, "পাসওয়ার্ড ও কনফার্ম পাসওয়ার্ড মেলে না", Toast.LENGTH_SHORT).show();
            }
            else {
                createNewAccount(email, password);
            }
        });

    }

    private void createNewAccount(String email, String password) {
        loadingDialog.setMessage("Create account...");
        loadingDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadingDialog.dismiss();
                        MyToast.showShort(this, "Account created successfully!");
                        gotoNextActivity();
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            checkSignInMethods(email, password);
                        } else {
                            loadingDialog.dismiss();
                            MyToast.showShort(this, "Signup Failed: " + (e != null ? e.getMessage() : "Unknown error"));
                        }
                    }
                });
    }

    private void checkSignInMethods(String email, String password) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(result -> {
                    loadingDialog.dismiss();
                    List<String> methods = result.getSignInMethods();
                    
                    if (methods != null && !methods.isEmpty()) {
                        if (methods.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)) {
                            MyToast.showShort(this, "এই ইমেইল Google দিয়ে তৈরি। Linking হচ্ছে...");
                            tempEmail = email;
                            tempPassword = password;
                            startGoogleSignIn();
                        } else if (methods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                            MyToast.showShort(this, "এই ইমেইল দিয়ে একাউন্ট আছে, লগইন করুন।");
                        } else {
                            MyToast.showShort(this, "এই ইমেইলটি অন্য মাধ্যমে ব্যবহৃত হচ্ছে।");
                        }
                    } else {
                        // যদি Enumeration Protection চালু থাকে তবে এখানে আসবে
                        MyToast.showShort(this, "ইমেইলটি ইতিমধ্যেই ব্যবহৃত। সরাসরি লগইন করার চেষ্টা করুন।");
                    }
                })
                .addOnFailureListener(err -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(this, "Error: " + err.getMessage());
                });
    }

    private void startGoogleSignIn() {
        googleSignInHelper.startGoogleSignIn();
    }

    private void linkEmailAccount(FirebaseUser user) {
        if (tempEmail == null || tempPassword == null) return;

        loadingDialog.setMessage("Linking account...");
        loadingDialog.show();

        AuthCredential credential = EmailAuthProvider.getCredential(tempEmail, tempPassword);
        user.linkWithCredential(credential)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        MyToast.showShort(this, "Account linked successfully!");
                        // Clear temp data
                        tempEmail = null;
                        tempPassword = null;
                        gotoNextActivity();
                    } else {
                        MyToast.showShort(this, "Link failed: " + task.getException().getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleSignInHelper.handleActivityResult(requestCode, resultCode, data);
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

    private void gotoNextActivity() {
        Intent intent = new Intent(SignUpActivity.this, UserTypeSelectionActivity.class);
        intent.putExtra(MyUtils.USER_SIGN_IN_WITH, MyUtils.USER_TYPE_EMAIL);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
