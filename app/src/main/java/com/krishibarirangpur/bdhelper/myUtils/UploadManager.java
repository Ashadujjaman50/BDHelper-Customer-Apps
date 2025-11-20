package com.krishibarirangpur.bdhelper.myUtils;

import android.net.Uri;
import android.util.Log;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UploadManager {

    private final StorageReference storageRef;

    public interface UploadCallback {
        void onUploadSuccess(Map<String, String> downloadUrls);
        void onUploadFailure(String errorMessage);
    }

    public UploadManager() {
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void uploadServiceImages(String serviceId, Uri brtaUri, Uri transportUri, Uri licenceUri, UploadCallback callback) {
        Map<String, String> downloadUrls = new HashMap<>();

        int totalFiles = 0;
        if (brtaUri != null) totalFiles++;
        if (transportUri != null) totalFiles++;
        if (licenceUri != null) totalFiles++;

        if (totalFiles == 0) {
            callback.onUploadSuccess(downloadUrls);
            return;
        }

        final int[] uploadedCount = {0};

        if (brtaUri != null) {
            int finalTotalFiles = totalFiles;
            uploadSingleFile(serviceId, "brtaImage", brtaUri, (url) -> {
                downloadUrls.put("brtaImage", url);
                uploadedCount[0]++;
                if (uploadedCount[0] == finalTotalFiles) callback.onUploadSuccess(downloadUrls);
            }, error -> {
                callback.onUploadFailure(String.valueOf(error));
            });
        }

        if (transportUri != null) {
            int finalTotalFiles1 = totalFiles;
            uploadSingleFile(serviceId, "transportImage", transportUri, (url) -> {
                downloadUrls.put("transportImage", url);
                uploadedCount[0]++;
                if (uploadedCount[0] == finalTotalFiles1) callback.onUploadSuccess(downloadUrls);
            }, error -> {
                callback.onUploadFailure(String.valueOf(error));
            });
        }

        if (licenceUri != null) {
            int finalTotalFiles2 = totalFiles;
            uploadSingleFile(serviceId, "driverLicence", licenceUri, (url) -> {
                downloadUrls.put("driverLicence", url);
                uploadedCount[0]++;
                if (uploadedCount[0] == finalTotalFiles2) callback.onUploadSuccess(downloadUrls);
            }, error -> {
                callback.onUploadFailure(String.valueOf(error));
            });
        }
    }

    private void uploadSingleFile(String serviceId, String fieldName, Uri fileUri,
                                  OnSuccessListener<String> successListener,
                                  OnFailureListener failureListener) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        String randomPart = sb.toString();
        String fileName = fieldName + "_" + randomPart;
        StorageReference fileRef = storageRef.child("services/" + serviceId + "_" + fileName+".jpg");

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    successListener.onSuccess(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Log.e("UploadManager", "Upload failed: " + e.getMessage());
                    failureListener.onFailure(e);
                });
    }
}