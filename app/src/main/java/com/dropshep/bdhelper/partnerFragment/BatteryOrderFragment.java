package com.dropshep.bdhelper.partnerFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentBatteryOrderBinding;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.partner.ProductActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class BatteryOrderFragment extends Fragment {

    private FragmentBatteryOrderBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    LoadingDialog loadingDialog;


    public BatteryOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_battery_order, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // ডিফল্ট ভিউ (newBattery visible, oldBattery gone)
        binding.newBatteryLl.setVisibility(View.VISIBLE);
        binding.oldBatteryLl.setVisibility(View.GONE);

        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        binding.orderTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbNewBattery){
                // নতুন ব্যাটারি সিলেক্ট
                binding.oldBatteryLl.setVisibility(View.GONE);
            }
            else if (checkedId == R.id.rbExchange){
                // এক্সচেঞ্জ সিলেক্ট
                binding.oldBatteryLl.setVisibility(View.VISIBLE);
            }
        });

        binding.batteryTypeEt.setOnClickListener(v ->
                showPopup(binding.batteryTypeEt, new String[]{
                        getString(R.string.acid_battery),
                        getString(R.string.dry_battery)
                }));

        binding.oldBatteryTypeEt.setOnClickListener(v ->
                showPopup(binding.oldBatteryTypeEt, new String[]{
                        getString(R.string.acid_battery),
                        getString(R.string.dry_battery)
                }));

        binding.batteryVoltageEt.setOnClickListener(v ->
                showPopup(binding.batteryVoltageEt, new String[]{
                        getString(R.string.twelve_volte),
                        getString(R.string.twenty_four_volte)
                }));

        binding.oldBatteryVoltageEt.setOnClickListener(v ->
                showPopup(binding.oldBatteryVoltageEt, new String[]{
                        getString(R.string.twelve_volte),
                        getString(R.string.twenty_four_volte)
                }));


        binding.submitOrderBtn.setOnClickListener(v -> {
            submitWithValidation();
        });

    }

    private void showPopup(TextView targetView, String[] options) {
        PopupMenu popupMenu = new PopupMenu(getContext(), targetView, Gravity.END);

        for (int i = 0; i < options.length; i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, options[i]);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            targetView.setText(options[item.getItemId()]);
            targetView.setBackgroundResource(R.drawable.bg_edit_text);
            popupMenu.dismiss();
            return true;
        });

        popupMenu.show();
    }


    private void submitWithValidation() {
        // Selected Order Type
        int selectedId = binding.orderTypeGroup.getCheckedRadioButtonId();


        // --- New Battery Required Fields ---
        if (isEmpty(binding.batteryNameEt, "ব্যাটারির নাম লিখুন")) return;
        if (isEmpty(binding.batteryTypeEt, "ব্যাটারির ধরন লিখুন")) return;
        if (isEmpty(binding.batteryVoltageEt, "ভোল্টেজ লিখুন")) return;
        if (isEmpty(binding.batteryCapacityEt, "ক্যাপাসিটি লিখুন")) return;

        // --- Old Battery Required Fields (Exchange only) ---
        if (selectedId == R.id.rbExchange) {
            if (isEmpty(binding.oldBatteryBrandEt, "পুরানো ব্যাটারির ব্র্যান্ড লিখুন")) return;
            if (isEmpty(binding.oldBatteryTypeEt, "পুরানো ব্যাটারির ধরন লিখুন")) return;
            if (isEmpty(binding.oldBatteryVoltageEt, "পুরানো ব্যাটারির ভোল্টেজ লিখুন")) return;
            if (isEmpty(binding.oldBatteryCapacityEt, "পুরানো ব্যাটারির ক্যাপাসিটি লিখুন")) return;
        }

        // --- Delivery Info (always required)
        if (isEmpty(binding.userNameEt, "নাম লিখুন")) return;
        if (isEmpty(binding.userPhoneEt, "মোবাইল নাম্বার লিখুন")) return;
        if (isEmpty(binding.deliveryAddressEt, "ঠিকানা লিখুন")) return;


        // ✅ সব ঠিক থাকলে
        //MyToast.showShort(getContext(), "অর্ডার সাবমিট হচ্ছে...");
        submitOrderToFirestore();
    }

    private void submitOrderToFirestore() {
        loadingDialog.setMessage("অর্ডার সাবমিট হচ্ছে...");
        loadingDialog.show();

        // --- New Battery ---
        String batteryName = binding.batteryNameEt.getText().toString().trim();
        String batteryType = binding.batteryTypeEt.getText().toString().trim();
        String batteryVoltage = binding.batteryVoltageEt.getText().toString().trim();
        String batteryCapacity = binding.batteryCapacityEt.getText().toString().trim();

        // --- Exchange ---
        String oldBatteryBrand = binding.oldBatteryBrandEt.getText().toString().trim();
        String oldBatteryType = binding.oldBatteryTypeEt.getText().toString().trim();
        String oldBatteryVoltage = binding.oldBatteryVoltageEt.getText().toString().trim();
        String oldBatteryCapacity = binding.oldBatteryCapacityEt.getText().toString().trim();

        // --- Delivery Info ---
        String userName = binding.userNameEt.getText().toString().trim();
        String userPhone = binding.userPhoneEt.getText().toString().trim();
        String deliveryAddress = binding.deliveryAddressEt.getText().toString().trim();


        String timestamp = String.valueOf(System.currentTimeMillis());

        // Battery Map
        Map<String, Object> batteryDetails = new HashMap<>();
        batteryDetails.put("batteryId", "");                    // battery product Id (inventory থেকে)
        batteryDetails.put("batteryName", batteryName);
        batteryDetails.put("batteryType", batteryType);
        batteryDetails.put("batteryVoltage", batteryVoltage);
        batteryDetails.put("batteryCapacity", batteryCapacity);
        batteryDetails.put("batteryPrice", "0");

        // Exchange Map
        Map<String, Object> exchangeDetails = new HashMap<>();
        exchangeDetails.put("oldBatteryBrand", oldBatteryBrand);
        exchangeDetails.put("oldBatteryType", oldBatteryType);
        exchangeDetails.put("oldBatteryVoltage", oldBatteryVoltage);
        exchangeDetails.put("oldBatteryCapacity", oldBatteryCapacity);
        exchangeDetails.put("exchangeDiscount", "0");       // কত ছাড় পেল

        // Delivery Map
        Map<String, Object> deliveryDetails = new HashMap<>();
        deliveryDetails.put("deliveryAddress", deliveryAddress);
        deliveryDetails.put("deliveryCharge", "0");
        deliveryDetails.put("deliveryStatus", "pending");

        // Payment Map
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("paymentMethod", "");
        paymentDetails.put("paymentStatus", "Unpaid");
        paymentDetails.put("transactionId", "");

        CommonClass.generateOrderId(
                db,
                "batteryOrder",             // collection path
                "orderId",                              // Document ID হিসেবে চাইলে documentId ব্যবহার করতে পারো
                "ord",                                  // prefix
                4,                                      // initial digit length, যেমন ord0001
                new CommonClass.OrderIdCallback() {
                    @Override
                    public void onSuccess(String orderId) {
                        Log.d("OrderID", "Generated: " + orderId);

                        // 🔽 এবার batteryOrder এ save করা
                        // Order Map
                        Map<String, Object> order = new HashMap<>();
                        order.put("orderId", orderId);
                        order.put("userId", firebaseUser.getUid());
                        order.put("userName", userName);
                        order.put("userPhone", userPhone);
                        order.put("userType", "partner");
                        order.put("orderType", "battery");
                        order.put("orderStatus", "pending");
                        order.put("exchangeType", !binding.oldBatteryBrandEt.getText().toString().isEmpty() ? "exchange" : "new");
                        order.put("timestamp", timestamp);
                        order.put("batteryDetails", batteryDetails);
                        order.put("exchangeDetails", exchangeDetails);
                        order.put("deliveryDetails", deliveryDetails);
                        order.put("paymentDetails", paymentDetails);

                        db.collection("batteryOrder")
                                .document(orderId)
                                .set(order)
                                .addOnSuccessListener(aVoid -> {
                                    loadingDialog.dismiss();
                                    Log.d("OrderID", "Document created with ID: " + orderId);
                                    MyToast.showShort(getContext(), "অর্ডার সফলভাবে সাবমিট হয়েছে");
                                    clearBatteryForm();

                                    ProductActivity parent = (ProductActivity) getActivity();
                                    assert parent != null;
                                    parent.setPagerFragment(0);
                                })
                                .addOnFailureListener(e -> {
                                    loadingDialog.dismiss();
                                    Log.e("OrderID", "Error: ", e);
                                    MyToast.showShort(getContext(), "অর্ডার সাবমিট ব্যর্থ হয়েছে");
                                });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        loadingDialog.dismiss();
                        Log.d("OrderID", "Failed: " + e.getMessage());
                    }
                }
        );

    }

    private void clearBatteryForm() {
        // ✅ Battery info clear
        binding.batteryNameEt.setText("");
        binding.batteryTypeEt.setText("");
        binding.batteryVoltageEt.setText("");
        binding.batteryCapacityEt.setText("");

        // ✅ Exchange battery clear
        binding.oldBatteryBrandEt.setText("");
        binding.oldBatteryTypeEt.setText("");
        binding.oldBatteryVoltageEt.setText("");
        binding.oldBatteryCapacityEt.setText("");

        // ✅ Delivery info clear
        binding.userNameEt.setText("");
        binding.userPhoneEt.setText("");
        binding.deliveryAddressEt.setText("");
    }

    private boolean isEmpty(TextView textView, String errorMsg) {
        if (textView.getText().toString().trim().isEmpty()) {
            if (textView instanceof EditText) {
                //শুধুমাত্র EditText হলে Error দেখাবে
                textView.setBackgroundResource(R.drawable.bg_edit_text_error);
                textView.setError(errorMsg);
                textView.requestFocus();
                textView.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        textView.setBackgroundResource(R.drawable.bg_edit_text);

                    }
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override public void afterTextChanged(Editable s) {}
                });
            } else {
                // TextView হলে Error এর পরিবর্তে Toast বা অন্য indication দেখাতে পারো
                textView.setBackgroundResource(R.drawable.bg_edit_text_error);
            }
            return true;
        }
        return false;
    }

}