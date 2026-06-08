package com.krishibarirangpur.bdhelper.utils.firebase;

import java.util.HashMap;
import java.util.Map;

public class ServiceMapHelper {

    /**
     * ডাইনামিক স্পেসিফিকেশন (specs) ম্যাপ তৈরি করে
     */
    public static Map<String, String> createSpecsMap(String brandOrModel, String registrationNumber, String sizeAndCapacity, String categoryAndYear) {
        Map<String, String> specsMap = new HashMap<>();
        specsMap.put("brandOrModel", brandOrModel != null ? brandOrModel : "");
        specsMap.put("registrationNumber", registrationNumber != null ? registrationNumber : "");
        specsMap.put("sizeAndCapacity", sizeAndCapacity != null ? sizeAndCapacity : "");
        specsMap.put("categoryAndYear", categoryAndYear != null ? categoryAndYear : "");
        return specsMap;
    }

    /**
     * মিডিয়া ফাইল (media) ম্যাপ তৈরি করে
     */
    public static Map<String, String> createMediaMap(String transportImage, String brtaImage, String driverLicence) {
        Map<String, String> mediaMap = new HashMap<>();
        mediaMap.put("transportImage", transportImage != null ? transportImage : "");
        mediaMap.put("brtaImage", brtaImage != null ? brtaImage : "");
        mediaMap.put("driverLicence", driverLicence != null ? driverLicence : "");
        return mediaMap;
    }

    /**
     * সম্পূর্ণ সার্ভিস ডাটা ম্যাপ তৈরি করে (New Submission বা Migration এর জন্য)
     */
    public static Map<String, Object> createFullServiceMap(String serviceId, String categoryId, String subCategoryId, 
                                                         String subCategoryName, String status, String verified,
                                                         Map<String, String> specs, Map<String, String> media) {
        Map<String, Object> serviceMap = new HashMap<>();
        serviceMap.put("serviceId", serviceId);
        serviceMap.put("categoryId", categoryId);
        serviceMap.put("subCategoryId", subCategoryId);
        serviceMap.put("subCategoryName", subCategoryName);
        serviceMap.put("serviceStatus", status != null ? status : "Inactive");
        serviceMap.put("serviceVerified", verified != null ? verified : "pending");
        serviceMap.put("specs", specs);
        serviceMap.put("media", media);
        return serviceMap;
    }
}
