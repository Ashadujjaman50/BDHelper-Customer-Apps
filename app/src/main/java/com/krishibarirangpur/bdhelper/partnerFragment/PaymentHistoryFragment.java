package com.krishibarirangpur.bdhelper.partnerFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.WithdrawAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentPaymentHistoryBinding;
import com.krishibarirangpur.bdhelper.model.WithdrawRequest;
import com.krishibarirangpur.bdhelper.myUtils.FinanceCache;
import com.krishibarirangpur.bdhelper.myUtils.FinanceManager;
import com.krishibarirangpur.bdhelper.myUtils.MyToast;
import com.krishibarirangpur.bdhelper.myUtils.PreloadingDialog;
import com.krishibarirangpur.bdhelper.myUtils.Replacement;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Map;


public class PaymentHistoryFragment extends Fragment {

    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    private FragmentPaymentHistoryBinding binding;
    private PreloadingDialog preloadingDialog;
    private LoadingDialog loadingDialog;
    private FirebaseFirestore db;
    private String userId, accountName, accountNumber;
    private WithdrawAdapter withdrawAdapter;
    private ArrayList<WithdrawRequest> withdrawRequestArrayList;
    private FinanceManager financeManager;
    double partnerEarn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_history, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preloadingDialog = new PreloadingDialog(requireContext());
        loadingDialog = new LoadingDialog(requireContext());
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
        financeManager = new FinanceManager();

        // 🔹 फाइनेंसियल Cache থেকে ডেটা দেখাও, যদি থাকে
        partnerFinanceSummaryLoad();

        // Default Payment Account Load
        loadDefaultAccountData();

        //Load Withdraw History
        loadWithdrawRequests();

        binding.withdrawPaymentButton.setOnClickListener(v -> {
            //show Bottom Dialog in Add Payment Method
            showBottomDialogAddPaymentAccount();
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

            // 🔹 মোট আয় দেখাও
            binding.totalAmount.setText(Replacement.ReplacementNumberInLocal(
                    requireContext(), String.valueOf(totalEarned)));

            // 🔹 কার পাওনা বেশি সেটার ভিত্তিতে টেক্সট আপডেট করো
            if (owedTo == 1.0) {
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(netAmount)));
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                partnerEarn = netAmount;
            } else if (owedTo == 2.0) {
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(netAmount)));
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                partnerEarn = 0;
            } else {
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                partnerEarn = 0;
            }

        }
        else {
            // 🔹 যদি cache লোড না থাকে, Firestore থেকে ডেটা নাও
            financeManager.getPartnerFinanceSummary(userId, (totalEarned, partnerReceivable, companyReceivable) -> {

                Map<String, Double> result = FinanceManager.getNetReceivable(partnerReceivable, companyReceivable);
                double netAmount = result.get("netAmount");
                double owedTo = result.get("owedTo");

                binding.totalAmount.setText(Replacement.ReplacementNumberInLocal(
                        requireContext(), String.valueOf(totalEarned)));

                if (owedTo == 1.0) {
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(netAmount)));
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                    partnerEarn = netAmount;
                } else if (owedTo == 2.0) {
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(netAmount)));
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                    partnerEarn = 0;
                } else {
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(requireContext(), "0"));
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
        db.collection("users")
                .document(userId)
                .collection("accounts")
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

        db.collection("withdrawRequests")
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



    private void showBottomDialogAddPaymentAccount() {
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
                    accountNumberEt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mfs_bkash, 0, 0, 0);
                    break;
                case "Rocket":
                    accountNumberEt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mfs_rocket, 0, 0, 0);
                    break;
                case "Nagad":
                    accountNumberEt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mfs_nagad, 0, 0, 0);
                    break;
                case "Upay":
                    accountNumberEt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mfs_upay, 0, 0, 0);
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
                db.collection("withdrawRequests")
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
