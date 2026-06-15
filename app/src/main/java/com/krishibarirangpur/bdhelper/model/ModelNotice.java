package com.krishibarirangpur.bdhelper.model;

public class ModelNotice {

    private String noticeId, sendUserId, receivedUserId, senderType, orderId, noticeCategory,
            noticeTitle, postDistrict, noticeDescription;
    private Object timestamp; // 🔥 Field type changed to Object to handle both String and Long

    public ModelNotice() {
    }

    public ModelNotice(String noticeId, String sendUserId, String receivedUserId, String senderType, String orderId, String noticeCategory, String noticeTitle, String postDistrict, String noticeDescription, Object timestamp) {
        this.noticeId = noticeId;
        this.sendUserId = sendUserId;
        this.receivedUserId = receivedUserId;
        this.senderType = senderType;
        this.orderId = orderId;
        this.noticeCategory = noticeCategory;
        this.noticeTitle = noticeTitle;
        this.postDistrict = postDistrict;
        this.noticeDescription = noticeDescription;
        this.timestamp = timestamp;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getReceivedUserId() {
        return receivedUserId;
    }

    public void setReceivedUserId(String receivedUserId) {
        this.receivedUserId = receivedUserId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNoticeCategory() {
        return noticeCategory;
    }

    public void setNoticeCategory(String noticeCategory) {
        this.noticeCategory = noticeCategory;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getPostDistrict() {
        return postDistrict;
    }

    public void setPostDistrict(String postDistrict) {
        this.postDistrict = postDistrict;
    }

    public String getNoticeDescription() {
        return noticeDescription;
    }

    public void setNoticeDescription(String noticeDescription) {
        this.noticeDescription = noticeDescription;
    }

    // ✅ Dynamic Getter: Always returns String regardless of type in DB
    public String getTimestamp() {
        if (timestamp instanceof Long) {
            return String.valueOf(timestamp);
        } else if (timestamp instanceof String) {
            return (String) timestamp;
        }
        return timestamp != null ? timestamp.toString() : "";
    }

    // ✅ Helper Getter: If you need it as Long for sorting or formatting
    public long getTimestampLong() {
        try {
            if (timestamp instanceof Long) {
                return (Long) timestamp;
            } else if (timestamp instanceof String) {
                return Long.parseLong((String) timestamp);
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    // ✅ Dynamic Setter: Handles Firestore's data mapping
    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
