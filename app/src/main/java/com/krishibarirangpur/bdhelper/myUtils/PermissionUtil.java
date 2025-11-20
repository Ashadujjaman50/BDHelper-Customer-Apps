package com.krishibarirangpur.bdhelper.myUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

public class PermissionUtil {

    public static void showPermissionSettingsDialog(Activity activity, String permissionType, String customMessage) {
        new AlertDialog.Builder(activity)
                .setTitle(permissionType + " Permission Required")
                .setMessage(customMessage + "\n\nPlease allow it from App Settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> openAppSettings(activity))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    public static void openAppSettings(Activity activity) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}

