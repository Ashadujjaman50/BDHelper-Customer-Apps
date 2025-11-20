package com.krishibarirangpur.bdhelper.model;

public class BidModel {

    private ServiceInfo serviceInfo;
    private BidInfo bidInfo;
    private OrderInfo orderInfo;

    public BidModel() {
    }

    public BidModel(ServiceInfo serviceInfo, BidInfo bidInfo, OrderInfo orderInfo) {
        this.serviceInfo = serviceInfo;
        this.bidInfo = bidInfo;
        this.orderInfo = orderInfo;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public BidInfo getBidInfo() {
        return bidInfo;
    }

    public void setBidInfo(BidInfo bidInfo) {
        this.bidInfo = bidInfo;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    // 🔹 Nested classes
    public static class ServiceInfo {
        private String vehicleModel;
        private String vehicleRegNo;
        private String vehicleCatAndYear;

        public ServiceInfo() {}

        public ServiceInfo(String vehicleModel, String vehicleRegNo, String vehicleCatAndYear) {
            this.vehicleModel = vehicleModel;
            this.vehicleRegNo = vehicleRegNo;
            this.vehicleCatAndYear = vehicleCatAndYear;
        }

        public String getVehicleModel() { return vehicleModel; }
        public String getVehicleRegNo() { return vehicleRegNo; }
        public String getVehicleCatAndYear() { return vehicleCatAndYear; }

        public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
        public void setVehicleRegNo(String vehicleRegNo) { this.vehicleRegNo = vehicleRegNo; }
        public void setVehicleCatAndYear(String vehicleCatAndYear) { this.vehicleCatAndYear = vehicleCatAndYear; }
    }

    public static class BidInfo {
        private String bidId;
        private String status;
        private String bidAmount;
        private String userId;
        private String vendorId;
        private String timestamp;

        public BidInfo() {}

        public BidInfo(String bidId, String status, String bidAmount, String userId, String vendorId, String timestamp) {
            this.bidId = bidId;
            this.status = status;
            this.bidAmount = bidAmount;
            this.userId = userId;
            this.vendorId = vendorId;
            this.timestamp = timestamp;
        }

        public String getBidId() { return bidId; }
        public String getStatus() { return status; }
        public String getBidAmount() { return bidAmount; }
        public String getUserId() { return userId; }
        public String getVendorId() { return vendorId; }
        public String getTimestamp() { return timestamp; }

        public void setBidId(String bidId) { this.bidId = bidId; }
        public void setStatus(String status) { this.status = status; }
        public void setBidAmount(String bidAmount) { this.bidAmount = bidAmount; }
        public void setUserId(String userId) { this.userId = userId; }
        public void setVendorId(String vendorId) { this.vendorId = vendorId; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    public static class OrderInfo {
        private String orderId;
        private String rentTime;
        private String categoryId;
        private String subCategoryId;

        public OrderInfo() {}

        public OrderInfo(String orderId, String rentTime, String categoryId, String subCategoryId) {
            this.orderId = orderId;
            this.rentTime = rentTime;
            this.categoryId = categoryId;
            this.subCategoryId = subCategoryId;
        }

        public String getOrderId() { return orderId; }
        public String getRentTime() { return rentTime; }
        public String getCategoryId() { return categoryId; }
        public String getSubCategoryId() { return subCategoryId; }

        public void setOrderId(String orderId) { this.orderId = orderId; }
        public void setRentTime(String rentTime) { this.rentTime = rentTime; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
        public void setSubCategoryId(String subCategoryId) { this.subCategoryId = subCategoryId; }
    }
}

