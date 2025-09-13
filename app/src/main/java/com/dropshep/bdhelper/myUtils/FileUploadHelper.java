package com.dropshep.bdhelper.myUtils;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUploadHelper {


    public static boolean checkAndRequestCameraPermissions(Activity activity, int requestCodeCamera) {
        boolean cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        boolean storagePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            storagePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }

        if (!cameraPermission || !storagePermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                        requestCodeCamera);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCodeCamera);
            }
            return false;
        }

        return true;
    }

    public static boolean checkAndRequestStoragePermission(Activity activity, int requestCodeGallery) {
        boolean storagePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            storagePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }

        if (!storagePermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, requestCodeGallery);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCodeGallery);
            }
            return false;
        }

        return true;
    }

    public static String getPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    public static Uri compressImage(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(context, "Invalid image bitmap", Toast.LENGTH_SHORT).show();
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();

        File file = new File(context.getCacheDir(), "image_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream outputStream1 = new FileOutputStream(file)) {
            outputStream1.write(bytes);
            outputStream1.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to compress image", Toast.LENGTH_SHORT).show();
            return null;
        }

        return Uri.fromFile(file);
    }

}

