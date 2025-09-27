package com.dropshep.bdhelper.partner;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.ActivityServiceDocumentBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.FileUploadHelper;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.myUtils.UploadManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServiceDocumentActivity extends BaseActivity {

    private ActivityServiceDocumentBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    FirebaseFirestore db;

    String serviceId, subCategoryId, subCategoryName;
    private String imageName;
    private static final int IMAGE_PICK_CAMERA_CODE = 1000;
    private static final int IMAGE_PICK_GALLERY_CODE = 1100;

    private Uri imageUri, brtaImageUri, transportImageUri, drivingLicenceImageUri;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_document);

        //init views
        serviceId = getIntent().getStringExtra("serviceId");
        subCategoryId = getIntent().getStringExtra(MyUtils.subCategoryId);
        subCategoryName = getIntent().getStringExtra(MyUtils.subCategoryName);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        binding.subCategoryNameTV.setText(subCategoryName);

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        //Sub Category wise Hide and required
        hideDocumentWithSubCategory();


        binding.brtaDocumentBtn.setOnClickListener(v -> {
            //BRTA Document
            imageName = "brta_document";
            showBottomPopupImagePicker();
        });

        binding.transportBtn.setOnClickListener(v -> {
            //Transport
            imageName = "transport_image";
            showBottomPopupImagePicker();
        });

        binding.drivingLicenceBtn.setOnClickListener(v -> {
            //Driving
            imageName = "driving_licence";
            showBottomPopupImagePicker();
        });


        binding.submitBtn.setOnClickListener(v -> {
            if (validateImages()) {
                fileUploadAndDatabaseSubmit();
            }
        });


    }

    private void fileUploadAndDatabaseSubmit() {
        loadingDialog.setMessage("আপনার ডকুমেন্ট সাবমিট হচ্ছে...");
        loadingDialog.show();

        String userId = firebaseUser.getUid();

        UploadManager uploadManager = new UploadManager();
        uploadManager.uploadServiceImages(serviceId, brtaImageUri, transportImageUri, drivingLicenceImageUri,
                new UploadManager.UploadCallback() {
                    @Override
                    public void onUploadSuccess(Map<String, String> downloadUrls) {
                        // এখন Firestore এ save করার জন্য serviceMap তৈরি করুন
                        Map<String, Object> serviceMap = new HashMap<>();
                        // image Uri যোগ করা
                        serviceMap.put("brtaImage", brtaImageUri != null ? brtaImageUri.toString() : "");
                        serviceMap.put("transportImage", transportImageUri != null ? transportImageUri.toString() : "");
                        serviceMap.put("driverLicence", drivingLicenceImageUri != null ? drivingLicenceImageUri.toString() : "");

                        serviceMap.put("serviceStatus", "active");
                        serviceMap.put("serviceVerified", "process");

                        // Download URLs বসিয়ে দিলাম
                        serviceMap.putAll(downloadUrls);

                        db.collection("users")
                                .document(userId)
                                .collection("services")
                                .document(serviceId)
                                .set(serviceMap, SetOptions.merge())
                                .addOnSuccessListener(unused -> {
                                    MyToast.showShort(ServiceDocumentActivity.this, "Service Submitted Successfully!");
                                    loadingDialog.dismiss();
                                    clearForm();

                                })
                                .addOnFailureListener(e -> {
                                    MyToast.showShort(ServiceDocumentActivity.this, "Firestore Error: " + e.getMessage());
                                    loadingDialog.dismiss();
                                });
                    }

                    @Override
                    public void onUploadFailure(String errorMessage) {
                        MyToast.showShort(ServiceDocumentActivity.this, "Upload Failed: " + errorMessage);
                    }
                });
    }

    private void clearForm() {

        transportImageUri = null;
        brtaImageUri = null;
        drivingLicenceImageUri = null;

        binding.transportIv.setImageResource(R.drawable.ic_logo);
        binding.brtaDocumentIv.setImageResource(R.drawable.ic_logo);
        binding.drivingLicenceIv.setImageResource(R.drawable.ic_logo);

        finishOnBack();
    }


    // Hidden field track করার জন্য
    private final Set<View> hiddenFields = new HashSet<>();

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

            if (Arrays.asList(MyUtils.SUB_CHARGER_VAN_ID, MyUtils.HARVESTER_MACHINE_ID).contains(subCategoryId)) {
                binding.subCategoryTv.setText("গাড়ির সামনের ছবি (গাড়ির নাম সহ)");
            } else if (Arrays.asList(MyUtils.HARVESTER_MACHINE_ID, MyUtils.SUB_TRACTOR_ID,
                    MyUtils.SUB_RICE_TRANSPLANTER_ID, MyUtils.SUB_EXCAVATOR_ID).contains(subCategoryId)) {
                binding.subCategoryTv.setText("গাড়ির সামনের ছবি (সম্পুর্ণ গাড়ির)");
            } else if (MyUtils.HOME_SHIFTING_ID.equals(subCategoryId)) {
                binding.subCategoryTv.setText("টিম মেম্বারদের গ্রুপ ছবি (ব্যবসায়িক)");
                binding.transportIv.setImageResource(R.drawable.ic_teamwork);
            } else if (Arrays.asList(MyUtils.SUB_MECHANIC_ID, MyUtils.SUB_ELECTRICIAN_ID,
                    MyUtils.SUB_STOVE_TECHNICIAN_ID, MyUtils.SUB_PLUMBER_ID).contains(subCategoryId)) {
                binding.subCategoryTv.setText("আপনার দোকানের ছবি দিন");
                binding.transportIv.setImageResource(R.drawable.ic_shop);
            }
        }
        else if (MyUtils.SUB_DRIVER_ID.equals(subCategoryId)) {
            binding.transportCv.setVisibility(View.GONE);
            binding.brtaCv.setVisibility(View.GONE);

            hiddenFields.add(binding.transportCv);
            hiddenFields.add(binding.brtaCv);
        }
    }


    private boolean validateImages() {

        // যদি brtaCv hidden না থাকে, তাহলে brtaImageUri লাগবে
        if (!hiddenFields.contains(binding.brtaCv) && brtaImageUri == null) {
            MyToast.showShort(this, "দয়া করে BRTA ডকুমেন্ট আপলোড করুন");
            return false;
        }

        // যদি transportCv hidden না থাকে, তাহলে transportImageUri লাগবে
        if (!hiddenFields.contains(binding.transportCv) && transportImageUri == null) {
            MyToast.showShort(this, "দয়া করে "+subCategoryName+" ডকুমেন্ট আপলোড করুন");
            return false;
        }

        // যদি drivingLicenceCv hidden না থাকে, তাহলে drivingLicenceImageUri লাগবে
        if (!hiddenFields.contains(binding.drivingLicenceCv) && drivingLicenceImageUri == null) {
            MyToast.showShort(this, "দয়া করে ড্রাইভিং লাইসেন্সের আপলোড করুন");
            return false;
        }

        return true;
    }


    private void showBottomPopupImagePicker() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_image_picker);
        dialog.show();

        dialog.findViewById(R.id.cameraBtn).setOnClickListener(v -> {
            dialog.dismiss();
            if (FileUploadHelper.checkAndRequestCameraPermissions(this, IMAGE_PICK_CAMERA_CODE)) {
                pickImageFromCamera();
            }
        });

        dialog.findViewById(R.id.galleryBtn).setOnClickListener(v -> {
            dialog.dismiss();
            if (FileUploadHelper.checkAndRequestStoragePermission(this, IMAGE_PICK_GALLERY_CODE)) {
                pickImageFromGallery();
            }
        });
    }

    private void pickImageFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        launcherForCamera.launch(cameraIntent);
    }

    private final ActivityResultLauncher<Intent> launcherForCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result->{
                if (result.getResultCode() == RESULT_OK){
                    startUCrop(imageUri);
                }
                else {
                    MyToast.showShort(this,"Camera capture cancelled");
                }
            });

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result->{
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                    Uri selectedImageUri = result.getData().getData();
                    String path = FileUploadHelper.getPathFromURI(this, selectedImageUri);
                    if (path != null) selectedImageUri = Uri.fromFile(new File(path));
                    startUCrop(selectedImageUri);
                }
                else {
                    MyToast.showShort(this,"Image selection cancelled");
                }
            });

    private void startUCrop(Uri sourceUri) {
        if (sourceUri == null) {
            MyToast.showShort(this,"Invalid image source");
            return;
        }

        Uri destinationUri= Uri.fromFile(new File(getCacheDir() ,"croppedImage.jpg"));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.gray));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.gray_mid));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.gray));

        UCrop uCrop = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(16, 12)
                .withMaxResultSize(1080, 1080)
                .withOptions(options);

        cropImageLauncher.launch(uCrop.getIntent(this));
    }

    private final ActivityResultLauncher<Intent> cropImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result->{
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    handleCropResult(result.getData());
                }
                else if (result.getResultCode() == UCrop.RESULT_ERROR){
                    handleCropError(result.getData());
                }
                else {
                    MyToast.showShort(this,"Image cropping cancelled");
                }
            });

    private void handleCropResult(Intent data) {
        Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null){
            try {
                Bitmap bitmap = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        ? ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), resultUri))
                        : BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));

                Uri compressedUri = FileUploadHelper.compressImage(this, bitmap);

                if (imageName.equals("brta_document")){
                    binding.brtaDocumentBtn.setBackgroundColor(getColor(R.color.primaryColor));
                    binding.brtaDocumentBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upload_done,0,0,0);
                    binding.brtaDocumentIv.setImageURI(compressedUri);

                    brtaImageUri = compressedUri;
                }
                if (imageName.equals("transport_image")){
                    binding.transportBtn.setBackgroundColor(getColor(R.color.primaryColor));
                    binding.transportBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upload_done,0,0,0);
                    binding.transportIv.setImageURI(compressedUri);

                    transportImageUri = compressedUri;
                }
                if (imageName.equals("driving_licence")){
                    binding.drivingLicenceBtn.setBackgroundColor(getColor(R.color.primaryColor));
                    binding.drivingLicenceBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upload_done,0,0,0);
                    binding.drivingLicenceIv.setImageURI(compressedUri);

                    drivingLicenceImageUri = compressedUri;
                }

            }
            catch (IOException e){
                e.printStackTrace();
                MyToast.showShort(this,"Failed to load cropped image");
            }
        }

    }

    private void handleCropError(Intent data) {
        if (data != null){
            Throwable cropError = UCrop.getError(data);
            if (cropError != null){
                MyToast.showShort(this,"Crop error: " + cropError.getMessage());
            }
        }
    }


}