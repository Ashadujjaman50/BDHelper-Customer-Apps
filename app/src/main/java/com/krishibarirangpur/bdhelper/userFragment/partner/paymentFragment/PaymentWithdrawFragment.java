package com.krishibarirangpur.bdhelper.userFragment.partner.paymentFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.partner.WithdrawAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentPaymentWithdrawBinding;
import com.krishibarirangpur.bdhelper.model.WithdrawRequest;
import com.krishibarirangpur.bdhelper.utils.FinanceCache;
import com.krishibarirangpur.bdhelper.utils.FinanceManager;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.core.PreloadingDialog;

import java.util.ArrayList;
import java.util.Map;

public class PaymentWithdrawFragment extends Fragment {

    private FragmentPaymentWithdrawBinding binding;

    private PreloadingDialog preloadingDialog;
    private LoadingDialog loadingDialog;
    private FirebaseFirestore db;
    private String userId, accountName, accountNumber;
    private WithdrawAdapter withdrawAdapter;
    private ArrayList<WithdrawRequest> withdrawRequestArrayList;
    private FinanceManager financeManager;
    double partnerEarn;

    public PaymentWithdrawFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_withdraw, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views

        preloadingDialog = new PreloadingDialog(requireContext());
        loadingDialog = new LoadingDialog(requireContext());
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
        financeManager = new FinanceManager();

        // 🔹 Cache থেকে ডেটা দেখাও, যদি থাকে
        partnerFinanceSummaryLoad();

        // Default Payment Account Load
        loadDefaultAccountData();

        //Load Withdraw History
        loadWithdrawRequests();

        binding.withdrawPaymentButton.setOnClickListener(v -> {
            //show Bottom Dialog in Add Payment Method
            showBottomDialogWithdrawPaymentAmount();
        });

    }


    private void partnerFinanceSummaryLoad() {

        if (FinanceCache.isLoaded) {
            FinanceCache.lastUpdated = System.currentTimeMillis();

            double totalEarned = FinanceCache.totalEarned;
            double partnerReceivable = FinanceCache.partnerReceivable;
            double companyReceivable = FinanceCache.companyReceivable;

            // 🔹 নেট হিসাব (কার পাওনা বেশি)
            Map<String, Double> result = FinanceManager.getNetReceivable(partnerReceivable, companyReceivable);
            double netAmount = result.get("netAmount");
            double owedTo = result.get("owedTo");


            // 🔹 কার পাওনা বেশি সেটার ভিত্তিতে টেক্সট আপডেট করো
            if (owedTo == 1.0) {
                partnerEarn = netAmount;
            } else if (owedTo == 2.0) {
                partnerEarn = 0;
            } else {
                partnerEarn = 0;
            }

        }
        else {
            // 🔹 যদি cache লোড না থাকে, Firestore থেকে ডেটা নাও
            financeManager.getPartnerFinanceSummary(userId, (totalEarned, partnerReceivable, companyReceivable) -> {

                Map<String, Double> result = FinanceManager.getNetReceivable(partnerReceivable, companyReceivable);
                double netAmount = result.get("netAmount");
                double owedTo = result.get("owedTo");


                if (owedTo == 1.0) {
                    partnerEarn = netAmount;
                } else if (owedTo == 2.0) {
                    partnerEarn = 0;
                } else {
                    partnerEarn = 0;
                }

            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (financeManager != null) financeManager.stopListening(); // 🔹 Stop realtime listener
    }

    private void loadDefaultAccountData() {
        db.collection(FirebaseCollectionTable.USERS)
                .document(userId)
                .collection(FirebaseCollectionTable.ACCOUNTS)
                .whereEqualTo("isPrimary", "Default") // ✅ শুধুমাত্র Default account আনবে
                .limit(1) // নিরাপত্তার জন্য ১টা result নেবে
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);

                        // 🔹 Account Data Load করো
                        accountName = doc.getString("accountName");
                        accountNumber = doc.getString("accountNumber");

                        Log.d("AccountDebug", "✅ Default account loaded: " + accountName);
                    }
                    else {
                        MyToast.showShort(requireContext(), "⚠️ No default payment account found.");
                        Log.d("AccountDebug",  "⚠️ No default account found.");
                    }
                })
                .addOnFailureListener(e ->
                        MyToast.showShort(requireContext(), "❌ Failed to load accounts: " + e.getMessage()));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadWithdrawRequests() {
        withdrawRequestArrayList = new ArrayList<>();
        withdrawAdapter = new WithdrawAdapter(requireContext(), withdrawRequestArrayList);
        binding.paymentAccountRv.setAdapter(withdrawAdapter);

        preloadingDialog.show();

        db.collection(FirebaseCollectionTable.WITHDRAW_REQUESTS)
                .whereEqualTo("vendorId", userId)
                .orderBy("requestedAt", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    preloadingDialog.dismiss();
                    if (error != null) {
                        Log.e("Withdraw Request", "❌ loadWithdrawRequests: " + error.getMessage(), error);
                        MyToast.showShort(requireContext(), "Failed to load withdraw requests: " + error.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        withdrawRequestArrayList.clear();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            WithdrawRequest request = doc.toObject(WithdrawRequest.class);
                            if (request != null) {
                                request.setId(doc.getId()); // 🔹 ডকুমেন্ট আইডি সেট করো
                                withdrawRequestArrayList.add(request);
                            }
                        }

                        // ✅ Empty view handle
                        if (withdrawRequestArrayList.isEmpty()) {
                            binding.noPaymentAccountFound.setVisibility(View.VISIBLE);
                        } else {
                            binding.noPaymentAccountFound.setVisibility(View.GONE);
                        }

                        withdrawAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void showBottomDialogWithdrawPaymentAmount() {
        // ✅ First, check if a default account has been loaded.
        if (accountName == null || accountNumber == null) {
            MyToast.showShort(requireContext(), "Please set a default payment account first.");
            return; // Stop execution if no account is set.
        }

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_withdraw_payment_request);


        TextView accountNumberEt = bottomSheetDialog.findViewById(R.id.accountNumberEt);
        EditText withdrawAmountEt = bottomSheetDialog.findViewById(R.id.withdrawAmountEt);
        CheckBox withdrawConfirmCheckbox = bottomSheetDialog.findViewById(R.id.withdrawConfirmCheckbox);


        TextView primaryText = bottomSheetDialog.findViewById(R.id.primaryText); // TextView এর id দিন
        TextView submitBtn = bottomSheetDialog.findViewById(R.id.submitBtn);

        if (primaryText != null) {
            primaryText.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Withdrawal Terms & Conditions");

                builder.setMessage("""
                    Before confirming withdrawal, please note:

                    1️⃣ Payment will be processed within 2-3 working days.
                    2️⃣ Ensure your primary account information is correct.
                    3️⃣ Once submitted, withdrawal requests cannot be cancelled.
                    4️⃣ The company reserves the right to review and verify all transactions.

                    ✅ By confirming, you agree to these terms and conditions.""");

                builder.setPositiveButton("Got it", (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        }

        if (accountNumberEt != null) {
            accountNumberEt.setText(accountNumber);
            switch (accountName) {
                case "bKash":
                    accountNumberEt.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_mfs_bkash),
                            null, null, null);
                    break;

                case "Rocket":
                    accountNumberEt.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_mfs_rocket),
                            null, null, null);
                    break;

                case "Nagad":
                    accountNumberEt.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_mfs_nagad),
                            null, null, null);
                    break;

                case "uPay":
                    accountNumberEt.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_mfs_upay),
                            null, null, null);
                    break;
            }

        }

        if (submitBtn != null) {
            submitBtn.setOnClickListener(v -> {
                loadingDialog.setMessage("আপনার রিকুয়েস্ট সাবমিট হচ্ছে");

                String accountNumber = accountNumberEt.getText().toString().trim();
                String withdrawAmountStr = withdrawAmountEt.getText().toString().trim();

                if (accountNumber.isEmpty()) {
                    accountNumberEt.setError("একাউন্ট নাম্বার দিন");
                    accountNumberEt.requestFocus();
                    return;
                } else if (withdrawAmountStr.isEmpty()) {
                    withdrawAmountEt.setError("এমাউন্ট দিন");
                    withdrawAmountEt.requestFocus();
                    return;
                }

                double withdrawAmount;
                try {
                    withdrawAmount = Double.parseDouble(withdrawAmountStr);
                } catch (NumberFormatException e) {
                    withdrawAmountEt.setError("সঠিক এমাউন্ট দিন");
                    withdrawAmountEt.requestFocus();
                    return;
                }

                if (withdrawAmount > partnerEarn) {
                    withdrawAmountEt.setError("আপনার পাওনার চেয়ে বেশি উত্তোলন করা যাবে না");
                    withdrawAmountEt.requestFocus();
                    return;
                } else if (withdrawAmount <= 0) {
                    withdrawAmountEt.setError("সঠিক এমাউন্ট দিন");
                    withdrawAmountEt.requestFocus();
                    return;
                } else if (!withdrawConfirmCheckbox.isChecked()) {
                    withdrawConfirmCheckbox.setError("আপনার উত্তোলন নিশ্চিত করুন");
                    MyToast.showShort(requireContext(), "আপনার উত্তোলন নিশ্চিত করুন");
                    return;
                }

                // ✅ প্রথমে চেক করবে কোনো Pending request আছে কি না
                loadingDialog.show();
                db.collection(FirebaseCollectionTable.WITHDRAW_REQUESTS)
                        .whereEqualTo("vendorId", userId)
                        .whereEqualTo("status", "pending")
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                // ⚠️ Pending request already exists
                                loadingDialog.dismiss();
                                MyToast.showShort(requireContext(), "আপনার একটি Pending রিকুয়েস্ট আছে,\nসেটি সম্পন্ন হওয়ার পর নতুন রিকুয়েস্ট দিতে পারবেন।");
                            }
                            else {
                                // ✅ কোনো Pending request নেই → এখন নতুন request সাবমিট করবে
                                financeManager.requestWithdraw(userId, withdrawAmount, accountName, accountNumber, new FinanceManager.OnWithdrawResult() {
                                    @Override
                                    public void onSuccess(String docId) {
                                        withdrawAmountEt.setText("");
                                        MyToast.showShort(requireContext(), "✅ রিকুয়েস্ট সফলভাবে সাবমিট হয়েছে!");
                                        bottomSheetDialog.dismiss();
                                        loadingDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        MyToast.showShort(requireContext(), "❌ রিকুয়েস্ট সাবমিট ব্যর্থ: " + e.getMessage());
                                        bottomSheetDialog.dismiss();
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            MyToast.showShort(requireContext(), "Error checking pending request: " + e.getMessage());
                        });
            });
        }


        bottomSheetDialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        partnerFinanceSummaryLoad();
    }

}