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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ashadujjaman.loadingdialog.LoadingDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentHomesShiftingFormBinding;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.customer.GenerateOrderId;
import com.krishibarirangpur.bdhelper.utils.customer.SubmitPostBottomSheetDialog;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.CustomDateAndTimePicker;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyToast;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.NoticeSend;
import com.krishibarirangpur.bdhelper.utils.firebase.OrderMapBuilder;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.userActivity.customer.AddressActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.ValidationClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomesShiftingFormFragment extends Fragment {

    private FragmentHomesShiftingFormBinding binding;

    private String categoryId, subCategoryId;
    private String loadLocation = "", unloadLocation="";

    private String userId, userName, userPhone, postDistrict, quantity, description, rentDateAndTime;
    private String specificationCapacity, specificationDuration, specificationTypes;

    LoadingDialog loadingDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    String selectedShifting;

    private static final long SPLASH_TIME_OUT = 5000;

    Typeface typeface1, typeface2;
    List<String> array_list = new ArrayList<>();

    String truckAccess = "", truckAccessTime;
    String loadFloor, unLoadFloor;


    public HomesShiftingFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            categoryId = getArguments().getString(MyUtils.categoryId);
            subCategoryId = getArguments().getString(MyUtils.subCategoryId);
            loadLocation = getArguments().getString(MyUtils.loadLocation);
            unloadLocation = getArguments().getString(MyUtils.unloadLocation);

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_homes_shifting_form, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inti view
        init();
    }

    @SuppressLint("SetTextI18n")
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


        // Default selected radio button এর text পাওয়া
        int defaultCheckedId = binding.radioGroup.getCheckedRadioButtonId();
        if (defaultCheckedId != -1) { // কোনো button select আছে কিনা চেক
            RadioButton defaultRadio = binding.radioGroup.findViewById(defaultCheckedId);
            if (defaultRadio != null) {
                String defaultText = defaultRadio.getText().toString();
                selectedShifting = defaultText;
                binding.roomTypeTitleTv.setText(defaultText);
                binding.roomTypeTv.setHint(defaultText);
            }
        }

        // RadioGroup listener
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadio = group.findViewById(checkedId);
            if (selectedRadio != null) {
                selectedShifting = selectedRadio.getText().toString();
                binding.roomTypeTitleTv.setText(selectedShifting);
                binding.roomTypeTv.setHint(selectedShifting);
            }
        });


        //shifting date and time
        binding.shiftingDateEt.setOnClickListener(v -> CustomDateAndTimePicker.showDateTimePicker(requireContext(), 3, (displayText, returnDate, millis) -> {
            binding.shiftingDateEt.setText(displayText);  // লোকেল অনুযায়ী UI
            rentDateAndTime = String.valueOf(millis);    // timestamp
        }));


        //Bottom sheet Dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_dialog_listview,
                        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet),
                        false);
        bottomSheetDialog.setContentView(view);

        // ---- Fix height 400dp ----
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400,
                    requireContext().getResources().getDisplayMetrics()
            );
            bottomSheet.getLayoutParams().height = heightInPx;
            bottomSheet.requestLayout();
            behavior.setPeekHeight(heightInPx); // fixed height
            behavior.setDraggable(false);       // চাইলে drag বন্ধ করে দিতে পারো
        }

        ListView lv = bottomSheetDialog.findViewById(R.id.listView);
        TextView titleTv = bottomSheetDialog.findViewById(R.id.titleTv);
        assert titleTv != null;
        assert lv != null;
        ArrayAdapter<String> adapter = new ArrayAdapter<>((requireContext()), R.layout.single_listview_item, R.id.listItem, array_list);
        lv.setAdapter(adapter);


        binding.roomTypeTv.setOnClickListener(v -> {
            titleTv.setText(getString(R.string.room_type));
            updateRoomList();
            adapter.notifyDataSetChanged();
            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.roomTypeTv.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

        //loading Floor
        binding.loadingFloorEt.setOnClickListener(v -> {
            titleTv.setText(getString(R.string.floor_info));
            updateFloorList();
            adapter.notifyDataSetChanged();
            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.loadingFloorEt.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

        //UnLoading Floor
        binding.unloadingFloorEt.setOnClickListener(v -> {
            titleTv.setText(getString(R.string.floor_info));
            updateFloorList();
            adapter.notifyDataSetChanged();
            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.unloadingFloorEt.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

        //Truck Access Radio Button Group
        binding.truckAccessRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.yesRB){
                binding.distanceTimeLl.setVisibility(View.GONE);
            }
            else if (checkedId == R.id.noRB){
                binding.distanceTimeLl.setVisibility(View.VISIBLE);
            }
        });


        //SeekBar ডিফল্ট 5 মিনিট সেট করা
        String lang = LocaleHelper.getLanguage(requireContext());
        int defaultProgress = 1; // 1 * 5 = 5 মিনিট
        binding.timeSeekBar.setProgress(defaultProgress);
        truckAccessTime = (defaultProgress * 5)  + " minutes";
        if (lang.equals("bn")){
            binding.minuteText.setText(Replacement.ReplacementNumberEnToBnInInteger(defaultProgress * 5) + " মিনিট");
        }
        else {
            binding.minuteText.setText((defaultProgress * 5)  + " minutes");
        }

        //SeekBar (Truck Not come in front of the house) visible this
        binding.timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int minutes = progress * 5; // প্রতি স্টেপে ৫ মিনিট
                if (lang.equals("bn")){
                    binding.minuteText.setText(Replacement.ReplacementNumberEnToBnInInteger(minutes) + " মিনিট");
                }
                else {
                    binding.minuteText.setText(minutes + " minutes");
                }
                truckAccessTime = minutes+ " minutes";

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        binding.continuePostBtn.setOnClickListener(v -> continueToReview());

    }

    private void updateFloorList() {
        array_list.clear();
        array_list.addAll(CommonClass.getFloorList(requireContext()));
    }

    private void updateRoomList() {
        array_list.clear();
        array_list.addAll(CommonClass.getRoomList(requireContext()));
    }

    private void getUserInfo() {
        db.collection(FirebaseCollectionTable.USERS)
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
                .addOnFailureListener(e -> Log.e("UserInfo", "Error fetching district", e));
    }

    @SuppressLint("SetTextI18n")
    private void continueToReview() {
        // ✅ Validate fields
        // Get selected radio button id
        int selectedId = binding.truckAccessRG.getCheckedRadioButtonId();

        if (ValidationClass.validateField(binding.shiftingDateEt)) return;
        if (ValidationClass.validateField(binding.roomTypeTv)) return;
        if (ValidationClass.validateField(binding.loadingFloorEt)) return;
        if (ValidationClass.validateField(binding.unloadingFloorEt)) return;
        if (selectedId == -1) {
            // কিছুই সিলেক্ট করা হয়নি
            MyToast.showShort(getContext(), "বাসার সামনে ট্রাক আসবে কি না সিলেক্ট করুন");
            return; // validation failed
        }

        if (selectedId == R.id.yesRB) {
            truckAccess = getString(R.string.yes);
            quantity ="yes";
        }
        else if (selectedId == R.id.noRB) {
            truckAccess = binding.minuteText.getText().toString().trim();
            quantity = truckAccessTime;
        }

        // ✅ Collect values
        specificationCapacity = binding.roomTypeTv.getText().toString().trim();
        specificationDuration = "";
        specificationTypes = selectedShifting;

        description = binding.detailsET.getText().toString().trim();
        loadFloor = binding.loadingFloorEt.getText().toString().trim();
        unLoadFloor = binding.unloadingFloorEt.getText().toString().trim();


        // ✅ Setup BottomSheet
        //After validation check submit
        SubmitPostBottomSheetDialog.show(this,
                categoryId,
                subCategoryId,
                specificationCapacity,     //subCategoryName == truckAccess
                quantity,
                loadLocation+"\n"+loadFloor,
                unloadLocation+"\n"+unLoadFloor,
                "",
                specificationTypes,
                specificationDuration,
                truckAccess,
                description,
                "",
                binding.shiftingDateEt.getText().toString(),
                this::postForRent);


    }

    private void postForRent() {
        loadingDialog.setMessage("অর্ডার সাবমিট হচ্ছে...");
        loadingDialog.show();


        // 🔽 CommonClass থেকে OrderId জেনারেট করব
        GenerateOrderId.newOrderId(
                db,
                FirebaseCollectionTable.ORDERS,
                "orderInfo.orderId",
                "BOL",
                5,
                new GenerateOrderId.OrderIdCallback() {
            @Override
            public void onSuccess(String orderId) {
                Map<String, Object> order = OrderMapBuilder.createOrderMap(
                        orderId,
                        userId,
                        userName,
                        userPhone,
                        categoryId,
                        subCategoryId,
                        loadLocation+"\n"+loadFloor,
                        unloadLocation+"\n"+unLoadFloor,
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

                db.collection(FirebaseCollectionTable.ORDERS)
                        .document(orderId)
                        .set(order)
                        .addOnSuccessListener(aVoid -> {
                            loadingDialog.dismiss();
                            //MyToast.showShort(getContext(), "✅ Order Submitted");

                            binding.mainBodyLl.setVisibility(View.GONE);
                            binding.donePostRent.setVisibility(View.VISIBLE);

                            //Send Custom Notice
                            sendCustomNotice(
                                    userId,
                                    orderId,
                                    rentDateAndTime,
                                    specificationCapacity
                            );

                            new Handler().postDelayed(()->{
                                // ✅ Submit Success হলে
                                Intent intent = new Intent(requireContext(), AddressActivity.class);
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

    private void sendCustomNotice( String currentUserId, String orderId, String rentDateAndTime,String quantity) {

        String sender = MyUtils.NOTICE_SENDER_CUSTOMER;
        String noticeType = MyUtils.NOTICE_TYPE_POST;
        String date = CommonClass.formatTime(rentDateAndTime, "dd MMMM");
        String qty = Replacement.ReplacementNumberEnToBn(quantity)+" এর ";

        String dec = "“"+loadLocation+ "” থেকে “" + unloadLocation +"”\n" + qty+ specificationTypes+ " শিফট করবে";
        dec = dec.replace("শিফট শিফট", "শিফট");

        String messageForUser = dec;
        String messageForAdmin = date +"তে "+dec;

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
