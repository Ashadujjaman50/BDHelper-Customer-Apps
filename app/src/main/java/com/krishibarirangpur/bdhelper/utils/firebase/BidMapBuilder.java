package com.krishibarirangpur.bdhelper.utils.firebase;

import com.krishibarirangpur.bdhelper.model.ServiceModel;

import java.util.HashMap;
import java.util.Map;

public class BidMapBuilder {
    public static Map<String, Object> createBidMap(
            ServiceModel serviceModel,
            String timestamp,
            String bidAmount,
            String userId,
            String vendorId,
            String orderId,
            String rentTime,
            String categoryId,
            String subCategoryId
    ) {

        int bidValue = (int) Double.parseDouble(bidAmount);
        String modelName = serviceModel.getSafeBrandOrModel();
        String sizeAndCapacity = serviceModel.getSafeSizeAndCapacity();
        String licenceNumber = serviceModel.getSafeRegistrationNumber();
        String modelYear = serviceModel.getSafeManufacturingYear();

        Map<String, Object> bid = new HashMap<>();

        // 🔹 service Info
        Map<String, Object> serviceInfo = new HashMap<>();
        serviceInfo.put("vehicleModel", modelName);
        serviceInfo.put("vehicleRegNo", licenceNumber);
        serviceInfo.put("vehicleCatAndYear", modelYear);
        serviceInfo.put("vehicleSizeAndCapacity", sizeAndCapacity);

        // 🔹 bid Info
        Map<String, Object> bidInfo = new HashMap<>();
        bidInfo.put("bidId", timestamp);
        bidInfo.put("status", "pending");
        bidInfo.put("bidAmount", String.valueOf(bidValue));
        bidInfo.put("userId", userId);
        bidInfo.put("vendorId", vendorId);
        bidInfo.put("timestamp", timestamp);

        // 🔹 order Info
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderId", orderId);
        orderInfo.put("rentTime", rentTime);
        orderInfo.put("categoryId", categoryId);
        orderInfo.put("subCategoryId", subCategoryId);

        // 🔹 final map
        bid.put("serviceInfo", serviceInfo);
        bid.put("bidInfo", bidInfo);
        bid.put("orderInfo", orderInfo);

        return bid;
    }
}
