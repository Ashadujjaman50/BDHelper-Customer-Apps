package com.dropshep.bdhelper.myUtils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ✅ FinanceManager
 * Handles all finance operations:
 *  - Ledger creation (job done)
 *  - Partner balance update
 *  - Withdraw request & approval
 *  - Partner/company receivable calculation
 *  - Partner profile summary
 */
public class FinanceManager {

    private static final String TAG = "FinanceManager";
    private static final String COLLECTION_LEDGER = "financialLedger";
    private static final String COLLECTION_BALANCE = "partnerBalance";
    private static final String COLLECTION_WITHDRAW = "withdrawRequests";

    private final FirebaseFirestore db;

    public FinanceManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    // 🔹 1️⃣ Create Ledger & Update Partner Balance
    public void createLedgerAndUpdateBalance(String bidId, String orderId, String vendorId, String bidAmount) {
        String finalAmount = CommonClass.getRoundedTenPercentValue(bidAmount, 10);
        double partnerEarn = Double.parseDouble(bidAmount);
        double companyEarn = Double.parseDouble(finalAmount) - partnerEarn;

        Map<String, Object> ledgerData = new HashMap<>();
        ledgerData.put("bidId", bidId);
        ledgerData.put("orderId", orderId);
        ledgerData.put("vendorId", vendorId);
        ledgerData.put("totalAmount", Double.parseDouble(finalAmount));
        ledgerData.put("companyEarn", companyEarn);
        ledgerData.put("partnerEarn", partnerEarn);
        ledgerData.put("paymentReceiver", "company"); // default - later change based on who received
        ledgerData.put("status", "completed");
        ledgerData.put("createdAt", System.currentTimeMillis());

        db.collection(COLLECTION_LEDGER)
                .add(ledgerData)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "✅ Ledger created: " + docRef.getId());
                    updatePartnerBalance(vendorId, partnerEarn);
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "❌ Failed to create ledger: " + e.getMessage(), e));
    }

    // 🔹 2️⃣ Update Partner Balance (Earned amount add হবে)
    public void updatePartnerBalance(String vendorId, double partnerEarn) {
        DocumentReference balanceRef = db.collection(COLLECTION_BALANCE).document(vendorId);

        db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(balanceRef);

                    double totalEarned = 0;
                    double totalWithdrawn = 0;

                    if (snapshot.exists()) {
                        totalEarned = snapshot.getDouble("totalEarned") != null ? snapshot.getDouble("totalEarned") : 0;
                        totalWithdrawn = snapshot.getDouble("totalWithdrawn") != null ? snapshot.getDouble("totalWithdrawn") : 0;
                    }

                    double newTotalEarned = totalEarned + partnerEarn;
                    double newBalance = newTotalEarned - totalWithdrawn;

                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("vendorId", vendorId);
                    updateData.put("totalEarned", newTotalEarned);
                    updateData.put("totalWithdrawn", totalWithdrawn);
                    updateData.put("currentBalance", newBalance);
                    updateData.put("lastUpdated", System.currentTimeMillis());

                    transaction.set(balanceRef, updateData, SetOptions.merge());
                    return null;
                }).addOnSuccessListener(aVoid ->
                        Log.d(TAG, "✅ Partner balance updated successfully"))
                .addOnFailureListener(e ->
                        Log.e(TAG, "❌ Failed to update partner balance: " + e.getMessage(), e));
    }

    // 🔹 3️⃣ Withdraw Request
    // ✅ fm.requestWithdraw(vendorId, 5000, "bkash", "017XXXXXXXX");
    public void requestWithdraw(String vendorId, double amount, String paymentMethod, String accountNumber) {
        Map<String, Object> withdrawData = new HashMap<>();
        withdrawData.put("vendorId", vendorId);
        withdrawData.put("requestedAmount", amount);
        withdrawData.put("status", "pending");
        withdrawData.put("requestedAt", System.currentTimeMillis());
        withdrawData.put("paymentMethod", paymentMethod);
        withdrawData.put("accountNumber", accountNumber);

        db.collection(COLLECTION_WITHDRAW)
                .add(withdrawData)
                .addOnSuccessListener(docRef ->
                        Log.d(TAG, "✅ Withdraw request created: " + docRef.getId()))
                .addOnFailureListener(e ->
                        Log.e(TAG, "❌ Failed to create withdraw request: " + e.getMessage(), e));
    }

    // 🔹 4️⃣ Approve Withdraw & Deduct from Partner Balance
    // ✅ fm.approveWithdraw(requestId, vendorId, 5000);
    public void approveWithdraw(String requestId, String vendorId, double amount) {
        DocumentReference balanceRef = db.collection(COLLECTION_BALANCE).document(vendorId);
        DocumentReference withdrawRef = db.collection(COLLECTION_WITHDRAW).document(requestId);

        db.runTransaction(transaction -> {
                    DocumentSnapshot balanceSnap = transaction.get(balanceRef);
                    if (!balanceSnap.exists()) {
                        throw new FirebaseFirestoreException("Balance record not found",
                                FirebaseFirestoreException.Code.NOT_FOUND);
                    }

                    double totalEarned = balanceSnap.getDouble("totalEarned");
                    double totalWithdrawn = balanceSnap.getDouble("totalWithdrawn");
                    double newWithdrawn = totalWithdrawn + amount;
                    double newBalance = totalEarned - newWithdrawn;

                    // ✅ Update Balance
                    Map<String, Object> updateBalance = new HashMap<>();
                    updateBalance.put("totalWithdrawn", newWithdrawn);
                    updateBalance.put("currentBalance", newBalance);
                    updateBalance.put("lastUpdated", System.currentTimeMillis());
                    transaction.update(balanceRef, updateBalance);

                    // ✅ Update Withdraw Status
                    Map<String, Object> updateWithdraw = new HashMap<>();
                    updateWithdraw.put("status", "approved");
                    updateWithdraw.put("approvedAt", System.currentTimeMillis());
                    transaction.update(withdrawRef, updateWithdraw);

                    return null;
                }).addOnSuccessListener(aVoid ->
                        Log.d(TAG, "✅ Withdraw approved and balance deducted"))
                .addOnFailureListener(e ->
                        Log.e(TAG, "❌ Withdraw approval failed: " + e.getMessage(), e));
    }

    // 🔹 5️⃣ Get Partner Finance Summary (for Profile UI)
    private ListenerRegistration financeListener;
    public void getPartnerFinanceSummary(String vendorId, OnFinanceSummary callback) {
        // যদি cache এখনো valid থাকে, cache থেকে দাও
        if (!FinanceCache.needsRefresh()) {
            // cache fresh → সরাসরি callback দাও
            callback.onSummary(
                    FinanceCache.totalEarned,
                    FinanceCache.partnerReceivable,
                    FinanceCache.companyReceivable
            );
            return;
        }


        // 🔹 Real-time listener on partnerBalance document
        financeListener = db.collection(COLLECTION_BALANCE)
                .document(vendorId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "❌ Real-time listener failed: " + e.getMessage());
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        double totalEarned = snapshot.getDouble("totalEarned") != null
                                ? snapshot.getDouble("totalEarned") : 0;

                        // 🔹 Recalculate real-time partner/company receivables
                        calculatePartnerReceivable(vendorId, partnerReceivable ->
                                calculateCompanyReceivable(vendorId, companyReceivable -> {

                                    // Cache update
                                    FinanceCache.totalEarned = totalEarned;
                                    FinanceCache.partnerReceivable = partnerReceivable;
                                    FinanceCache.companyReceivable = companyReceivable;
                                    FinanceCache.lastUpdated = System.currentTimeMillis();
                                    FinanceCache.isLoaded = true;

                                    // ✅ Callback to UI
                                    callback.onSummary(totalEarned, partnerReceivable, companyReceivable);
                                })
                        );
                    }
                });
    }

    // 🔹 2️⃣ Stop listening (যদি Fragment destroy হয়)
    public void stopListening() {
        if (financeListener != null) {
            financeListener.remove();
            financeListener = null;
        }
    }

    // 🔹 6️⃣ Calculate Partner Receivable (যেখানে paymentReceiver = company)
    public void calculatePartnerReceivable(String vendorId, OnFinanceResult callback) {
        db.collection(COLLECTION_LEDGER)
                .whereEqualTo("vendorId", vendorId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null || querySnapshot == null) {
                        Log.e(TAG, "❌ Partner receivable listener failed: " + (e != null ? e.getMessage() : "null"));
                        callback.onResult(0);
                        return;
                    }

                    double total = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String receiver = doc.getString("paymentReceiver");
                        double partnerShare = doc.getDouble("partnerEarn") != null ? doc.getDouble("partnerEarn") : 0;
                        if ("company".equals(receiver)) total += partnerShare;
                    }
                    callback.onResult(total);
                });
    }

    // 🔹 7️⃣ Calculate Company Receivable (যেখানে paymentReceiver = partner)
    public void calculateCompanyReceivable(String vendorId, OnFinanceResult callback) {
        db.collection(COLLECTION_LEDGER)
                .whereEqualTo("vendorId", vendorId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null || querySnapshot == null) {
                        Log.e(TAG, "❌ Company receivable listener failed: " + (e != null ? e.getMessage() : "null"));
                        callback.onResult(0);
                        return;
                    }

                    double total = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String receiver = doc.getString("paymentReceiver");
                        double companyShare = doc.getDouble("companyEarn") != null ? doc.getDouble("companyEarn") : 0;
                        if ("partner".equals(receiver)) total += companyShare;
                    }
                    callback.onResult(total);
                });
    }

    // 🔹 Interfaces for callbacks
    public interface OnFinanceResult {
        void onResult(double value);
    }

    public interface OnFinanceSummary {
        void onSummary(double totalEarned, double partnerReceivable, double companyReceivable);
    }

    public static Map<String, Double> getNetReceivable(double partnerReceivable, double companyReceivable) {
        Map<String, Double> result = new HashMap<>();

        double netAmount;
        String owedTo;

        if (partnerReceivable > companyReceivable) {
            netAmount = partnerReceivable - companyReceivable;
            owedTo = "partner";
        } else if (companyReceivable > partnerReceivable) {
            netAmount = companyReceivable - partnerReceivable;
            owedTo = "company";
        } else {
            netAmount = 0;
            owedTo = "none";
        }

        result.put("netAmount", netAmount);
        result.put("owedTo", owedTo.equals("partner") ? 1.0 :
                owedTo.equals("company") ? 2.0 : 0.0);

        return result;
    }


}
