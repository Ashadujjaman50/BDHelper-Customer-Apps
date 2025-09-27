package com.dropshep.bdhelper.userFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.DistrictAdapter;
import com.dropshep.bdhelper.databinding.FragmentEditUserProfileBinding;
import com.dropshep.bdhelper.myUtils.LocaleHelper;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.Replacement;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditUserProfileFragment extends Fragment {

    private FragmentEditUserProfileBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    String userId;

    private LoadingDialog loadingDialog;
    String selectDistrict;

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
        selectDistrict = binding.userDistrictEt.getText().toString().trim();

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
        else if (TextUtils.isEmpty(selectDistrict)) {
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
            updateMap .put("district", selectDistrict);
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
                        selectDistrict = documentSnapshot.getString("district"); // Always English

                        binding.userNameEt.setText(name);
                        binding.userMobileEt.setText(phone);
                        binding.userLocationEt.setText(location);
                        binding.userEmailEt.setText(email);
                        binding.userEmailEt.setFocusable(false);
                        binding.userEmailEt.setEnabled(false);

                        // 🔁 ভাষা চেক করে District নাম বাংলা দেখাও
                        String shownDistrict = Replacement.getLocalizedDistrict(requireActivity(), selectDistrict);
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
                .inflate(R.layout.bottom_sheet_dialog_recycleview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        titleTv.setText("Select District");

        // ভাষা অনুযায়ী display list, কিন্তু ইংরেজি লিস্ট সবসময় দরকার হবে Database-এর জন্য
        String[] districtListEng = MyUtils.DISTRICT_ENG;
        String[] districtListBan = MyUtils.DISTRICT_BAN;

        // কোন ভাষা চালু আছে
        boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");

        // UI-তে যেটা দেখাবে
        String[] displayList = isBangla ? districtListBan : districtListEng;


        // Adapter
        DistrictAdapter adapter = new DistrictAdapter(Arrays.asList(displayList), (item, position) -> {
            binding.userDistrictEt.setText(item); // UI text (localised)
            selectDistrict = isBangla ? districtListEng[position] : item; // DB-র জন্য always ইংরেজি
            bottomSheetDialog.dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);

        // Important: modify bottom-sheet AFTER it is shown -> use onShowListener
        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                int heightPx = dpToPx(400); // fixed 400dp height

                // Force the bottomSheet container height to 400dp
                bottomSheet.getLayoutParams().height = heightPx;
                bottomSheet.requestLayout();

                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(heightPx);           // peek at 400dp
                behavior.setDraggable(true);               // allow dragging / scrolling
                behavior.setHideable(true);                // optional
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // show at peekHeight
            }
        });

        binding.userDistrictEt.setOnClickListener(v -> bottomSheetDialog.show());
    }

    // helper
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

}