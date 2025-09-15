package com.dropshep.bdhelper.partnerFragment;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentEditPartnerProfileBinding;
import com.dropshep.bdhelper.myUtils.FileUploadHelper;
import com.dropshep.bdhelper.myUtils.LocaleHelper;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.Replacement;
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

    private ProgressDialog progressDialog;
    String selectedDistrict;

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


        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setCanceledOnTouchOutside(false);

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
        selectedDistrict = binding.userDistrictEt.getText().toString().trim();

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
        else if (TextUtils.isEmpty(selectedDistrict)) {
            binding.userDistrictEt.setBackgroundResource(R.drawable.bg_edit_text_error);
            Toast.makeText(requireActivity(), "আপনার জেলা নির্বাচন করুন", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setMessage("আপনার তথ্য আপডেট হচ্ছে...");
            progressDialog.show();

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
            updateMap .put("district", selectedDistrict);
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
                        progressDialog.dismiss();
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        // Error
                        progressDialog.dismiss();
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
                        selectedDistrict = documentSnapshot.getString("district"); // Always English

                        binding.userNameEt.setText(name);
                        binding.userMobileEt.setText(phone);
                        binding.userLocationEt.setText(location);
                        binding.businessNameEt.setText(businessName);

                        binding.userEmailEt.setText(email);
                        binding.userEmailEt.setFocusable(false);
                        binding.userEmailEt.setEnabled(false);

                        // 🔁 ভাষা চেক করে District নাম বাংলা দেখাও
                        String shownDistrict = Replacement.getLocalizedDistrict(requireActivity(), selectedDistrict);
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
                .inflate(R.layout.bottom_sheet_dialog_listview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        ListView listView = view.findViewById(R.id.listView);

        titleTv.setText("Select District");

        // ভাষা অনুযায়ী display list, কিন্তু ইংরেজি লিস্ট সবসময় দরকার হবে Database-এর জন্য
        String[] districtListEng = MyUtils.DISTRICT_ENG;
        String[] districtListBan = MyUtils.DISTRICT_BAN;

        // কোন ভাষা চালু আছে
        boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");

        // UI-তে যেটা দেখাবে
        String[] displayList = isBangla ? districtListBan : districtListEng;

        listView.setAdapter(new ArrayAdapter<>(
                requireActivity(),
                R.layout.single_listview_item,
                R.id.listItem,
                displayList
        ));

        // ✅ BottomSheet fixed height + prevent dismiss on swipe
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight((int) (400 * getResources().getDisplayMetrics().density));
                behavior.setDraggable(false);
            }
        });


        // ✅ Select করলে Toast করবে ইংরেজি নাম দিয়ে
        binding.userDistrictEt.setOnClickListener(v -> {
            bottomSheetDialog.show();

            listView.setOnItemClickListener((parent, view1, position, id) -> {
                binding.userDistrictEt.setText(displayList[position]); // UI-তে যা দেখা যাচ্ছে সেটাই রাখো

                selectedDistrict = isBangla ? districtListEng[position] : displayList[position];

                bottomSheetDialog.dismiss();
            });
        });
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
            Toast.makeText(requireContext(), "প্রোফাইলের ছবি সিলেক্ট করুন !", Toast.LENGTH_SHORT).show();
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
                    Map<String, Object> docLinks = new HashMap<>();
                    docLinks.put("profileImage", downloadUrl);

                    db.collection("users")
                            .document(userId)
                            .collection("Document")
                            .document("info")
                            .set(docLinks, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(requireContext(), "ছবি আপলোড এবং সংরক্ষণ সফল হয়েছে!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(requireContext(), "ছবি আপলোড হয়েছে, কিন্তু সংরক্ষণ ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "ছবি আপলোড ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }
    }



}