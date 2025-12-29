package com.krishibarirangpur.bdhelper.utils;

import java.util.HashMap;
import java.util.Map;

public class OrderCreateHelper {

    public static Map<String, Object> createOrder(
            String orderId,
            String userId,
            String userName,
            String userPhone,
            String categoryId,
            String subCategoryId,
            String loadLocation,
            String unloadLocation,
            String rentLocation,
            String rentDateAndTime,
            String specificationCapacity,
            String specificationDuration,
            String specificationTypes,
            String quantity,
            String description,
            String postDistrict
    ) {

        Map<String, Object> order = new HashMap<>();

        // 🔹 Order Info
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderId", orderId);
        orderInfo.put("uid", userId);
        orderInfo.put("categoryId", categoryId);
        orderInfo.put("subCategoryId", subCategoryId);
        orderInfo.put("status", "pending"); // post status
        orderInfo.put("timestamp", System.currentTimeMillis());

        // 🔹 Route Info
        Map<String, Object> routeInfo = new HashMap<>();
        routeInfo.put("load", loadLocation);
        routeInfo.put("unload", unloadLocation);
        routeInfo.put("rentLocation", rentLocation);
        routeInfo.put("rentTime", rentDateAndTime);

        // 🔹 Specification Info
        Map<String, Object> specInfo = new HashMap<>();
        specInfo.put("capacity", specificationCapacity);
        specInfo.put("duration", specificationDuration);
        specInfo.put("types", specificationTypes);
        specInfo.put("quantity", Replacement.ReplacementNumberBnToEn(quantity));
        specInfo.put("desc", description);
        specInfo.put("postDistrict", postDistrict);

        // 🔹 Bid Info
        Map<String, Object> bidInfo = new HashMap<>();
        bidInfo.put("bidStatus", "pending");
        bidInfo.put("bidId", "");
        bidInfo.put("vendorId", "");
        bidInfo.put("vendorPrice", 0);

        // 🔹 Payment Info
        Map<String, Object> paymentInfo = new HashMap<>();
        paymentInfo.put("payMethod", "");
        paymentInfo.put("payStatus", "pending");
        paymentInfo.put("transactionId", "");

        // 🔹 User Info
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userName", userName);
        userInfo.put("userPhone", userPhone);
        userInfo.put("userType", "customer"); // অথবা partner

        // সব একসাথে বসানো
        order.put("orderInfo", orderInfo);
        order.put("routeInfo", routeInfo);
        order.put("specInfo", specInfo);
        order.put("bidInfo", bidInfo);
        order.put("paymentInfo", paymentInfo);
        order.put("userInfo", userInfo);

        return order;
    }
}

