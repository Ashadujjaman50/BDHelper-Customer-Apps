package com.krishibarirangpur.bdhelper.sharedActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.imagecropprocessor.CropOptions;
import com.ashadujjaman.imagecropprocessor.ImageManager;
import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityNidphotoBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.utils.uploadController.ImageUploadHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NIDPhotoActivity extends BaseActivity {

    private ActivityNidphotoBinding binding;
    private ImageManager imageManager;
    private CropOptions cropOptions;
    private boolean isNidFront = true;
    private Uri fontImageUrl, backImageUrl;

    private StorageReference storageReference;
    private LoadingDialog loadingDialog;
    private FirebaseFirestore db;
    private String userId;
    private final String TAG = "NID_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nidphoto);

        init();
        setupListeners();
        loadCurrentUserDocument();
    }

    private void init() {
        imageManager = new ImageManager(this);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        
        if (firebaseAuth.getCurrentUser() != null) {
            userId = firebaseAuth.getCurrentUser().getUid();
        }


    }

    private void setupListeners() {
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        binding.openNidFrontImage.setOnClickListener(v -> {
            isNidFront = true;
            openImagePicker();
        });

        binding.openNidBackImage.setOnClickListener(v -> {
            isNidFront = false;
            openImagePicker();
        });

        binding.saveBtn.setOnClickListener(v -> submitAllPhoto());
    }


    private void openImagePicker() {
        cropOptions = ImageUploadHelper.getCropOptions(CropOptions.FrameType.RECTANGLE, 340, 210);
        ImageUploadHelper.showImagePickerDialog(this, imageManager, galleryLauncher, cameraLauncher, requestPermissionLauncher);
    }

    private void loadCurrentUserDocument() {
        db.collection(FirebaseCollectionTable.USERS)
                .document(userId)
                .collection(FirebaseCollectionTable.DOCUMENT)
                .document("info")
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String nidFont = documentSnapshot.getString("nidFont");
                        String nidBack = documentSnapshot.getString("nidBack");

                        Picasso.get().load(nidFont)
                                .placeholder(R.drawable.nid_font)
                                .error(R.drawable.nid_font)
                                .into(binding.nidFontImage);

                        Picasso.get().load(nidBack)
                                .placeholder(R.drawable.nid_back)
                                .error(R.drawable.nid_back)
                                .into(binding.nidBackImage);
                    }
                });
    }

    private final ActivityResultLauncher<Intent> cropResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri croppedUri = result.getData().getParcelableExtra("croppedUri");
                    if (croppedUri != null) {
                        if (isNidFront) {
                            fontImageUrl = croppedUri;
                            binding.nidFontImage.setImageURI(fontImageUrl);
                        } else {
                            backImageUrl = croppedUri;
                            binding.nidBackImage.setImageURI(backImageUrl);
                        }
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

    private void submitAllPhoto() {
        if (fontImageUrl == null && backImageUrl == null) {
            ToastMessage( "এনআইডির ছবি সিলেক্ট করুন!");
            return;
        }

        loadingDialog.setMessage("ছবি আপলোড হচ্ছে...");
        loadingDialog.show();

        Map<String, Object> docLinks = new HashMap<>();
        List<Task<Uri>> uploadTasks = new ArrayList<>();

        if (fontImageUrl != null) {
            StorageReference fontPath = storageReference.child("NID/nid_font_" + userId + ".jpg");
            uploadTasks.add(fontPath.putFile(fontImageUrl).continueWithTask(task -> fontPath.getDownloadUrl())
                    .addOnSuccessListener(uri -> docLinks.put("nidFont", uri.toString())));
        }

        if (backImageUrl != null) {
            StorageReference backPath = storageReference.child("NID/nid_back_" + userId + ".jpg");
            uploadTasks.add(backPath.putFile(backImageUrl).continueWithTask(task -> backPath.getDownloadUrl())
                    .addOnSuccessListener(uri -> docLinks.put("nidBack", uri.toString())));
        }

        Tasks.whenAllComplete(uploadTasks)
                .addOnSuccessListener(tasks -> db.collection(FirebaseCollectionTable.USERS)
                        .document(userId)
                        .collection(FirebaseCollectionTable.DOCUMENT)
                        .document("info")
                        .set(docLinks, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            loadingDialog.dismiss();
                            fontImageUrl = null;
                            backImageUrl = null;
                            Toast.makeText(this, "ছবি আপলোড এবং সংরক্ষণ সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            Toast.makeText(this, "সংরক্ষণ ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }))
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    ToastMessage( "ছবি আপলোড ব্যর্থ: " + e.getMessage());
                });
    }


    private void ToastMessage(String message) {
        MyToast.showShort(NIDPhotoActivity.this, message);
    }
}
