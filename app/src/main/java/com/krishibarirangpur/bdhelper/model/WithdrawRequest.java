package com.krishibarirangpur.bdhelper.model;

public class WithdrawRequest {
    private String vendorId;
    private double requestedAmount;
    private String status;
    private long requestedAt;
    private String paymentMethod;
    private String accountNumber;
    private String id;

    public WithdrawRequest() {} // Firestore জন্য default constructor

    public WithdrawRequest(String vendorId, double requestedAmount, String status, long requestedAt, String paymentMethod, String accountNumber, String id) {
        this.vendorId = vendorId;
        this.requestedAmount = requestedAmount;
        this.status = status;
        this.requestedAt = requestedAt;
        this.paymentMethod = paymentMethod;
        this.accountNumber = accountNumber;
        this.id = id;
    }

    public String getVendorId() { return vendorId; }
    public double getRequestedAmount() { return requestedAmount; }
    public String getStatus() { return status; }
    public long getRequestedAt() { return requestedAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getAccountNumber() { return accountNumber; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
