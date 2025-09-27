package com.dropshep.bdhelper.userFragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentRentLocationFormBinding;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.MyToast;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.OrderHelper;
import com.dropshep.bdhelper.user.AddressActivity;
import com.dropshep.bdhelper.user.SubCategoryActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RentLocationFormFragment extends Fragment {

    private FragmentRentLocationFormBinding binding;

    String categoryId, subCategoryId, subCategoryName;
    String loadLocation = "", unloadLocation="", rentLocation="";
    String userId, userName, userPhone, postDistrict, quantity, description, rentDateAndTime;
    String specificationCapacity, specificationDuration, specificationTypes;

    LoadingDialog loadingDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    final int SHORT_ID_LENGTH = 4;
    private static final long SPLASH_TIME_OUT = 5000;

    Typeface typeface1, typeface2;
    List<String> array_list = new ArrayList<>();

    public RentLocationFormFragment() {
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
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_rent_location_form, container, false);
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
            case "এক্সকাভেটর":
            case "Excavator":
                binding.capacitySizeProgramTv.setText(R.string.size);
                binding.capacityTV.setHint(R.string.size);
                binding.vehicleNameTV.setText(subCategoryName);
                binding.productTypeTV.setHint(R.string.which_types);
                break;
            case "রাইস ট্রান্সপ্লান্টার":
            case "Rice Transplanter":
                binding.capacitySizeProgramTv.setText(R.string.size);
                binding.capacityTV.setHint(R.string.size);
                binding.vehicleNameTV.setText(subCategoryName);
                binding.productTypeTV.setHint(getString(R.string.rice_transplanter)+" "+getString(R.string.which_types));
                break;
            case "ট্রাক্টর":
            case "Tractor":
                binding.vehicleNameTV.setText(subCategoryName);
                binding.productTypeTV.setHint(getString(R.string.tractor)+" "+getString(R.string.which_types));
                break;
            default:
                binding.capacitySizeProgramTv.setText(subCategoryName+" "+getString(R.string.types));
                binding.capacityTV.setHint(R.string.which_types);

                binding.vehicleNameTV.setText(getString(R.string.working));
                binding.productTypeTV.setHint(getString(R.string.working)+" "+getString(R.string.types));
                break;
        }

        //Date and Time picker
        binding.dateTimeTV.setOnClickListener(v -> {
            CommonClass.showDateTimePicker(requireContext(), 3, (displayText, englishDate, millis) -> {
                binding.dateTimeTV.setText(displayText); // লোকেল অনুযায়ী UI
                rentDateAndTime = String.valueOf(millis);    // timestamp
            });
        });

        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_listview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        ListView lv = bottomSheetDialog.findViewById(R.id.listView);
        TextView titleTv = bottomSheetDialog.findViewById(R.id.titleTv);
        titleTv.setVisibility(View.GONE);
        lv.setAdapter(new ArrayAdapter((requireContext()), R.layout.single_listview_item, R.id.listItem, array_list));

        binding.capacityTV.setOnClickListener(v -> {
            array_list.clear();

            switch (subCategoryName){
                case "এক্সকাভেটর":
                case "Excavator":
                    array_list.add(getString(R.string.point_three));
                    array_list.add(getString(R.string.point_five));
                    array_list.add(getString(R.string.point_seven));
                    array_list.add(getString(R.string.point_nine));
                    break;
                case "রাইস ট্রান্সপ্লান্টার":
                case "Rice Transplanter":
                    array_list.add(getString(R.string.four_line_machine));
                    array_list.add(getString(R.string.six_Line_machine));
                    break;
                case "ট্রাক্টর":
                case "Tractor":
                    array_list.add(getString(R.string.standard_tractor));
                    array_list.add(getString(R.string.mini_tractor));
                    break;
                default:
                    array_list.add(getString(R.string.combine_harvester));
                    array_list.add(getString(R.string.specialized_harvester));
                    break;
            }

            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, view1, position, id) -> {
                binding.capacityTV.setText(array_list.get(position));

                if (subCategoryName.equals("ট্রাক্টর") || subCategoryName.equals("Tractor")){
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
            switch (subCategoryName){
                case "এক্সকাভেটর":
                case "Excavator":
                    array_list.add(getString(R.string.trip_based));
                    array_list.add(getString(R.string.body_rent));
                    array_list.add(getString(R.string.daily));
                    array_list.add(getString(R.string.weekly));
                    array_list.add(getString(R.string.monthly));
                    break;
                case "রাইস ট্রান্সপ্লান্টার":
                case "Rice Transplanter":
                    array_list.add(getString(R.string.trip_based));
                    array_list.add(getString(R.string.daily));
                    array_list.add(getString(R.string.weekly));
                    array_list.add(getString(R.string.monthly));
                    break;
                case "ট্রাক্টর":
                case "Tractor":
                    array_list.add(getString(R.string.trip_based));
                    array_list.add(getString(R.string.daily));
                    array_list.add(getString(R.string.weekly));
                    array_list.add(getString(R.string.monthly));
                    break;
                default:
                    array_list.add(getString(R.string.trip_based));
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
                case "এক্সকাভেটর":
                case "Excavator":
                    array_list.add(getString(R.string.short_boom));
                    array_list.add(getString(R.string.long_boom));
                    break;
                case "রাইস ট্রান্সপ্লান্টার":
                case "Rice Transplanter":
                    array_list.add(getString(R.string.riding_type));
                    array_list.add(getString(R.string.walking_type));
                    break;
                case "ট্রাক্টর":
                case "Tractor":
                    String selectedCapacity = (String) binding.productTypeTV.getTag(); // capacity stored in Tag

                    if (selectedCapacity != null) {
                        if (selectedCapacity.equals(getString(R.string.mini_tractor))) {
                            array_list.add(getString(R.string.power_tiller));
                        } else if (selectedCapacity.equals(getString(R.string.standard_tractor))) {
                            array_list.add(getString(R.string.utility_tractor));
                            array_list.add(getString(R.string.compact_tractor));
                        }
                    }
                    else {
                        array_list.add(getString(R.string.power_tiller));
                        array_list.add(getString(R.string.utility_tractor));
                        array_list.add(getString(R.string.compact_tractor));
                    }
                    break;
                default:
                    array_list.add(getString(R.string.rice_harvesting));
                    array_list.add(getString(R.string.wheat_harvesting));
                    array_list.add(getString(R.string.corn_harvesting));
                    array_list.add(getString(R.string.soybean_harvesting));
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


    private void continueToReview() {
        // ✅ Validate fields
        if (CommonClass.validateField(binding.dateTimeTV)) return;
        if (CommonClass.validateField(binding.capacityTV)) return;
        if (CommonClass.validateField(binding.durationTV)) return;
        if (CommonClass.validateField(binding.countTV)) return;
        if (CommonClass.validateField(binding.productTypeTV)) return;

        // ✅ Collect values
        specificationCapacity = binding.capacityTV.getText().toString().trim();
        specificationDuration = binding.durationTV.getText().toString().trim();
        specificationTypes = binding.productTypeTV.getText().toString().trim();
        quantity = binding.countTV.getText().toString().trim();
        description = binding.detailsET.getText().toString().trim();

        // ✅ Setup BottomSheet
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.layout_submit_post);

        ImageView iconView = dialog.findViewById(R.id.iconImageViewSub);
        TextView size = dialog.findViewById(R.id.sizeCapacityTV);
        TextView count = dialog.findViewById(R.id.totalCountTV);
        TextView duration = dialog.findViewById(R.id.popupDurationTV);
        TextView product = dialog.findViewById(R.id.productTV);
        TextView sizeDef = dialog.findViewById(R.id.sizeCapacityDefTV);
        TextView countDef = dialog.findViewById(R.id.totalCountDefTV);
        TextView durationDef = dialog.findViewById(R.id.popupDurationDefTV);
        TextView productDef = dialog.findViewById(R.id.productDefTV);
        TextView time = dialog.findViewById(R.id.popupTimeTV);
        TextView submitBtn = dialog.findViewById(R.id.postSubmitBtn);

        RelativeLayout loadLocationRl = dialog.findViewById(R.id.loadLocationRl);
        RelativeLayout unloadLocationRl = dialog.findViewById(R.id.unloadLocationRl);
        RelativeLayout areaLocationRl = dialog.findViewById(R.id.areaLocationRl);

        TextView locationTv = dialog.findViewById(R.id.locationTv);
        TextView unloadLocationTv = dialog.findViewById(R.id.unloadLocationTv);
        TextView areaLocationTv = dialog.findViewById(R.id.areaLocationTv);

        dialog.show();


        // ✅ Toggle visibility
        boolean isEquipment = categoryId.equals(MyUtils.EQUIPMENT_ID) || categoryId.equals(MyUtils.HARVESTER_MACHINE_ID);
        if (loadLocationRl != null && unloadLocationRl != null && areaLocationRl != null) {
            loadLocationRl.setVisibility(isEquipment ? View.GONE : View.VISIBLE);
            unloadLocationRl.setVisibility(isEquipment ? View.GONE : View.VISIBLE);
            areaLocationRl.setVisibility(isEquipment ? View.VISIBLE : View.GONE);
        }

        // ✅ Set texts safely
        if (countDef != null) countDef.setText(subCategoryName);
        if (count != null) count.setText(quantity);

        if (locationTv != null) locationTv.setText(loadLocation);
        if (unloadLocationTv != null) unloadLocationTv.setText(unloadLocation);
        if (areaLocationTv != null) areaLocationTv.setText(rentLocation);

        if (sizeDef != null) sizeDef.setText(getString(R.string.size_dot));
        if (size != null) size.setText(specificationCapacity);

        if (durationDef != null) durationDef.setText(getString(R.string.duration_dot));
        if (duration != null) duration.setText(specificationDuration);

        if (productDef != null) productDef.setText(getString(R.string.product_type));
        if (product != null) product.setText(specificationTypes);

        if (time != null) time.setText(binding.dateTimeTV.getText().toString());

        // ✅ Set icon dynamically
        if (iconView != null) {
            int iconRes = getIconForSubCategory(subCategoryId);
            if (iconRes != 0) {
                iconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), iconRes));
            }
        }

        // ✅ Submit
        if (submitBtn != null) {
            submitBtn.setOnClickListener(v -> {
                dialog.dismiss();
                postForRent();
            });
        }


    }

    // Helper method to get correct drawable for subCategory
    private int getIconForSubCategory(String subCatId) {
        switch (subCatId) {
            case MyUtils.SUB_EXCAVATOR_ID: return R.drawable.ic_excavator;
            case MyUtils.SUB_RICE_TRANSPLANTER_ID: return R.drawable.ic_rice_transplanter;
            case MyUtils.SUB_TRACTOR_ID: return R.drawable.ic_tractor;
            case MyUtils.HARVESTER_MACHINE_ID: return R.drawable.ic_harvester;
            default: return 0;
        }
    }

    private void postForRent() {
        loadingDialog.setMessage("অর্ডার সাবমিট হচ্ছে...");
        loadingDialog.show();

        String orderId = ""+System.currentTimeMillis();

        Map<String, Object> order = OrderHelper.createOrder(
                orderId,
                userId,
                userName,
                userPhone,
                categoryId,
                subCategoryId,
                "",
                "",
                rentLocation,
                rentDateAndTime,
                specificationCapacity,
                specificationDuration,
                specificationTypes,
                quantity,
                description,
                postDistrict
        );

        Log.d("UserInfo", "order: " + order);

        db.collection("orders")
                .document(orderId)
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    loadingDialog.dismiss();
                    //MyToast.showShort(getContext(), "✅ Order Submitted");

                    binding.mainBodyLl.setVisibility(View.GONE);
                    binding.donePostRent.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(()->{
                        // ✅ Submit Success হলে
                        Intent intent;
                        if (categoryId.equals(MyUtils.HARVESTER_MACHINE_ID)){
                            intent = new Intent(requireContext(), AddressActivity.class);
                        }
                        else {
                            intent = new Intent(requireContext(), SubCategoryActivity.class);
                        }

                        // চাইলে extra পাঠাতে পারো
                        intent.putExtra(MyUtils.categoryId, categoryId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        // এই fragment/activity বন্ধ হয়ে যাবে
                        requireActivity().finish();
                        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    },SPLASH_TIME_OUT);

                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    MyToast.showShort(getContext(), "❌ Error: " + e.getMessage());
                });

    }

}