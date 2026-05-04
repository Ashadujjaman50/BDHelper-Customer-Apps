package com.krishibarirangpur.bdhelper.userFragment.customer.requirementFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentLoadUnloadFormBinding;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.customer.GenerateOrderId;
import com.krishibarirangpur.bdhelper.utils.customer.SubmitPostBottomSheetDialog;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.CustomDateAndTimePicker;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.NoticeSend;
import com.krishibarirangpur.bdhelper.utils.firebase.OrderMapBuilder;
import com.krishibarirangpur.bdhelper.userActivity.customer.AddressActivity;
import com.krishibarirangpur.bdhelper.userActivity.customer.SubCategoryActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.ValidationClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LoadUnloadFormFragment extends Fragment{

    private FragmentLoadUnloadFormBinding binding;

    String categoryId, subCategoryId, subCategoryName;
    String loadLocation = "", unloadLocation="";
    String userId, userName, userPhone, postDistrict, quantity, description, rentDateAndTime;
    String specificationCapacity, specificationDuration, specificationTypes;

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
        typeface1 = ResourcesCompat.getFont(requireContext(), R.font.solaiman_lipi);
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
            case "ট্রেইলর":
            case "Trailer":
                binding.productTypeTV.setHint(R.string.container);
                break;
            case "লো বেড":
            case "Lo bet":
                binding.productTypeTV.setHint(R.string.harvester);
                break;
            case "চার্জার ভ্যান":
            case "Charger van":
                binding.vehicleNameTV.setText(getString(R.string.transport));
                binding.productTypeTV.setHint(getString(R.string.transport));
                break;
        }


        //Date and Time picker
        binding.dateTimeTV.setOnClickListener(v -> {
            CustomDateAndTimePicker.showDateTimePicker(requireContext(), 3, (displayText, englishDate, millis) -> {
                binding.dateTimeTV.setText(displayText); // লোকেল অনুযায়ী UI
                rentDateAndTime = String.valueOf(millis);    // timestamp
                Log.d("UserInfo", "rentDateAndTime: " + rentDateAndTime);
            });
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
                    array_list.add(getString(R.string.eight_feet_one_ton_open));
                    array_list.add(getString(R.string.nine_feet_one_half_ton_open));
                    array_list.add(getString(R.string.ten_feet_one_half_ton_open));
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
                    array_list.add(getString(R.string.twenty_feet));
                    array_list.add(getString(R.string.fourty_feet));
                    break;
                case "লো বেড":
                case "Lo bet":
                    array_list.add(getString(R.string.forteen_feet));
                    array_list.add(getString(R.string.sixteen_feet));
                    array_list.add(getString(R.string.eighteen_feet));
                    array_list.add(getString(R.string.twenty_feet));
                    array_list.add(getString(R.string.twenty_four_feet));
                    array_list.add(getString(R.string.twenty_six_feet));
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
                    array_list.add(getString(R.string.regular_ambulance));
                    array_list.add(getString(R.string.hearse_ambulance));
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

                if (subCategoryName.equals("অ্যাম্বুলেন্স") || subCategoryName.equals("Ambulance")){
                    String selectedCapacity = array_list.get(position);
                    // ✅ Capacity অনুযায়ী ProductType এর options reset করে রাখা
                    binding.productTypeTV.setText(""); // clear old value
                    binding.productTypeTV.setTag(selectedCapacity); // save selected capacity
                }

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
                    array_list.add(getString(R.string.container));
                    array_list.add(getString(R.string.others));
                    break;
                case "লো বেড":
                case "Lo bet":
                    array_list.add(getString(R.string.harvester));
                    array_list.add(getString(R.string.excavator));
                    array_list.add(getString(R.string.tractor));
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
                    String selectedCapacity = (String) binding.productTypeTV.getTag(); // capacity stored in Tag

                    if (selectedCapacity != null){
                        if (selectedCapacity.equals(getString(R.string.regular_ambulance))){
                            array_list.add(getString(R.string.bls_ambulances));
                            array_list.add(getString(R.string.als_ambulances));
                            array_list.add(getString(R.string.icu_ambulances));
                        }
                        else {
                            array_list.add(getString(R.string.regular));
                        }
                    }
                    else {
                        array_list.add(getString(R.string.regular));
                        array_list.add(getString(R.string.bls_ambulances));
                        array_list.add(getString(R.string.als_ambulances));
                        array_list.add(getString(R.string.icu_ambulances));
                    }
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

        binding.continuePostBtn.setOnClickListener(v -> {
            continueToReview();
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
                        userName = documentSnapshot.getString("name");
                        userPhone = documentSnapshot.getString("phone");

                        if (postDistrict != null) {
                            // UI তে দেখাও বা Log করো
                            Log.d("UserInfo", "Username: " + userName);
                            Log.d("UserInfo", "UserPhone: " + userPhone);
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



    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void continueToReview() {
        // ✅ Validate fields
        if (ValidationClass.validateField(binding.dateTimeTV)) return;
        if (ValidationClass.validateField(binding.capacityTV)) return;
        if (ValidationClass.validateField(binding.durationTV)) return;
        if (ValidationClass.validateField(binding.countTV)) return;
        if (ValidationClass.validateField(binding.productTypeTV)) return;

        // ✅ Collect values
        specificationCapacity = binding.capacityTV.getText().toString().trim();
        specificationDuration = binding.durationTV.getText().toString().trim();
        specificationTypes = binding.productTypeTV.getText().toString().trim();
        quantity = binding.countTV.getText().toString().trim();
        description = binding.detailsET.getText().toString().trim();


        // ✅ Setup BottomSheet
        //After validation check submit
        SubmitPostBottomSheetDialog.show(this,
                categoryId,
                subCategoryId,
                subCategoryName,
                quantity,
                loadLocation,
                unloadLocation,
                "",
                specificationCapacity,
                specificationDuration,
                specificationTypes,
                description,
                "",
                binding.dateTimeTV.getText().toString(),
                this::postForRent);

    }

    private void postForRent() {
        loadingDialog.setMessage("অর্ডার সাবমিট হচ্ছে...");
        loadingDialog.show();

        // 🔽 CommonClass থেকে OrderId জেনারেট করব
        GenerateOrderId.newOrderId(
                db,
                "orders",
                "orderInfo.orderId",
                "BOL",
                5,
                new GenerateOrderId.OrderIdCallback() {
            @Override
            public void onSuccess(String orderId) {
                // 🔽 Order Map তৈরি
                Map<String, Object> order = OrderMapBuilder.createOrderMap(
                        orderId,
                        userId,
                        userName,
                        userPhone,
                        categoryId,
                        subCategoryId,
                        loadLocation,
                        unloadLocation,
                        "",
                        "",
                        rentDateAndTime,
                        specificationCapacity,
                        specificationDuration,
                        specificationTypes,
                        quantity,
                        description,
                        postDistrict
                );

                // 🔽 এবার Firestore এ সেভ করব
                db.collection("orders")
                        .document(orderId)
                        .set(order)
                        .addOnSuccessListener(aVoid -> {
                            loadingDialog.dismiss();
                            binding.mainBodyLl.setVisibility(View.GONE);
                            binding.donePostRent.setVisibility(View.VISIBLE);

                            //Send Custom Notice
                            sendCustomNotice(
                                    userId,
                                    orderId,
                                    subCategoryId,
                                    rentDateAndTime,
                                    quantity
                            );

                            new Handler().postDelayed(() -> {
                                Intent intent;
                                if (subCategoryId.equals(MyUtils.SUB_LOW_BED_ID)) {
                                    intent = new Intent(requireContext(), AddressActivity.class);
                                } else {
                                    intent = new Intent(requireContext(), SubCategoryActivity.class);
                                }
                                intent.putExtra(MyUtils.categoryId, categoryId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                requireActivity().finish();
                                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }, SPLASH_TIME_OUT);

                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            Log.d("Last Order", "Order: " + e.getMessage());
                            MyToast.showShort(getContext(), "❌ Error: " + e.getMessage());
                        });
            }

            @Override
            public void onFailure(Exception e) {
                loadingDialog.dismiss();
                Log.d("Last Order", "OrderID: " + e.getMessage());
                //MyToast.showShort(getContext(), "❌ Failed to generate orderId: " + e.getMessage());
            }
        });
    }

    private void sendCustomNotice( String currentUserId, String orderId, String subCategoryId, String rentDateAndTime,String quantity) {

        String sender = MyUtils.NOTICE_SENDER_CUSTOMER;
        String subCatName = CommonClass.getSubCategoryName(requireContext(), subCategoryId);
        String noticeType = MyUtils.NOTICE_TYPE_POST;
        String date = CommonClass.formatTime(rentDateAndTime, "dd MMMM");
        String qty = Replacement.ReplacementNumberEnToBn(quantity)+" টি";

        String messageForUser = "“"+loadLocation+ "” থেকে “" + unloadLocation +"” \n" + qty+ " " + subCatName +" লাগবে ";
        String messageForAdmin = date +" “"+loadLocation+ "” থেকে “" + unloadLocation +"”\n" + qty+ " " + subCatName +" লাগবে ";

        // 🔹 Send to User
        NoticeSend.sendNotice(
                sender,
                noticeType,
                currentUserId,
                MyUtils.NOTICE_RECEIVER_PARTNER,
                orderId,
                messageForUser
        );

        // 🔹 Send to Admin
        NoticeSend.sendNotice(
                sender,
                noticeType,
                currentUserId,
                MyUtils.NOTICE_RECEIVER_ADMIN,
                orderId,
                messageForAdmin
        );
    }


}