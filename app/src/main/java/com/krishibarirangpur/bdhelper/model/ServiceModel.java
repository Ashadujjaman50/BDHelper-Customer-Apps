package com.krishibarirangpur.bdhelper.model;

public class ServiceModel {
    private String serviceId;
    private String serviceRegistrationNumber;
    private String serviceModelNumber;
    private String serviceCategoryAndYear;
    private String brtaImage;
    private String transportImage;
    private String driverLicence;
    private String categoryId;
    private String subCategoryId;
    private String subCategoryName;
    private String serviceStatus;
    private String serviceVerified;

    // Empty constructor (Firebase এর জন্য দরকার)
    public ServiceModel() {}

    // Constructor
    public ServiceModel(String serviceId, String serviceRegistrationNumber, String serviceModelNumber,
                        String serviceCategoryAndYear, String brtaImage, String transportImage,
                        String driverLicence, String categoryId, String subCategoryId,
                        String subCategoryName, String serviceStatus, String serviceVerified) {
        this.serviceId = serviceId;
        this.serviceRegistrationNumber = serviceRegistrationNumber;
        this.serviceModelNumber = serviceModelNumber;
        this.serviceCategoryAndYear = serviceCategoryAndYear;
        this.brtaImage = brtaImage;
        this.transportImage = transportImage;
        this.driverLicence = driverLicence;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.serviceStatus = serviceStatus;
        this.serviceVerified = serviceVerified;
    }

    // Getters & Setters
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
}

