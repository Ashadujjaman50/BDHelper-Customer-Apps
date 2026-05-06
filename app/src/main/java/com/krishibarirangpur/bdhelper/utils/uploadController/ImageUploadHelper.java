package com.krishibarirangpur.bdhelper.utils.uploadController;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import com.ashadujjaman.imagecropprocessor.CropOptions;
import com.ashadujjaman.imagecropprocessor.ImageManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.krishibarirangpur.bdhelper.R;

public class ImageUploadHelper {

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    /**
     * Common CropOptions generator
     * @param frameType CIRCLE or RECTANGLE
     * @param aspectX Aspect Ratio X
     * @param aspectY Aspect Ratio Y
     */
    public static CropOptions getCropOptions(CropOptions.FrameType frameType, int aspectX, int aspectY) {
        return new CropOptions.Builder()
                .setRotationEnabled(true)
                .setFlipEnabled(true)
                .setDefaultScaleEnabled(false)
                .setToolbarConfig(Color.parseColor("#46A35C"), Color.WHITE, "Crop Photo")
                .setStatusBarColor(Color.parseColor("#D6E4D7"))
                .setActiveWidgetColor(Color.parseColor("#02B860"))
                .setControlPanelColor(Color.parseColor("#FFFFFF"))
                .setCompressionFormat(Bitmap.CompressFormat.JPEG)
                .setCompressionQuality(80)
                .setFrameType(frameType)
                .setAspectRatio(aspectX, aspectY)
                .setMaxResultSize(1080, 1080)
                .setShowGuides(true)
                .build();
    }

    public static void showImagePickerDialog(Context context, ImageManager imageManager,
                                            ActivityResultLauncher<Intent> galleryLauncher,
                                            ActivityResultLauncher<Intent> cameraLauncher,
                                            ActivityResultLauncher<String> permissionLauncher) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.dialog_image_picker);
        bottomSheetDialog.show();

        LinearLayout cameraBtn = bottomSheetDialog.findViewById(R.id.cameraBtn);
        LinearLayout galleryBtn = bottomSheetDialog.findViewById(R.id.galleryBtn);

        if (cameraBtn != null) {
            cameraBtn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    imageManager.openCamera(cameraLauncher);
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA);
                }
            });
        }
        if (galleryBtn != null) {
            galleryBtn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                imageManager.openGallery(galleryLauncher);
            });
        }
    }

}
