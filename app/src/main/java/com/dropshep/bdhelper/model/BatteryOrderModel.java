package com.dropshep.bdhelper.model;

public class BatteryOrderModel {

    private String orderId;
    private String userId;
    private String userName;
    private String userPhone;
    private String userType;
    private String orderType;
    private String orderStatus;
    private String exchangeType;
    private String timestamp;

    private BatteryDetails batteryDetails;
    private ExchangeDetails exchangeDetails;
    private DeliveryDetails deliveryDetails;
    private PaymentDetails paymentDetails;

    public BatteryOrderModel() {
        // Firestore এর জন্য empty constructor দরকার
    }

    public BatteryOrderModel(String orderId, String userId, String userName, String userPhone,
                             String userType, String orderType, String orderStatus,
                             String exchangeType, String timestamp,
                             BatteryDetails batteryDetails, ExchangeDetails exchangeDetails,
                             DeliveryDetails deliveryDetails, PaymentDetails paymentDetails) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userType = userType;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.exchangeType = exchangeType;
        this.timestamp = timestamp;
        this.batteryDetails = batteryDetails;
        this.exchangeDetails = exchangeDetails;
        this.deliveryDetails = deliveryDetails;
        this.paymentDetails = paymentDetails;
    }

    // ✅ Getter & Setter
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getExchangeType() { return exchangeType; }
    public void setExchangeType(String exchangeType) { this.exchangeType = exchangeType; }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BatteryDetails getBatteryDetails() { return batteryDetails; }
    public void setBatteryDetails(BatteryDetails batteryDetails) { this.batteryDetails = batteryDetails; }

    public ExchangeDetails getExchangeDetails() { return exchangeDetails; }
    public void setExchangeDetails(ExchangeDetails exchangeDetails) { this.exchangeDetails = exchangeDetails; }

    public DeliveryDetails getDeliveryDetails() { return deliveryDetails; }
    public void setDeliveryDetails(DeliveryDetails deliveryDetails) { this.deliveryDetails = deliveryDetails; }

    public PaymentDetails getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(PaymentDetails paymentDetails) { this.paymentDetails = paymentDetails; }


    // ================================
    // ✅ Nested Models
    // ================================

    public static class BatteryDetails {
        private String batteryId;
        private String batteryName;
        private String batteryType;
        private String batteryVoltage;
        private String batteryCapacity;
        private String batteryPrice;

        public BatteryDetails() {}

        public BatteryDetails(String batteryId, String batteryName, String batteryType,
                              String batteryVoltage, String batteryCapacity, String batteryPrice) {
            this.batteryId = batteryId;
            this.batteryName = batteryName;
            this.batteryType = batteryType;
            this.batteryVoltage = batteryVoltage;
            this.batteryCapacity = batteryCapacity;
            this.batteryPrice = batteryPrice;
        }

        public String getBatteryId() { return batteryId; }
        public void setBatteryId(String batteryId) { this.batteryId = batteryId; }

        public String getBatteryName() { return batteryName; }
        public void setBatteryName(String batteryName) { this.batteryName = batteryName; }

        public String getBatteryType() { return batteryType; }
        public void setBatteryType(String batteryType) { this.batteryType = batteryType; }

        public String getBatteryVoltage() { return batteryVoltage; }
        public void setBatteryVoltage(String batteryVoltage) { this.batteryVoltage = batteryVoltage; }

        public String getBatteryCapacity() { return batteryCapacity; }
        public void setBatteryCapacity(String batteryCapacity) { this.batteryCapacity = batteryCapacity; }

        public String getBatteryPrice() { return batteryPrice; }
        public void setBatteryPrice(String batteryPrice) { this.batteryPrice = batteryPrice; }
    }

    public static class ExchangeDetails {
        private String oldBatteryBrand;
        private String oldBatteryType;
        private String oldBatteryVoltage;
        private String oldBatteryCapacity;
        private String exchangeDiscount;

        public ExchangeDetails() {}

        public ExchangeDetails(String oldBatteryBrand, String oldBatteryType,
                               String oldBatteryVoltage, String oldBatteryCapacity,
                               String exchangeDiscount) {
            this.oldBatteryBrand = oldBatteryBrand;
            this.oldBatteryType = oldBatteryType;
            this.oldBatteryVoltage = oldBatteryVoltage;
            this.oldBatteryCapacity = oldBatteryCapacity;
            this.exchangeDiscount = exchangeDiscount;
        }

        public String getOldBatteryBrand() { return oldBatteryBrand; }
        public void setOldBatteryBrand(String oldBatteryBrand) { this.oldBatteryBrand = oldBatteryBrand; }

        public String getOldBatteryType() { return oldBatteryType; }
        public void setOldBatteryType(String oldBatteryType) { this.oldBatteryType = oldBatteryType; }

        public String getOldBatteryVoltage() { return oldBatteryVoltage; }
        public void setOldBatteryVoltage(String oldBatteryVoltage) { this.oldBatteryVoltage = oldBatteryVoltage; }

        public String getOldBatteryCapacity() { return oldBatteryCapacity; }
        public void setOldBatteryCapacity(String oldBatteryCapacity) { this.oldBatteryCapacity = oldBatteryCapacity; }

        public String getExchangeDiscount() { return exchangeDiscount; }
        public void setExchangeDiscount(String exchangeDiscount) { this.exchangeDiscount = exchangeDiscount; }
    }

    public static class DeliveryDetails {
        private String deliveryAddress;
        private String deliveryCharge;
        private String deliveryStatus;

        public DeliveryDetails() {}

        public DeliveryDetails(String deliveryAddress, String deliveryCharge, String deliveryStatus) {
            this.deliveryAddress = deliveryAddress;
            this.deliveryCharge = deliveryCharge;
            this.deliveryStatus = deliveryStatus;
        }

        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

        public String getDeliveryCharge() { return deliveryCharge; }
        public void setDeliveryCharge(String deliveryCharge) { this.deliveryCharge = deliveryCharge; }

        public String getDeliveryStatus() { return deliveryStatus; }
        public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    }

    public static class PaymentDetails {
        private String paymentMethod;
        private String paymentStatus;
        private String transactionId;

        public PaymentDetails() {}

        public PaymentDetails(String paymentMethod, String paymentStatus, String transactionId) {
            this.paymentMethod = paymentMethod;
            this.paymentStatus = paymentStatus;
            this.transactionId = transactionId;
        }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    }
}

