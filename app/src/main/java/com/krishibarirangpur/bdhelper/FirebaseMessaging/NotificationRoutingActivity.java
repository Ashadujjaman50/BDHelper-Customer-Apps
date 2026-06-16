package com.krishibarirangpur.bdhelper.FirebaseMessaging;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.krishibarirangpur.bdhelper.authentication.LoginActivity;
import com.krishibarirangpur.bdhelper.userActivity.customer.MainActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.BidActivity;
import com.krishibarirangpur.bdhelper.userActivity.partner.DashboardActivity;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

public class NotificationRoutingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Extract data from intent
        String orderId = getIntent().getStringExtra("orderId");
        String type = getIntent().getStringExtra("type");
        String categoryId = getIntent().getStringExtra("categoryId");
        String subCategoryId = getIntent().getStringExtra("subCategoryId");

        SharedPrefHelper sharedPref = new SharedPrefHelper(this);
        String userRole = sharedPref.getString(MyUtils.USER_LOGIN_MODE, "");

        // Check if user is logged in
        if (userRole.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Route based on available data from FCM payload
        if (orderId != null && categoryId != null && subCategoryId != null) {
            routeToActivity(orderId, categoryId, subCategoryId, type, userRole);
        } else {
            // If data is missing (e.g., from old notifications or general ones), go to Main
            goToMain(userRole);
        }
    }

    private void routeToActivity(String orderId, String categoryId, String subCategoryId, String type, String userRole) {
        Intent intent = new Intent(this, BidActivity.class);
        intent.putExtra(MyUtils.orderId, orderId);
        intent.putExtra(MyUtils.categoryId, categoryId);
        intent.putExtra(MyUtils.subCategoryId, subCategoryId);
        intent.putExtra("fromNotification", true);

        if ("partner".equalsIgnoreCase(userRole)) {
            intent.putExtra(MyUtils.USER_TYPE, MyUtils.PARTNER);
            if ("NEW_ORDER".equals(type)) {
                intent.putExtra(MyUtils.bidAction, "new");
            } else if ("BID_CONFIRMED".equals(type)) {
                intent.putExtra(MyUtils.bidAction, "confirmed");
            } else {
                intent.putExtra(MyUtils.bidAction, "pending");
            }
        } else {
            // Customer routing
            intent.putExtra(MyUtils.USER_TYPE, MyUtils.CUSTOMER);
            intent.putExtra(MyUtils.bidAction, "new"); // Customer viewing bids
        }

        startActivity(intent);
        finish();
    }

    private void goToMain(String userRole) {
        Intent intent;
        if (MyUtils.PARTNER.equalsIgnoreCase(userRole)) {
            intent = new Intent(this, DashboardActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
