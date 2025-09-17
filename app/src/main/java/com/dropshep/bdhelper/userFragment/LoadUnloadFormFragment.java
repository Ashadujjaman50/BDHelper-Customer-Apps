package com.dropshep.bdhelper.userFragment;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentLoadUnloadFormBinding;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.LoadingDialog;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class LoadUnloadFormFragment extends Fragment{

    private FragmentLoadUnloadFormBinding binding;

    String categoryId, subCategoryId, subCategoryName;
    String loadLocation = "", unloadLocation="";
    String userId, postDistrict, quantity, description, rentDate, rentTime;
    String specification, specificationCapacity, specificationDuration, specificationTypes;

    LoadingDialog loadingDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    final int SHORT_ID_LENGTH = 4;
    private static final long SPLASH_TIME_OUT = 5000;

    Typeface typeface1, typeface2;


    public LoadUnloadFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            categoryId = getArguments().getString(MyUtils.categoryId);
            subCategoryId = getArguments().getString(MyUtils.subCategoryId);
            subCategoryName = getArguments().getString(MyUtils.subCategoryName);
            loadLocation = getArguments().getString(MyUtils.loadLocation);
            unloadLocation = getArguments().getString(MyUtils.unloadLocation);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_load_unload_form, container, false);
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


        //Typeface
        typeface1 = ResourcesCompat.getFont(requireContext(), R.font.solaimanlipi);
        typeface2 = ResourcesCompat.getFont(requireContext(), R.font.open_sans_regular);

        loadingDialog = new LoadingDialog(requireContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        //current user info
        if (firebaseAuth.getCurrentUser() != null){
            getUserInfo();
        }


        switch (subCategoryName){
            case "মাইক্রোবাস":
            case "Microbus":
                binding.capacitySizeProgramTv.setText(getString(R.string.category));
                binding.capacityTV.setHint(getString(R.string.category));
                binding.vehicleNameTV.setText(subCategoryName);
                binding.productTypeTV.setHint(getString(R.string.capacity));
                break;
            case "কার":
            case "Car":
                binding.capacitySizeProgramTv.setText(getString(R.string.category));
                binding.capacityTV.setHint(getString(R.string.category));
                binding.vehicleNameTV.setText(getString(R.string.program));
                binding.productTypeTV.setHint(R.string.types);
                break;
            case "অ্যাম্বুলেন্স":
            case "Ambulance":
                binding.capacitySizeProgramTv.setText(getString(R.string.category));
                binding.capacityTV.setHint(getString(R.string.category));
                binding.vehicleNameTV.setText(subCategoryName);
                binding.productTypeTV.setHint(R.string.which_types);
                break;
            case "ড্রাম্প ট্রাক":
            case "Dump Truck":
                binding.capacitySizeProgramTv.setText(R.string.capacity);
                binding.vehicleNameTV.setText(subCategoryName);
                binding.productTypeTV.setHint(R.string.which_types);
                break;
            case "চার্জার ভ্যান":
            case "Charger van":
                binding.vehicleNameTV.setText(getString(R.string.transport));
                binding.productTypeTV.setHint(getString(R.string.transport));
                break;
        }


        //Date and Time picker
        binding.dateTimeTV.setOnClickListener(v -> {
            CommonClass.showDateTimePicker(requireContext(),3, binding.dateTimeTV);
        });


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_listview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        ListView lv = bottomSheetDialog.findViewById(R.id.listView);
        TextView titleTv = bottomSheetDialog.findViewById(R.id.titleTv);
        titleTv.setVisibility(View.GONE);
        List<String>  array_list = new ArrayList<>();
        lv.setAdapter(new ArrayAdapter((requireContext()), R.layout.single_listview_item, R.id.listItem, array_list));


        binding.capacityTV.setOnClickListener(v -> {
            array_list.clear();
            switch (subCategoryName){
                case "পিকাপ":
                case "Pickup":
                    array_list.add(getString(R.string.seven_feet_one_ton_open));
                    array_list.add(getString(R.string.nine_feet_one_half_ton_open));
                    array_list.add(getString(R.string.twelve_feet_two_ton_open));
                    array_list.add(getString(R.string.forteen_feet_three_half_ton_open));
                    break;
                case "ট্রাক":
                case "Truck":
                    array_list.add(getString(R.string.sixteen_feet_seven_half));
                    array_list.add(getString(R.string.eighteen_feet_fifteen_ton));
                    array_list.add(getString(R.string.twenty_feet_fifteen_ton));
                    array_list.add(getString(R.string.twenty_three_feet_twenty_five_ton));
                    break;
                case "কাভার্ড ভ্যান":
                case "Covered Van":
                    array_list.add(getString(R.string.seven_feet_one_ton_covered));
                    array_list.add(getString(R.string.nine_feet_one_half_ton_covered));
                    array_list.add(getString(R.string.twelve_feet_two_ton_covered));
                    array_list.add(getString(R.string.forteen_feet_three_half_ton_covered));
                    array_list.add(getString(R.string.sixteen_feet_seven_half));
                    array_list.add(getString(R.string.eighteen_feet_fifteen_ton));
                    array_list.add(getString(R.string.twenty_feet_fifteen_ton));
                    array_list.add(getString(R.string.twenty_three_feet_fifteen_ton));
                    break;
                case "ফ্রিজার ভ্যান":
                case "Freezer Van":
                    array_list.add(getString(R.string.twelve_feet));
                    array_list.add(getString(R.string.forteen_feet));
                    array_list.add(getString(R.string.sixteen_feet));
                    array_list.add(getString(R.string.eighteen_feet));
                    break;
                case "ট্রেইলর":
                case "Trailer":
                case "লো বেড":
                case "Lo bet":
                    array_list.add(getString(R.string.twenty_feet));
                    array_list.add(getString(R.string.fourty_feet));
                    break;
                case "কার":
                case "Car":
                    array_list.add(getString(R.string.ac_car));
                    array_list.add(getString(R.string.non_ac_car));
                    break;
                case "মাইক্রোবাস":
                case "Microbus":
                    array_list.add(getString(R.string.ac_microbus));
                    array_list.add(getString(R.string.non_ac_microbus));
                    break;
                case "অ্যাম্বুলেন্স":
                case "Ambulance":
                    array_list.add(getString(R.string.ac_ambulances));
                    array_list.add(getString(R.string.non_ac_ambulances));
                    break;
                case "ড্রাম্প ট্রাক":
                case "Dump Truck":
                    array_list.add(getString(R.string.onehundred_and_twentytwo));
                    array_list.add(getString(R.string.one_hundren_eighty_cft));
                    array_list.add(getString(R.string.two_hundren_cft));
                    array_list.add(getString(R.string.two_hundren_and_fifty_cft));
                    array_list.add(getString(R.string.three_hundred_cft));
                    array_list.add(getString(R.string.four_hundred_cft));
                    array_list.add(getString(R.string.five_hundred_cft));
                    array_list.add(getString(R.string.five_hundred_and_fifty_cft));
                    array_list.add(getString(R.string.six_hundred_cft));
                    array_list.add(getString(R.string.six_hundred_and_fifty_cft));
                    array_list.add(getString(R.string.seven_hundred_cft));
                    array_list.add(getString(R.string.seven_hundred_and_fifty_cft));
                    array_list.add(getString(R.string.eight_hundred));
                    array_list.add(getString(R.string.eight_hundred_and_fifty));
                    break;
                default:
                    array_list.add(getString(R.string.regular));
                    array_list.add(getString(R.string.passenger_carrier));
                    break;
            }

            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.capacityTV.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

        binding.durationTV.setOnClickListener(v -> {
            array_list.clear();
            switch (subCategoryName) {
                case "মাইক্রোবাস":
                case "Microbus":
                case "Car":
                case "কার":
                    array_list.add(getString(R.string.single_trip));
                    array_list.add(getString(R.string.round_trip));
                    array_list.add(getString(R.string.daily));
                    array_list.add(getString(R.string.weekly));
                    array_list.add(getString(R.string.monthly));
                    break;
                case "অ্যাম্বুলেন্স":
                case "Ambulance":
                    array_list.add(getString(R.string.single_trip));
                    array_list.add(getString(R.string.round_trip));
                    break;
                case "ড্রাম্প ট্রাক":
                case "Dump Truck":
                    array_list.add(getString(R.string.trip_based));
                    array_list.add(getString(R.string.body_rent));
                    array_list.add(getString(R.string.daily));
                    array_list.add(getString(R.string.weekly));
                    array_list.add(getString(R.string.monthly));
                    break;
                case "চার্জার ভ্যান":
                case "Charger van":
                    array_list.add(getString(R.string.single_trip));
                    array_list.add(getString(R.string.round_trip));
                    array_list.add(getString(R.string.daily));
                    array_list.add(getString(R.string.weekly));
                    array_list.add(getString(R.string.monthly));
                    break;
                default:
                    array_list.add(getString(R.string.single_trip));
                    array_list.add(getString(R.string.round_trip));
                    array_list.add(getString(R.string.body_rent));
                    array_list.add(getString(R.string.daily));
                    array_list.add(getString(R.string.weekly));
                    array_list.add(getString(R.string.monthly));
                    break;
            }

            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.durationTV.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

        binding.countTV.setOnClickListener(v -> {
            array_list.clear();

            array_list.add(getString(R.string.one));
            array_list.add(getString(R.string.two));
            array_list.add(getString(R.string.three));
            array_list.add(getString(R.string.four));
            array_list.add(getString(R.string.five));

            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.countTV.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

        binding.productTypeTV.setOnClickListener(v -> {
            array_list.clear();
            switch (subCategoryName){
                case "ড্রাম্প ট্রাক":
                case "Dump Truck":
                    array_list.add(getString(R.string.six_wheel));
                    array_list.add(getString(R.string.ten_wheel));
                    break;
                case "ট্রেইলর":
                case "Trailer":
                case "লো বেড":
                case "Lo bet":
                    array_list.add(getString(R.string.container));
                    array_list.add(getString(R.string.others));
                    break;
                case "ফ্রিজার ভ্যান":
                case "Freezer Van":
                    array_list.add(getString(R.string.meat));
                    array_list.add(getString(R.string.fish));
                    array_list.add(getString(R.string.medicine));
                    array_list.add(getString(R.string.others));
                    break;
                case "কার":
                case "Car":
                    array_list.add(getString(R.string.airport_trip));
                    array_list.add(getString(R.string.wedding_trip));
                    array_list.add(getString(R.string.travel_trip));
                    break;
                case "মাইক্রোবাস":
                case "Microbus":
                    array_list.add(getString(R.string.seven_seat));
                    array_list.add(getString(R.string.eleven_seat));
                    break;
                case "অ্যাম্বুলেন্স":
                case "Ambulance":
                    array_list.add(getString(R.string.regular));
                    array_list.add(getString(R.string.bls_ambulances));
                    array_list.add(getString(R.string.als_ambulances));
                    array_list.add(getString(R.string.icu_ambulances));
                    break;
                case "চার্জার ভ্যান":
                case "Charger van":
                    array_list.add(getString(R.string.passenger_transport));
                    array_list.add(getString(R.string.groceries));
                    array_list.add(getString(R.string.feed));
                    array_list.add(getString(R.string.furniture));
                    array_list.add(getString(R.string.household_products));
                    array_list.add(getString(R.string.construction_product));
                    array_list.add(getString(R.string.others));
                    break;
                default:
                    array_list.add(getString(R.string.animal));
                    array_list.add(getString(R.string.feed));
                    array_list.add(getString(R.string.furniture));
                    array_list.add(getString(R.string.glassware));
                    array_list.add(getString(R.string.household_products));
                    array_list.add(getString(R.string.construction_product));
                    array_list.add(getString(R.string.others));
                    break;
            }

            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.productTypeTV.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

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