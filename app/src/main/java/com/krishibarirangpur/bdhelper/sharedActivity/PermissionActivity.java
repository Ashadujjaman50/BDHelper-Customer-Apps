package com.krishibarirangpur.bdhelper.sharedActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityPermissionBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.PermissionUtil;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;

public class PermissionActivity extends BaseActivity {

    private ActivityPermissionBinding binding;

    private final String[] requiredPermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
    }
            : new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    private final String[] requiredLocationPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private final String[] cameraPermissionsLegacy = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final String[] storagePermissionsLegacy = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final String[] locationPermissionsLegacy = requiredLocationPermissions;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int LOCATION_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission);

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        checkPermissionStatus();

        binding.cameraPermissionCv.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionCameraAccess();
            } else {
                requestLegacyPermission(cameraPermissionsLegacy, CAMERA_REQUEST_CODE);
            }
        });

        binding.storagePermissionCv.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionStorageImages();
            } else {
                requestLegacyPermission(storagePermissionsLegacy, STORAGE_REQUEST_CODE);
            }
        });

        binding.locationPermissionCv.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLocationAccess();
            } else {
                requestLegacyPermission(locationPermissionsLegacy, LOCATION_REQUEST_CODE);
            }
        });
    }

    private void checkPermissionStatus() {
        binding.cameraPermissionTv.setText(checkPermission(Manifest.permission.CAMERA) ? "Granted" : "Permission");
        binding.storagePermissionTv.setText(checkStoragePermission() ? "Granted" : "Permission");
        binding.locationPermissionTv.setText(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ? "Granted" : "Permission");
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkPermission(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void requestLegacyPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    @SuppressLint("SetTextI18n")
    private void requestPermissionStorageImages() {
        if (checkPermission(requiredPermissions[0])) {
            binding.storagePermissionTv.setText("Granted");
        } else {
            requestPermissionStorageImagesLauncher.launch(requiredPermissions[0]);
        }
    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String> requestPermissionStorageImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    binding.storagePermissionTv.setText("Granted");
                } else {
                    binding.storagePermissionTv.setText("Deny");
                    PermissionUtil.showPermissionSettingsDialog(
                            this,
                            "Storage",
                            "Storage permission is required to access images from your device."
                    );
                }
            });

    @SuppressLint("SetTextI18n")
    private void requestPermissionCameraAccess() {
        if (checkPermission(requiredPermissions[1])) {
            binding.cameraPermissionTv.setText("Granted");
        } else {
            requestPermissionCameraAccessLauncher.launch(requiredPermissions[1]);
        }
    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String> requestPermissionCameraAccessLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    binding.cameraPermissionTv.setText("Granted");
                } else {
                    binding.cameraPermissionTv.setText("Deny");
                    PermissionUtil.showPermissionSettingsDialog(
                            this,
                            "Camera",
                            "Camera permission is required to take photos."
                    );
                }
            });

    @SuppressLint("SetTextI18n")
    private void requestPermissionLocationAccess() {
        if (checkPermission(requiredLocationPermissions[0]) && checkPermission(requiredLocationPermissions[1])) {
            binding.locationPermissionTv.setText("Granted");
        } else {
            requestPermissionLocationAccessLauncher.launch(requiredLocationPermissions);
        }
    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String[]> requestPermissionLocationAccessLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allGranted = true;
                for (Boolean isGranted : result.values()) {
                    allGranted &= isGranted;
                }
                if (allGranted) {
                    binding.locationPermissionTv.setText("Granted");
                } else {
                    binding.locationPermissionTv.setText("Deny");
                    PermissionUtil.showPermissionSettingsDialog(
                            this,
                            "Location",
                            "Location access is required for map services and current position."
                    );
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean granted = grantResults.length > 0 && allPermissionsGranted(grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                binding.cameraPermissionTv.setText(granted ? "Granted" : "Deny");
                if (!granted)
                    PermissionUtil.showPermissionSettingsDialog(this, "Camera", "Camera permission is required to take photos.");
                break;

            case STORAGE_REQUEST_CODE:
                binding.storagePermissionTv.setText(granted ? "Granted" : "Deny");
                if (!granted)
                    PermissionUtil.showPermissionSettingsDialog(this, "Storage", "Storage permission is required to access images.");
                break;

            case LOCATION_REQUEST_CODE:
                binding.locationPermissionTv.setText(granted ? "Granted" : "Deny");
                if (!granted)
                    PermissionUtil.showPermissionSettingsDialog(this, "Location", "Location permission is required for accurate location access.");
                break;
        }
    }

    private boolean allPermissionsGranted(@NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
