package com.krishibarirangpur.bdhelper.userActivity.partner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.imagecropprocessor.CropOptions;
import com.ashadujjaman.imagecropprocessor.ImageManager;
import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityServiceDocumentBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.uploadController.ImageUploadHelper;
import com.krishibarirangpur.bdhelper.utils.uploadController.UploadManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServiceDocumentActivity extends BaseActivity {

    private ActivityServiceDocumentBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    private String serviceId, subCategoryId, subCategoryName;
    private String currentImageKey;

    private ImageManager imageManager;
    private CropOptions cropOptions;
    private LoadingDialog loadingDialog;

    private Uri brtaImageUri, transportImageUri, drivingLicenceImageUri;
    private final Set<View> hiddenFields = new HashSet<>();

    // Image Keys Constants
    private static final String KEY_BRTA = "brta_document";
    private static final String KEY_TRANSPORT = "transport_image";
    private static final String KEY_DRIVING = "driving_licence";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_document);

        init();
        setupListeners();
        hideDocumentWithSubCategory();
    }

    private void init() {
        serviceId = getIntent().getStringExtra("serviceId");
        subCategoryId = getIntent().getStringExtra(MyUtils.subCategoryId);
        subCategoryName = getIntent().getStringExtra(MyUtils.subCategoryName);

        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.subCategoryNameTV.setText(subCategoryName);

        imageManager = new ImageManager(this);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);


    }

    private void setupListeners() {
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        binding.brtaDocumentBtn.setOnClickListener(v -> openImagePicker(KEY_BRTA,12,16));
        binding.transportBtn.setOnClickListener(v -> openImagePicker(KEY_TRANSPORT,1,1));
        binding.drivingLicenceBtn.setOnClickListener(v -> openImagePicker(KEY_DRIVING,16,12));

        binding.submitBtn.setOnClickListener(v -> {
            if (validateImages()) {
                submitData();
            }
        });
    }

    private void openImagePicker(String key, int x, int y) {
        currentImageKey = key;
        cropOptions = ImageUploadHelper.getCropOptions(CropOptions.FrameType.RECTANGLE, x, y);
        ImageUploadHelper.showImagePickerDialog(this, imageManager, galleryLauncher, cameraLauncher, requestPermissionLauncher);
    }

    private void submitData() {
        loadingDialog.setMessage("আপনার ডকুমেন্ট সাবমিট হচ্ছে...");
        loadingDialog.show();

        UploadManager uploadManager = new UploadManager();
        uploadManager.uploadServiceImages(serviceId, brtaImageUri, transportImageUri, drivingLicenceImageUri,
                new UploadManager.UploadCallback() {
                    @Override
                    public void onUploadSuccess(Map<String, String> downloadUrls) {
                        // ৩. মিডিয়া ফাইল (media) ম্যাপ তৈরি
                        Map<String, String> mediaMap = new HashMap<>();
                        mediaMap.put("transportImage", downloadUrls.getOrDefault("transportImage", ""));
                        mediaMap.put("brtaImage", downloadUrls.getOrDefault("brtaImage", ""));
                        mediaMap.put("driverLicence", downloadUrls.getOrDefault("driverLicence", ""));

                        saveToFirestore(mediaMap);

                    }

                    @Override
                    public void onUploadFailure(String errorMessage) {
                        loadingDialog.dismiss();
                        MyToast.showShort(ServiceDocumentActivity.this, "Upload Failed: " + errorMessage);
                    }
                });
    }

    private void saveToFirestore(Map<String, String> mediaMap) {
        Map<String, Object> serviceMap = new HashMap<>();
        serviceMap.put("serviceStatus", "active");
        serviceMap.put("serviceVerified", "process");

        // মূল ম্যাপে media যুক্ত করা
        serviceMap.put("media", mediaMap);

        db.collection(FirebaseCollectionTable.USERS)
                .document(firebaseUser.getUid())
                .collection(FirebaseCollectionTable.SERVICES)
                .document(serviceId)
                .set(serviceMap, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(this, "Service Submitted Successfully!");
                    finishOnBack();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(this, "Firestore Error: " + e.getMessage());
                });
    }

    private void hideDocumentWithSubCategory() {
        hiddenFields.clear();

        Set<String> hiddenDocs = new HashSet<>(Arrays.asList(
                MyUtils.SUB_CHARGER_VAN_ID, MyUtils.HARVESTER_MACHINE_ID,
                MyUtils.SUB_TRACTOR_ID, MyUtils.SUB_RICE_TRANSPLANTER_ID,
                MyUtils.SUB_EXCAVATOR_ID, MyUtils.HOME_SHIFTING_ID,
                MyUtils.SUB_MECHANIC_ID, MyUtils.SUB_ELECTRICIAN_ID,
                MyUtils.SUB_STOVE_TECHNICIAN_ID, MyUtils.SUB_PLUMBER_ID
        ));

        if (hiddenDocs.contains(subCategoryId)) {
            binding.brtaCv.setVisibility(View.GONE);
            binding.drivingLicenceCv.setVisibility(View.GONE);
            hiddenFields.add(binding.brtaCv);
            hiddenFields.add(binding.drivingLicenceCv);

            updateTransportLabel();
        } else if (MyUtils.SUB_DRIVER_ID.equals(subCategoryId)) {
            binding.transportCv.setVisibility(View.GONE);
            binding.brtaCv.setVisibility(View.GONE);
            hiddenFields.add(binding.transportCv);
            hiddenFields.add(binding.brtaCv);
        }
    }

    private void updateTransportLabel() {
        if (Arrays.asList(MyUtils.SUB_CHARGER_VAN_ID, MyUtils.HARVESTER_MACHINE_ID).contains(subCategoryId)) {
            binding.subCategoryTv.setText("গাড়ির সামনের ছবি (গাড়ির নাম সহ)");
        } else if (Arrays.asList(MyUtils.SUB_TRACTOR_ID, MyUtils.SUB_RICE_TRANSPLANTER_ID, MyUtils.SUB_EXCAVATOR_ID).contains(subCategoryId)) {
            binding.subCategoryTv.setText("গাড়ির সামনের ছবি (সম্পুর্ণ গাড়ির)");
        } else if (MyUtils.HOME_SHIFTING_ID.equals(subCategoryId)) {
            binding.subCategoryTv.setText("টিম মেম্বারদের গ্রুপ ছবি (ব্যবসায়িক)");
            binding.transportIv.setImageResource(R.drawable.ic_teamwork);
        } else {
            binding.subCategoryTv.setText("আপনার দোকানের ছবি দিন");
            binding.transportIv.setImageResource(R.drawable.ic_shop);
        }
    }

    private boolean validateImages() {
        if (!hiddenFields.contains(binding.brtaCv) && brtaImageUri == null) {
            MyToast.showShort(this, "দয়া করে BRTA ডকুমেন্ট আপলোড করুন");
            return false;
        }
        if (!hiddenFields.contains(binding.transportCv) && transportImageUri == null) {
            MyToast.showShort(this, "দয়া করে " + subCategoryName + " ডকুমেন্ট আপলোড করুন");
            return false;
        }
        if (!hiddenFields.contains(binding.drivingLicenceCv) && drivingLicenceImageUri == null) {
            MyToast.showShort(this, "দয়া করে ড্রাইভিং লাইসেন্সের আপলোড করুন");
            return false;
        }
        return true;
    }

    private final ActivityResultLauncher<Intent> cropResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri croppedUri = result.getData().getParcelableExtra("croppedUri");
                    if (croppedUri != null) {
                        handleCropSuccess(croppedUri);
                    }
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    MyToast.showShort(this, "Crop cancelled");
                }
            }
    );

    private void handleCropSuccess(Uri uri) {
        switch (currentImageKey) {
            case KEY_BRTA:
                brtaImageUri = uri;
                updateButtonStatus(binding.brtaDocumentBtn, binding.brtaDocumentIv, uri);
                break;
            case KEY_TRANSPORT:
                transportImageUri = uri;
                updateButtonStatus(binding.transportBtn, binding.transportIv, uri);
                break;
            case KEY_DRIVING:
                drivingLicenceImageUri = uri;
                updateButtonStatus(binding.drivingLicenceBtn, binding.drivingLicenceIv, uri);
                break;
        }
    }

    private void updateButtonStatus(View btn, android.widget.ImageView iv, Uri uri) {
        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor));
        if (btn instanceof android.widget.Button) {
            ((android.widget.Button) btn).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upload_done, 0, 0, 0);
        } else if (btn instanceof android.widget.TextView) {
            ((android.widget.TextView) btn).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upload_done, 0, 0, 0);
        }
        iv.setImageURI(uri);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) imageManager.startCrop(selectedUri, cropOptions, cropResultLauncher);
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri cameraUri = imageManager.getCameraUri();
                    if (cameraUri != null) imageManager.startCrop(cameraUri, cropOptions, cropResultLauncher);
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) imageManager.openCamera(cameraLauncher);
                else MyToast.showShort(this, "Camera permission denied");
            }
    );
}
