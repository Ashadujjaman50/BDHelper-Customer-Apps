package com.krishibarirangpur.bdhelper.userFragment.partner.profileFragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ashadujjaman.imagecropprocessor.CropOptions;
import com.ashadujjaman.imagecropprocessor.ImageManager;
import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentEditPartnerProfileBinding;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.DistrictPickerBottomSheet;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.ValidationClass;
import com.krishibarirangpur.bdhelper.utils.uploadController.ImageUploadHelper;
import com.krishibarirangpur.bdhelper.utils.uploadController.UploadManager;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditPartnerProfileFragment extends Fragment {

    private FragmentEditPartnerProfileBinding binding;
    private ImageManager imageManager;
    private CropOptions cropOptions;
    private FirebaseFirestore db;
    private String userId;
    private LoadingDialog loadingDialog;
    private String selectDistrict;

    public EditPartnerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_partner_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        loadCurrentPartnerInfo();
        setupClickListeners();
    }

    private void initViews() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            userId = firebaseAuth.getCurrentUser().getUid();
        }

        imageManager = new ImageManager(getContext());
        loadingDialog = new LoadingDialog(requireActivity());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        
        cropOptions = ImageUploadHelper.getCropOptions(CropOptions.FrameType.CIRCLE, 1, 1);
    }

    private void setupClickListeners() {
        binding.saveBtn.setOnClickListener(v -> saveUpdatePartnerInfo());
        binding.openGalleryToLoadImage.setOnClickListener(v -> 
                ImageUploadHelper.showImagePickerDialog(requireContext(), imageManager, galleryLauncher, cameraLauncher, requestPermissionLauncher));
        showBottomPopUpDistrictList();
    }

    private void saveUpdatePartnerInfo() {
        String name = binding.userNameEt.getText().toString().trim();
        String mobile = binding.userMobileEt.getText().toString().trim();
        String businessName = binding.businessNameEt.getText().toString().trim();
        String location = binding.userLocationEt.getText().toString().trim();
        String districtInput = binding.userDistrictEt.getText().toString().trim();

        if (!validateFields(name, mobile, businessName, location, districtInput)) return;

        loadingDialog.setMessage("আপনার তথ্য আপডেট হচ্ছে...");
        loadingDialog.show();

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", name);
        updateMap.put("phone", mobile);
        updateMap.put("district", selectDistrict);
        updateMap.put("location", location);
        updateMap.put("businessName", businessName);

        db.collection("users").document(userId)
                .update(updateMap)
                .addOnSuccessListener(unused -> {
                    loadingDialog.dismiss();
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(requireActivity(), "আপডেট ব্যর্থ হয়েছে");
                });
    }

    private boolean validateFields(String name, String mobile, String businessName, String location, String district) {
        if (TextUtils.isEmpty(name)) {
            showError(binding.userNameEt, "আপনার নাম লিখুন");
            return false;
        }
        if (TextUtils.isEmpty(mobile) || mobile.length() < 11) {
            showError(binding.userMobileEt, "আপনার ১১ ডিজিট এর মোবাইল নাম্বার দিন");
            return false;
        }
        if (TextUtils.isEmpty(businessName)) {
            showError(binding.businessNameEt, "আপনার ব্যবসা/পেশার নাম লিখুন");
            return false;
        }
        if (TextUtils.isEmpty(location)) {
            showError(binding.userLocationEt, "আপনার ঠিকানা লিখুন");
            return false;
        }
        if (TextUtils.isEmpty(district)) {
            showError(binding.userDistrictEt, "আপনার জেলা নির্বাচন করুন");
            return false;
        }
        return true;
    }

    private void showError(View view, String message) {
        ValidationClass.setErrorWatcher(view, true);
        MyToast.showShort(requireActivity(), message);
    }

    private void loadCurrentPartnerInfo() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        binding.userNameEt.setText(documentSnapshot.getString("name"));
                        binding.userMobileEt.setText(documentSnapshot.getString("phone"));
                        binding.userLocationEt.setText(documentSnapshot.getString("location"));
                        binding.businessNameEt.setText(documentSnapshot.getString("businessName"));

                        String email = documentSnapshot.getString("email");
                        binding.userEmailEt.setText(email);
                        binding.userEmailEt.setEnabled(false);

                        selectDistrict = documentSnapshot.getString("district");
                        String shownDistrict = Replacement.getLocalizedDistrict(getActivity(), selectDistrict);
                        binding.userDistrictEt.setText(shownDistrict);
                    }
                });

        db.collection("users").document(userId).collection("Document").document("info")
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profileImage = documentSnapshot.getString("profileImage");
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_profile).into(binding.userProfilePicIV);
                    }
                });
    }

    private void showBottomPopUpDistrictList() {
        binding.userDistrictEt.setOnClickListener(v -> {
            boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");
            String[] displayList = isBangla ? MyUtils.DISTRICT_BAN : MyUtils.DISTRICT_ENG;

            DistrictPickerBottomSheet.show(
                    requireActivity(),
                    "Select District",
                    Arrays.asList(displayList),
                    (item, position) -> {
                        binding.userDistrictEt.setText(item);
                        selectDistrict = isBangla ? MyUtils.DISTRICT_ENG[position] : item;
                    }
            );
        });
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
                    MyToast.showShort(requireActivity(), "Crop cancelled");
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
                else MyToast.showShort(requireActivity(), "Camera permission denied");
            }
    );

    private void uploadImage(Uri uri) {
        UploadManager.uploadProfileImage(requireContext(), userId, uri, loadingDialog, new ImageUploadHelper.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_profile).into(binding.userProfilePicIV);
                Toast.makeText(requireContext(), "ছবি আপলোড সফল হয়েছে!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "ব্যর্থ: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
