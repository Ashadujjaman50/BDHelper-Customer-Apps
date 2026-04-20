package com.krishibarirangpur.bdhelper.userFragment.partner.serviceManage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.DistrictAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentAddServiceFormBinding;
import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.userActivity.partner.ServiceDocumentActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddServiceFormFragment extends Fragment {

    private FragmentAddServiceFormBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    FirebaseFirestore db;

    private String categoryId;
    private String subCategoryId;
    private String subCategoryName;

    LoadingDialog loadingDialog;


    String selectedMetro, selectSerial, serialNumber;
    String equipmentCapability, equipmentCategory, harvesterName, harvesterCategory, metroName,
            serialCategory, modelNumber, categoryAndYear, carModelNumber, carYear, chargerVanName,
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_service_form, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //Loading Dialog
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

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

    private void checkAndNextAction() {
        // Collect values
        metroName = binding.metroNameEt.getText().toString().trim();
        serialCategory = binding.serialCategoryEt.getText().toString().trim();
        serialNumber = binding.serialNumberEt.getText().toString().trim();

        modelNumber = binding.modelNumberEt.getText().toString().trim();
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
                        modelNumber = binding.lowBedSizeEt.getText().toString().trim();
                        categoryAndYear = binding.lowBedCategoryEt.getText().toString().trim();
                        requiredFields.add(new Pair<>(binding.metroNameEt, metroName));
                        requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory));
                        requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber));
                        requiredFields.add(new Pair<>(binding.lowBedSizeEt, modelNumber));
                        requiredFields.add(new Pair<>(binding.lowBedCategoryEt, categoryAndYear));
                        break;

                    default:
                        requiredFields.add(new Pair<>(binding.metroNameEt, metroName));
                        requiredFields.add(new Pair<>(binding.serialCategoryEt, serialCategory));
                        requiredFields.add(new Pair<>(binding.serialNumberEt, serialNumber));
                        requiredFields.add(new Pair<>(binding.modelNumberEt, modelNumber));
                        requiredFields.add(new Pair<>(binding.categoryOrYearEt, categoryAndYear));
                        break;
                }

                if (validateFields(requiredFields)) {
                    String prefix = selectedMetro + "-" + selectSerial + "-" + serialNumber;
                    switch (subCategoryId) {
                        case MyUtils.SUB_CAR_ID:
                        case MyUtils.SUB_MICROBUS_ID:
                            submitServiceData(prefix, carModelNumber, carYear);
                            break;

                        case MyUtils.SUB_AMBULANCE_ID:
                            submitServiceData(prefix, ambulanceCategory, ambulanceVanType);
                            break;

                        case MyUtils.SUB_CHARGER_VAN_ID:
                            submitServiceData("", chargerVanName, ambulanceVanType);
                            break;

                        case MyUtils.SUB_DUMP_TRUCK_ID:
                            submitServiceData(prefix, equipmentCapability, equipmentCategory);
                            break;

                        default:
                            submitServiceData(prefix, modelNumber, categoryAndYear);
                            break;
                    }
                }

                break;
            case MyUtils.EQUIPMENT_ID:
                requiredFields.add(new Pair<>(binding.equipmentCapabilityEt, equipmentCapability));
                requiredFields.add(new Pair<>(binding.equipmentCategoryEt, equipmentCategory));
                if (validateFields(requiredFields)) {
                    submitServiceData("", equipmentCapability, equipmentCategory);
                }
                break;
            case MyUtils.HARVESTER_MACHINE_ID:
                requiredFields.add(new Pair<>(binding.harvesterNameEt, harvesterName));
                requiredFields.add(new Pair<>(binding.harvesterCategoryEt, harvesterCategory));
                if (validateFields(requiredFields)) {
                    submitServiceData("", harvesterName, harvesterCategory);
                }
                break;
            case MyUtils.HOME_SHIFTING_ID:
                requiredFields.add(new Pair<>(binding.teamLeaderNameEt, teamLeaderName));
                requiredFields.add(new Pair<>(binding.teamMemberEt, teamMember));
                requiredFields.add(new Pair<>(binding.workAreaEt, workArea));
                if (validateFields(requiredFields)) {
                    submitServiceData(teamLeaderName, teamMember, workArea);
                }
                break;
            case MyUtils.SKILLED_LABOR_ID:
                requiredFields.add(new Pair<>(binding.serviceSkillEt, serviceSkill));
                requiredFields.add(new Pair<>(binding.serviceExperienceEt, serviceExperience));
                requiredFields.add(new Pair<>(binding.serviceAreaEt, serviceArea));
                if (validateFields(requiredFields)) {
                    submitServiceData(serviceSkill, serviceExperience, serviceArea);
                }
                break;
        }

    }

    private void submitServiceData(String serviceRegistrationNumber, String serviceModelType, String serviceCategoryAndYear) {
        loadingDialog.setMessage("আপনার সার্ভিস রিকুয়েস্ট সাবমিট হচ্ছে..");
        loadingDialog.show();

        //Timestamp
        String serviceId = "" + System.currentTimeMillis();
        String userId = firebaseUser.getUid();

        Map<String, Object> serviceMap = new HashMap<>();
        serviceMap.put("serviceId", serviceId);
        serviceMap.put("serviceRegistrationNumber", serviceRegistrationNumber);
        serviceMap.put("serviceModelNumber", serviceModelType);
        serviceMap.put("serviceCategoryAndYear", serviceCategoryAndYear);
        serviceMap.put("brtaImage", "");
        serviceMap.put("transportImage", "");
        serviceMap.put("driverLicence", "");
        serviceMap.put("categoryId", categoryId);
        serviceMap.put("subCategoryId", subCategoryId);
        serviceMap.put("subCategoryName", subCategoryName);
        serviceMap.put("serviceStatus", "Inactive");
        serviceMap.put("serviceVerified", "pending");

        db.collection("users")
                .document(userId)
                .collection("services")
                .document(serviceId)
                .set(serviceMap)
                .addOnSuccessListener(unused -> {
                    //
                    loadingDialog.dismiss();
                    MyToast.showShort(requireContext(), "Request submit");
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

    //Validate multiple TextView/EditText fields
    private boolean validateFields(List<Pair<TextView, String>> fields) {
        for (Pair<TextView, String> field : fields) {
            if (TextUtils.isEmpty(field.second)) {
                setErrorWatcher(field.first, true);
                return false;
            }
        }
        return true;
    }



    @SuppressLint("SetTextI18n")
    private void initActivity() {
        binding.transportNameTv.setText(subCategoryName);

        // 🔹 Step 1: সব Layout লুকাও (reset state)
        hideAllLayouts();

        // 🔹 Step 2: category অনুযায়ী visible করো
        showLayoutByCategory();

        // 🔹 Step 3: subCategory অনুযায়ী visible করো
        showLayoutBySubCategory();

        // 🔹 Step 4: subCategory অনুযায়ী Text/Hints সেট করো
        setupSubCategoryUI();

        // 🔹 Step 5: Click listeners
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

    private void showLayoutByCategory() {
        switch (categoryId) {
            case MyUtils.ROAD_TRANSPORT_ID:
            case MyUtils.RENT_A_CAR_ID:
                binding.transportLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.EQUIPMENT_ID:
                binding.equipmentLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.HARVESTER_MACHINE_ID:
                binding.harvesterLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.HOME_SHIFTING_ID:
                binding.homeOfficeShiftingLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.SKILLED_LABOR_ID:
                binding.skilledLaborerLL.setVisibility(View.VISIBLE);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void showLayoutBySubCategory() {
        switch (subCategoryId) {
            case MyUtils.SUB_CAR_ID:
            case MyUtils.SUB_MICROBUS_ID:
                binding.carAndMicroLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.SUB_AMBULANCE_ID:
                binding.ambulanceVanTv.setText(subCategoryName + " " + getString(R.string.types));
                binding.ambulanceCategoryEt.setVisibility(View.VISIBLE);
                binding.chargerVanNameEt.setVisibility(View.GONE);
                binding.ambulanceVanLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.SUB_CHARGER_VAN_ID:
                binding.serialLL.setVisibility(View.GONE);
                binding.ambulanceVanLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.SUB_DUMP_TRUCK_ID:
                binding.equipmentCapabilityEt.setHint("সিএফটি");
                binding.equipmentLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.SUB_LOW_BED_ID:
                binding.lowBedLL.setVisibility(View.VISIBLE);
                break;
            default:
                binding.truckAndOthersLL.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupSubCategoryUI() {
        switch (subCategoryId) {
            case MyUtils.SUB_COVERED_VAN_ID:
                binding.categoryOrYearEt.setText("কাভার্ড ভ্যান");
                break;
            case MyUtils.SUB_TRUCK_ID:
            case MyUtils.SUB_PICKUP_ID:
                binding.categoryOrYearEt.setText("খোলা গাড়ী");
                break;
            case MyUtils.SUB_TRAILER_ID:
                binding.categoryOrYearEt.setText("ফ্লাট বেড");
                break;
            case MyUtils.SUB_LOW_BED_ID:
                binding.lowBedCategoryEt.setText("লো বেড");
                break;
            case MyUtils.SUB_DRIVER_ID:
                binding.serviceSkillTv.setText("কোন ধরনের গাড়ি চালাতে পারেন?");
                binding.serviceSkillEt.setHint("যেমনঃ কার, মাইক্রোবাস, ট্রাক, ট্রেইলর");
                break;
            case MyUtils.SUB_MECHANIC_ID:
                binding.serviceSkillTv.setText("কোন ধরনের সার্ভিসে দক্ষ?");
                binding.serviceSkillEt.setHint("যেমনঃ  বাইক, কার, মাইক্রোবাস, ভারী যানবাহন");
                break;
            case MyUtils.SUB_ELECTRICIAN_ID:
                binding.serviceSkillTv.setText("কোন ধরনের ইলেকট্রিক্যাল কাজে দক্ষ?");
                binding.serviceSkillEt.setHint("যেমনঃ  ওয়্যারিং, লাইট, ফ্যান, পাম্প সেটআপ");
                break;
            case MyUtils.SUB_STOVE_TECHNICIAN_ID:
                binding.serviceSkillTv.setText("কোন ধরনের চুলার কাজে দক্ষ?");
                binding.serviceSkillEt.setHint("যেমনঃ গ্যাস চুলা সেটআপ, চুলার রিপেয়ার ");
                break;
            case MyUtils.SUB_PLUMBER_ID:
                binding.serviceSkillTv.setText("কোন ধরনের পানির লাইনের কাজে দক্ষ?");
                binding.serviceSkillEt.setHint("যেমনঃ পাইপলাইন, লিকেজ, বাথরুম ফিটিংস");
                break;
        }

        if (categoryId.equals(MyUtils.EQUIPMENT_ID) &&
                subCategoryId.equals(MyUtils.SUB_EXCAVATOR_ID)) {
            binding.equipmentCapabilityEt.setHint("সাইজ");
        }
    }

    private void initClickListeners() {
        // Transport category
        binding.metroNameEt.setOnClickListener(v -> popupMetroList());
        binding.serialCategoryEt.setOnClickListener(v -> popupSerialCategory());
        binding.categoryOrYearEt.setOnClickListener(v -> popupCategory(binding.categoryOrYearEt));
        binding.lowBedCategoryEt.setOnClickListener(v -> popupCategory(binding.lowBedCategoryEt));
        binding.lowBedSizeEt.setOnClickListener(v -> popupLowBetSizeCategory());
        binding.ambulanceVanTypeEt.setOnClickListener(v -> popupChargerVanType());
        binding.ambulanceCategoryEt.setOnClickListener(v -> popupAmbulanceCategory());

        // Equipment category
        binding.equipmentCapabilityEt.setOnClickListener(v -> popupEquipmentCapability());
        binding.equipmentCategoryEt.setOnClickListener(v -> popupEquipmentCategory());

        // Harvester
        binding.harvesterCategoryEt.setOnClickListener(v -> popupHarvesterCategory());
    }


    private void popupHarvesterCategory() {
        //popup menu
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.harvesterCategoryEt, Gravity.END);
        if (subCategoryId.equals(MyUtils.HARVESTER_MACHINE_ID)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, getString(R.string.combine_harvester));
            popupMenu.getMenu().add(Menu.NONE, 1, 0, getString(R.string.specialized_harvester));
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 0) {
                binding.harvesterCategoryEt.setText("কম্বাইন হারভেস্টার");
            } else if (id == 1) {
                binding.harvesterCategoryEt.setText("স্পেশালাইজড হারভেস্টার");
            }
            return false;
        });
        popupMenu.show();
    }

    @SuppressLint("SetTextI18n")
    private void popupLowBetSizeCategory() {
        //popup menu
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.lowBedSizeEt, Gravity.END);
        if (subCategoryId.equals(MyUtils.SUB_LOW_BED_ID)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, getString(R.string.forteen_feet));
            popupMenu.getMenu().add(Menu.NONE, 1, 0, getString(R.string.sixteen_feet));
            popupMenu.getMenu().add(Menu.NONE, 2, 0, getString(R.string.eighteen_feet));
            popupMenu.getMenu().add(Menu.NONE, 3, 0, getString(R.string.twenty_feet));
            popupMenu.getMenu().add(Menu.NONE, 4, 0, getString(R.string.twenty_four_feet));
            popupMenu.getMenu().add(Menu.NONE, 5, 0, getString(R.string.twenty_six_feet));
            popupMenu.getMenu().add(Menu.NONE, 6, 0, getString(R.string.fourty_feet));
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 0) {
                binding.lowBedSizeEt.setText("১৪ ফিট");
            } else if (id == 1) {
                binding.lowBedSizeEt.setText("১৬ ফিট");
            } else if (id == 2) {
                binding.lowBedSizeEt.setText("১৮ ফিট");
            } else if (id == 3) {
                binding.lowBedSizeEt.setText("২০ ফিট");
            } else if (id == 4) {
                binding.lowBedSizeEt.setText("২৪ ফিট");
            } else if (id == 5) {
                binding.lowBedSizeEt.setText("২৬ ফিট");
            } else if (id == 6) {
                binding.lowBedSizeEt.setText("৪০ ফিট");
            }
            setErrorWatcher(binding.lowBedSizeEt, false);
            return false;
        });
        popupMenu.show();
    }

    @SuppressLint("SetTextI18n")
    private void popupAmbulanceCategory() {
        //popup menu
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.ambulanceCategoryEt, Gravity.END);
        if (subCategoryId.equals(MyUtils.SUB_AMBULANCE_ID)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, getString(R.string.regular));
            popupMenu.getMenu().add(Menu.NONE, 1, 0, getString(R.string.bls_ambulances));
            popupMenu.getMenu().add(Menu.NONE, 2, 0, getString(R.string.als_ambulances));
            popupMenu.getMenu().add(Menu.NONE, 3, 0, getString(R.string.icu_ambulances));
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 0) {
                binding.ambulanceCategoryEt.setText("রেগুলার");
            } else if (id == 1) {
                binding.ambulanceCategoryEt.setText("বেসিক লাইফ সাপোর্ট (BLS)");
            } else if (id == 2) {
                binding.ambulanceCategoryEt.setText("অ্যাডভান্সড লাইফ সাপোর্ট (ALS)");
            } else if (id == 3) {
                binding.ambulanceCategoryEt.setText("আইসিইউ অ্যাম্বুলেন্স");
            }
            setErrorWatcher(binding.ambulanceCategoryEt, false);
            return false;
        });
        popupMenu.show();
    }

    private void popupChargerVanType() {
        //popup menu
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.ambulanceVanTypeEt, Gravity.END);
        if (subCategoryId.equals(MyUtils.SUB_CHARGER_VAN_ID)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, getString(R.string.regular));
            popupMenu.getMenu().add(Menu.NONE, 1, 0, getString(R.string.passenger_carrier));
        }
        else if (subCategoryId.equals(MyUtils.SUB_AMBULANCE_ID)){
            popupMenu.getMenu().add(Menu.NONE, 2, 0, getString(R.string.ac_ambulances));
            popupMenu.getMenu().add(Menu.NONE, 3, 0, getString(R.string.non_ac_ambulances));
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 0) {
                binding.ambulanceVanTypeEt.setText("রেগুলার");
            } else if (id == 1) {
                binding.ambulanceVanTypeEt.setText("যাত্রী বাহী");
            } else if (id == 2) {
                binding.ambulanceVanTypeEt.setText("এসি অ্যাম্বুলেন্স");
            } else if (id == 3) {
                binding.ambulanceVanTypeEt.setText("নন-এসি অ্যাম্বুলেন্স");
            }
            setErrorWatcher(binding.ambulanceVanTypeEt, false);
            return false;
        });
        popupMenu.show();
    }

    private void popupMetroList() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());

        // Inflate WITHOUT parent (important)
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_recycleview, null);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        titleTv.setText(getString(R.string.select_metro));

        String[] districtListEng = MyUtils.METRO_LIST_ENG;
        String[] districtListBan = MyUtils.METRO_LIST_BAN;
        boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");
        String[] displayList = isBangla ? districtListBan : districtListEng;

        // Adapter
        DistrictAdapter adapter = new DistrictAdapter(Arrays.asList(displayList), (item, position) -> {
            binding.metroNameEt.setText(item); // UI text (localised)
            selectedMetro = isBangla ? districtListEng[position] : item; // DB-র জন্য always ইংরেজি
            setErrorWatcher(binding.metroNameEt, false);
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

        // ✅ এখন এখানে show করো
        bottomSheetDialog.show();
    }

    private void popupSerialCategory() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity());

        // Inflate WITHOUT parent (important)
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_recycleview, null);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        titleTv.setText(getString(R.string.select_serial));

        String[] serialEng = MyUtils.SERIAL_ENG;
        String[] serialBan = MyUtils.SERIAL_BAN;
        boolean isBangla = LocaleHelper.getLanguage(requireActivity()).equals("bn");
        String[] displayList = isBangla ? serialBan : serialEng;

        // Adapter
        DistrictAdapter adapter = new DistrictAdapter(Arrays.asList(displayList), (item, position) -> {
            binding.serialCategoryEt.setText(item); // UI text (localised)
            selectSerial = isBangla ? serialEng[position] : item; // DB-র জন্য always ইংরেজি
            setErrorWatcher(binding.serialCategoryEt, false);
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

        // ✅ এখন এখানে show করো
        bottomSheetDialog.show();
    }

    // helper
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void popupCategory(TextView categoryEt) {
        //popup menu
        PopupMenu popupMenu = new PopupMenu(requireContext(), categoryEt, Gravity.END);
        switch (subCategoryName) {
            case "ট্রাক":
            case "Truck":
            case "পিকাপ":
            case "Pickup":
                popupMenu.getMenu().add(Menu.NONE, 0, 0, "খোলা গাড়ী");
                break;
            case "কাভার্ড ভ্যান":
            case "Covered Van":
                popupMenu.getMenu().add(Menu.NONE, 1, 0, "কাভার্ড ভ্যান");
                break;
            case "লো বেড":
            case "Lo bet":
                popupMenu.getMenu().add(Menu.NONE, 2, 0, "লো বেড");
                break;
            case "ট্রেইলর":
            case "Trailer":
                popupMenu.getMenu().add(Menu.NONE, 3, 0, "ফ্লাট বেড");
                break;
            case "ফ্রিজার ভ্যান":
            case "Freezer Van":
                popupMenu.getMenu().add(Menu.NONE, 4, 0, "১২ ফিট");
                popupMenu.getMenu().add(Menu.NONE, 5, 0, "১৪ ফিট");
                popupMenu.getMenu().add(Menu.NONE, 6, 0, "১৬ ফিট");
                popupMenu.getMenu().add(Menu.NONE, 7, 0, "১৮ ফিট");
                break;
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 0) {
                binding.categoryOrYearEt.setText("খোলা গাড়ী");
            } else if (id == 1) {
                binding.categoryOrYearEt.setText("কাভার্ড ভ্যান");
            } else if (id == 2) {
                binding.categoryOrYearEt.setText("লো বেড");
            } else if (id == 3) {
                binding.categoryOrYearEt.setText("ফ্লাট বেড");
            } else if (id == 4) {
                binding.categoryOrYearEt.setText("১২ ফিট");
            } else if (id == 5) {
                binding.categoryOrYearEt.setText("১৪ ফিট");
            } else if (id == 6) {
                binding.categoryOrYearEt.setText("১৬ ফিট");
            } else if (id == 7) {
                binding.categoryOrYearEt.setText("১৮ ফিট");
            }

            setErrorWatcher(binding.categoryOrYearEt, false);
            return false;
        });
        popupMenu.show();
    }

    private void popupEquipmentCapability() {
        //popup menu
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.equipmentCapabilityEt, Gravity.END);

        if (subCategoryId.equals(MyUtils.SUB_EXCAVATOR_ID)) {

            popupMenu.getMenu().add(Menu.NONE, 1, 0, ".৩ সাইজ");
            popupMenu.getMenu().add(Menu.NONE, 2, 0, ".৫ সাইজ");
            popupMenu.getMenu().add(Menu.NONE, 3, 0, ".৭ সাইজ");
            popupMenu.getMenu().add(Menu.NONE, 4, 0, ".৯ সাইজ");
        }
        else if (subCategoryId.equals(MyUtils.SUB_DUMP_TRUCK_ID)) {
            popupMenu.getMenu().add(Menu.NONE, 5, 0, "১২০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 6, 0, "১৮০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 7, 0, "২০০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 8, 0, "২৫০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 9, 0, "৩০০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 10, 0, "৪০০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 11, 0, "৫০০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 12, 0, "৫৫০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 13, 0, "৬০০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 14, 0, "৬৫০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 15, 0, "৭০০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 16, 0, "৭৫০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 17, 0, "৮০০ সিএফটি");
            popupMenu.getMenu().add(Menu.NONE, 18, 0, "৮৫০ সিএফটি");
        }
        else if (subCategoryId.equals(MyUtils.SUB_TRACTOR_ID)) {
           popupMenu.getMenu().add(Menu.NONE, 19, 0, getString(R.string.sonalika_tractor));
           popupMenu.getMenu().add(Menu.NONE, 20, 0, getString(R.string.mahindra_tractor));
           popupMenu.getMenu().add(Menu.NONE, 21, 0, getString(R.string.yanmar_tractor));
           popupMenu.getMenu().add(Menu.NONE, 22, 0, getString(R.string.john_deere_tractor));
           popupMenu.getMenu().add(Menu.NONE, 23, 0, getString(R.string.new_holland_tractor));
           popupMenu.getMenu().add(Menu.NONE, 24, 0, getString(R.string.massey_ferguson_tractor));
           popupMenu.getMenu().add(Menu.NONE, 25, 0, getString(R.string.eicher_tractor));
           popupMenu.getMenu().add(Menu.NONE, 26, 0, getString(R.string.foton_tractor));
           popupMenu.getMenu().add(Menu.NONE, 27, 0, getString(R.string.force_motors_tractor));
        }
        else if (subCategoryId.equals(MyUtils.SUB_RICE_TRANSPLANTER_ID)) {
            popupMenu.getMenu().add(Menu.NONE, 28, 0, getString(R.string.riding_type));
            popupMenu.getMenu().add(Menu.NONE, 29, 0, getString(R.string.walking_type));
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
             if (id == 1) {
                 binding.equipmentCapabilityEt.setText(".৩ সাইজ");
             } else if (id == 2) {
                 binding.equipmentCapabilityEt.setText(".৫ সাইজ");
             } else if (id == 3) {
                 binding.equipmentCapabilityEt.setText(".৭ সাইজ");
             } else if (id == 4) {
                 binding.equipmentCapabilityEt.setText(".৯ সাইজ");
             } else if (id == 5) {
                 binding.equipmentCapabilityEt.setText("১২০ সিএফটি");
             } else if (id == 6) {
                 binding.equipmentCapabilityEt.setText("১৮০ সিএফটি");
             } else if (id == 7) {
                 binding.equipmentCapabilityEt.setText("২০০ সিএফটি");
             } else if (id == 8) {
                 binding.equipmentCapabilityEt.setText("২৫০ সিএফটি");
             } else if (id == 9) {
                 binding.equipmentCapabilityEt.setText("৩০০ সিএফটি");
             } else if (id == 10) {
                 binding.equipmentCapabilityEt.setText("৪০০ সিএফটি");
             } else if (id == 11) {
                 binding.equipmentCapabilityEt.setText("৫০০ সিএফটি");
             } else if (id == 12) {
                 binding.equipmentCapabilityEt.setText("৫৫০ সিএফটি");
             } else if (id == 13) {
                 binding.equipmentCapabilityEt.setText("৬০০ সিএফটি");
             } else if (id == 14) {
                 binding.equipmentCapabilityEt.setText("৬৫০ সিএফটি");
             } else if (id == 15) {
                 binding.equipmentCapabilityEt.setText("৭০০ সিএফটি");
             } else if (id == 16) {
                 binding.equipmentCapabilityEt.setText("৭৫০ সিএফটি");
             } else if (id == 17) {
                 binding.equipmentCapabilityEt.setText("৮০০ সিএফটি");
             } else if (id == 18) {
                 binding.equipmentCapabilityEt.setText("৮৫০ সিএফটি");
             } else if (id == 19) {
                 binding.equipmentCapabilityEt.setText("সোনালিকা");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.sonalika_tractor)); // save selected capacity
             }  else if (id == 20) {
                 binding.equipmentCapabilityEt.setText("মাহিন্দ্রা");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.mahindra_tractor)); // save selected capacity
             } else if (id == 21) {
                 binding.equipmentCapabilityEt.setText("ইয়ানমার");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.yanmar_tractor)); // save selected capacity
             }  else if (id == 22) {
                 binding.equipmentCapabilityEt.setText("জন ডিয়ার");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.john_deere_tractor)); // save selected capacity
             } else if (id == 23) {
                 binding.equipmentCapabilityEt.setText("নিউ হল্যান্ড");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.new_holland_tractor)); // save selected capacity
             }  else if (id == 24) {
                 binding.equipmentCapabilityEt.setText("মেসি ফার্গুসন");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.massey_ferguson_tractor)); // save selected capacity
             } else if (id == 25) {
                 binding.equipmentCapabilityEt.setText("আইশার");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.eicher_tractor)); // save selected capacity
             }  else if (id == 26) {
                 binding.equipmentCapabilityEt.setText("ফোটন");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.foton_tractor)); // save selected capacity
             }  else if (id == 27) {
                 binding.equipmentCapabilityEt.setText("ফোর্স মোটরস");
                 binding.equipmentCategoryEt.setText("");
                 binding.equipmentCapabilityEt.setTag(getString(R.string.force_motors_tractor)); // save selected capacity
             } else if (id == 28) {
                 binding.equipmentCapabilityEt.setText("রাইডিং টাইপ");
             } else if (id == 29) {
                 binding.equipmentCapabilityEt.setText("ওয়াকিং টাইপ");
             }

            setErrorWatcher(binding.equipmentCapabilityEt, false);
            return false;
        });
        popupMenu.show();
    }

    private void popupEquipmentCategory() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.equipmentCategoryEt, Gravity.END);
        if (subCategoryId.equals(MyUtils.SUB_DUMP_TRUCK_ID)) {
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "৬ চাকা");
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "১০ চাকা");
        }
        else if (subCategoryId.equals(MyUtils.SUB_EXCAVATOR_ID)) {
            popupMenu.getMenu().add(Menu.NONE, 3, 0, "রেগুলার");
            popupMenu.getMenu().add(Menu.NONE, 4, 0, "লং বুম");
        }
        else if (subCategoryId.equals(MyUtils.SUB_TRACTOR_ID)) {
            String selectedCapacity = (String) binding.equipmentCapabilityEt.getTag(); // capacity stored in Tag

            if (selectedCapacity != null) {
                if (selectedCapacity.equals(getString(R.string.sonalika_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 5, 0, getString(R.string.hp_fal_40));
                    popupMenu.getMenu().add(Menu.NONE, 6, 0, getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 7, 0, getString(R.string.hp_fal_60_4));
                }
                else if (selectedCapacity.equals(getString(R.string.mahindra_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 8, 0, getString(R.string.hp_fal_42));
                    popupMenu.getMenu().add(Menu.NONE, 9, 0, getString(R.string.hp_fal_45));
                    popupMenu.getMenu().add(Menu.NONE, 10, 0, getString(R.string.hp_fal_62));
                    popupMenu.getMenu().add(Menu.NONE, 11, 0, getString(R.string.hp_fal_71));
                }
                else if (selectedCapacity.equals(getString(R.string.yanmar_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 12, 0, getString(R.string.hp_fal_21));
                    popupMenu.getMenu().add(Menu.NONE, 13, 0, getString(R.string.hp_fal_26));
                    popupMenu.getMenu().add(Menu.NONE, 14, 0, getString(R.string.hp_fal_42));
                    popupMenu.getMenu().add(Menu.NONE, 15, 0, getString(R.string.hp_fal_46_4));
                    popupMenu.getMenu().add(Menu.NONE, 16, 0, getString(R.string.hp_fal_59));
                }
                else if (selectedCapacity.equals(getString(R.string.john_deere_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 17, 0, getString(R.string.hp_fal_35));
                    popupMenu.getMenu().add(Menu.NONE, 18, 0, getString(R.string.hp_fal_55));
                    popupMenu.getMenu().add(Menu.NONE, 19, 0, getString(R.string.hp_fal_113));
                }
                else if (selectedCapacity.equals(getString(R.string.new_holland_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 20, 0, getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 21, 0, getString(R.string.hp_fal_56));
                    popupMenu.getMenu().add(Menu.NONE, 22, 0, getString(R.string.hp_fal_60_5));
                }
                else if (selectedCapacity.equals(getString(R.string.massey_ferguson_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 23, 0, getString(R.string.hp_fal_50_3));
                    popupMenu.getMenu().add(Menu.NONE, 24, 0, getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 25, 0, getString(R.string.hp_fal_85));
                }
                else if (selectedCapacity.equals(getString(R.string.eicher_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 26, 0, getString(R.string.hp_fal_25));
                    popupMenu.getMenu().add(Menu.NONE, 27, 0, getString(R.string.hp_fal_36));
                    popupMenu.getMenu().add(Menu.NONE, 28, 0, getString(R.string.hp_fal_40));
                    popupMenu.getMenu().add(Menu.NONE, 29, 0, getString(R.string.hp_fal_45));
                    popupMenu.getMenu().add(Menu.NONE, 30, 0, getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 31, 0, getString(R.string.hp_fal_60_5));
                }
                else if (selectedCapacity.equals(getString(R.string.foton_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 32, 0, getString(R.string.hp_fal_24));
                    popupMenu.getMenu().add(Menu.NONE, 33, 0, getString(R.string.hp_fal_46_3));
                    popupMenu.getMenu().add(Menu.NONE, 34, 0, getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 35, 0, getString(R.string.hp_fal_70));
                    popupMenu.getMenu().add(Menu.NONE, 36, 0, getString(R.string.hp_fal_90));
                }
                else if (selectedCapacity.equals(getString(R.string.force_motors_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 37, 0, getString(R.string.hp_fal_27));
                    popupMenu.getMenu().add(Menu.NONE, 38, 0, getString(R.string.hp_fal_31));
                    popupMenu.getMenu().add(Menu.NONE, 39, 0, getString(R.string.hp_fal_40));
                    popupMenu.getMenu().add(Menu.NONE, 40, 0, getString(R.string.hp_fal_45));
                    popupMenu.getMenu().add(Menu.NONE, 41, 0, getString(R.string.hp_fal_50_4));
                }
            }

        }
        else if (subCategoryId.equals(MyUtils.SUB_RICE_TRANSPLANTER_ID)) {
            popupMenu.getMenu().add(Menu.NONE, 42, 0, getString(R.string.four_line_machine));
            popupMenu.getMenu().add(Menu.NONE, 43, 0, getString(R.string.six_Line_machine));
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 1) {
                binding.equipmentCategoryEt.setText("৬ চাকা");
            } else if (id == 2) {
                binding.equipmentCategoryEt.setText("১০ চাকা");
            } else if (id == 3) {
                binding.equipmentCategoryEt.setText("রেগুলার");
            } else if (id == 4) {
                binding.equipmentCategoryEt.setText("লং বুম");
            } else if (id == 5) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_40));
            } else if (id == 6) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_50_4));
            } else if (id == 7) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_60_4));
            } else if (id == 8) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_42));
            } else if (id == 9) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_45));
            } else if (id == 10) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_62));
            } else if (id == 11) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_71));
            } else if (id == 12) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_21));
            } else if (id == 13) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_26));
            } else if (id == 14) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_42));
            } else if (id == 15) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_46_4));
            } else if (id == 16) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_59));
            } else if (id == 17) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_35));
            } else if (id == 18) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_55));
            } else if (id == 19) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_113));
            } else if (id == 20) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_50_4));
            } else if (id == 21) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_56));
            } else if (id == 22) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_60_5));
            } else if (id == 23) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_50_3));
            } else if (id == 24) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_50_4));
            } else if (id == 25) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_85));
            } else if (id == 26) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_25));
            } else if (id == 27) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_36));
            } else if (id == 28) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_40));
            } else if (id == 29) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_45));
            } else if (id == 30) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_50_4));
            } else if (id == 31) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_60_5));
            } else if (id == 32) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_24));
            } else if (id == 33) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_46_3));
            } else if (id == 34) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_50_4));
            }  else if (id == 35) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_70));
            }  else if (id == 36) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_90));
            } else if (id == 37) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_27));
            }  else if (id == 38) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_31));
            }  else if (id == 39) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_40));
            }  else if (id == 40) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_45));
            }  else if (id == 41) {
                binding.equipmentCategoryEt.setText(getString(R.string.hp_fal_50_4));
            } else if (id == 42) {
                binding.equipmentCategoryEt.setText("৪ সারির যন্ত্র");
            } else if (id == 43) {
                binding.equipmentCategoryEt.setText("৬ সারির যন্ত্র");
            }
            setErrorWatcher(binding.equipmentCategoryEt, false);
            return false;
        });
        popupMenu.show();
    }

    private void setErrorWatcher(View view, boolean hasError) {
        if (hasError) {
            view.setBackgroundResource(R.drawable.bg_edit_text_error);

            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.requestFocus();
                editText.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        editText.setBackgroundResource(R.drawable.bg_edit_text);
                    }
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override public void afterTextChanged(Editable s) {}
                });
            }
        }
        else {
            view.setBackgroundResource(R.drawable.bg_edit_text);
        }
    }

}