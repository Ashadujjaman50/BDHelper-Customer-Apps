package com.krishibarirangpur.bdhelper.userFragment.partner.paymentFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.partner.PaymentAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentPaymentPaidToCompanyBinding;
import com.krishibarirangpur.bdhelper.model.PaymentModel;
import com.krishibarirangpur.bdhelper.utils.FinanceCache;
import com.krishibarirangpur.bdhelper.utils.FinanceManager;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.Replacement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PaymentPaidToCompanyFragment extends Fragment {

    private FragmentPaymentPaidToCompanyBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FinanceManager financeManager;

    private LoadingDialog loadingDialog;
    private String userId;
    double partnerEarn;

    private PaymentAdapter adapter;
    private List<PaymentModel> paymentList;


    public PaymentPaidToCompanyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_paid_to_company, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        financeManager = new FinanceManager();
        userId = firebaseAuth.getUid();

        loadingDialog = new LoadingDialog(requireContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);


        // 🔹 Cache থেকে ডেটা দেখাও, যদি থাকে
        partnerFinanceSummaryLoad();
        
        //load Payment Data
        loadPaymentData();


        binding.PayNowButton.setOnClickListener(v -> {
            //show Bottom Dialog in Company Due pay
            showBottomDialogCompanyDuePay();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPaymentData() {

        paymentList = new ArrayList<>();
        adapter = new PaymentAdapter(getContext(), paymentList);
        binding.paymentAccountRv.setAdapter(adapter);

        // 🔹 বর্তমান ইউজারের আইডি (userId) দিয়ে ফিল্টার করা হয়েছে
        db.collection(FirebaseCollectionTable.PARTNER_PAYMENTS)
                .whereEqualTo("vendorId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("PaymentData", "Error: " + e.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        paymentList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            PaymentModel payment = document.toObject(PaymentModel.class);
                            if (payment != null) {
                                payment.setId(document.getId());
                                paymentList.add(payment);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        // ডেটা না থাকলে মেসেজ দেখানো
                        if (paymentList.isEmpty()) {
                            binding.noPaymentAccountFound.setVisibility(View.VISIBLE);
                        } else {
                            binding.noPaymentAccountFound.setVisibility(View.GONE);
                        }

                    }
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
                partnerEarn = 0;
            } else if (owedTo == 2.0) {
                partnerEarn = netAmount;
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
                    partnerEarn = 0;
                } else if (owedTo == 2.0) {
                    partnerEarn = netAmount;
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

    private void showBottomDialogCompanyDuePay() {
        //First, check if a default amount load to Current Due
        if (partnerEarn <= 0){
            MyToast.showShort(requireContext(),"According to our records, you have no outstanding due.");
            return;
        }

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_company_due_payment_request);

        TextView withdrawAmountEt = bottomSheetDialog.findViewById(R.id.withdrawAmountEt);
        TextView submitBtn = bottomSheetDialog.findViewById(R.id.submitBtn);
        EditText accountNumberEt = bottomSheetDialog.findViewById(R.id.accountNumberEt);
        EditText transactionIdEt = bottomSheetDialog.findViewById(R.id.transactionIdEt);

        withdrawAmountEt.setText(Replacement.ReplacementNumberInLocal(requireContext(), String.valueOf(partnerEarn)));


        submitBtn.setOnClickListener(v -> {
            String accountNumber = accountNumberEt.getText().toString();
            String transactionId = transactionIdEt.getText().toString().toUpperCase().trim();

            if (accountNumber.isEmpty() && transactionId.isEmpty()){
                MyToast.showShort(requireContext(),"Please enter all fields.");
            }
            else if (accountNumber.isEmpty()){
                MyToast.showShort(requireContext(),"Please enter account number.");
            }
            else if (transactionId.isEmpty()){
                MyToast.showShort(requireContext(),"Please enter transaction id.");
            }
            else {
                submitPartnerPayment(userId, partnerEarn,accountNumber, transactionId);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();

    }

    public void submitPartnerPayment(String vendorId, double amount,String accountNumber, String trxId){
        loadingDialog.show();

        db.collection(FirebaseCollectionTable.PARTNER_PAYMENTS)
                .whereEqualTo("vendorId", vendorId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Pending request exists
                        loadingDialog.dismiss();
                        MyToast.showShort(requireContext(), "Your previous payment request is still pending.");
                    } else {
                        // No pending request, proceed to submit
                        Map<String, Object> paymentMap = new HashMap<>();

                        paymentMap.put("vendorId", vendorId);
                        paymentMap.put("amount", amount);
                        paymentMap.put("accountNumber", accountNumber);
                        paymentMap.put("trxId", trxId);
                        paymentMap.put("paymentMethod", "bKash");

                        paymentMap.put("status", "pending");
                        paymentMap.put("createdAt", System.currentTimeMillis());

                        db.collection(FirebaseCollectionTable.PARTNER_PAYMENTS)
                                .add(paymentMap)
                                .addOnSuccessListener(documentReference -> loadingDialog.dismiss())
                                .addOnFailureListener(e -> loadingDialog.dismiss());
                    }
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(requireContext(), "Error: " + e.getMessage());
                });
    }

}