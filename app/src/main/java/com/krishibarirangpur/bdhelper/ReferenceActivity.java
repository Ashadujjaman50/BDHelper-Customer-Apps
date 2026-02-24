package com.krishibarirangpur.bdhelper;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.databinding.ActivityReferenceBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class ReferenceActivity extends BaseActivity {

    private ActivityReferenceBinding binding;

    String referralId;


    private ListenerRegistration referralListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reference);
        //inti views


        binding.backBtn.setOnClickListener(v -> finishOnBack());

        //load Current User referral code
        loadUserReferralCode();

        binding.copyBtn.setOnClickListener(v -> {
            referralId = binding.referIdTv.getText().toString().trim();
            if (!referralId.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Refer ID", referralId);
                clipboard.setPrimaryClip(clip);

                MyToast.snackBar(v, "Refer code successfully copied");
            }
        });

        binding.shareNowBtn.setOnClickListener(v -> {
            referralId = binding.referIdTv.getText().toString().trim();
            String packageName = v.getContext().getPackageName();   // ✅ dynamic package name

            String shareMessage = "Hello friend, I am using BDHelper! You can join using my referral code and enjoy referral bonus!" +
                    "To accept, use My CODE: " + referralId + " in the referral box after signing up.\n\n" +
                    "Download the app from here: https://play.google.com/store/apps/details?id=" + packageName;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BDHelper App Referral");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            v.getContext().startActivity(Intent.createChooser(shareIntent, "Share via"));
        });


    }

    @SuppressLint("SetTextI18n")
    private void loadUserReferralCode() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        String userId = firebaseUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        referralListener = db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        binding.referIdTv.setText("Error");
                        MyToast.showShort(ReferenceActivity.this, "Failed to load referral code");
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String referralCode = documentSnapshot.getString("referralCode");
                        if (!TextUtils.isEmpty(referralCode)) {
                            binding.referIdTv.setText(referralCode);
                        } else {
                            binding.referIdTv.setText("No Code");
                        }
                    }
                });
    }

    // Activity destroy হলে listener remove করতে হবে
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (referralListener != null) {
            referralListener.remove();
        }
    }


}