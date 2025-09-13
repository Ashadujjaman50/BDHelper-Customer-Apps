package com.dropshep.bdhelper.model;

public class ModelServiceRequest {

    String serviceId, serviceName, district, userId, note, status, timestamp;

    public ModelServiceRequest() {
    }

    public ModelServiceRequest(String serviceId, String serviceName, String district, String userId, String note, String status, String timestamp) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.district = district;
        this.userId = userId;
        this.note = note;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
