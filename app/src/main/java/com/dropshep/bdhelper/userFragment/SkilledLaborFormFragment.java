package com.dropshep.bdhelper.userFragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentSkilledLaborFormBinding;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SkilledLaborFormFragment extends Fragment {

    private FragmentSkilledLaborFormBinding binding;

    String categoryId, subCategoryId, subCategoryName;
    String loadLocation = "", unloadLocation="", rentLocation="";

    String userId, postDistrict, quantity, description, rentDate, rentTime;
    String specification, specificationCapacity, specificationDuration, specificationTypes;

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    final int SHORT_ID_LENGTH = 4;
    private static final long SPLASH_TIME_OUT = 5000;
    List<String> array_list = new ArrayList<>();

    public SkilledLaborFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            categoryId = getArguments().getString(MyUtils.categoryId);
            subCategoryId = getArguments().getString(MyUtils.subCategoryId);
            subCategoryName = getArguments().getString(MyUtils.subCategoryName);
            rentLocation = getArguments().getString(MyUtils.rentLocation);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_skilled_labor_form, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views
        init();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userId = firebaseUser.getUid();

        progressDialog = new ProgressDialog(requireContext());

        //current user info
        if (firebaseAuth.getCurrentUser() != null){
            getUserInfo();
        }

        binding.dateTimeTV.setOnClickListener(v -> {
            CommonClass.showDateTimePicker(requireContext(),3, binding.dateTimeTV);
        });

        switch (subCategoryName){
            case "ড্রাইভার":
            case "Driver":
                binding.experienceLl.setVisibility(View.VISIBLE);
                binding.laborerNeedTitleTv.setText(getString(R.string.driver_required));
                binding.laborerNeedEt.setHint(getString(R.string.driver_required));
                break;
            case "মেকানিক":
            case "Mechanic":
                binding.laborerNeedTitleTv.setText(getString(R.string.mechanic_required));
                binding.laborerNeedEt.setHint(getString(R.string.mechanic_required));
                break;
            case "ইলেক্ট্রিশিয়ান":
            case "Electrician":
                binding.laborerNeedTitleTv.setText(getString(R.string.electrician_required));
                binding.laborerNeedEt.setHint(getString(R.string.electrician_required));
                break;
            case "পানির লাইনের মিস্ত্রি":
            case "Plumber":
                binding.laborerNeedTitleTv.setText(getString(R.string.plumber_required));
                binding.laborerNeedEt.setHint(getString(R.string.plumber_required));
                break;
            case "চুলার মিস্ত্রি":
            case "Stove mechanic":
                binding.laborerNeedTitleTv.setText(getString(R.string.stove_mechanic_required));
                binding.laborerNeedEt.setHint(getString(R.string.stove_mechanic_required));
                break;
            default:
                binding.experienceLl.setVisibility(View.GONE);
                break;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_listview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = bottomSheetDialog.findViewById(R.id.titleTv);
        ListView lv = bottomSheetDialog.findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter((requireContext()), R.layout.single_listview_item, R.id.listItem, array_list));


        //Load Need Laborer Need
        binding.laborerNeedEt.setOnClickListener(v -> {
            switch (subCategoryName){
                case "ড্রাইভার":
                case "Driver":
                    titleTv.setText(getString(R.string.driver_required));
                    break;
                case "মেকানিক":
                case "Mechanic":
                    titleTv.setText(getString(R.string.mechanic_required));
                    break;
                case "ইলেক্ট্রিশিয়ান":
                case "Electrician":
                    titleTv.setText(getString(R.string.electrician_required));
                    break;
                case "পানির লাইনের মিস্ত্রি":
                case "Plumber":
                    titleTv.setText(getString(R.string.plumber_required));
                    break;
                case "চুলার মিস্ত্রি":
                case "Stove mechanic":
                    titleTv.setText(getString(R.string.stove_mechanic_required));
                    break;
                default:
                    titleTv.setText(getString(R.string.how_many_laborers));
                    break;
            }
            bottomSheetFixedDialog(bottomSheetDialog, true);
            getCountList();
            bottomSheetDialog.show();
            lv.setOnItemClickListener(((parent, viewList, position, id) -> {
                binding.laborerNeedEt.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            }));
        });



        //Load Work Type
        binding.workTypeEt.setOnClickListener(v -> {
            array_list.clear();
            bottomSheetFixedDialog(bottomSheetDialog, false);
            titleTv.setText(getString(R.string.which_type_laborer));
            switch (subCategoryName){
                case "ইলেক্ট্রিশিয়ান":
                case "Electrician":
                    array_list.add(getString(R.string.ceiling_fan_install_uninstall));
                    array_list.add(getString(R.string.ceiling_fan_service));
                    array_list.add(getString(R.string.electric_line_check_up));
                    array_list.add(getString(R.string.light_service_fitting));
                    array_list.add(getString(R.string.switch_board_servicing_setup));
                    array_list.add(getString(R.string.main_circuit_breaker_servicing));
                    array_list.add(getString(R.string.main_distribution_board_servicing));
                    break;
                case "চুলার মিস্ত্রি":
                case "Stove mechanic":
                    array_list.add(getString(R.string.gas_stove_burner_check_up));
                    array_list.add(getString(R.string.gas_stove_cabinet_stove_and_burner_setup));
                    array_list.add(getString(R.string.gas_stove_leak_repair));
                    array_list.add(getString(R.string.gas_stove_component_change));
                    array_list.add(getString(R.string.gas_cabinet_stove_cleaning));
                    array_list.add(getString(R.string.cabinet_stove_burner_repair));
                    break;
                case "পানির লাইনের মিস্ত্রি":
                case "Plumber":
                    array_list.add(getString(R.string.plumbing_check_up));
                    array_list.add(getString(R.string.water_tap_servicing));
                    array_list.add(getString(R.string.shower_servicing));
                    array_list.add(getString(R.string.sink_issue));
                    break;
                case "মেকানিক":
                case "Mechanic":
                    array_list.add(getString(R.string.motorcycle_servicing));
                    array_list.add(getString(R.string.car_servicing));
                    array_list.add(getString(R.string.microbus_servicing));
                    array_list.add(getString(R.string.generator_servicing));
                    array_list.add(getString(R.string.others));
                    break;
                case "ড্রাইভার":
                case "Driver":
                    array_list.add(getString(R.string.car_microbus_driver));
                    array_list.add(getString(R.string.ambulance_driver));
                    array_list.add(getString(R.string.pickup_driver));
                    array_list.add(getString(R.string.truck_driver));
                    array_list.add(getString(R.string.drum_truck_driver));
                    array_list.add(getString(R.string.freezer_van_driver));
                    array_list.add(getString(R.string.trailer_low_bed_driver));
                    break;
            }

            bottomSheetDialog.show();
            lv.setOnItemClickListener(((parent, viewList, position, id) -> {
                binding.workTypeEt.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            }));

        });

        //Load Experience
        binding.experienceEt.setOnClickListener(v -> {
            titleTv.setText(getString(R.string.work_experience));
            bottomSheetFixedDialog(bottomSheetDialog, true);
            getExperienceList();
            bottomSheetDialog.show();
            lv.setOnItemClickListener(((parent, viewList, position, id) -> {
                binding.experienceEt.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            }));
        });

    }

    private void getExperienceList() {
        array_list.clear();
        array_list.add(getString(R.string.one)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.two)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.three)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.four)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.five)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.six)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.seven)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.eight)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.nine)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.ten)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.eleven)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.twelve)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.thirteen)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.forteen)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.fifteen)+" "+getResources().getString(R.string.year));
        array_list.add(getString(R.string.sixteen)+" "+getResources().getString(R.string.year)+" +");
    }

    private void bottomSheetFixedDialog(BottomSheetDialog bottomSheetDialog, boolean isTrue) {
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet == null) return;

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

        if (isTrue) {
            // ✅ Fixed height 400dp
            int heightInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    400,
                    requireContext().getResources().getDisplayMetrics()
            );
            bottomSheet.getLayoutParams().height = heightInPx;
            bottomSheet.requestLayout();
            behavior.setPeekHeight(heightInPx);
            behavior.setDraggable(false);
        } else {
            // ✅ Wrap content (list size অনুযায়ী)
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            bottomSheet.requestLayout();
            behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO); // content অনুযায়ী
            behavior.setDraggable(true);
        }
    }


    private void getCountList() {
        array_list.clear();
        array_list.add(getString(R.string.one)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.two)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.three)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.four)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.five)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.six)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.seven)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.eight)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.nine)+" "+getResources().getString(R.string.person));
        array_list.add(getString(R.string.ten)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.eleven)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.twelve)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.thirteen)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.forteen)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.fifteen)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.sixteen)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.seventeen)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.eighteen)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.nineteen)+" "+getResources().getString(R.string.person));
//        array_list.add(getString(R.string.twenty)+" "+getResources().getString(R.string.person));
    }

    private void getUserInfo() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 🔹 district field পড়া
                        postDistrict = documentSnapshot.getString("district");

                        if (postDistrict != null) {
                            // UI তে দেখাও বা Log করো
                            Log.d("UserInfo", "District: " + postDistrict);
                        } else {
                            Log.d("UserInfo", "District not found for user");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserInfo", "Error fetching district", e);
                });
    }


}