package com.krishibarirangpur.bdhelper.userActivity.customer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.AddressBookAdapter;
import com.krishibarirangpur.bdhelper.databinding.ActivityAddressBookBinding;
import com.krishibarirangpur.bdhelper.model.AddressBookModel;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.ValidationClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddressBookActivity extends BaseActivity {

    private ActivityAddressBookBinding binding;

    boolean isUpdate = false;
    private String locationData;
    private String currentAddressId;

    FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private AddressBookAdapter addressBookAdapter;
    private ArrayList<AddressBookModel> addressBookArrayList;
    private LoadingDialog loadingDialog;
    private ListenerRegistration addressBookListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_book);

        initFirebase();
        initViews();
        loadAddressBookData();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
    }

    private void initViews() {

        addressBookArrayList = new ArrayList<>();
        addressBookAdapter = new AddressBookAdapter(this, addressBookArrayList);
        binding.addressListRv.setAdapter(addressBookAdapter);

        // Add new address
        binding.addAddressBtn.setOnClickListener(v -> openMapActivity());
        binding.addAddressNewBtn.setOnClickListener(v -> openMapActivity());

        // Back button
        binding.backBtn.setOnClickListener(v -> handleCustomBack());

        // Pick contact
        binding.btnContacts.setOnClickListener(v -> pickContact());

        // Save button (default listener)
        binding.saveAddressBookBtn.setOnClickListener(v -> saveAddressBook());

        // Adapter item click listeners
        addressBookAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View model, int position) {
                if (getIntent().getBooleanExtra(MyUtils.IS_PICKER, false)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", addressBookArrayList.get(position).getAddress());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }

            @Override
            public void onShowItemClick(int position) {
                showUpdateAddressBook(position);
            }

            @Override
            public void onDeleteItemClick(int position) {
                showDeleteDialog(position);
            }
        });
    }

    // -------------------- Map & Contact --------------------
    private void openMapActivity() {
        Intent intent = new Intent(this, MapLocationActivity.class);
        rentLocationResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> rentLocationResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Intent data = result.getData();
                            if (data != null) {
                                locationData = data.getStringExtra("result");
                                isUpdate = false;
                                currentAddressId = null;

                                binding.addressSaveLL.setVisibility(View.VISIBLE);
                                binding.addressListRl.setVisibility(View.GONE);
                                binding.saveAddressBookBtn.setText(getString(R.string.save));
                                binding.saveAddressBookBtn.setOnClickListener(v -> saveAddressBook());

                                populateLocationUI(locationData);
                            }
                        }
                    });


    // 1️⃣ Contact Picker Launcher declare
    private final ActivityResultLauncher<Intent> pickContactLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            handlePickedContact(result.getData());
                        }
                    });

    // 2️⃣ Method to open contact picker
    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        pickContactLauncher.launch(intent);
    }

    // 3️⃣ Method to handle picked contact
    private void handlePickedContact(Intent data) {
        Uri contactUri = data.getData();
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        assert contactUri != null;
        Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            @SuppressLint("Range")
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            // Clean number
            String cleanNumber = number.replaceAll("[^0-9]", "");
            if (cleanNumber.startsWith("88")) cleanNumber = cleanNumber.substring(2);
            if (cleanNumber.startsWith("01") && cleanNumber.length() > 11) cleanNumber = cleanNumber.substring(0, 11);

            // Set in UI
            binding.recipientNameEt.setText(name);
            binding.recipientMobileEt.setText(cleanNumber);

            cursor.close();
        }
    }


    private void populateLocationUI(String locationData) {
        String[] parts = locationData.split(",", 2);
        binding.addressNameEt.setText(parts[0].trim());
        binding.showGetAddressTitleTv.setText(parts[0].trim());
        if (parts.length > 1) binding.showGetAddressTv.setText(parts[1].trim());
    }

    // -------------------- Save & Update --------------------
    private void saveAddressBook() {
        String addressName = binding.addressNameEt.getText().toString().trim();
        String recipientMobile = binding.recipientMobileEt.getText().toString().trim();
        String recipientName = binding.recipientNameEt.getText().toString().trim();

        if (TextUtils.isEmpty(addressName)) {
            ValidationClass.setErrorWatcher(binding.addressNameEt, true);
            return;
        }
        if (TextUtils.isEmpty(recipientMobile)) {
            ValidationClass.setErrorWatcher(binding.recipientMobileEt, true);
            return;
        }

        if (TextUtils.isEmpty(recipientName)) {
            ValidationClass.setErrorWatcher(binding.recipientNameEt, true);
            return;
        }

        loadingDialog.setMessage("আপনার ঠিকানা অ্যাড্রেসবুকে সেভ হচ্ছে..");
        loadingDialog.show();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String userId = firebaseUser.getUid();
        String addressId = db.collection(FirebaseCollectionTable.USERS).document(userId)
                .collection(FirebaseCollectionTable.ADDRESS_BOOK).document().getId();

        Map<String, Object> map = new HashMap<>();
        map.put("addressId", addressId);
        map.put("address", locationData);
        map.put("addressName", addressName);
        map.put("recipientMobile", recipientMobile);
        map.put("recipientName", recipientName);
        map.put("timestamp", timestamp);

        db.collection(FirebaseCollectionTable.USERS).document(userId)
                .collection(FirebaseCollectionTable.ADDRESS_BOOK).document(addressId)
                .set(map)
                .addOnSuccessListener(unused -> {
                    loadingDialog.dismiss();
                    clearForm();
                    binding.addressSaveLL.setVisibility(View.GONE);
                    binding.addressListRl.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> loadingDialog.dismiss());
    }

    private void showUpdateAddressBook(int position) {
        isUpdate = true;
        AddressBookModel model = addressBookArrayList.get(position);
        locationData = model.getAddress();
        currentAddressId = model.getAddressId();

        binding.addressSaveLL.setVisibility(View.VISIBLE);
        binding.addressListRl.setVisibility(View.GONE);
        binding.saveAddressBookBtn.setText(getString(R.string.update));
        populateLocationUI(locationData);

        //MyToast.showShort(this, "Update: "+currentAddressId);

        binding.addressNameEt.setText(model.getAddressName());
        binding.recipientNameEt.setText(model.getRecipientName());
        binding.recipientMobileEt.setText(model.getRecipientMobile());

        binding.saveAddressBookBtn.setOnClickListener(v -> updateAddressBook(currentAddressId));
    }

    private void updateAddressBook(String addressId) {
        String addressName = binding.addressNameEt.getText().toString().trim();
        String recipientMobile = binding.recipientMobileEt.getText().toString().trim();
        String recipientName = binding.recipientNameEt.getText().toString().trim();

        if (TextUtils.isEmpty(addressName) || TextUtils.isEmpty(recipientMobile)) return;

        loadingDialog.setMessage("আপনার অ্যাড্রেসবুক আপডেট হচ্ছে..");
        loadingDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("address", locationData);
        map.put("addressName", addressName);
        map.put("recipientMobile", recipientMobile);
        map.put("recipientName", recipientName);

        String userId = firebaseUser.getUid();
        db.collection(FirebaseCollectionTable.USERS).document(userId).collection(FirebaseCollectionTable.ADDRESS_BOOK).document(addressId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    loadingDialog.dismiss();
                    clearForm();
                    binding.addressSaveLL.setVisibility(View.GONE);
                    binding.addressListRl.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> loadingDialog.dismiss());
    }

    private void clearForm() {
        binding.addressNameEt.setText("");
        binding.recipientNameEt.setText("");
        binding.recipientMobileEt.setText("");
        binding.showGetAddressTitleTv.setText("");
        binding.showGetAddressTv.setText("");
        locationData = null;
        isUpdate = false;
        currentAddressId = null;
    }

    // -------------------- Delete --------------------
    private void showDeleteDialog(int position) {
        AddressBookModel model = addressBookArrayList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        TextView message = dialogView.findViewById(R.id.messageTv);

        message.setText(model.getAddress());

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            db.collection(FirebaseCollectionTable.USERS).document(firebaseUser.getUid())
                    .collection(FirebaseCollectionTable.ADDRESS_BOOK).document(model.getAddressId())
                    .delete()
                    .addOnSuccessListener(unused -> MyToast.showShort(AddressBookActivity.this, "Address deleted"))
                    .addOnFailureListener(e -> MyToast.showShort(AddressBookActivity.this, "Failed: " + e.getMessage()));
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    // -------------------- Load Data --------------------
    @SuppressLint("NotifyDataSetChanged")
    private void loadAddressBookData() {
        String userId = firebaseUser.getUid();

        if (addressBookListener != null) addressBookListener.remove();

        addressBookListener = db.collection(FirebaseCollectionTable.USERS).document(userId)
                .collection(FirebaseCollectionTable.ADDRESS_BOOK)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) return;

                    addressBookArrayList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            AddressBookModel model = doc.toObject(AddressBookModel.class);
                            if (model != null) addressBookArrayList.add(model);
                        }
                    }
                    addressBookAdapter.notifyDataSetChanged();

                    binding.noAddressRl.setVisibility(addressBookArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.addressListRv.setVisibility(addressBookArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }

    // -------------------- Back Handling --------------------
    protected boolean handleCustomBack() {
        if (binding.addressSaveLL.getVisibility() == View.VISIBLE
                && binding.addressListRl.getVisibility() == View.GONE) {
            binding.addressSaveLL.setVisibility(View.GONE);
            binding.addressListRl.setVisibility(View.VISIBLE);
            clearForm();
            return true;
        }
        finishOnBack(); // Exit activity
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (addressBookListener != null) addressBookListener.remove();
    }
}
