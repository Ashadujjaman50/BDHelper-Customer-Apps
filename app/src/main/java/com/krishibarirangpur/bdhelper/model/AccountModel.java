package com.krishibarirangpur.bdhelper.model;


import com.google.firebase.Timestamp;

public class AccountModel {

    private String accountId;
    private String accountName;
    private String accountNumber;
    private String contactName;
    private String isPrimary;
    private Timestamp timestamp;

    public AccountModel() {} // Firestore requires empty constructor

    public AccountModel(String accountId, String accountName, String accountNumber, String contactName, String isPrimary, Timestamp timestamp) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.contactName = contactName;
        this.isPrimary = isPrimary;
        this.timestamp = timestamp;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setAccountId(String accountId) { this.accountId = accountId; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public void setPrimary(String primary) { this.isPrimary = primary; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

}
