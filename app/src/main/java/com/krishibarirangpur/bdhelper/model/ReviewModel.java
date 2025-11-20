package com.krishibarirangpur.bdhelper.model;

public class ReviewModel {
    private String reviewId;
    private String vendorId;
    private String customerId;
    private String orderId;
    private float rating;
    private String review;
    private long createdAt;

    public ReviewModel() {
        // Firestore এর জন্য empty constructor দরকার
    }

    public ReviewModel(String reviewId, String vendorId, String customerId, String orderId, float rating, String review, long createdAt) {
        this.reviewId = reviewId;
        this.vendorId = vendorId;
        this.customerId = customerId;
        this.orderId = orderId;
        this.rating = rating;
        this.review = review;
        this.createdAt = createdAt;
    }

    // ✅ Getters
    public String getReviewId() { return reviewId; }
    public String getVendorId() { return vendorId; }
    public String getCustomerId() { return customerId; }
    public String getOrderId() { return orderId; }
    public float getRating() { return rating; }
    public String getReview() { return review; }
    public long getCreatedAt() { return createdAt; }

    // ✅ Setters (optional)
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setRating(float rating) { this.rating = rating; }
    public void setReview(String review) { this.review = review; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
