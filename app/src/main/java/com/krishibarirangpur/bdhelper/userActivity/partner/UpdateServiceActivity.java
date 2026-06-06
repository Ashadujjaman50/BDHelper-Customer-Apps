package com.krishibarirangpur.bdhelper.userActivity.partner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentAddServiceFormBinding;
import com.krishibarirangpur.bdhelper.model.ServiceModel;
import com.krishibarirangpur.bdhelper.utils.ServiceFormHelper;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateServiceActivity extends BaseActivity {

    private FragmentAddServiceFormBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private String serviceId, categoryId, subCategoryId, subCategoryName;
    private LoadingDialog loadingDialog;
    ServiceModel serviceModel;
    private ServiceFormHelper formHelper;

    String selectedMetro, selectSerial, serialNumber;
    String equipmentCapability, equipmentCategory, harvesterName, harvesterCategory, metroName,
            serialCategory, serviceSizeAndCapacity, categoryAndYear, carModelNumber, carYear, chargerVanName,
            ambulanceCategory, ambulanceVanType, teamLeaderName, teamMember, workArea, serviceSkill,
            serviceExperience, serviceArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_add_service_form);

        if (getIntent() != null) {
            serviceId = getIntent().getStringExtra("serviceId");
            categoryId = getIntent().getStringExtra(MyUtils.categoryId);
            subCategoryId = getIntent().getStringExtra(MyUtils.subCategoryId);
            subCategoryName = getIntent().getStringExtra(MyUtils.subCategoryName);
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        formHelper = new ServiceFormHelper(this, binding);

        binding.nextMenuBtn.setText("Update Service");

        initActivity();

        loadExistingData();

        //binding.backBtn.setOnClickListener(v -> finish());
        binding.nextMenuBtn.setOnClickListener(v -> checkAndNextAction());
    }

    private void loadExistingData() {
        if (serviceId == null) return;
        loadingDialog.setMessage("ডেটা লোড হচ্ছে...");
        loadingDialog.show();

        db.collection(FirebaseCollectionTable.USERS).document(firebaseUser.getUid())
                .collection(FirebaseCollectionTable.SERVICES).document(serviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    loadingDialog.dismiss();
                    if (documentSnapshot.exists()) {
                        serviceModel = documentSnapshot.toObject(ServiceModel.class);
                        if (serviceModel != null) {
                            categoryId = serviceModel.getCategoryId();
                            subCategoryId = serviceModel.getSubCategoryId();
                            subCategoryName = serviceModel.getSubCategoryName();

                            initActivity();
                            populateFields(serviceModel);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(UpdateServiceActivity.this, "Error: " + e.getMessage());
                });
    }

    private void populateFields(ServiceModel serviceModel) {
        String regNo = serviceModel.getSafeRegistrationNumber();
        String brandModel = serviceModel.getSafeBrandOrModel();
        String sizeCap = serviceModel.getSafeSizeAndCapacity();
        String mfgYear = serviceModel.getSafeManufacturingYear();

        if (regNo != null && regNo.contains("-")) {
            String[] parts = regNo.split("-");
            if (parts.length == 3) {
                selectedMetro = parts[0]; selectSerial = parts[1]; serialNumber = parts[2];
                boolean isBan = LocaleHelper.getLanguage(this).equals("bn");
                int mIdx = Arrays.asList(MyUtils.METRO_LIST_ENG).indexOf(selectedMetro);
                int sIdx = Arrays.asList(MyUtils.SERIAL_ENG).indexOf(selectSerial);
                binding.metroNameEt.setText(mIdx != -1 && isBan ? MyUtils.METRO_LIST_BAN[mIdx] : selectedMetro);
                binding.serialCategoryEt.setText(sIdx != -1 && isBan ? MyUtils.SERIAL_BAN[sIdx] : selectSerial);
                binding.serialNumberEt.setText(serialNumber);
            }
        }

        switch (categoryId) {
            case MyUtils.ROAD_TRANSPORT_ID:
            case MyUtils.RENT_A_CAR_ID:
                switch (subCategoryId) {
                    case MyUtils.SUB_CAR_ID:
                    case MyUtils.SUB_MICROBUS_ID: binding.carModelNumberEt.setText(brandModel); binding.carYearEt.setText(mfgYear); break;
                    case MyUtils.SUB_AMBULANCE_ID: binding.ambulanceCategoryEt.setText(brandModel); binding.ambulanceVanTypeEt.setText(mfgYear); break;
                    case MyUtils.SUB_CHARGER_VAN_ID: binding.chargerVanNameEt.setText(brandModel); binding.ambulanceVanTypeEt.setText(mfgYear); break;
                    case MyUtils.SUB_DUMP_TRUCK_ID: binding.equipmentCapabilityEt.setText(sizeCap); binding.equipmentCategoryEt.setText(mfgYear); break;
                    case MyUtils.SUB_LOW_BED_ID: binding.lowBedSizeEt.setText(sizeCap); binding.lowBedCategoryEt.setText(mfgYear); break;
                    default: binding.serviceSizeAndCapacityEt.setText(sizeCap); binding.categoryOrYearEt.setText(mfgYear); break;
                }
                break;
            case MyUtils.EQUIPMENT_ID:
                if (MyUtils.SUB_TRACTOR_ID.equals(subCategoryId)) {
                    binding.equipmentCapabilityEt.setText(mfgYear);
                    binding.equipmentCategoryEt.setText(sizeCap);
                } else {
                    binding.equipmentCapabilityEt.setText(sizeCap);
                    binding.equipmentCategoryEt.setText(mfgYear);
                }
                break;
            case MyUtils.HARVESTER_MACHINE_ID: binding.harvesterNameEt.setText(brandModel); binding.harvesterCategoryEt.setText(sizeCap); break;
            case MyUtils.HOME_SHIFTING_ID: binding.teamLeaderNameEt.setText(regNo); binding.teamMemberEt.setText(sizeCap); binding.workAreaEt.setText(mfgYear); break;
            case MyUtils.SKILLED_LABOR_ID: binding.serviceSkillEt.setText(regNo); binding.serviceExperienceEt.setText(sizeCap); binding.serviceAreaEt.setText(mfgYear); break;
        }
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
        if (categoryId == null) return;

        switch (categoryId) {
            case MyUtils.ROAD_TRANSPORT_ID:
            case MyUtils.RENT_A_CAR_ID:
                switch (subCategoryId) {
                    case MyUtils.SUB_AMBULANCE_ID: requiredFields.add(new Pair<>(binding.metroNameEt, metroName)); requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory)); requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber)); requiredFields.add(new Pair<>(binding.ambulanceCategoryEt, ambulanceCategory)); requiredFields.add(new Pair<>(binding.ambulanceVanTypeEt, ambulanceVanType)); break;
                    case MyUtils.SUB_CAR_ID:
                    case MyUtils.SUB_MICROBUS_ID: requiredFields.add(new Pair<>(binding.metroNameEt, metroName)); requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory)); requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber)); requiredFields.add(new Pair<>(binding.carModelNumberEt, carModelNumber)); requiredFields.add(new Pair<>(binding.carYearEt, carYear)); break;
                    case MyUtils.SUB_CHARGER_VAN_ID: requiredFields.add(new Pair<>(binding.chargerVanNameEt, chargerVanName)); requiredFields.add(new Pair<>(binding.ambulanceVanTypeEt, ambulanceVanType)); break;
                    case MyUtils.SUB_DUMP_TRUCK_ID: requiredFields.add(new Pair<>(binding.metroNameEt, metroName)); requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory)); requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber)); requiredFields.add(new Pair<>(binding.equipmentCapabilityEt, equipmentCapability)); requiredFields.add(new Pair<>(binding.equipmentCategoryEt, equipmentCategory)); break;
                    case MyUtils.SUB_LOW_BED_ID: serviceSizeAndCapacity = binding.lowBedSizeEt.getText().toString().trim(); categoryAndYear = binding.lowBedCategoryEt.getText().toString().trim(); requiredFields.add(new Pair<>(binding.metroNameEt, metroName)); requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory)); requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber)); requiredFields.add(new Pair<>(binding.lowBedSizeEt, serviceSizeAndCapacity)); requiredFields.add(new Pair<>(binding.lowBedCategoryEt, categoryAndYear)); break;
                    default: requiredFields.add(new Pair<>(binding.metroNameEt, metroName)); requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory)); requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber)); requiredFields.add(new Pair<>(binding.serviceSizeAndCapacityEt, serviceSizeAndCapacity)); requiredFields.add(new Pair<>(binding.categoryOrYearEt, categoryAndYear)); break;
                }
                if (formHelper.validateFields(requiredFields)) {
                    String prefix = (selectedMetro != null ? selectedMetro : "") + "-" + (selectSerial != null ? selectSerial : "") + "-" + serialNumber;
                    switch (subCategoryId) {
                        case MyUtils.SUB_CAR_ID:
                        case MyUtils.SUB_MICROBUS_ID: updateServiceData(prefix, carModelNumber,"", carYear); break;
                        case MyUtils.SUB_AMBULANCE_ID: updateServiceData(prefix, ambulanceCategory,"", ambulanceVanType); break;
                        case MyUtils.SUB_CHARGER_VAN_ID: updateServiceData("", chargerVanName,"", ambulanceVanType); break;
                        case MyUtils.SUB_DUMP_TRUCK_ID: updateServiceData(prefix,"", equipmentCapability, equipmentCategory); break;
                        case MyUtils.SUB_TRACTOR_ID: updateServiceData(prefix,"", serviceSizeAndCapacity, categoryAndYear); break;
                        default: updateServiceData(prefix,"", serviceSizeAndCapacity, categoryAndYear); break;
                    }
                }
                break;
            case MyUtils.EQUIPMENT_ID:
                requiredFields.add(new Pair<>(binding.equipmentCapabilityEt, equipmentCapability));
                requiredFields.add(new Pair<>(binding.equipmentCategoryEt, equipmentCategory));
                if (formHelper.validateFields(requiredFields)) {
                    if (MyUtils.SUB_TRACTOR_ID.equals(subCategoryId)) {
                        updateServiceData("", "", equipmentCategory, equipmentCapability);
                    } else {
                        updateServiceData("", "", equipmentCapability, equipmentCategory);
                    }
                }
                break;
            case MyUtils.HARVESTER_MACHINE_ID: requiredFields.add(new Pair<>(binding.harvesterNameEt, harvesterName)); requiredFields.add(new Pair<>(binding.harvesterCategoryEt, harvesterCategory)); if (formHelper.validateFields(requiredFields)) updateServiceData("", harvesterName, harvesterCategory,""); break;
            case MyUtils.HOME_SHIFTING_ID: requiredFields.add(new Pair<>(binding.teamLeaderNameEt, teamLeaderName)); requiredFields.add(new Pair<>(binding.teamMemberEt, teamMember)); requiredFields.add(new Pair<>(binding.workAreaEt, workArea)); if (formHelper.validateFields(requiredFields)) updateServiceData(teamLeaderName, "", teamMember, workArea); break;
            case MyUtils.SKILLED_LABOR_ID: requiredFields.add(new Pair<>(binding.serviceSkillEt, serviceSkill)); requiredFields.add(new Pair<>(binding.serviceExperienceEt, serviceExperience)); requiredFields.add(new Pair<>(binding.serviceAreaEt, serviceArea)); if (formHelper.validateFields(requiredFields)) updateServiceData(serviceSkill, "", serviceExperience, serviceArea); break;
        }
    }

    private void updateServiceData(String reg, String model, String size, String year) {
        loadingDialog.setMessage("সার্ভিস আপডেট হচ্ছে..");
        loadingDialog.show();

        long idLong = 0;
        try {
            idLong = Long.parseLong(serviceId);
        } catch (Exception ignored) {}

        // User specific cutoff: 06 June 2026 (Assuming 1780694400000L)
        long cutoff = 1780694400000L; // Setting to June 6, 2024 as it's more logical for migration

        if (idLong > 0 && idLong < cutoff) {
            // Migration logic: Re-structure the whole document like submitServiceData()
            Map<String, Object> serviceMap = new HashMap<>();
            serviceMap.put("serviceId", serviceId);
            serviceMap.put("categoryId", categoryId);
            serviceMap.put("subCategoryId", subCategoryId);
            serviceMap.put("subCategoryName", subCategoryName);

            // Preserve status and verification
            serviceMap.put("serviceStatus", serviceModel != null ? serviceModel.getServiceStatus() : "Inactive");
            serviceMap.put("serviceVerified", serviceModel != null ? serviceModel.getServiceVerified() : "pending");

            Map<String, String> specsMap = new HashMap<>();
            specsMap.put("brandOrModel", model);
            specsMap.put("registrationNumber", reg);
            specsMap.put("sizeAndCapacity", size);
            specsMap.put("categoryAndYear", year);

            serviceMap.put("specs", specsMap);

            Map<String, String> mediaMap = new HashMap<>();
            mediaMap.put("transportImage", serviceModel != null ? serviceModel.getSafeTransportImage() : "");
            mediaMap.put("brtaImage", serviceModel != null ? serviceModel.getSafeBrtaImage() : "");
            mediaMap.put("driverLicence", serviceModel != null ? serviceModel.getSafeDriverLicence() : "");

            serviceMap.put("media", mediaMap);

            db.collection(FirebaseCollectionTable.USERS).document(firebaseUser.getUid())
                    .collection(FirebaseCollectionTable.SERVICES).document(serviceId)
                    .set(serviceMap)
                    .addOnSuccessListener(unused -> {
                        loadingDialog.dismiss();
                        MyToast.showShort(UpdateServiceActivity.this, "Updated and Migrated Successfully!");
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        MyToast.showShort(UpdateServiceActivity.this, "Failed: " + e.getMessage());
                    });
        }
        else {
            // Standard update for newer documents
            Map<String, String> specs = new HashMap<>();
            specs.put("brandOrModel", model);
            specs.put("registrationNumber", reg);
            specs.put("sizeAndCapacity", size);
            specs.put("categoryAndYear", year);

            db.collection(FirebaseCollectionTable.USERS).document(firebaseUser.getUid())
                    .collection(FirebaseCollectionTable.SERVICES).document(serviceId)
                    .update("specs", specs)
                    .addOnSuccessListener(unused -> {
                        loadingDialog.dismiss();
                        MyToast.showShort(UpdateServiceActivity.this, "Updated Successfully!");
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        MyToast.showShort(UpdateServiceActivity.this, "Failed: " + e.getMessage());
                    });
        }
    }

}
