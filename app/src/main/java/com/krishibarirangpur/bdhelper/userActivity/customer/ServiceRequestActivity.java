package com.krishibarirangpur.bdhelper.userActivity.customer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.AdapterServiceRequest;
import com.krishibarirangpur.bdhelper.adapter.customer.DistrictAdapter;
import com.krishibarirangpur.bdhelper.databinding.ActivityServiceRequestBinding;
import com.krishibarirangpur.bdhelper.model.ModelServiceRequest;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServiceRequestActivity extends BaseActivity {

    private ActivityServiceRequestBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    FirebaseFirestore db;

    LoadingDialog loadingDialog;

    ArrayList<ModelServiceRequest> serviceRequestArrayList;
    AdapterServiceRequest adapterServiceRequest;
    String selectDistrict;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_request);

        //init views
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(v ->  finishOnBack());

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        //load Location
        showBottomPopUpDistrictList();

        binding.submitBtn.setOnClickListener(v -> {
            submitServiceRequest();
        });

        binding.requestShowBtn.setOnClickListener(v -> {
            showBottomServiceRequestList();
        });

    }

    private void submitServiceRequest() {
        String serviceName = binding.serviceNameEt.getText().toString().trim();
        String district = binding.districtEt.getText().toString().trim();

        if (TextUtils.isEmpty(serviceName)){
            setErrorWatcher(binding.serviceNameEt, true);
        }
        else if (TextUtils.isEmpty(district)){
            setErrorWatcher(binding.districtEt, true);
        }
        else {
            //set Dialog
            loadingDialog.setMessage("আপনার সার্ভিস রিকুয়েস্ট আপডেট হচ্ছে...");
            loadingDialog.show();

            //String to set Time stamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String userId = firebaseUser.getUid();

            // Create unique document ID
            String serviceId = db.collection("serviceRequest")
                    .document()
                    .getId();

            Map<String, Object> serviceMap = new HashMap<>();
            serviceMap.put("serviceId", serviceId);
            serviceMap.put("serviceName", serviceName);
            serviceMap.put("district", selectDistrict);
            serviceMap.put("userId", userId);
            serviceMap.put("note", "");
            serviceMap.put("status", "pending");
            serviceMap.put("timestamp", timestamp);

            db.collection("serviceRequest")
                    .document(serviceId)
                    .set(serviceMap)
                    .addOnSuccessListener(unused -> {
                        loadingDialog.dismiss();
                        MyToast.showShort(ServiceRequestActivity.this, "Your Request Submit Successful.");
                        binding.serviceNameEt.setText("");
                        binding.districtEt.setText("");
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        Log.d("ServiceRequest", "submitServiceRequest: "+e.getMessage());
                    });

        }
    }

    private void showBottomPopUpDistrictList() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ServiceRequestActivity.this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.bottom_sheet_dialog_recycleview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material. R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        titleTv.setText("Select District");

        // ভাষা অনুযায়ী display list, কিন্তু ইংরেজি লিস্ট সবসময় দরকার হবে Database-এর জন্য
        String[] districtListEng = MyUtils.DISTRICT_ENG;
        String[] districtListBan = MyUtils.DISTRICT_BAN;

        // কোন ভাষা চালু আছে
        boolean isBangla = LocaleHelper.getLanguage(ServiceRequestActivity.this).equals("bn");

        // UI-তে যেটা দেখাবে
        String[] displayList = isBangla ? districtListBan : districtListEng;

        // Adapter
        DistrictAdapter adapter = new DistrictAdapter(Arrays.asList(displayList), (item, position) -> {
            binding.districtEt.setText(item); // UI text (localised)
            selectDistrict = isBangla ? districtListEng[position] : item; // DB-র জন্য always ইংরেজি
            bottomSheetDialog.dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(ServiceRequestActivity.this));
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

        binding.districtEt.setOnClickListener(v -> bottomSheetDialog.show());
    }

    // helper
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setErrorWatcher(View view, boolean hasError) {
        if (hasError) {
            view.setBackgroundResource(R.drawable.bg_edit_text_error);

            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        editText.setBackgroundResource(R.drawable.bg_edit_text);
                    }
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override public void afterTextChanged(Editable s) {}
                });
            }
        }
        else {
            view.setBackgroundResource(R.drawable.bg_edit_text);
        }
    }

    private ListenerRegistration listenerRegistration;

    @SuppressLint("NotifyDataSetChanged")
    private void showBottomServiceRequestList() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ServiceRequestActivity.this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_service_request);

        TextView titleTv = bottomSheetDialog.findViewById(R.id.titleTv);
        RecyclerView serviceRv = bottomSheetDialog.findViewById(R.id.serviceRv);

        serviceRequestArrayList = new ArrayList<>();
        adapterServiceRequest = new AdapterServiceRequest(ServiceRequestActivity.this, serviceRequestArrayList);
        serviceRv.setAdapter(adapterServiceRequest);

        // 🔹 Real-time listener
        listenerRegistration = db.collection("serviceRequest")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        MyToast.showShort(ServiceRequestActivity.this, e.getMessage());
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        serviceRequestArrayList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            ModelServiceRequest modelServiceRequest = doc.toObject(ModelServiceRequest.class);
                            if (modelServiceRequest != null) {
                                serviceRequestArrayList.add(modelServiceRequest);
                            }
                        }
                        adapterServiceRequest.notifyDataSetChanged();
                    }
                });

        bottomSheetDialog.setOnDismissListener(dialog -> {
            if (listenerRegistration != null) {
                listenerRegistration.remove(); // ✅ BottomSheet বন্ধ হলে লিসেনার বন্ধ করব
                listenerRegistration = null;
            }
        });

        bottomSheetDialog.show();
    }

}