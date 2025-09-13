package com.dropshep.bdhelper;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.dropshep.bdhelper.databinding.ActivityProfileBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.FileUploadHelper;
import com.dropshep.bdhelper.myUtils.Replacement;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;


    private static final int IMAGE_PICK_CAMERA_CODE = 1000;
    private static final int IMAGE_PICK_GALLERY_CODE = 1100;

    // Arrays of permissions
    private String[] cameraPermissions;
    private String[] storagePermissions;

    // Variables for image
    private Uri imageUri = null, finalImageUrl = null;

    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট করব
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        //init views
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        // Init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        /// init
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        //Load Database to Current User Info
        loadCurrentUserInfo();


        binding.documentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NIDPhotoActivity.class);
            intent.putExtra("user_type", "customer");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("user_type", "customer");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.openGalleryToLoadImage.setOnClickListener(v -> {
            //showImagePickerDialog
            showBottomPopupImagePicker();
        });


        binding.backBtn.setOnClickListener(v -> finishOnBack());

    }



    private void loadCurrentUserInfo() {
        // 🔹 Load main user document
        db.collection("users")
                .document(userId)
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        String location = documentSnapshot.getString("location");
                        String district = documentSnapshot.getString("district"); // Always English

                        binding.userNameTV.setText(name);
                        binding.userMobileTv.setText(phone);
                        binding.userLocationTv.setText(location);
                        binding.userEmailTv.setText(email);

                        // 🔁 ভাষা চেক করে District নাম বাংলা দেখাও
                        String shownDistrict = Replacement.getLocalizedDistrict(this, district);
                        binding.userDistrictTv.setText(shownDistrict);
                    }
                })
                .addOnFailureListener(e -> {
                    // Error handling
                });

        // 🔹 Load profile image
        db.collection("users")
                .document(userId)
                .collection("Document")
                .document("info")
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImage = documentSnapshot.getString("profileImage");

                        // Use these URLs as needed
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_profile).into(binding.userProfilePicIV);
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentUserInfo(); // 🔁 Force reload every time Activity resumes
    }

    private void showBottomPopupImagePicker() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_image_picker);
        bottomSheetDialog.show();

        LinearLayout cameraBtn = bottomSheetDialog.findViewById(R.id.cameraBtn);
        LinearLayout galleryBtn = bottomSheetDialog.findViewById(R.id.galleryBtn);


        if (cameraBtn != null){
            cameraBtn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                if (FileUploadHelper.checkAndRequestCameraPermissions(this, IMAGE_PICK_CAMERA_CODE)) {
                    pickImageFromCamera();
                }
            });
        }
        if (galleryBtn != null){
            galleryBtn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                if (FileUploadHelper.checkAndRequestStoragePermission(this, IMAGE_PICK_GALLERY_CODE)) {
                    pickImageFromGallery();
                }
            });
        }

    }

    private void pickImageFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        launcher_for_camera.launch(cameraIntent);
    }

    ActivityResultLauncher<Intent> launcher_for_camera =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            startUCrop(imageUri);
                        } else {
                            Toast.makeText(this, "Camera capture cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickImageActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                String path = FileUploadHelper.getPathFromURI(this,selectedImageUri);
                                if (path != null) {
                                    File f = new File(path);
                                    //uploadImage(f);
                                    selectedImageUri = Uri.fromFile(f);
                                }
                                startUCrop(selectedImageUri);

                            }
                        } else {
                            Toast.makeText(this, "Image selection cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

    private void startUCrop(Uri sourceUri) {
        if (sourceUri == null) {
            Toast.makeText(this, "Invalid image source", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "croppedImage.jpg"));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.gray));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.gray_mid));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.gray));

        UCrop uCrop = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1080, 1080)
                .withOptions(options);
        cropImageLauncher.launch(uCrop.getIntent(this));
    }

    private final ActivityResultLauncher<Intent> cropImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            handleCropResult(result.getData());
                        } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                            handleCropError(result.getData());
                        } else {
                            Toast.makeText(this, "Image cropping cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

    private void handleCropResult(Intent data) {
        Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
            try {
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), resultUri));
                } else {
                    try (InputStream inputStream = this.getContentResolver().openInputStream(resultUri)) {
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    }
                }
                finalImageUrl = FileUploadHelper.compressImage(this,bitmap);
                binding.userProfilePicIV.setImageURI(finalImageUrl);
                uploadProfileImageToServer();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load cropped image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleCropError(Intent data) {
        if (data != null) {
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, "Crop error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == IMAGE_PICK_CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadProfileImageToServer() {
        if (finalImageUrl == null) {
            Toast.makeText(this, "প্রোফাইলের ছবি সিলেক্ট করুন !", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("ছবি আপলোড হচ্ছে...");
            progressDialog.show();

            if (binding.userProfilePicIV.getDrawable() != null && finalImageUrl != null) {
                StorageReference profilePicPath = storageReference.child("Profile/").child("profile_" + userId + ".jpg");
                UploadTask uploadProfileTask = profilePicPath.putFile(finalImageUrl);

                uploadProfileTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return profilePicPath.getDownloadUrl();
                }).addOnSuccessListener(downloadUri -> {
                    String downloadUrl = downloadUri.toString();

                    // 🔥 Step 2: Save downloadUrl to Firestore
                    Map<String, Object> docLinks  = new HashMap<>();
                    docLinks.put("profileImage", downloadUrl);

                    db.collection("users")
                            .document(userId)
                            .collection("Document")
                            .document("info")
                            .set(docLinks, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "ছবি আপলোড এবং সংরক্ষণ সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "ছবি আপলোড হয়েছে, কিন্তু সংরক্ষণ ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "ছবি আপলোড ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }
    }



}