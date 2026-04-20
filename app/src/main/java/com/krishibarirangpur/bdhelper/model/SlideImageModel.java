package com.krishibarirangpur.bdhelper.model;

public class SlideImageModel {
    private String bannerId;
    private String title;
    private String imageUrl;
    private String audience;
    private String placement;
    private int order;
    private boolean active;
    private long timestamp;

    // Empty constructor (Firestore এর জন্য দরকার)
    public SlideImageModel() {}

    public SlideImageModel(String bannerId, String title, String imageUrl,
                           String audience, String placement,
                           int order, boolean active, long timestamp) {
        this.bannerId = bannerId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.audience = audience;
        this.placement = placement;
        this.order = order;
        this.active = active;
        this.timestamp = timestamp;
    }

    // 🔹 Getter & Setter

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

