package com.krishibarirangpur.bdhelper.model;

import com.google.firebase.firestore.Exclude;

public class PaymentModel {

    private String id; // Document ID
    private String vendorId;
    private double amount;
    private String accountNumber;
    private String trxId;
    private String paymentMethod;
    private String status;
    private long createdAt;

    // Empty constructor (Firebase বা JSON parsing-এর জন্য প্রয়োজন)
    public PaymentModel() {
    }

    // Parameterized constructor
    public PaymentModel(String vendorId, double amount, String accountNumber,
                        String trxId, String paymentMethod, String status, long createdAt) {
        this.vendorId = vendorId;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.trxId = trxId;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
