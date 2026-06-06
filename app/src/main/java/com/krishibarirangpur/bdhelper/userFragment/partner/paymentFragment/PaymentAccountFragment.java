package com.krishibarirangpur.bdhelper.userFragment.partner.paymentFragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.partner.AccountAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentPaymentAccountBinding;
import com.krishibarirangpur.bdhelper.model.AccountModel;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.core.PreloadingDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PaymentAccountFragment extends Fragment {

    private FragmentPaymentAccountBinding binding;

    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    LoadingDialog loadingDialog;

    List<AccountModel> accountList = new ArrayList<>();
    private AccountAdapter adapter;

    private PreloadingDialog preloadingDialog;


    public PaymentAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_payment_account, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();


        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        db = FirebaseFirestore.getInstance();

        preloadingDialog = new PreloadingDialog(requireContext());


        loadData();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //
                //MyToast.showShort(requireContext(),"Hello");
            }

            @Override
            public void onShowItemClick(int position) {
                // এখানে edit করার logic বসাও
                showEditDialog(position);
            }

            @Override
            public void onDeleteItemClick(int position) {
                // এখানে delete করার logic বসাও
                showDeleteDialog(position);

            }
        });


        binding.addPaymentAccountButton.setOnClickListener(v -> {
            //show Bottom Dialog in Add Payment Method
            showBottomDialogAddPaymentAccount();
        });
    }

    private void showEditDialog(int position) {
        AccountModel account = accountList.get(position);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_add_payment_account);

        // Views init
        MaterialRadioButton bkashRB = bottomSheetDialog.findViewById(R.id.bkashRB);
        MaterialRadioButton nagadRB = bottomSheetDialog.findViewById(R.id.nagadRB);
        MaterialRadioButton rocketRB = bottomSheetDialog.findViewById(R.id.rocketRB);
        MaterialRadioButton upayRB = bottomSheetDialog.findViewById(R.id.upayRB);

        SwitchMaterial primarySwitch = bottomSheetDialog.findViewById(R.id.primaryAccountSwitch);
        TextView primaryText = bottomSheetDialog.findViewById(R.id.primaryText);
        EditText accountNumberEt = bottomSheetDialog.findViewById(R.id.accountNumberEt);
        EditText accountTypeEt = bottomSheetDialog.findViewById(R.id.accountTypeEt);
        TextView submitBtn = bottomSheetDialog.findViewById(R.id.submitBtn);

        // সব button array তে রাখো
        MaterialRadioButton[] paymentButtons = {bkashRB, nagadRB, rocketRB, upayRB};

        // প্রতিটি button এ click listener set করো
        for (MaterialRadioButton rb : paymentButtons) {
            rb.setOnClickListener(v -> {
                // click হওয়া button বাদে সব button uncheck করো
                for (MaterialRadioButton other : paymentButtons) {
                    if (other != rb) other.setChecked(false);
                }
            });
        }

        primaryText.setOnClickListener(v -> {
            primarySwitch.setChecked(!primarySwitch.isChecked());
        });


        // পুরোনো value বসাও
        accountNumberEt.setText(account.getAccountNumber());
        accountTypeEt.setText(account.getContactName()); // অথবা accountType হলে সেটাও বসাও

        // Payment Method set করো
        switch (account.getAccountName()) {
            case "bKash":
                bkashRB.setChecked(true);
                break;
            case "Nagad":
                nagadRB.setChecked(true);
                break;
            case "Rocket":
                rocketRB.setChecked(true);
                break;
            case "uPay":
                upayRB.setChecked(true);
                break;
        }

        // Primary status set
        if (account.getIsPrimary().equals("Default")) {
            primarySwitch.setChecked(true);
        } else {
            primarySwitch.setChecked(false);
        }

        // Submit button -> Firestore Update
        submitBtn.setText(getString(R.string.update)); // Add নয়, Update দেখাও
        submitBtn.setOnClickListener(v -> {
            loadingDialog.setMessage("একাউন্টের তথ্য আপডেট করা হচ্ছে...");

            String accountName = "";
            if (bkashRB.isChecked()) accountName = "bKash";
            else if (nagadRB.isChecked()) accountName = "Nagad";
            else if (rocketRB.isChecked()) accountName = "Rocket";
            else if (upayRB.isChecked()) accountName = "uPay";

            String updatedNumber = accountNumberEt.getText().toString().trim();
            String updatedType = accountTypeEt.getText().toString().trim();

            boolean isPrimary = primarySwitch.isChecked();
            String setPrimary = isPrimary ? "Default" : "No";

            // Validation
            if (accountName.isEmpty()) {
                Toast.makeText(requireContext(), "দয়া করে পেমেন্ট মেথড সিলেক্ট করুন", Toast.LENGTH_SHORT).show();
                return;
            } else if (updatedNumber.isEmpty()) {
                accountNumberEt.setError("একাউন্ট নাম্বার দিন");
                accountNumberEt.requestFocus();
                return;
            } else if (updatedType.isEmpty()) {
                accountTypeEt.setError("একাউন্টের পরিচিতি নাম দিন");
                accountTypeEt.requestFocus();
                return;
            }

            loadingDialog.show();

            // Prepare update map
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("accountName", accountName.trim());
            updateMap.put("accountNumber", updatedNumber.trim());
            updateMap.put("contactName", updatedType.trim());
            updateMap.put("isPrimary", setPrimary);

            if (setPrimary.equals("Default")) {
                // আগে অন্য Default কে "No" করে দিব
                db.collection(FirebaseCollectionTable.USERS)
                        .document(userId)
                        .collection(FirebaseCollectionTable.ACCOUNTS)
                        .whereEqualTo("isPrimary", "Default")
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            WriteBatch batch = db.batch();

                            for (DocumentSnapshot doc : querySnapshot) {
                                // বর্তমান যে account টা update হচ্ছে সেটা বাদ দেবে
                                if (!doc.getId().equals(account.getAccountId())) {
                                    batch.update(doc.getReference(), "isPrimary", "No");
                                }
                            }

                            // এবার নিজের টা update করবে
                            DocumentReference currentDocRef = db.collection(FirebaseCollectionTable.USERS)
                                    .document(userId)
                                    .collection(FirebaseCollectionTable.ACCOUNTS)
                                    .document(account.getAccountId());

                            batch.update(currentDocRef, updateMap);

                            batch.commit().addOnSuccessListener(unused -> {
                                MyToast.showShort(requireContext(), "Account updated");
                                bottomSheetDialog.dismiss();
                                loadingDialog.dismiss();
                                loadData();
                            }).addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                MyToast.showShort(requireContext(), "Update failed: " + e.getMessage());
                            });
                        });
            }
            else {
                // যদি Default না হয় তবে সরাসরি update হবে
                db.collection(FirebaseCollectionTable.USERS)
                        .document(userId)
                        .collection(FirebaseCollectionTable.ACCOUNTS)
                        .document(account.getAccountId())
                        .update(updateMap)
                        .addOnSuccessListener(unused -> {
                            MyToast.showShort(requireContext(), "Account updated");
                            bottomSheetDialog.dismiss();
                            loadingDialog.dismiss();
                            loadData();
                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            MyToast.showShort(requireContext(), "Update failed: " + e.getMessage());
                        });
            }
        });


        bottomSheetDialog.show();
    }


    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_confirm, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnYes = dialogView.findViewById(R.id.btn_yes);

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v1 -> dialog.dismiss());
        }

        if (btnYes != null) {
            btnYes.setOnClickListener(v2 -> {
                loadingDialog.setMessage("Deleting account...");
                loadingDialog.show();

                AccountModel accountToDelete = accountList.get(position);

                // Correct Firestore reference
                DocumentReference docToDeleteRef = db.collection(FirebaseCollectionTable.USERS)
                        .document(userId)
                        .collection(FirebaseCollectionTable.ACCOUNTS)
                        .document(accountToDelete.getAccountId());

                // Check if the account to be deleted is the default one
                if ("Default".equals(accountToDelete.getIsPrimary())) {
                    // It's the default account. We need to find a new one.
                    AccountModel newDefaultAccount = null;
                    for (AccountModel account : accountList) {
                        if (!account.getAccountId().equals(accountToDelete.getAccountId())) {
                            newDefaultAccount = account;
                            break; // Found a candidate, exit loop
                        }
                    }

                    if (newDefaultAccount != null) {
                        // Another account exists. Set it as default, then delete the old one.
                        DocumentReference newDefaultRef = db.collection(FirebaseCollectionTable.USERS)
                                .document(userId)
                                .collection(FirebaseCollectionTable.ACCOUNTS)
                                .document(newDefaultAccount.getAccountId());

                        WriteBatch batch = db.batch();
                        batch.update(newDefaultRef, "isPrimary", "Default"); // Set new default
                        batch.delete(docToDeleteRef); // Delete the old default

                        batch.commit().addOnSuccessListener(unused -> {
                            loadingDialog.dismiss();
                            dialog.dismiss();
                            MyToast.showShort(requireContext(), "Account deleted. New default set.");
                            loadData(); // Reload all data to ensure consistency
                        }).addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            dialog.dismiss();
                            MyToast.showShort(requireContext(), "Operation failed: " + e.getMessage());
                        });
                    } else {
                        // It's the only account in the list. Just delete it.
                        docToDeleteRef.delete().addOnSuccessListener(unused -> {
                            loadingDialog.dismiss();
                            dialog.dismiss();
                            MyToast.showShort(requireContext(), "Account deleted successfully.");
                            loadData(); // Reload
                        }).addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            dialog.dismiss();
                            MyToast.showShort(requireContext(), "Delete failed: " + e.getMessage());
                        });
                    }
                } else {
                    // It's not a default account, so just delete it directly.
                    docToDeleteRef.delete().addOnSuccessListener(unused -> {
                        loadingDialog.dismiss();
                        dialog.dismiss();
                        MyToast.showShort(requireContext(), "Account deleted successfully.");
                        loadData(); // Reload
                    }).addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        dialog.dismiss();
                        MyToast.showShort(requireContext(), "Delete failed: " + e.getMessage());
                    });
                }
            });
        }

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadData() {
        preloadingDialog.show();

        // Adapter একবার সেট করা (onCreateView বা init এ ভালো হবে)
        if (adapter == null) {
            adapter = new AccountAdapter(accountList);
            binding.paymentAccountRv.setAdapter(adapter);
        }

        db.collection(FirebaseCollectionTable.USERS)
                .document(userId)
                .collection(FirebaseCollectionTable.ACCOUNTS)
                .orderBy("isPrimary", Query.Direction.ASCENDING) // যদি field থাকে
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    preloadingDialog.dismiss();
                    accountList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        AccountModel account = doc.toObject(AccountModel.class);
                        if (account != null) {
                            accountList.add(account);
                        }
                    }

                    // চেক list empty কিনা
                    if (accountList.isEmpty()) {
                        binding.paymentAccountNotFoundTv.setVisibility(View.VISIBLE);
                        binding.paymentAccountFoundTv.setVisibility(View.GONE);
                    } else {
                        binding.paymentAccountNotFoundTv.setVisibility(View.GONE);
                        binding.paymentAccountFoundTv.setVisibility(View.VISIBLE);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    preloadingDialog.dismiss();
                    MyToast.showShort(requireContext(), "Failed to load accounts: " + e.getMessage());
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showBottomDialogAddPaymentAccount() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_add_payment_account);

        MaterialRadioButton bkashRB = bottomSheetDialog.findViewById(R.id.bkashRB);
        MaterialRadioButton nagadRB = bottomSheetDialog.findViewById(R.id.nagadRB);
        MaterialRadioButton rocketRB = bottomSheetDialog.findViewById(R.id.rocketRB);
        MaterialRadioButton upayRB = bottomSheetDialog.findViewById(R.id.upayRB);

        SwitchMaterial primarySwitch = bottomSheetDialog.findViewById(R.id.primaryAccountSwitch);
        TextView primaryText = bottomSheetDialog.findViewById(R.id.primaryText); // TextView এর id দিন
        EditText accountNumberEt = bottomSheetDialog.findViewById(R.id.accountNumberEt);
        EditText accountTypeEt = bottomSheetDialog.findViewById(R.id.accountTypeEt);
        TextView submitBtn = bottomSheetDialog.findViewById(R.id.submitBtn);

        // সব button array তে রাখো
        MaterialRadioButton[] paymentButtons = {bkashRB, nagadRB, rocketRB, upayRB};

        // প্রতিটি button এ click listener set করো
        for (MaterialRadioButton rb : paymentButtons) {
            rb.setOnClickListener(v -> {
                // click হওয়া button বাদে সব button uncheck করো
                for (MaterialRadioButton other : paymentButtons) {
                    if (other != rb) other.setChecked(false);
                }
            });
        }

        primaryText.setOnClickListener(v -> {
            primarySwitch.setChecked(!primarySwitch.isChecked());
        });

        submitBtn.setOnClickListener(v -> {
            loadingDialog.setMessage("আপনার একাউন্ট যোগ করা হচ্ছে");


            // 1. Get selected payment method
            String accountName = "";
            if (bkashRB.isChecked()) accountName = "bKash";
            else if (nagadRB.isChecked()) accountName = "Nagad";
            else if (rocketRB.isChecked()) accountName = "Rocket";
            else if (upayRB.isChecked()) accountName = "uPay";

            // 2. Get EditText values
            String accountNumber = accountNumberEt.getText().toString().trim();
            String accountType = accountTypeEt.getText().toString().trim();

            // 3. Get switch value
            assert primarySwitch != null;
            boolean isPrimary = primarySwitch.isChecked();
            String setPrimary = isPrimary ? "Default" : "No";

            // 4. Validation Check
            if (accountName.isEmpty()) {
                Toast.makeText(requireContext(), "দয়া করে পেমেন্ট মেথড সিলেক্ট করুন", Toast.LENGTH_SHORT).show();
                return; // exit click
            }
            else if (accountNumber.isEmpty()) {
                accountNumberEt.setError("একাউন্ট নাম্বার দিন");
                accountNumberEt.requestFocus();
                return;
            }
            else if (accountType.isEmpty()) {
                accountTypeEt.setError("একাউন্টের পরিচিতি নাম দিন");
                accountTypeEt.requestFocus();
                return;
            }

            loadingDialog.show();


            // Create unique document ID
            String docId = db.collection(FirebaseCollectionTable.USERS)
                    .document(userId)
                    .collection(FirebaseCollectionTable.ACCOUNTS)
                    .document()
                    .getId();

            // Prepare account data
            Map<String, Object> accountMap = new HashMap<>();
            accountMap.put("accountId", docId);
            accountMap.put("accountName", accountName.trim());
            accountMap.put("accountNumber", accountNumber.trim());
            accountMap.put("contactName", accountType.trim());
            accountMap.put("isPrimary", setPrimary);  // "Default" or "No"
            accountMap.put("timestamp", FieldValue.serverTimestamp());

            if (isPrimary) {
                // Batch operation শুরু
                WriteBatch batch = db.batch();

                // Step 1: পুরোনো Default গুলোকে "No" করে দাও
                db.collection(FirebaseCollectionTable.USERS)
                        .document(userId)
                        .collection(FirebaseCollectionTable.ACCOUNTS)
                        .whereEqualTo("isPrimary", "Default")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                batch.update(doc.getReference(), "isPrimary", "No");
                            }

                            // Step 2: নতুন অ্যাকাউন্ট add করো
                            DocumentReference newDocRef = db.collection(FirebaseCollectionTable.USERS)
                                    .document(userId)
                                    .collection(FirebaseCollectionTable.ACCOUNTS)
                                    .document(docId);
                            batch.set(newDocRef, accountMap, SetOptions.merge());

                            // Step 3: batch commit
                            batch.commit()
                                    .addOnSuccessListener(unused -> {
                                        loadData();
                                        loadingDialog.dismiss();
                                        bottomSheetDialog.dismiss();
                                        MyToast.showShort(requireContext(), "Account saved successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        loadingDialog.dismiss();
                                        bottomSheetDialog.dismiss();
                                        MyToast.showShort(requireContext(), "Failed: " + e.getMessage());
                                    });
                        })
                        .addOnFailureListener(e -> {
                            MyToast.showShort(requireContext(), "Failed to check existing primary: " + e.getMessage());
                        });

            }
            else {
                // সরাসরি Save করো
                db.collection(FirebaseCollectionTable.USERS)
                        .document(userId)
                        .collection(FirebaseCollectionTable.ACCOUNTS)
                        .document(docId)
                        .set(accountMap, SetOptions.merge())
                        .addOnSuccessListener(unused -> {
                            loadData();
                            loadingDialog.dismiss();
                            bottomSheetDialog.dismiss();
                            MyToast.showShort(requireContext(), "Account saved successfully");
                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            bottomSheetDialog.dismiss();
                            MyToast.showShort(requireContext(), "Failed to save: " + e.getMessage());
                        });
            }



        });

        bottomSheetDialog.show();
    }
}