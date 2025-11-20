package com.krishibarirangpur.bdhelper.model;

public class UserModel {

    private String userId;
    private String userType; // "customer" or "partner"
    private String userName;
    private String userEmail;
    private String phone;

    private String userNID;
    private String userNIDPhoto;
    private String userDateOfBirth;
    private String userDistrict;

    // Partner-specific fields
    private String userLocation;
    private String verifyStatus;
    private String rentCategoryName;

    private String device_token;
    private String userSignWith; // "email", "google", "phone"

    private Long userLoginDateTime;
    private Long userLastLogin;

    private double rating;

    // ✅ Empty constructor for Firebase
    public UserModel() {
    }

    // ✅ Full constructor
    public UserModel(String userId, String userType, String userName, String userEmail, String phone,
                     String userNID, String userNIDPhoto, String userDateOfBirth, String userDistrict,
                     String userLocation, String verifyStatus, String rentCategoryName,
                     String device_token, String userSignWith,
                     Long userLoginDateTime, Long userLastLogin, double rating) {

        this.userId = userId;
        this.userType = userType;
        this.userName = userName;
        this.userEmail = userEmail;
        this.phone = phone;

        this.userNID = userNID;
        this.userNIDPhoto = userNIDPhoto;
        this.userDateOfBirth = userDateOfBirth;
        this.userDistrict = userDistrict;

        this.userLocation = userLocation;
        this.verifyStatus = verifyStatus;
        this.rentCategoryName = rentCategoryName;

        this.device_token = device_token;
        this.userSignWith = userSignWith;
        this.userLoginDateTime = userLoginDateTime;
        this.userLastLogin = userLastLogin;
        this.rating = rating;
    }

    // ✅ Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserNID() {
        return userNID;
    }

    public void setUserNID(String userNID) {
        this.userNID = userNID;
    }

    public String getUserNIDPhoto() {
        return userNIDPhoto;
    }

    public void setUserNIDPhoto(String userNIDPhoto) {
        this.userNIDPhoto = userNIDPhoto;
    }

    public String getUserDateOfBirth() {
        return userDateOfBirth;
    }

    public void setUserDateOfBirth(String userDateOfBirth) {
        this.userDateOfBirth = userDateOfBirth;
    }

    public String getUserDistrict() {
        return userDistrict;
    }

    public void setUserDistrict(String userDistrict) {
        this.userDistrict = userDistrict;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getRentCategoryName() {
        return rentCategoryName;
    }

    public void setRentCategoryName(String rentCategoryName) {
        this.rentCategoryName = rentCategoryName;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUserSignWith() {
        return userSignWith;
    }

    public void setUserSignWith(String userSignWith) {
        this.userSignWith = userSignWith;
    }

    public Long getUserLoginDateTime() {
        return userLoginDateTime;
    }

    public void setUserLoginDateTime(Long userLoginDateTime) {
        this.userLoginDateTime = userLoginDateTime;
    }

    public Long getUserLastLogin() {
        return userLastLogin;
    }

    public void setUserLastLogin(Long userLastLogin) {
        this.userLastLogin = userLastLogin;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
