package com.dropshep.bdhelper.partnerFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dropshep.bdhelper.FirebaseMessaging.FCMTokenManager;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.LoadingDialog;
import com.dropshep.bdhelper.myUtils.LocaleHelper;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.SharedPrefHelper;
import com.dropshep.bdhelper.partner.DashboardActivity;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentPartnerRegisterBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PartnerRegisterFragment extends Fragment {

    private FragmentPartnerRegisterBinding binding;
    private String userSignWith;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    FirebaseFirestore db;

    private LoadingDialog loadingDialog;
    SharedPrefHelper prefHelper;

    String device_token;
    String selectDistrict;

    public PartnerRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userSignWith = getArguments().getString("userSignWith");
            // এখন এই ভ্যালু তুমি fragment-এ ব্যবহার করতে পারো
            //MyToast.showShort(requireActivity(), userSignWith);
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
        db = FirebaseFirestore.getInstance();

        loadingDialog = new LoadingDialog(requireActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        prefHelper = new SharedPrefHelper(requireActivity());

        FCMTokenManager.getToken(token -> {
            Log.d("FCMTokenManager", "Token: " + token);
            device_token = token;
        });

        // শুধু uppercase letter এবং digit allow করব
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            StringBuilder filtered = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (Character.isLetter(c)) {
                    filtered.append(Character.toUpperCase(c)); // letter হলে uppercase করে দেবে
                } else if (Character.isDigit(c)) {
                    filtered.append(c); // digit হলে 그대로 রাখবে
                }
            }
            return filtered.toString();
        };

        // filters সেট করা
        binding.referCodeEt.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10)});

        //Show Bottom Popup Menu With District List
        showBottomPopUpDistrictList();

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
        String inputReferralCode = binding.referCodeEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            setErrorWatcher(binding.nameET, true);
            Toast.makeText(requireActivity(), "আপনার নাম লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mobile) ||  mobile.length() < 11) {
            setErrorWatcher(binding.mobileET, true);
            Toast.makeText(requireActivity(), "আপনার ১১ ডিজিট এর মোবাইল নাম্বার দিন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(businessName)) {
            setErrorWatcher(binding.businessNameET, true);
            Toast.makeText(requireActivity(), "আপনার ব্যবসা/পেশা লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(location)) {
            setErrorWatcher(binding.locationET, true);
            Toast.makeText(requireActivity(), "আপনার ঠিকানা লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(district)) {
            binding.districtET.setBackgroundResource(R.drawable.bg_edit_text_error);
            Toast.makeText(requireActivity(), "আপনার জেলা নির্বাচন করুন", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingDialog.setMessage("আপনার তথ্য আপডেট হচ্ছে...");
            loadingDialog.show();

            //String to set Time stamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String userId = firebaseUser.getUid();
            String email = firebaseUser.getEmail();
            double rating = 0.0;

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", userId);
            userMap.put("userType", "partner"); // "customer" or "partner"
            userMap.put("name", name);
            userMap.put("email", email);
            userMap.put("phone", mobile);
            userMap.put("nidNo", "");
            userMap.put("nidVerify", "false");
            userMap.put("userDob", "");
            userMap.put("district", selectDistrict);
            userMap.put("location", location);
            userMap.put("businessName", businessName);  // only for partner
            userMap.put("verifyStatus", "pending");     // only for partner
            userMap.put("rentService", "");             // only for partner
            userMap.put("device_token", device_token);
            userMap.put("userSignWith", userSignWith);
            userMap.put("userJoinTime", timestamp);
            userMap.put("userLastLogin", timestamp);
            userMap.put("rating", rating);

            // Referral system fields
            userMap.put("referralCode", CommonClass.generateReferralCode());  // Your unique refer code
            userMap.put("referredBy", "");                        // set later if used
            userMap.put("bonusBalance", 0);                       // initial bonus

            // Ensure unique referral code before saving
            ensureUniqueReferralCode(userId, userMap, inputReferralCode);

            /*FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .set(userMap)
                    .addOnSuccessListener(unused -> {
                        prefHelper.remove("userSignWith");
                        // Success
                        gotoNextActivity();
                    })
                    .addOnFailureListener(e -> {
                        // Error
                    });*/

        }

    }

    private void ensureUniqueReferralCode(String userId, Map<String, Object> userMap, String inputReferralCode) {
        String code = (String) userMap.get("referralCode");
        db.collection("users").whereEqualTo("referralCode", code)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        // Unique code, save user
                        saveUserWithReferral(userId, userMap, inputReferralCode);
                    } else {
                        // Not unique, generate new code and retry
                        userMap.put("referralCode", CommonClass.generateReferralCode());
                        ensureUniqueReferralCode(userId, userMap, inputReferralCode);
                    }
                });
    }

    private void saveUserWithReferral(String newUserId, Map<String, Object> userMap, String inputReferralCode) {
        db.collection("users").document(newUserId)
                .set(userMap)
                .addOnSuccessListener(unused -> {

                    // Handle referral code if user entered someone else's code
                    if (!TextUtils.isEmpty(inputReferralCode)) {
                        db.collection("users").whereEqualTo("referralCode", inputReferralCode)
                                .get()
                                .addOnSuccessListener(query -> {
                                    if (!query.isEmpty()) {
                                        DocumentSnapshot referrer = query.getDocuments().get(0);
                                        String referrerId = referrer.getId();

                                        // Update new user's referredBy and bonus
                                        db.collection("users").document(newUserId)
                                                .update("referredBy", inputReferralCode,
                                                        "bonusBalance", 10); // new user bonus

                                        // Update referrer's bonus
                                        double referrerBonus = referrer.getDouble("bonusBalance") + 10;
                                        db.collection("users").document(referrerId)
                                                .update("bonusBalance", referrerBonus);

                                        // Save referral record
                                        Map<String, Object> referralMap = new HashMap<>();
                                        referralMap.put("referrerId", referrerId);
                                        referralMap.put("refereeId", newUserId);
                                        referralMap.put("bonusGiven", 10);
                                        referralMap.put("createdAt", FieldValue.serverTimestamp());

                                        db.collection("referrals").add(referralMap);
                                        notification("Refer", referrerId, inputReferralCode);
                                    }
                                });
                    }

                    prefHelper.remove("userSignWith");
                    loadingDialog.dismiss();
                    notification("Welcome", newUserId,  userMap.get("district"));
                    gotoNextActivity();

                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(requireActivity(), "তথ্য আপডেট করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
                });
    }




    private void gotoNextActivity(){
        Intent intent = new Intent(requireActivity(), DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        requireActivity().finish();
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

    private void showBottomPopUpDistrictList() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_listview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        ListView listView = view.findViewById(R.id.listView);

        // ভাষা অনুযায়ী display list, কিন্তু ইংরেজি লিস্ট সবসময় দরকার হবে Database-এর জন্য
        String[] districtListEng = MyUtils.DISTRICT_ENG;
        String[] districtListBan = MyUtils.DISTRICT_BAN;

        // কোন ভাষা চালু আছে
        boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");

        // UI-তে যেটা দেখাবে
        String[] displayList = isBangla ? districtListBan : districtListEng;

        listView.setAdapter(new ArrayAdapter<>(
                requireContext(),
                R.layout.single_listview_item,
                R.id.listItem,
                displayList
        ));

        // ✅ BottomSheet fixed height + prevent dismiss on swipe
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight((int) (400 * getResources().getDisplayMetrics().density)); // fixed height in dp
            behavior.setDraggable(false); // disable swipe-to-dismiss
        }

        // ✅ কেবল select করলে dismiss হবে
        binding.districtET.setOnClickListener(v -> {
            bottomSheetDialog.show();

            listView.setOnItemClickListener((parent, view1, position, id) -> {
                binding.districtET.setText(displayList[position]);

                selectDistrict = isBangla ? districtListEng[position] : displayList[position];


                // 🔹 Error clear করে normal background apply
                binding.districtET.setBackgroundResource(R.drawable.bg_edit_text);
                bottomSheetDialog.dismiss();
            });
        });
    }

    private void notification(String type, String userId, Object msg) {
        //When Successfully rent post submit then Notice Vendor apps
        String timestamp = "" + System.currentTimeMillis();
        long noticeId = System.currentTimeMillis();

        String description, title;
        if (type.equals("Welcome")){
            title= "Welcome";
            description = "অ্যাপ এ রেজিস্ট্রেশন করে যুক্ত হওয়ায় আপনাকে স্বাগতম";
        }
        else {
            title = "নতুন রেফারেল পেয়েছেন";
            description = "আপনার রেফারেল কোড "+msg+ " ব্যবহার করে একজন রেজিস্ট্রেশন করেছে।";
        }


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("noticeId", String.valueOf(noticeId + 1000));
        hashMap.put("sendUserId", "");
        hashMap.put("receivedUserId", userId);
        hashMap.put("senderType", "admin");
        hashMap.put("postId", timestamp);
        hashMap.put("noticeCategory", "Customer Account create");
        hashMap.put("noticeTitle", title);
        hashMap.put("postDistrict", "");
        hashMap.put("noticeDescription", description);
        hashMap.put("timestamp", timestamp);

        db = FirebaseFirestore.getInstance();
        db.collection("Notice")
                .document(timestamp)   // timestamp কে documentId হিসেবে ব্যবহার করছ
                .set(hashMap)
                .addOnSuccessListener(unused -> {
                    MyToast.showShort(getContext(), "Register successfully....");
                });
    }



    //BDH4P1QA


}