package com.krishibarirangpur.bdhelper.model;

import com.google.firebase.firestore.Exclude;
import java.util.Map;

public class ServiceModel {
    // --- পুরোনো গ্লোবাল ভ্যারিয়েবল (সংরক্ষিত রাখা হয়েছে ব্যাকওয়ার্ড কমপ্যাটিবিলিটির জন্য) ---
    private String serviceId;
    private String serviceRegistrationNumber;
    private String serviceModelNumber;
    private String serviceCategoryAndYear;
    private String sizeAndCapacity;
    private String brtaImage;
    private String transportImage;
    private String driverLicence;
    private String categoryId;
    private String subCategoryId;
    private String subCategoryName;
    private String serviceStatus;
    private String serviceVerified;

    // --- নতুন ডাইনামিক ফিল্ডস (Nested Maps) ---
    // এই দুটি ম্যাপের মাধ্যমে Truck, Pickup, Harvester, Mechanic সহ সব সার্ভিস ডাইনামিকালি হ্যান্ডেল হবে
    private Map<String, String> specs;
    private Map<String, String> media;

    // --- 1. Empty Constructor ---
    // ফায়ারবেস ফায়ারস্টোর থেকে .toObject(ServiceModel.class) করার জন্য এটি বাধ্যতামূলক
    public ServiceModel() {}

    // --- 2. Overloaded Constructor ---
    // পুরোনো কোনো কোড বা অবজেক্ট ক্রিয়েশন যেন ব্রেক না করে, সেজন্য এটি অপরিবর্তিত রাখা হয়েছে
    public ServiceModel(String serviceId, String serviceRegistrationNumber, String serviceModelNumber,
                        String serviceCategoryAndYear, String sizeAndCapacity, String brtaImage, String transportImage,
                        String driverLicence, String categoryId, String subCategoryId,
                        String subCategoryName, String serviceStatus, String serviceVerified) {
        this.serviceId = serviceId;
        this.serviceRegistrationNumber = serviceRegistrationNumber;
        this.serviceModelNumber = serviceModelNumber;
        this.serviceCategoryAndYear = serviceCategoryAndYear;
        this.sizeAndCapacity = sizeAndCapacity;
        this.brtaImage = brtaImage;
        this.transportImage = transportImage;
        this.driverLicence = driverLicence;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.serviceStatus = serviceStatus;
        this.serviceVerified = serviceVerified;
    }

    // --- 3. 🎯 স্মার্ট ও ক্র্যাশ-ফ্রি গেটার্স (Safe Fallback Methods) ---
    // অ্যাপের UI, Adapter বা সর্বত্র পুরোনো গেটারের বদলে এই মেথডগুলো কল করবেন।
    // ডাটাবেজে নতুন ম্যাপ থাকলে সেখান থেকে নিবে, না থাকলে পুরোনো সিঙ্গেল ফিল্ড থেকে ডেটা রিসিভ করবে।

    @Exclude
    public String getSafeSizeAndCapacity() {
        if (specs != null && specs.containsKey("sizeAndCapacity") && specs.get("sizeAndCapacity") != null) {
            return specs.get("sizeAndCapacity");
        }
        return sizeAndCapacity != null ? sizeAndCapacity : "";
    }

    @Exclude
    public String getSafeBrandOrModel() {
        if (specs != null && specs.containsKey("brandOrModel") && specs.get("brandOrModel") != null) {
            return specs.get("brandOrModel");
        }
        return serviceModelNumber != null ? serviceModelNumber : "";
    }

    @Exclude
    public String getSafeRegistrationNumber() {
        if (specs != null && specs.containsKey("registrationNumber") && specs.get("registrationNumber") != null) {
            return specs.get("registrationNumber");
        }
        return serviceRegistrationNumber != null ? serviceRegistrationNumber : "";
    }

    @Exclude
    public String getSafeManufacturingYear() {
        if (specs != null && specs.containsKey("categoryAndYear") && specs.get("categoryAndYear") != null) {
            return specs.get("categoryAndYear");
        }
        return serviceCategoryAndYear != null ? serviceCategoryAndYear : "";
    }

    @Exclude
    public String getSafeTransportImage() {
        if (media != null && media.containsKey("transportImage") && media.get("transportImage") != null) {
            return media.get("transportImage");
        }
        return transportImage != null ? transportImage : "";
    }

    @Exclude
    public String getSafeBrtaImage() {
        if (media != null && media.containsKey("brtaImage") && media.get("brtaImage") != null) {
            return media.get("brtaImage");
        }
        return brtaImage != null ? brtaImage : "";
    }

    @Exclude
    public String getSafeDriverLicence() {
        if (media != null && media.containsKey("driverLicence") && media.get("driverLicence") != null) {
            return media.get("driverLicence");
        }
        return driverLicence != null ? driverLicence : "";
    }


    // --- 4. স্ট্যান্ডার্ড Getters & Setters (Firebase Automatic Mapping এর জন্য) ---

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getServiceRegistrationNumber() { return serviceRegistrationNumber; }
    public void setServiceRegistrationNumber(String serviceRegistrationNumber) { this.serviceRegistrationNumber = serviceRegistrationNumber; }

    public String getServiceModelNumber() { return serviceModelNumber; }
    public void setServiceModelNumber(String serviceModelNumber) { this.serviceModelNumber = serviceModelNumber; }

    public String getServiceCategoryAndYear() { return serviceCategoryAndYear; }
    public void setServiceCategoryAndYear(String serviceCategoryAndYear) { this.serviceCategoryAndYear = serviceCategoryAndYear; }

    public String getBrtaImage() { return brtaImage; }
    public void setBrtaImage(String brtaImage) { this.brtaImage = brtaImage; }

    public String getTransportImage() { return transportImage; }
    public void setTransportImage(String transportImage) { this.transportImage = transportImage; }

    public String getDriverLicence() { return driverLicence; }
    public void setDriverLicence(String driverLicence) { this.driverLicence = driverLicence; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getSubCategoryId() { return subCategoryId; }
    public void setSubCategoryId(String subCategoryId) { this.subCategoryId = subCategoryId; }

    public String getSubCategoryName() { return subCategoryName; }
    public void setSubCategoryName(String subCategoryName) { this.subCategoryName = subCategoryName; }

    public String getServiceStatus() { return serviceStatus; }
    public void setServiceStatus(String serviceStatus) { this.serviceStatus = serviceStatus; }

    public String getServiceVerified() { return serviceVerified; }
    public void setServiceVerified(String serviceVerified) { this.serviceVerified = serviceVerified; }

    public Map<String, String> getSpecs() { return specs; }
    public void setSpecs(Map<String, String> specs) { this.specs = specs; }

    public Map<String, String> getMedia() { return media; }
    public void setMedia(Map<String, String> media) { this.media = media; }
}