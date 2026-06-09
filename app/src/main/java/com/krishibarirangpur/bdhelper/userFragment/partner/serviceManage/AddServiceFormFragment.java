package com.krishibarirangpur.bdhelper.userFragment.partner.serviceManage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentAddServiceFormBinding;
import com.krishibarirangpur.bdhelper.utils.firebase.ServiceMapHelper;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.userActivity.partner.ServiceDocumentActivity;
import com.krishibarirangpur.bdhelper.utils.ServiceFormHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AddServiceFormFragment extends Fragment {

    private FragmentAddServiceFormBinding binding;

    private FirebaseUser firebaseUser;
    FirebaseFirestore db;

    private String categoryId;
    private String subCategoryId;
    private String subCategoryName;

    LoadingDialog loadingDialog;
    private ServiceFormHelper formHelper;


    String selectedMetro, selectSerial, serialNumber;
    String equipmentCapability, equipmentCategory, harvesterName, harvesterCategory, metroName,
            serialCategory, serviceSizeAndCapacity, categoryAndYear, carModelNumber, carYear, chargerVanName,
            ambulanceCategory, ambulanceVanType, teamLeaderName, teamMember, workArea, serviceSkill,
            serviceExperience, serviceArea;


    public AddServiceFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bundle থেকে data get করো
        if (getArguments() != null) {
            categoryId = getArguments().getString(MyUtils.categoryId);
            subCategoryId = getArguments().getString(MyUtils.subCategoryId);
            subCategoryName = getArguments().getString(MyUtils.subCategoryName);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_service_form, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Loading Dialog
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        formHelper = new ServiceFormHelper(requireContext(), binding);

        initActivity();


        // যদি backstack এ কিছু থাকে, তাহলে popBackStack করো
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // এখানে আগের fragment এ ফিরে যাবে
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        binding.nextMenuBtn.setOnClickListener(v -> checkAndNextAction());
    }

    @SuppressLint("SetTextI18n")
    private void initActivity() {
        binding.transportNameTv.setText(subCategoryName);
        hideAllLayouts();
        formHelper.showLayoutByCategory(categoryId);
        formHelper.showLayoutBySubCategory(subCategoryId, subCategoryName);
        formHelper.setupSubCategoryUI(subCategoryId);
        initClickListeners();
    }

    private void hideAllLayouts() {
        binding.transportLL.setVisibility(View.GONE);
        binding.lowBedLL.setVisibility(View.GONE);
        binding.equipmentLL.setVisibility(View.GONE);
        binding.harvesterLL.setVisibility(View.GONE);
        binding.truckAndOthersLL.setVisibility(View.GONE);
        binding.carAndMicroLL.setVisibility(View.GONE);
        binding.ambulanceVanLL.setVisibility(View.GONE);
        binding.homeOfficeShiftingLL.setVisibility(View.GONE);
        binding.skilledLaborerLL.setVisibility(View.GONE);
    }
    private void initClickListeners() {
        binding.metroNameEt.setOnClickListener(v -> formHelper.popupMetroList((item, position) -> selectedMetro = item));
        binding.serialCategoryEt.setOnClickListener(v -> formHelper.popupSerialCategory((item, position) -> selectSerial = item));
        binding.serviceSizeAndCapacityEt.setOnClickListener(v -> formHelper.popupServiceSizeAndCapacity(subCategoryName));
        binding.categoryOrYearEt.setOnClickListener(v -> formHelper.popupCategory(binding.categoryOrYearEt, subCategoryName));
        binding.lowBedCategoryEt.setOnClickListener(v -> formHelper.popupCategory(binding.lowBedCategoryEt, subCategoryName));
        binding.lowBedSizeEt.setOnClickListener(v -> formHelper.popupLowBetSizeCategory());
        binding.ambulanceVanTypeEt.setOnClickListener(v -> formHelper.popupChargerVanType(subCategoryId));
        binding.ambulanceCategoryEt.setOnClickListener(v -> formHelper.popupAmbulanceCategory());
        binding.equipmentCapabilityEt.setOnClickListener(v -> formHelper.popupEquipmentCapability(subCategoryId));
        binding.equipmentCategoryEt.setOnClickListener(v -> formHelper.popupEquipmentCategory(subCategoryId));
        binding.harvesterCategoryEt.setOnClickListener(v -> formHelper.popupHarvesterCategory());
    }

    private void checkAndNextAction() {
        // Collect values
        metroName = binding.metroNameEt.getText().toString().trim();
        serialCategory = binding.serialCategoryEt.getText().toString().trim();
        serialNumber = binding.serialNumberEt.getText().toString().trim();

        serviceSizeAndCapacity = binding.serviceSizeAndCapacityEt.getText().toString().trim();
        categoryAndYear = binding.categoryOrYearEt.getText().toString().trim();
        carModelNumber = binding.carModelNumberEt.getText().toString().trim();
        carYear = binding.carYearEt.getText().toString().trim();

        chargerVanName = binding.chargerVanNameEt.getText().toString().trim();
        ambulanceCategory = binding.ambulanceCategoryEt.getText().toString().trim();
        ambulanceVanType = binding.ambulanceVanTypeEt.getText().toString().trim();

        equipmentCapability = binding.equipmentCapabilityEt.getText().toString().trim();
        equipmentCategory = binding.equipmentCategoryEt.getText().toString().trim();

        harvesterName = binding.harvesterNameEt.getText().toString().trim();
        harvesterCategory = binding.harvesterCategoryEt.getText().toString().trim();

        teamLeaderName = binding.teamLeaderNameEt.getText().toString().trim();
        teamMember = binding.teamMemberEt.getText().toString().trim();
        workArea = binding.workAreaEt.getText().toString().trim();

        serviceSkill = binding.serviceSkillEt.getText().toString().trim();
        serviceExperience = binding.serviceExperienceEt.getText().toString().trim();
        serviceArea = binding.serviceAreaEt.getText().toString().trim();

        List<Pair<TextView, String>> requiredFields = new ArrayList<>();

        switch (categoryId) {
            case MyUtils.ROAD_TRANSPORT_ID:
            case MyUtils.RENT_A_CAR_ID:

                switch (subCategoryId) {
                    case MyUtils.SUB_AMBULANCE_ID:
                        requiredFields.add(new Pair<>(binding.metroNameEt, metroName));
                        requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory));
                        requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber));
                        requiredFields.add(new Pair<>(binding.ambulanceCategoryEt, ambulanceCategory));
                        requiredFields.add(new Pair<>(binding.ambulanceVanTypeEt, ambulanceVanType));
                        break;

                    case MyUtils.SUB_CAR_ID:
                    case MyUtils.SUB_MICROBUS_ID:
                        requiredFields.add(new Pair<>(binding.metroNameEt, metroName));
                        requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory));
                        requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber));
                        requiredFields.add(new Pair<>(binding.carModelNumberEt, carModelNumber));
                        requiredFields.add(new Pair<>(binding.carYearEt, carYear));
                        break;

                    case MyUtils.SUB_CHARGER_VAN_ID:
                        requiredFields.add(new Pair<>(binding.chargerVanNameEt, chargerVanName));
                        requiredFields.add(new Pair<>(binding.ambulanceVanTypeEt, ambulanceVanType));
                        break;

                    case MyUtils.SUB_DUMP_TRUCK_ID:
                        requiredFields.add(new Pair<>(binding.metroNameEt, metroName));
                        requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory));
                        requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber));
                        requiredFields.add(new Pair<>(binding.equipmentCapabilityEt, equipmentCapability));
                        requiredFields.add(new Pair<>(binding.equipmentCategoryEt, equipmentCategory));
                        break;

                    case MyUtils.SUB_LOW_BED_ID:
                        serviceSizeAndCapacity = binding.lowBedSizeEt.getText().toString().trim();
                        categoryAndYear = binding.lowBedCategoryEt.getText().toString().trim();
                        requiredFields.add(new Pair<>(binding.metroNameEt, metroName));
                        requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory));
                        requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber));
                        requiredFields.add(new Pair<>(binding.lowBedSizeEt, serviceSizeAndCapacity));
                        requiredFields.add(new Pair<>(binding.lowBedCategoryEt, categoryAndYear));
                        break;

                    default:
                        requiredFields.add(new Pair<>(binding.metroNameEt, metroName));
                        requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory));
                        requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber));
                        requiredFields.add(new Pair<>(binding.serviceSizeAndCapacityEt, serviceSizeAndCapacity));
                        requiredFields.add(new Pair<>(binding.categoryOrYearEt, categoryAndYear));
                        break;
                }

                if (formHelper.validateFields(requiredFields)) {
                    String prefix = selectedMetro + "-" + selectSerial + "-" + serialNumber;
                    switch (subCategoryId) {
                        case MyUtils.SUB_CAR_ID:
                        case MyUtils.SUB_MICROBUS_ID:
                            submitServiceData(prefix, carModelNumber,"", carYear);
                            break;

                        case MyUtils.SUB_AMBULANCE_ID:
                            submitServiceData(prefix, ambulanceCategory,"", ambulanceVanType);
                            break;

                        case MyUtils.SUB_CHARGER_VAN_ID:
                            submitServiceData("", chargerVanName,"", ambulanceVanType);
                            break;

                        case MyUtils.SUB_DUMP_TRUCK_ID:
                            submitServiceData(prefix,"", equipmentCapability, equipmentCategory);
                            break;

                        default:
                            submitServiceData(prefix,"", serviceSizeAndCapacity, categoryAndYear);
                            break;
                    }
                }

                break;
            case MyUtils.EQUIPMENT_ID:
                requiredFields.add(new Pair<>(binding.equipmentCapabilityEt, equipmentCapability));
                requiredFields.add(new Pair<>(binding.equipmentCategoryEt, equipmentCategory));
                if (formHelper.validateFields(requiredFields)) {
                    if (MyUtils.SUB_TRACTOR_ID.equals(subCategoryId)) {
                        submitServiceData("", "", equipmentCategory, equipmentCapability);
                    } else {
                        submitServiceData("", "", equipmentCapability, equipmentCategory);
                    }
                }
                break;
            case MyUtils.HARVESTER_MACHINE_ID:
                requiredFields.add(new Pair<>(binding.harvesterNameEt, harvesterName));
                requiredFields.add(new Pair<>(binding.harvesterCategoryEt, harvesterCategory));
                if (formHelper.validateFields(requiredFields)) {
                    submitServiceData("", harvesterName,"", harvesterCategory);
                }
                break;
            case MyUtils.HOME_SHIFTING_ID:
                requiredFields.add(new Pair<>(binding.teamLeaderNameEt, teamLeaderName));
                requiredFields.add(new Pair<>(binding.teamMemberEt, teamMember));
                requiredFields.add(new Pair<>(binding.workAreaEt, workArea));
                if (formHelper.validateFields(requiredFields)) {
                    submitServiceData(teamLeaderName, "", teamMember, workArea);
                }
                break;
            case MyUtils.SKILLED_LABOR_ID:
                requiredFields.add(new Pair<>(binding.serviceSkillEt, serviceSkill));
                requiredFields.add(new Pair<>(binding.serviceExperienceEt, serviceExperience));
                requiredFields.add(new Pair<>(binding.serviceAreaEt, serviceArea));
                if (formHelper.validateFields(requiredFields)) {
                    submitServiceData(serviceSkill, serviceExperience,"", serviceArea);
                }
                break;
        }

    }

    private void submitServiceData(String serviceRegistrationNumber, String serviceModelType, String serviceSizeAndCapacity, String serviceCategoryAndYear) {
        loadingDialog.setMessage("আপনার সার্ভিস রিকুয়েস্ট সাবমিট হচ্ছে..");
        loadingDialog.show();

        //Timestamp
        String serviceId = "" + System.currentTimeMillis();
        String userId = firebaseUser.getUid();

        // ১. Specs ম্যাপ তৈরি
        Map<String, String> specsMap = ServiceMapHelper.createSpecsMap(serviceModelType, serviceRegistrationNumber, serviceSizeAndCapacity, serviceCategoryAndYear);

        // ২. Media ম্যাপ তৈরি (নতুন সার্ভিসের জন্য খালি)
        Map<String, String> mediaMap = ServiceMapHelper.createMediaMap("", "", "");

        // ৩. সম্পূর্ণ সার্ভিস ম্যাপ তৈরি
        Map<String, Object> serviceMap = ServiceMapHelper.createFullServiceMap(serviceId, categoryId, subCategoryId, subCategoryName, "Inactive", "pending", specsMap, mediaMap);

        db.collection(FirebaseCollectionTable.USERS)
                .document(userId)
                .collection(FirebaseCollectionTable.SERVICES)
                .document(serviceId)
                .set(serviceMap)
                .addOnSuccessListener(unused -> {
                    //
                    loadingDialog.dismiss();
                    //MyToast.showShort(requireContext(), "Request submit");
                    formHelper.clearAllFields();
                    Intent intent = new Intent(requireActivity(), ServiceDocumentActivity.class);
                    intent.putExtra("serviceId", serviceId);
                    intent.putExtra(MyUtils.subCategoryId, subCategoryId);
                    intent.putExtra(MyUtils.subCategoryName, subCategoryName);
                    requireActivity().startActivity(intent);
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                })
                .addOnFailureListener(e -> {
                    //
                    loadingDialog.dismiss();
                    MyToast.showShort(requireContext(), "Failed to save: " + e.getMessage());
                });
    }

}