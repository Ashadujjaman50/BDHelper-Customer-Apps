package com.krishibarirangpur.bdhelper.model;

public class SlideImage {
    private String slideId;
    private String slideDescription;
    private String slideImage;
    private String slideType;
    private String timestamp;

    // Empty constructor (Firestore এর জন্য দরকার)
    public SlideImage() {}

    public SlideImage(String slideId, String slideDescription, String slideImage, String slideType, String timestamp) {
        this.slideId = slideId;
        this.slideDescription = slideDescription;
        this.slideImage = slideImage;
        this.slideType = slideType;
        this.timestamp = timestamp;
    }

    public String getSlideId() {
        return slideId;
    }

    public void setSlideId(String slideId) {
        this.slideId = slideId;
    }

    public String getSlideDescription() {
        return slideDescription;
    }

    public void setSlideDescription(String slideDescription) {
        this.slideDescription = slideDescription;
    }

    public String getSlideImage() {
        return slideImage;
    }

    public void setSlideImage(String slideImage) {
        this.slideImage = slideImage;
    }

    public String getSlideType() {
        return slideType;
    }

    public void setSlideType(String slideType) {
        this.slideType = slideType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

