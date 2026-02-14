package com.krishibarirangpur.bdhelper.partnerFragment;

import android.Manifest;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.DistrictAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentEditPartnerProfileBinding;
import com.krishibarirangpur.bdhelper.utils.FileUploadHelper;
import com.krishibarirangpur.bdhelper.utils.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.MyToast;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class EditPartnerProfileFragment extends Fragment {

    private FragmentEditPartnerProfileBinding binding;

    private static final int IMAGE_PICK_CAMERA_CODE = 1000;
    private static final int IMAGE_PICK_GALLERY_CODE = 1100;

    // Arrays of permissions
    private String[] cameraPermissions;
    private String[] storagePermissions;

    // Variables for image
    private Uri imageUri = null, finalImageUrl = null;
    private StorageReference storageReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    String userId;

    LoadingDialog loadingDialog;

    String selectDistrict;

    public EditPartnerProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_partner_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        userId = firebaseAuth.getCurrentUser().getUid();

        // Init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        loadingDialog = new LoadingDialog(requireActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        //Show Bottom Popup Menu With District List
        showBottomPopUpDistrictList();

        loadCurrentPartnerInfo();

        binding.saveBtn.setOnClickListener(v -> {
            saveUpdatePartnerInfo();
        });

        binding.openGalleryToLoadImage.setOnClickListener(v -> {
            //showImagePickerDialog
            showBottomPopupImagePicker();
        });

    }

    private void saveUpdatePartnerInfo() {
        String name = binding.userNameEt.getText().toString().trim();
        String mobile = binding.userMobileEt.getText().toString().trim();
        String businessName = binding.businessNameEt.getText().toString().trim();
        String location = binding.userLocationEt.getText().toString().trim();
        selectDistrict = binding.userDistrictEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            setErrorWatcher(binding.userNameEt, true);
            Toast.makeText(requireActivity(), "আপনার নাম লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mobile) ||  mobile.length() < 11) {
            setErrorWatcher(binding.userMobileEt, true);
            Toast.makeText(requireActivity(), "আপনার ১১ ডিজিট এর মোবাইল নাম্বার দিন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(businessName)) {
            setErrorWatcher(binding.businessNameEt, true);
            Toast.makeText(requireActivity(), "আপনার ব্যবসা/পেশার নাম লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(location)) {
            setErrorWatcher(binding.userLocationEt, true);
            Toast.makeText(requireActivity(), "আপনার ঠিকানা লিখুন", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectDistrict)) {
            binding.userDistrictEt.setBackgroundResource(R.drawable.bg_edit_text_error);
            Toast.makeText(requireActivity(), "আপনার জেলা নির্বাচন করুন", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingDialog.setMessage("আপনার তথ্য আপডেট হচ্ছে...");
            loadingDialog.show();

            //String to set Time stamp
            Map<String, Object> updateMap  = new HashMap<>();
            //.put("userId", userId);
            //updateMap .put("userType", "partner"); // "customer" or "partner"
            updateMap .put("name", name);
            //updateMap .put("email", email);
            updateMap .put("phone", mobile);
            //updateMap .put("nidNo", "");
            //updateMap .put("nidVerify", "false");
            //updateMap .put("userDob", "");
            updateMap .put("district", selectDistrict);
            updateMap .put("location", location);
            updateMap .put("businessName", businessName);  // only for partner
            //updateMap .put("verifyStatus", "pending");     // only for partner
            //updateMap .put("rentService", "");             // only for partner
            //updateMap .put("device_token", device_token);
            //updateMap .put("userSignWith", userSignWith);
            //updateMap .put("userJoinTime", timestamp);
            //updateMap .put("userLastLogin", timestamp);
            //updateMap .put("rating", rating);

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .update(updateMap )
                    .addOnSuccessListener(unused -> {
                        // Success
                        loadingDialog.dismiss();
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        // Error
                        loadingDialog.dismiss();
                    });

        }
    }


    private void setErrorWatcher(EditText editText, boolean hasError) {
        if (hasError) {
            editText.setBackgroundResource(R.drawable.bg_edit_text_error);
            editText.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    editText.setBackgroundResource(R.drawable.bg_edit_text);
                }
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }
    private void loadCurrentPartnerInfo() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        String location = documentSnapshot.getString("location");
                        String businessName = documentSnapshot.getString("businessName");
                        selectDistrict = documentSnapshot.getString("district"); // Always English

                        binding.userNameEt.setText(name);
                        binding.userMobileEt.setText(phone);
                        binding.userLocationEt.setText(location);
                        binding.businessNameEt.setText(businessName);

                        binding.userEmailEt.setText(email);
                        binding.userEmailEt.setFocusable(false);
                        binding.userEmailEt.setEnabled(false);

                        // 🔁 ভাষা চেক করে District নাম বাংলা দেখাও
                        String shownDistrict = Replacement.getLocalizedDistrict(getActivity(), selectDistrict);
                        binding.userDistrictEt.setText(shownDistrict);
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

    private void showBottomPopUpDistrictList() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_recycleview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        titleTv.setText("Select District");

        // ভাষা অনুযায়ী display list, কিন্তু ইংরেজি লিস্ট সবসময় দরকার হবে Database-এর জন্য
        String[] districtListEng = MyUtils.DISTRICT_ENG;
        String[] districtListBan = MyUtils.DISTRICT_BAN;

        // কোন ভাষা চালু আছে
        boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");

        // UI-তে যেটা দেখাবে
        String[] displayList = isBangla ? districtListBan : districtListEng;


        // Adapter
        DistrictAdapter adapter = new DistrictAdapter(Arrays.asList(displayList), (item, position) -> {
            binding.userDistrictEt.setText(item); // UI text (localised)
            selectDistrict = isBangla ? districtListEng[position] : item; // DB-র জন্য always ইংরেজি
            bottomSheetDialog.dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);

        // Important: modify bottom-sheet AFTER it is shown -> use onShowListener
        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                int heightPx = dpToPx(400); // fixed 400dp height

                // Force the bottomSheet container height to 400dp
                bottomSheet.getLayoutParams().height = heightPx;
                bottomSheet.requestLayout();

                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(heightPx);           // peek at 400dp
                behavior.setDraggable(true);               // allow dragging / scrolling
                behavior.setHideable(true);                // optional
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED); // show at peekHeight
            }
        });

        binding.userDistrictEt.setOnClickListener(v -> bottomSheetDialog.show());
    }

    // helper
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void showBottomPopupImagePicker() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_image_picker);
        bottomSheetDialog.show();

        LinearLayout cameraBtn = bottomSheetDialog.findViewById(R.id.cameraBtn);
        LinearLayout galleryBtn = bottomSheetDialog.findViewById(R.id.galleryBtn);


        if (cameraBtn != null){
            cameraBtn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                if (FileUploadHelper.checkAndRequestCameraPermissions(requireActivity(), IMAGE_PICK_CAMERA_CODE)) {
                    pickImageFromCamera();
                }
            });
        }
        if (galleryBtn != null){
            galleryBtn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                if (FileUploadHelper.checkAndRequestStoragePermission(requireActivity(), IMAGE_PICK_GALLERY_CODE)) {
                    pickImageFromGallery();
                }
            });
        }

    }

    private void pickImageFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        imageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        launcher_for_camera.launch(cameraIntent);
    }

    ActivityResultLauncher<Intent> launcher_for_camera =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            startUCrop(imageUri);
                        } else {
                            Toast.makeText(requireContext(), "Camera capture cancelled", Toast.LENGTH_SHORT).show();
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
                                String path = FileUploadHelper.getPathFromURI(requireContext(), selectedImageUri);
                                if (path != null) {
                                    File f = new File(path);
                                    selectedImageUri = Uri.fromFile(f);
                                }
                                startUCrop(selectedImageUri);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Image selection cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

    private void startUCrop(Uri sourceUri) {
        if (sourceUri == null) {
            Toast.makeText(requireContext(), "Invalid image source", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "croppedImage.jpg"));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.gray));
        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.gray_mid));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(requireContext(), R.color.gray));

        UCrop uCrop = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1080, 1080)
                .withOptions(options);
        cropImageLauncher.launch(uCrop.getIntent(requireContext()));
    }

    private final ActivityResultLauncher<Intent> cropImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            handleCropResult(result.getData());
                        } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                            handleCropError(result.getData());
                        } else {
                            Toast.makeText(requireContext(), "Image cropping cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

    private void handleCropResult(Intent data) {
        Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null) {
            try {
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), resultUri));
                } else {
                    try (InputStream inputStream = requireContext().getContentResolver().openInputStream(resultUri)) {
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    }
                }
                finalImageUrl = FileUploadHelper.compressImage(requireContext(), bitmap);
                binding.userProfilePicIV.setImageURI(finalImageUrl);
                uploadProfileImageToServer();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to load cropped image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Failed to crop image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropError(Intent data) {
        if (data != null) {
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(requireContext(), "Crop error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "Storage permission is required", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == IMAGE_PICK_CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromCamera();
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfileImageToServer() {
        if (finalImageUrl == null) {
            MyToast.showShort(requireContext(), "প্রোফাইলের ছবি সিলেক্ট করুন !");
        } else {
            loadingDialog.setMessage("ছবি আপলোড হচ্ছে...");
            loadingDialog.show();

            if (binding.userProfilePicIV.getDrawable() != null && finalImageUrl != null) {
                StorageReference profilePicPath = storageReference.child("Profile/").child("profile_" + userId + ".jpg");
                UploadTask uploadProfileTask = profilePicPath.putFile(finalImageUrl);

                uploadProfileTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return profilePicPath.getDownloadUrl();
                }).addOnSuccessListener(downloadUri -> {
                    String downloadUrl = downloadUri.toString();

                    // 🔥 Step 2: Save downloadUrl to Firestore
                    Map<String, Object> docLinks = new HashMap<>();
                    docLinks.put("profileImage", downloadUrl);

                    db.collection("users")
                            .document(userId)
                            .collection("Document")
                            .document("info")
                            .set(docLinks, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                loadingDialog.dismiss();
                                Toast.makeText(requireContext(), "ছবি আপলোড এবং সংরক্ষণ সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                Toast.makeText(requireContext(), "ছবি আপলোড হয়েছে, কিন্তু সংরক্ষণ ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                }).addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(requireContext(), "ছবি আপলোড ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }
    }



}