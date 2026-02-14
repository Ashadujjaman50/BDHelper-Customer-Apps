package com.krishibarirangpur.bdhelper.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
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
    public void createLedgerAndUpdateBalance(
            String bidId,
            String orderId,
            String vendorId,
            String bidAmount,
            String confirmOrderPrice,
            String paymentReceiver) {

        double partnerEarn = Double.parseDouble(bidAmount);
        double companyEarn = Double.parseDouble(confirmOrderPrice) - partnerEarn;

        Map<String, Object> ledgerData = new HashMap<>();
        ledgerData.put("bidId", bidId);
        ledgerData.put("orderId", orderId);
        ledgerData.put("vendorId", vendorId);
        ledgerData.put("totalAmount", Double.parseDouble(confirmOrderPrice));
        ledgerData.put("companyEarn", companyEarn);
        ledgerData.put("partnerEarn", partnerEarn);
        ledgerData.put("paymentReceiver", paymentReceiver);  //"partner" * "company" -Default later change based on who received
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
    public void requestWithdraw(String vendorId, double amount, String paymentMethod, String accountNumber,
                                OnWithdrawResult callback) {
        Map<String, Object> withdrawData = new HashMap<>();
        withdrawData.put("vendorId", vendorId);
        withdrawData.put("requestedAmount", amount);
        withdrawData.put("status", "pending");
        withdrawData.put("requestedAt", System.currentTimeMillis());
        withdrawData.put("paymentMethod", paymentMethod);
        withdrawData.put("accountNumber", accountNumber);

        db.collection(COLLECTION_WITHDRAW)
                .add(withdrawData)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "✅ Withdraw request created: " + docRef.getId());
                    if (callback != null) callback.onSuccess(docRef.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to create withdraw request: " + e.getMessage(), e);
                    if (callback != null) callback.onFailure(e);
                });
    }


    public interface OnWithdrawResult {
        void onSuccess(String docId); // ডকুমেন্ট আইডি ফেরত দিবে
        void onFailure(Exception e);
    }


    // 🔹 4️⃣ Approve Withdraw & Deduct from Partner Balance + Ledger Entry (Schema Maintained)
    // ✅ fm.approveWithdraw(requestId, vendorId, 5000);
    public void approveWithdraw(String requestId, String vendorId, double amount) {
        DocumentReference balanceRef = db.collection(COLLECTION_BALANCE).document(vendorId);
        DocumentReference withdrawRef = db.collection(COLLECTION_WITHDRAW).document(requestId);

        db.runTransaction(transaction -> {
            DocumentSnapshot balanceSnap = transaction.get(balanceRef);
            DocumentSnapshot withdrawSnap = transaction.get(withdrawRef);

            if (!balanceSnap.exists()) {
                throw new FirebaseFirestoreException("Balance record not found",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }
            if (!withdrawSnap.exists()) {
                throw new FirebaseFirestoreException("Withdraw request not found",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            double currentBalance = balanceSnap.getDouble("currentBalance") != null
                    ? balanceSnap.getDouble("currentBalance") : 0;
            double totalWithdrawn = balanceSnap.getDouble("totalWithdrawn") != null
                    ? balanceSnap.getDouble("totalWithdrawn") : 0;

            if (currentBalance < amount) {
                throw new FirebaseFirestoreException("Insufficient balance",
                        FirebaseFirestoreException.Code.ABORTED);
            }

            double newWithdrawn = totalWithdrawn + amount;
            double newBalance = currentBalance - amount;

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
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "✅ Withdraw approved and balance deducted");

            // ✅ Ledger entry (maintaining original schema)
            Map<String, Object> ledgerEntry = new HashMap<>();
            ledgerEntry.put("bidId", "withdraw"); // static placeholder since not tied to bid
            ledgerEntry.put("orderId", requestId); // use withdraw request id as order ref
            ledgerEntry.put("vendorId", vendorId);
            ledgerEntry.put("totalAmount", amount);
            ledgerEntry.put("partnerEarn", -amount); // negative because it's deducted
            ledgerEntry.put("companyEarn", 0); // company gets nothing from withdraw
            ledgerEntry.put("paymentReceiver", "company"); // changed to "company" to reflect that this is an adjustment for partner receivable (company paying out to partner)
            ledgerEntry.put("status", "withdraw");
            ledgerEntry.put("createdAt", System.currentTimeMillis());

            db.collection(COLLECTION_LEDGER)
                    .add(ledgerEntry)
                    .addOnSuccessListener(doc ->
                            Log.d(TAG, "✅ Withdraw ledger entry added: " + doc.getId()))
                    .addOnFailureListener(err ->
                            Log.e(TAG, "❌ Failed to add withdraw ledger entry: " + err.getMessage()));

        }).addOnFailureListener(e ->
                Log.e(TAG, "❌ Withdraw approval failed: " + e.getMessage(), e)
        );
    }


    // 🔹 5️⃣ Get Partner Finance Summary (for Profile UI)
    private ListenerRegistration financeListener;
    public void getPartnerFinanceSummary(String vendorId, OnFinanceSummary callback) {
        if (!FinanceCache.needsRefresh()) {
            callback.onSummary(
                    FinanceCache.totalEarned,
                    FinanceCache.partnerReceivable,
                    FinanceCache.companyReceivable
            );
            return;
        }

        // 🔹 Ledger listener
        financeListener = db.collection(COLLECTION_LEDGER)
                .whereEqualTo("vendorId", vendorId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null || querySnapshot == null) return;

                    double totalEarned = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        double earn = doc.getDouble("partnerEarn") != null ? doc.getDouble("partnerEarn") : 0;
                        String status = doc.getString("status");
                        if (status != null && !status.equalsIgnoreCase("withdraw"))
                            totalEarned += earn;
                    }

                    // 🔹 Recalculate receivables
                    double finalTotalEarned = totalEarned;
                    calculatePartnerReceivable(vendorId, partnerReceivable ->
                            calculateCompanyReceivable(vendorId, companyReceivable -> {

                                FinanceCache.totalEarned = finalTotalEarned;
                                FinanceCache.partnerReceivable = partnerReceivable;
                                FinanceCache.companyReceivable = companyReceivable;
                                FinanceCache.lastUpdated = System.currentTimeMillis();
                                FinanceCache.isLoaded = true;

                                callback.onSummary(finalTotalEarned, partnerReceivable, companyReceivable);
                            })
                    );
                });
    }


    // 🔹 6️⃣ Calculate Partner Receivable (যেখানে paymentReceiver = company)
    private ListenerRegistration partnerReceivableListener;

    public void calculatePartnerReceivable(String vendorId, OnFinanceResult callback) {

        partnerReceivableListener = db.collection(COLLECTION_LEDGER)
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
                        String status = doc.getString("status");

                        double partnerShare = doc.getDouble("partnerEarn") != null
                                ? doc.getDouble("partnerEarn") : 0;

                        if(status == null || status.equalsIgnoreCase("cancelled"))
                            continue;

                        if("company".equals(receiver)) {

                            // 🔹 Normal earn -> add
                            if(!status.equalsIgnoreCase("payment")) {
                                total += partnerShare;
                            }

                            // 🔹 Payment approved -> subtract
                            else {
                                total -= partnerShare;
                            }
                        }
                    }

                    callback.onResult(total);
                });
    }



    // 🔹 7️⃣ Calculate Company Receivable (যেখানে paymentReceiver = partner)
    private ListenerRegistration companyReceivableListener;

    public void calculateCompanyReceivable(String vendorId, OnFinanceResult callback) {

        companyReceivableListener = db.collection(COLLECTION_LEDGER)
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
                        String status = doc.getString("status");

                        double companyShare = doc.getDouble("companyEarn") != null
                                ? doc.getDouble("companyEarn") : 0;

                        if(status == null || status.equalsIgnoreCase("cancelled"))
                            continue;

                        if("partner".equals(receiver)) {

                            // 🔹 Normal earn -> add
                            if(!status.equalsIgnoreCase("payment")) {
                                total += companyShare;
                            }

                            // 🔹 Payment approved -> subtract
                            else {
                                total -= companyShare;
                            }
                        }
                    }

                    callback.onResult(total);
                });
    }


    // Stop listening (যদি Fragment destroy হয়)
    public void stopListening() {
        if (financeListener != null) {
            financeListener.remove();
            financeListener = null;
        }
        if (partnerReceivableListener != null) {
            partnerReceivableListener.remove();
            partnerReceivableListener = null;
        }
        if (companyReceivableListener != null) {
            companyReceivableListener.remove();
            companyReceivableListener = null;
        }
    }



    // 🔹8 Interfaces for callbacks
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