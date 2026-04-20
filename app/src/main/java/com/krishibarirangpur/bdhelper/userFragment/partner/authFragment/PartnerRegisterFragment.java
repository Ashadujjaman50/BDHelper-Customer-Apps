package com.krishibarirangpur.bdhelper.userFragment.partner.authFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.FirebaseMessaging.FCMTokenManager;
import com.krishibarirangpur.bdhelper.utils.firebase.UserMapBuilder;
import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.DistrictPickerBottomSheet;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.NoticeSend;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.userActivity.partner.DashboardActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentPartnerRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.AlphanumericKeyListener;
import com.krishibarirangpur.bdhelper.utils.authWidget.ReferralManager;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.ValidationClass;

import java.util.Arrays;
import java.util.Map;

public class PartnerRegisterFragment extends Fragment {

    private FragmentPartnerRegisterBinding binding;
    private String userSignWith;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private LoadingDialog loadingDialog;
    SharedPrefHelper prefHelper;

    String device_token;
    String selectDistrict;
    ReferralManager referralManager;
    public PartnerRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userSignWith = getArguments().getString("userSignWith");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_partner_register, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        loadingDialog = new LoadingDialog(requireActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        prefHelper = new SharedPrefHelper(requireActivity());
        referralManager = new ReferralManager();

        FCMTokenManager.getToken(token -> {
            Log.d("FCMTokenManager", "Token: " + token);
            device_token = token;
        });

        // KeyListener set করুন
        binding.referCodeEt.setKeyListener(AlphanumericKeyListener.getInstance());

        // শুধু length filter রাখুন
        binding.referCodeEt.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(8)
        });

        //Show Bottom Popup Menu With District List
        binding.districtET.setOnClickListener(v -> {

            String[] districtListEng = MyUtils.DISTRICT_ENG;
            String[] districtListBan = MyUtils.DISTRICT_BAN;

            boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");

            String[] displayList = isBangla ? districtListBan : districtListEng;

            DistrictPickerBottomSheet.show(
                    requireContext(),
                    "Select District",
                    Arrays.asList(displayList),
                    (item, position) -> {

                        binding.districtET.setText(item);

                        // DB-তে সবসময় English save
                        selectDistrict = isBangla ? districtListEng[position] : item;
                    }
            );
        });

        binding.submitBtn.setOnClickListener(v -> {
            registrationPartnerInfo();
        });

    }

    private void registrationPartnerInfo() {
        String name = binding.nameET.getText().toString().trim();
        String mobile = binding.mobileET.getText().toString().trim();
        String businessName = binding.businessNameET.getText().toString().trim();
        String location = binding.locationET.getText().toString().trim();
        String district = binding.districtET.getText().toString().trim();
        String inputReferralCode = binding.referCodeEt.getText().toString().toUpperCase().trim();

        if (TextUtils.isEmpty(name)) {
            ValidationClass.setErrorWatcher(binding.nameET, true);
            Toast.makeText(requireActivity(), "আপনার নাম লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
            ValidationClass.setErrorWatcher(binding.mobileET, true);
            Toast.makeText(requireActivity(), "আপনার ১১ ডিজিট এর মোবাইল নাম্বার দিন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(businessName)) {
            ValidationClass.setErrorWatcher(binding.businessNameET, true);
            Toast.makeText(requireActivity(), "আপনার ব্যবসা/পেশা লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(location)) {
            ValidationClass.setErrorWatcher(binding.locationET, true);
            Toast.makeText(requireActivity(), "আপনার ঠিকানা লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(district)) {
            ValidationClass.setErrorWatcher(binding.districtET, true);
            Toast.makeText(requireActivity(), "আপনার জেলা নির্বাচন করুন", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingDialog.setMessage("আপনার তথ্য আপডেট হচ্ছে...");
            loadingDialog.show();

            //String to set Time stamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String userId = firebaseUser.getUid();
            String email = firebaseUser.getEmail();
            double rating = 5.0;


            Map<String, Object> userMap = UserMapBuilder.createUserMap(
                    userId,
                    "partner",
                    name,
                    email,
                    mobile,
                    selectDistrict,
                    location,
                    businessName,
                    "partner",
                    device_token,
                    userSignWith,
                    timestamp,
                    rating
            );

            // Ensure unique referral code before saving
            referralManager.createUserWithReferral(
                    userId,
                    userMap,
                    inputReferralCode,
                    new ReferralManager.Callback() {
                        @Override
                        public void onSuccess() {

                            prefHelper.remove("userSignWith");
                            loadingDialog.dismiss();

                            customNoticeSend(true, userId, userMap.get("district"));
                            gotoNextActivity();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loadingDialog.dismiss();
                            Toast.makeText(requireActivity(), "তথ্য আপডেট করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

        }

    }


    private void gotoNextActivity(){
        Intent intent = new Intent(requireActivity(), DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requireActivity().finish();
    }


    private void customNoticeSend(boolean firstLogin, String userId, Object referCode) {
        //When Successfully rent post submit then Notice Vendor apps

        String noticeType, msg;
        if (firstLogin){
            msg = "অ্যাপ এ রেজিস্ট্রেশন করে যুক্ত হওয়ায় আপনাকে স্বাগতম";
            noticeType = MyUtils.NOTICE_TYPE_WELCOME;
        }
        else {
            msg = "আপনার রেফারেল কোড "+referCode+ " ব্যবহার করে একজন রেজিস্ট্রেশন করেছে।";
            noticeType = MyUtils.NOTICE_TYPE_REFERRAL;
        }

        NoticeSend.sendNotice(
                MyUtils.NOTICE_SENDER_ADMIN,
                noticeType,
                "",
                userId,
                "",
                msg
        );

    }

    //BDH4P1QA
}