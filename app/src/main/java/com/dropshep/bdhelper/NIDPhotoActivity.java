package com.dropshep.bdhelper;

import android.app.Activity;
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
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.databinding.ActivityNidphotoBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.FileUploadHelper;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NIDPhotoActivity extends BaseActivity {

    private ActivityNidphotoBinding binding;
    private static final int IMAGE_PICK_CAMERA_CODE = 1000;
    private static final int IMAGE_PICK_GALLERY_CODE = 1100;

    private boolean isNidFront = true;
    private Uri imageUri, fontImageUrl, backImageUrl;

    private StorageReference storageReference;
    private LoadingDialog loadingDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    String userId;

    String TAG = "NID Service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nidphoto);

        init();
        setupListeners();
    }

    private void init() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        loadCurrentUserDocument(); // 🔁 Force reload every time Activity resumes
    }

    private void setupListeners() {
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        binding.openNidFrontImage.setOnClickListener(v -> {
            isNidFront = true;
            showBottomPopupImagePicker();
        });

        binding.openNidBackImage.setOnClickListener(v -> {
            isNidFront = false;
            showBottomPopupImagePicker();
        });

        binding.saveBtn.setOnClickListener(v -> submitAllPhoto());
    }

    private void loadCurrentUserDocument() {
        db.collection("users")
                .document(userId)
                .collection("Document")
                .document("info")
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String nidFont = documentSnapshot.getString("nidFont");
                        String nidBack = documentSnapshot.getString("nidBack");

                        // 🔹 Front NID load
                        if (nidFont != null && !nidFont.isEmpty()) {
                            Picasso.get()
                                    .load(nidFont)
                                    .placeholder(R.drawable.nid_font)
                                    .error(R.drawable.nid_font)
                                    .into(binding.nidFontImage);
                        } else {
                            binding.nidFontImage.setImageResource(R.drawable.nid_font);
                        }

                        // 🔹 Back NID load
                        if (nidBack != null && !nidBack.isEmpty()) {
                            Picasso.get()
                                    .load(nidBack)
                                    .placeholder(R.drawable.nid_back)
                                    .error(R.drawable.nid_back)
                                    .into(binding.nidBackImage);
                        } else {
                            binding.nidBackImage.setImageResource(R.drawable.nid_back);
                        }
                    }
                });
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
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    startUCrop(imageUri);
                } else {
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
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    String path = FileUploadHelper.getPathFromURI(this, selectedImageUri);
                    if (path != null) selectedImageUri = Uri.fromFile(new File(path));
                    startUCrop(selectedImageUri);
                } else {
                    MyToast.showShort(this,"Image selection cancelled");
                }
            });

    private void startUCrop(Uri sourceUri) {
        if (sourceUri == null) {
            MyToast.showShort(this,"Invalid image source");
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
                .withAspectRatio(340, 210)
                .withMaxResultSize(1080, 1080)
                .withOptions(options);

        cropImageLauncher.launch(uCrop.getIntent(this));
    }

    private final ActivityResultLauncher<Intent> cropImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleCropResult(result.getData());
                } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                    handleCropError(result.getData());
                } else {
                    MyToast.showShort(this,"Image cropping cancelled");
                }
            });

    private void handleCropResult(Intent data) {
        Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
            try {
                Bitmap bitmap = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        ? ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), resultUri))
                        : BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));

                Uri compressedUri = FileUploadHelper.compressImage(this, bitmap);
                Log.d(TAG, "image: "+compressedUri);
                if (isNidFront) {
                    fontImageUrl = compressedUri;
                    Picasso.get().load(fontImageUrl)
                            .placeholder(R.drawable.nid_font)
                            .into(binding.nidFontImage);
                } else {
                    backImageUrl = compressedUri;
                    binding.nidBackImage.setImageURI(backImageUrl);
                }

            } catch (IOException e) {
                e.printStackTrace();
                MyToast.showShort(this,"Failed to load cropped image");
            }
        } else {
            MyToast.showShort(this,"Failed to crop image");
        }
    }

    private void handleCropError(Intent data) {
        if (data != null) {
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                MyToast.showShort(this,"Crop error: " + cropError.getMessage());
            }
        }
    }

    private void submitAllPhoto() {
        if (fontImageUrl == null && backImageUrl == null) {
            MyToast.showShort(this,"এনআইডির ছবি সিলেক্ট করুন!");
            return;
        }

        loadingDialog.setMessage("ছবি আপলোড হচ্ছে...");
        loadingDialog.show();

        List<Task<Uri>> downloadTasks = new ArrayList<>();

        if (fontImageUrl != null) {
            StorageReference fontPath = storageReference.child("NID/").child("nid_font_"+userId+".jpg");
            UploadTask uploadFont = fontPath.putFile(fontImageUrl);
            downloadTasks.add(uploadFont.continueWithTask(task -> fontPath.getDownloadUrl()));
        }

        if (backImageUrl != null) {
            StorageReference backPath = storageReference.child("NID/").child("nid_back_"+userId+".jpg");
            UploadTask uploadBack = backPath.putFile(backImageUrl);
            downloadTasks.add(uploadBack.continueWithTask(task -> backPath.getDownloadUrl()));
        }

        Tasks.whenAllSuccess(downloadTasks)
                .addOnSuccessListener(results -> {
                    //loadingDialog.dismiss();
                    List<String> links = new ArrayList<>();
                    StringBuilder msg = new StringBuilder("আপলোড সম্পন্ন:\n");
                    for (Object link : results) {
                        links.add(link.toString());
                        msg.append(link.toString()).append("\n");
                    }
                    //MyToast.showShort(this,msg.toString());
                    // এখন links.get(0), links.get(1) ব্যবহার করা যাবে
                    // 🔥 Step 2: Save downloadUrl to Firestore
                    Map<String, Object> docLinks  = new HashMap<>();
                    docLinks.put("nidFont", links.get(0));
                    docLinks.put("nidBack", links.get(1));

                    db.collection("users")
                            .document(userId)
                            .collection("Document")
                            .document("info")
                            .set(docLinks, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                loadingDialog.dismiss();
                                loadCurrentUserDocument();
                                Toast.makeText(this, "ছবি আপলোড এবং সংরক্ষণ সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                Toast.makeText(this, "ছবি আপলোড হয়েছে, কিন্তু সংরক্ষণ ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(this,"ছবি আপলোড ব্যর্থ: " + e.getMessage());
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == IMAGE_PICK_GALLERY_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        } else if (requestCode == IMAGE_PICK_CAMERA_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromCamera();
        } else {
            MyToast.showShort(this,"Permission denied");
        }
    }
}
