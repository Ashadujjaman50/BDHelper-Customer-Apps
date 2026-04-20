package com.krishibarirangpur.bdhelper.utils.firebase;

import com.krishibarirangpur.bdhelper.model.ServiceModel;

import java.util.HashMap;
import java.util.Map;

public class BidMapBuilder {
    public static Map<String, Object> createBidMap(
            ServiceModel model,
            String timestamp,
            String bidAmount,
            String userId,
            String vendorId,
            String orderId,
            String rentTime,
            String categoryId,
            String subCategoryId
    ) {

        String modelName = model.getServiceModelNumber();
        String licenceNumber = model.getServiceRegistrationNumber();
        String modelYear = model.getServiceCategoryAndYear();

        Map<String, Object> bid = new HashMap<>();

        // 🔹 service Info
        Map<String, Object> serviceInfo = new HashMap<>();
        serviceInfo.put("vehicleModel", modelName);
        serviceInfo.put("vehicleRegNo", licenceNumber);
        serviceInfo.put("vehicleCatAndYear", modelYear);

        // 🔹 bid Info
        Map<String, Object> bidInfo = new HashMap<>();
        bidInfo.put("bidId", timestamp);
        bidInfo.put("status", "pending");
        bidInfo.put("bidAmount", bidAmount);
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
