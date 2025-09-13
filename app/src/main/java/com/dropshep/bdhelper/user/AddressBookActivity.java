package com.dropshep.bdhelper.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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

import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.AddressBookAdapter;
import com.dropshep.bdhelper.databinding.ActivityAddressBookBinding;
import com.dropshep.bdhelper.model.AccountModel;
import com.dropshep.bdhelper.model.ModelAddressBook;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressBookActivity extends BaseActivity {

    private ActivityAddressBookBinding binding;

    String controlType,locationData;
    boolean isPicker = false;
    private static final int PICK_CONTACT = 1001;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    FirebaseFirestore db;

    AddressBookAdapter addressBookAdapter;

    ProgressDialog progressDialog;

    ArrayList<ModelAddressBook> addressBookArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_book);

        //Init views
        isPicker = getIntent().getBooleanExtra("isPicker", false);
        controlType = getIntent().getStringExtra("controlType");
        if (controlType != null && controlType.equals("addressList")){
            //
            binding.addressListRl.setVisibility(View.VISIBLE);
            binding.addressSaveLL.setVisibility(View.GONE);
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);


        //Map activity Open
        binding.addAddressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AddressBookActivity.this, MapLocationActivity.class);
            rentLocationResultLauncher.launch(intent);
        });

        //No Address Found
        binding.addAddressNewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AddressBookActivity.this, MapLocationActivity.class);
            rentLocationResultLauncher.launch(intent);
        });

        binding.backBtn.setOnClickListener(v -> finishOnBack());


        //get Contact In picker Contact Number And Name
        binding.btnContacts.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        });

        //Save Address in Database FireStore
        binding.saveAddressBookBtn.setOnClickListener(v -> {
            saveAddressBook();
        });

        //Load Data and Show in database FireStore
        addressBookArrayList = new ArrayList<>();
        loadDataInAddressBook();


        //OnClick Adapter Item Lister Edit/ Delete / Address Pick
        addressBookAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View model, int position) {
                ModelAddressBook modelAddressBook = addressBookArrayList.get(position);
                if (isPicker){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", modelAddressBook.getAddress());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                else {
                    Log.e("AddressBook", "onAddressClick: "+modelAddressBook.getAddress() );
                }

            }

            @Override
            public void onShowItemClick(int position) {
                //Edit AddressBook
            }

            @Override
            public void onDeleteItemClick(int position) {
                // এখানে delete করার logic বসাও
                showDeleteDialog(position);
            }
        });

    }


    private void saveAddressBook() {
        String addressName = binding.addressNameEt.getText().toString().trim();
        String recipientMobile = binding.recipientMobileEt.getText().toString().trim();
        String recipientName = binding.recipientNameEt.getText().toString().trim();

        if (TextUtils.isEmpty(addressName)){
            setErrorWatcher(binding.addressNameEt, true);
        }
        else if (TextUtils.isEmpty(recipientMobile)){
            setErrorWatcher(binding.recipientMobileEt, true);
        }
        else if (TextUtils.isEmpty(recipientName)){
            setErrorWatcher(binding.recipientNameEt, true);
        }
        else {
            //set dialog
            progressDialog.setMessage("আপনার ঠিকানা অ্যাড্রেসবুকে সেভ হচ্ছে..");
            progressDialog.show();


            //String timestamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String userId = firebaseUser.getUid();

            //AddressBook Id
            String addressId = db.collection("users")
                    .document(userId)
                    .collection("addressBook")
                    .document()
                    .getId();

            Map<String, Object> addressBookMap = new HashMap<>();
            addressBookMap.put("addressId", addressId);
            addressBookMap.put("address", locationData);
            addressBookMap.put("addressName", addressName);
            addressBookMap.put("recipientMobile", recipientMobile);
            addressBookMap.put("recipientName", recipientName);
            addressBookMap.put("timestamp", timestamp);

            db.collection("users")
                    .document(userId)
                    .collection("addressBook")
                    .document(addressId)
                    .set(addressBookMap)
                    .addOnSuccessListener(unused -> {
                        //Save Successful
                        progressDialog.dismiss();
                        binding.addressSaveLL.setVisibility(View.GONE);
                        binding.addressListRl.setVisibility(View.VISIBLE);
                        binding.addressNameEt.setText("");
                        binding.recipientNameEt.setText("");
                        binding.recipientMobileEt.setText("");
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Log.d("addressBook", "Failed: "+e.getMessage());
                    });

        }
    }

    private void showDeleteDialog(int position) {
        ModelAddressBook modelAddressBook = addressBookArrayList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        TextView message = dialogView.findViewById(R.id.messageTv);


        message.setText(modelAddressBook.getAddress());

        btnCancel.setOnClickListener(v1 -> dialog.dismiss());

        btnYes.setOnClickListener(v2 -> {

            String addressId = modelAddressBook.getAddressId();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = firebaseUser.getUid();
            dialog.dismiss();

            db.collection("users")
                    .document(userId)
                    .collection("addressBook")
                    .document(addressId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        MyToast.showShort(AddressBookActivity.this, "Address deleted");
                    })
                    .addOnFailureListener(e -> {
                        MyToast.showShort(AddressBookActivity.this, "Failed: " + e.getMessage());
                    });

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }




    private ListenerRegistration addressBookListener; // real-time listener ref

    private void loadDataInAddressBook() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = firebaseUser.getUid();

        addressBookAdapter = new AddressBookAdapter(AddressBookActivity.this, addressBookArrayList);
        binding.addressListRv.setAdapter(addressBookAdapter);

        // remove old listener (avoid multiple listeners)
        if (addressBookListener != null) {
            addressBookListener.remove();
        }

        addressBookListener = db.collection("users")
                .document(userId)
                .collection("addressBook")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.d("addressBook", "Real-time load failed: " + error.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        addressBookArrayList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            ModelAddressBook model = doc.toObject(ModelAddressBook.class);
                            if (model != null) {
                                addressBookArrayList.add(model);
                            }
                        }
                        addressBookAdapter.notifyDataSetChanged();

                        // ✅ check empty state
                        if (addressBookArrayList.isEmpty()) {
                            binding.noAddressRl.setVisibility(View.VISIBLE);
                            binding.addressListRv.setVisibility(View.GONE);
                        } else {
                            binding.noAddressRl.setVisibility(View.GONE);
                            binding.addressListRv.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    // 👉 Activity destroy হলে অবশ্যই listener remove করবে
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (addressBookListener != null) {
            addressBookListener.remove();
        }
    }


    private final ActivityResultLauncher<Intent> rentLocationResultLauncher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Intent data = result.getData();
                            if (data != null) {
                                locationData = data.getStringExtra("result");

                                binding.addressSaveLL.setVisibility(View.VISIBLE);
                                binding.addressListRl.setVisibility(View.GONE);
                                //binding.rentLocationTv.setText(locationData);
                                //binding.rentPointClear.setVisibility(View.VISIBLE);
                                String[] parts = locationData.split(",", 2); // শুধু ২ ভাগে ভাগ করবে

                                if (parts.length > 0) {
                                    binding.showGetAddressTitleTv.setText(parts[0].trim()); // প্রথম অংশ (Title)
                                }
                                if (parts.length > 1) {
                                    binding.showGetAddressTv.setText(parts[1].trim()); // বাকি অংশ
                                }

                                //goto next activity
                                //String rentLocation = binding.rentLocationTv.getText().toString();
                                //gotoNextActivity("", "", rentLocation);
                            } else {
                                //binding.rentLocationTv.setHint(R.string.select_location);
                            }
                        }
                    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();

            String[] projection = {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);

                // সব নন-ডিজিট বাদ দাও
                String cleanNumber = number.replaceAll("[^0-9]", "");

                // যদি 88 দিয়ে শুরু হয় → কাট
                if (cleanNumber.startsWith("88")) {
                    cleanNumber = cleanNumber.substring(2); // প্রথম ২ ডিজিট বাদ দাও
                }

                // যদি 01 দিয়ে শুরু হয় এবং দৈর্ঘ্য ≥ 11 হয় → ১১ ডিজিট নাও
                if (cleanNumber.startsWith("01") && cleanNumber.length() >= 11) {
                    cleanNumber = cleanNumber.substring(0, 11);
                }

                binding.recipientNameEt.setText(name);
                binding.recipientMobileEt.setText(cleanNumber);

                cursor.close();
            }

        }
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

}