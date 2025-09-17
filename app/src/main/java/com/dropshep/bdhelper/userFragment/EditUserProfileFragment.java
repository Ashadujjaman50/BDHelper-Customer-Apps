package com.dropshep.bdhelper.userFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentEditUserProfileBinding;
import com.dropshep.bdhelper.myUtils.LoadingDialog;
import com.dropshep.bdhelper.myUtils.LocaleHelper;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.Replacement;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditUserProfileFragment extends Fragment {

    private FragmentEditUserProfileBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    String userId;

    private LoadingDialog loadingDialog;
    String selectedDistrict;

    public EditUserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_user_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();

        loadingDialog = new LoadingDialog(requireActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        //Show Bottom Popup Menu With District List
        showBottomPopUpDistrictList();
        
        
        loadCurrentUserInfo();

        binding.saveBtn.setOnClickListener(v -> {
            saveUpdateUserInfo();
        });
    }

    private void saveUpdateUserInfo() {
        String name = binding.userNameEt.getText().toString().trim();
        String mobile = binding.userMobileEt.getText().toString().trim();
        String location = binding.userLocationEt.getText().toString().trim();
        selectedDistrict = binding.userDistrictEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            setErrorWatcher(binding.userNameEt, true);
            Toast.makeText(requireActivity(), "আপনার নাম লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mobile) ||  mobile.length() < 11) {
            setErrorWatcher(binding.userMobileEt, true);
            Toast.makeText(requireActivity(), "আপনার ১১ ডিজিট এর মোবাইল নাম্বার দিন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(location)) {
            setErrorWatcher(binding.userLocationEt, true);
            Toast.makeText(requireActivity(), "আপনার ঠিকানা লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedDistrict)) {
            binding.userDistrictEt.setBackgroundResource(R.drawable.bg_edit_text_error);
            Toast.makeText(requireActivity(), "আপনার জেলা নির্বাচন করুন", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingDialog.setMessage("আপনার তথ্য আপডেট হচ্ছে...");
            loadingDialog.show();

            //String to set Time stamp
            Map<String, Object> updateMap  = new HashMap<>();
            //.put("userId", userId);
            //updateMap .put("userType", "partner"); // "customer" or "partner"
            updateMap .put("name", name);
            //updateMap .put("email", email);
            updateMap .put("phone", mobile);
            //updateMap .put("nidNo", "");
            //updateMap .put("nidVerify", "false");
            //updateMap .put("userDob", "");
            updateMap .put("district", selectedDistrict);
            updateMap .put("location", location);
            //updateMap .put("businessName", businessName);  // only for partner
            //updateMap .put("verifyStatus", "pending");     // only for partner
            //updateMap .put("rentService", "");             // only for partner
            //updateMap .put("device_token", device_token);
            //updateMap .put("userSignWith", userSignWith);
            //updateMap .put("userJoinTime", timestamp);
            //updateMap .put("userLastLogin", timestamp);
            //updateMap .put("rating", rating);

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .update(updateMap )
                    .addOnSuccessListener(unused -> {
                        // Success
                        loadingDialog.dismiss();
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        // Error
                        loadingDialog.dismiss();
                    });

        }
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


    private void loadCurrentUserInfo() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        String location = documentSnapshot.getString("location");
                        selectedDistrict = documentSnapshot.getString("district"); // Always English

                        binding.userNameEt.setText(name);
                        binding.userMobileEt.setText(phone);
                        binding.userLocationEt.setText(location);
                        binding.userEmailEt.setText(email);
                        binding.userEmailEt.setFocusable(false);
                        binding.userEmailEt.setEnabled(false);

                        // 🔁 ভাষা চেক করে District নাম বাংলা দেখাও
                        String shownDistrict = Replacement.getLocalizedDistrict(requireActivity(), selectedDistrict);
                        binding.userDistrictEt.setText(shownDistrict);
                    }
                })
                .addOnFailureListener(e -> {
                    // Error handling
                });
    }



    @SuppressLint("SetTextI18n")
    private void showBottomPopUpDistrictList() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_listview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        ListView listView = view.findViewById(R.id.listView);

        titleTv.setText("Select District");

        // ভাষা অনুযায়ী display list, কিন্তু ইংরেজি লিস্ট সবসময় দরকার হবে Database-এর জন্য
        String[] districtListEng = MyUtils.DISTRICT_ENG;
        String[] districtListBan = MyUtils.DISTRICT_BAN;

        // কোন ভাষা চালু আছে
        boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");

        // UI-তে যেটা দেখাবে
        String[] displayList = isBangla ? districtListBan : districtListEng;

        listView.setAdapter(new ArrayAdapter<>(
                requireActivity(),
                R.layout.single_listview_item,
                R.id.listItem,
                displayList
        ));

        // ✅ BottomSheet fixed height + prevent dismiss on swipe
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight((int) (400 * getResources().getDisplayMetrics().density));
                behavior.setDraggable(false);
            }
        });


        // ✅ Select করলে Toast করবে ইংরেজি নাম দিয়ে
        binding.userDistrictEt.setOnClickListener(v -> {
            bottomSheetDialog.show();

            listView.setOnItemClickListener((parent, view1, position, id) -> {
                binding.userDistrictEt.setText(displayList[position]); // UI-তে যা দেখা যাচ্ছে সেটাই রাখো

                selectedDistrict = isBangla ? districtListEng[position] : displayList[position];

                bottomSheetDialog.dismiss();
            });
        });
    }

}