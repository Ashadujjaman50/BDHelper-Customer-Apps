package com.krishibarirangpur.bdhelper.model;

public class AddressBookModel {
    private String addressId;
    private String address;
    private String addressName;
    private String recipientMobile;
    private String recipientName;
    private String timestamp;

    public AddressBookModel() {
    }

    public AddressBookModel(String addressId, String address, String addressName, String recipientMobile, String recipientName, String timestamp) {
        this.addressId = addressId;
        this.address = address;
        this.addressName = addressName;
        this.recipientMobile = recipientMobile;
        this.recipientName = recipientName;
        this.timestamp = timestamp;
    }


    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getRecipientMobile() {
        return recipientMobile;
    }

    public void setRecipientMobile(String recipientMobile) {
        this.recipientMobile = recipientMobile;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
