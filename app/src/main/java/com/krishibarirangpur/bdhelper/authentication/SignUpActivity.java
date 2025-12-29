package com.krishibarirangpur.bdhelper.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivitySignUpBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.MyToast;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.krishibarirangpur.bdhelper.utils.NetworkUtils;
import com.krishibarirangpur.bdhelper.utils.NoInternetDialog;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends BaseActivity {

    private ActivitySignUpBinding binding;

    FirebaseAuth firebaseAuth;
    private LoadingDialog loadingDialog;

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

        binding.signInTV.setOnClickListener(v -> finishOnBack());

        binding.logInBtn.setOnClickListener(v -> {
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
        //progress dialog show
        loadingDialog.setMessage("Create account...");
        loadingDialog.show(); // প্রথমেই progress শুরু

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        // ✅ নতুন অ্যাকাউন্ট তৈরি হয়েছে
                        MyToast.showShort(this, "Account created successfully!");
                        gotoNextActivity();
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            // ইমেইল আগে থেকেই ইউজড, একাউন্ট তৈরি হয়নি
                            MyToast.showShort(this, "এই ইমেইল দিয়ে ইতিমধ্যে একাউন্ট তৈরি হয়েছে। দয়া করে লগইন করুন।");
                        } else {
                            // অন্যান্য ব্যর্থতা (e.g. network, invalid email, etc)
                            MyToast.showShort(this, "Signup Failed: " + e.getMessage());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Log.d("Create", "Error: " + e.getMessage());
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


    private void gotoNextActivity() {
        Intent intent = new Intent(SignUpActivity.this, UserTypeSelectionActivity.class);
        intent.putExtra(MyUtils.USER_SIGN_IN_WITH, MyUtils.USER_TYPE_EMAIL);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}