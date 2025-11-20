package com.krishibarirangpur.bdhelper.model;

public class ModelNotice {

    private String noticeId, sendUserId, receivedUserId, senderType, postId, noticeCategory,
            noticeTitle, postDistrict, noticeDescription, timestamp;

    public ModelNotice() {
    }

    public ModelNotice(String noticeId, String sendUserId, String receivedUserId, String senderType, String postId, String noticeCategory, String noticeTitle, String postDistrict, String noticeDescription, String timestamp) {
        this.noticeId = noticeId;
        this.sendUserId = sendUserId;
        this.receivedUserId = receivedUserId;
        this.senderType = senderType;
        this.postId = postId;
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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
