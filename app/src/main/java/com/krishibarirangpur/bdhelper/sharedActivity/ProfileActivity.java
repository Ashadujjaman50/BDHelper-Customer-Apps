package com.krishibarirangpur.bdhelper.sharedActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.imagecropprocessor.CropOptions;
import com.ashadujjaman.imagecropprocessor.ImageManager;
import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityProfileBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;
import com.krishibarirangpur.bdhelper.utils.uploadController.ImageUploadHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.krishibarirangpur.bdhelper.utils.uploadController.UploadManager;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private ImageManager imageManager;
    private CropOptions cropOptions;
    private LoadingDialog loadingDialog;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        initMembers();
        setupClickListeners();
        loadCurrentUserInfo();
    }

    private void initMembers() {
        imageManager = new ImageManager(this);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            userId = firebaseAuth.getCurrentUser().getUid();
        }
        
        cropOptions = ImageUploadHelper.getCropOptions(CropOptions.FrameType.CIRCLE, 1, 1);
    }

    private void setupClickListeners() {
        binding.documentBtn.setOnClickListener(v -> navigateTo(NIDPhotoActivity.class));
        binding.editBtn.setOnClickListener(v -> navigateTo(EditProfileActivity.class));
        binding.openGalleryToLoadImage.setOnClickListener(v -> 
                ImageUploadHelper.showImagePickerDialog(this, imageManager, galleryLauncher, cameraLauncher, requestPermissionLauncher));
        binding.backBtn.setOnClickListener(v -> finishOnBack());
    }

    private void navigateTo(Class<?> targetClass) {
        Intent intent = new Intent(this, targetClass);
        intent.putExtra("user_type", "customer");
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void loadCurrentUserInfo() {
        // Load main user info
        db.collection("users").document(userId).get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        binding.userNameTV.setText(documentSnapshot.getString("name"));
                        binding.userMobileTv.setText(documentSnapshot.getString("phone"));
                        binding.userLocationTv.setText(documentSnapshot.getString("location"));
                        binding.userEmailTv.setText(documentSnapshot.getString("email"));

                        String district = documentSnapshot.getString("district");
                        String shownDistrict = Replacement.getLocalizedDistrict(this, district);
                        binding.userDistrictTv.setText(shownDistrict);
                    }
                });

        // Load profile image
        db.collection("users").document(userId).collection("Document").document("info")
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImage = documentSnapshot.getString("profileImage");
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_profile).into(binding.userProfilePicIV);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentUserInfo();
    }

    private final ActivityResultLauncher<Intent> cropResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri croppedUri = result.getData().getParcelableExtra("croppedUri");
                    if (croppedUri != null) {
                        binding.userProfilePicIV.setImageURI(croppedUri);
                        uploadImage(croppedUri);
                    }
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    ToastMessage( "Crop cancelled");
                }
            }
    );

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
                else ToastMessage( "Camera permission denied");
            }
    );

    private void uploadImage(Uri uri) {
        UploadManager.uploadProfileImage(this, userId, uri, loadingDialog, new ImageUploadHelper.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_profile).into(binding.userProfilePicIV);
                ToastMessage( "ছবি সফলভাবে আপডেট হয়েছে!");
            }

            @Override
            public void onFailure(Exception e) {
                ToastMessage( "ব্যর্থ: " + e.getMessage());
            }
        });
    }

    private void ToastMessage(String message) {
        MyToast.showShort(ProfileActivity.this, message);
    }
}
