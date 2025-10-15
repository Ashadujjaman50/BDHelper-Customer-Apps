package com.dropshep.bdhelper.partnerFragment;

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
import android.widget.Toast;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.AccountAdapter;
import com.dropshep.bdhelper.databinding.FragmentPaymentHistoryBinding;
import com.dropshep.bdhelper.model.AccountModel;
import com.dropshep.bdhelper.myUtils.FinanceCache;
import com.dropshep.bdhelper.myUtils.FinanceManager;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.Replacement;
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

import java.util.HashMap;
import java.util.Map;

public class PaymentHistoryFragment extends Fragment {

    private FragmentPaymentHistoryBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    String userId;
    FinanceManager financeManager;

    private LoadingDialog loadingDialog;

    String accountId, accountName, accountNumber, contactName;

    public PaymentHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_payment_history, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views

        // init
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();

        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        // 🔹 Partner Finance Summary Load
        partnerFinanceSummaryLoad();


        //load Default Account
        loadDefaultAccountData();

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
                    getContext(), String.valueOf(totalEarned)));

            // 🔹 কার পাওনা বেশি সেটার ভিত্তিতে টেক্সট আপডেট করো
            if (owedTo == 1.0) {
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(netAmount)));
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
            } else if (owedTo == 2.0) {
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(netAmount)));
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
            } else {
                binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
            }

        }
        else {
            // 🔹 যদি cache লোড না থাকে, Firestore থেকে ডেটা নাও
            financeManager.getPartnerFinanceSummary(userId, (totalEarned, partnerReceivable, companyReceivable) -> {

                Map<String, Double> result = FinanceManager.getNetReceivable(partnerReceivable, companyReceivable);
                double netAmount = result.get("netAmount");
                double owedTo = result.get("owedTo");

                binding.totalAmount.setText(Replacement.ReplacementNumberInLocal(
                        getContext(), String.valueOf(totalEarned)));

                if (owedTo == 1.0) {
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(netAmount)));
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                } else if (owedTo == 2.0) {
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), String.valueOf(netAmount)));
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                } else {
                    binding.partnerEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                    binding.companyEarnTv.setText(Replacement.ReplacementNumberInLocal(getContext(), "0"));
                }

            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (financeManager != null) financeManager.stopListening(); // 🔹 Stop realtime listener
    }

    @SuppressLint("NotifyDataSetChanged")
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
                        accountId = doc.getString("accountId");
                        accountName = doc.getString("accountName");
                        accountNumber = doc.getString("accountNumber");
                        contactName = doc.getString("contactName");

                        Log.d("AccountDebug", "✅ Default account loaded: " + accountName);
                    } else {
                        MyToast.showShort(requireContext(), "⚠️ No default account found.");
                    }
                })
                .addOnFailureListener(e -> {
                    MyToast.showShort(requireContext(), "❌ Failed to load accounts: " + e.getMessage());
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
            String docId = db.collection("users")
                    .document(userId)
                    .collection("accounts")
                    .document()
                    .getId();




        });

        bottomSheetDialog.show();
    }

}