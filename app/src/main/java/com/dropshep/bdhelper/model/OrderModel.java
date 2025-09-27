package com.dropshep.bdhelper.model;

public class OrderModel {

    private OrderInfo orderInfo;
    private RouteInfo routeInfo;
    private SpecInfo specInfo;
    private BidInfo bidInfo;
    private PaymentInfo paymentInfo;
    private UserInfo userInfo;

    public OrderModel() {
        // Firestore এর জন্য empty constructor
    }

    public OrderModel(OrderInfo orderInfo, RouteInfo routeInfo, SpecInfo specInfo,
                      BidInfo bidInfo, PaymentInfo paymentInfo, UserInfo userInfo) {
        this.orderInfo = orderInfo;
        this.routeInfo = routeInfo;
        this.specInfo = specInfo;
        this.bidInfo = bidInfo;
        this.paymentInfo = paymentInfo;
        this.userInfo = userInfo;
    }

    // 🔹 Getters & Setters
    public OrderInfo getOrderInfo() { return orderInfo; }
    public void setOrderInfo(OrderInfo orderInfo) { this.orderInfo = orderInfo; }

    public RouteInfo getRouteInfo() { return routeInfo; }
    public void setRouteInfo(RouteInfo routeInfo) { this.routeInfo = routeInfo; }

    public SpecInfo getSpecInfo() { return specInfo; }
    public void setSpecInfo(SpecInfo specInfo) { this.specInfo = specInfo; }

    public BidInfo getBidInfo() { return bidInfo; }
    public void setBidInfo(BidInfo bidInfo) { this.bidInfo = bidInfo; }

    public PaymentInfo getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(PaymentInfo paymentInfo) { this.paymentInfo = paymentInfo; }

    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }

    // ===================== Nested Classes =====================

    public static class OrderInfo {
        private String orderId;
        private String uid;
        private String categoryId;
        private String subCategoryId;
        private String status;
        private long timestamp;

        public OrderInfo() {}

        public OrderInfo(String orderId, String uid, String categoryId, String subCategoryId,
                         String status, long timestamp) {
            this.orderId = orderId;
            this.uid = uid;
            this.categoryId = categoryId;
            this.subCategoryId = subCategoryId;
            this.status = status;
            this.timestamp = timestamp;
        }

        public String getOrderId() { return orderId; }
        public String getUid() { return uid; }
        public String getCategoryId() { return categoryId; }
        public String getSubCategoryId() { return subCategoryId; }
        public String getStatus() { return status; }
        public long getTimestamp() { return timestamp; }
    }

    public static class RouteInfo {
        private String load;
        private String unload;
        private String rentLocation;
        private String rentTime;

        public RouteInfo() {}

        public RouteInfo(String load, String unload, String rentLocation, String rentTime) {
            this.load = load;
            this.unload = unload;
            this.rentLocation = rentLocation;
            this.rentTime = rentTime;
        }

        public String getLoad() { return load; }
        public String getUnload() { return unload; }
        public String getRentLocation() { return rentLocation; }
        public String getRentTime() { return rentTime; }
    }

    public static class SpecInfo {
        private String capacity;
        private String duration;
        private String types;
        private String quantity;
        private String desc;
        private String postDistrict;

        public SpecInfo() {}

        public SpecInfo(String capacity, String duration, String types, String quantity,
                        String desc, String postDistrict) {
            this.capacity = capacity;
            this.duration = duration;
            this.types = types;
            this.quantity = quantity;
            this.desc = desc;
            this.postDistrict = postDistrict;
        }

        public String getCapacity() { return capacity; }
        public String getDuration() { return duration; }
        public String getTypes() { return types; }
        public String getQuantity() { return quantity; }
        public String getDesc() { return desc; }
        public String getPostDistrict() { return postDistrict; }
    }

    public static class BidInfo {
        private String bidStatus;
        private String bidId;
        private String vendorId;
        private int vendorPrice;

        public BidInfo() {}

        public BidInfo(String bidStatus, String bidId, String vendorId, int vendorPrice) {
            this.bidStatus = bidStatus;
            this.bidId = bidId;
            this.vendorId = vendorId;
            this.vendorPrice = vendorPrice;
        }

        public String getBidStatus() { return bidStatus; }
        public String getBidId() { return bidId; }
        public String getVendorId() { return vendorId; }
        public int getVendorPrice() { return vendorPrice; }
    }

    public static class PaymentInfo {
        private String payMethod;
        private String payStatus;
        private String transactionId;

        public PaymentInfo() {}

        public PaymentInfo(String payMethod, String payStatus, String transactionId) {
            this.payMethod = payMethod;
            this.payStatus = payStatus;
            this.transactionId = transactionId;
        }

        public String getPayMethod() { return payMethod; }
        public String getPayStatus() { return payStatus; }
        public String getTransactionId() { return transactionId; }
    }

    public static class UserInfo {
        private String userId;
        private String userName;
        private String userPhone;
        private String userType;

        public UserInfo() {}

        public UserInfo(String userId, String userName, String userPhone, String userType) {
            this.userId = userId;
            this.userName = userName;
            this.userPhone = userPhone;
            this.userType = userType;
        }

        public String getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getUserPhone() { return userPhone; }
        public String getUserType() { return userType; }
    }
}

