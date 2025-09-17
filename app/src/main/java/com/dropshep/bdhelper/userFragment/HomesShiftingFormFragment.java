package com.dropshep.bdhelper.userFragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentHomesShiftingFormBinding;
import com.dropshep.bdhelper.myUtils.CommonClass;
import com.dropshep.bdhelper.myUtils.LoadingDialog;
import com.dropshep.bdhelper.myUtils.LocaleHelper;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomesShiftingFormFragment extends Fragment {

    private FragmentHomesShiftingFormBinding binding;

    private String categoryId, subCategoryId, subCategoryName;
    private String loadLocation = "", unloadLocation="";

    private String userId, postDistrict, quantity, description, rentDate, rentTime;
    private String specification, specificationCapacity, specificationDuration, specificationTypes;

    LoadingDialog loadingDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    final int SHORT_ID_LENGTH = 4;
    private static final long SPLASH_TIME_OUT = 5000;

    Typeface typeface1, typeface2;
    List<String> array_list = new ArrayList<>();


    public HomesShiftingFormFragment() {
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
        typeface1 = ResourcesCompat.getFont(requireContext(), R.font.solaimanlipi);
        typeface2 = ResourcesCompat.getFont(requireContext(), R.font.open_sans_regular);

        loadingDialog = new LoadingDialog(requireContext());
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        //current user info
        if (firebaseAuth.getCurrentUser() != null){
            getUserInfo();
        }


        //Radio Button
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioBachelor){
                //Home Bachelor
                binding.roomTypeTitleTv.setText(getString(R.string.room_type));
                binding.roomTypeTv.setHint(getString(R.string.room_type));
            }
            else if (checkedId == R.id.radioHome){
                //Home Family
                binding.roomTypeTitleTv.setText(getString(R.string.room_type));
                binding.roomTypeTv.setHint(getString(R.string.room_type));
            }
            else if (checkedId == R.id.radioOffice){
                //Office
                binding.roomTypeTitleTv.setText("অফিসের সাইজ");
                binding.roomTypeTv.setHint("অফিসের সাইজ");
            }
        });

        //shifting date and time
        binding.shiftingDateEt.setOnClickListener(v -> {
            CommonClass.showDateTimePicker(requireContext(), 30, binding.shiftingDateEt);
        });

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
        lv.setAdapter(new ArrayAdapter((requireContext()), R.layout.single_listview_item, R.id.listItem, array_list));


        binding.roomTypeTv.setOnClickListener(v -> {
            titleTv.setText(getString(R.string.room_type));
            getRoomList();
            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.roomTypeTv.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

        //loading Floor
        binding.loadingFloorEt.setOnClickListener(v -> {
            titleTv.setText(getString(R.string.floor_info));
            getFloorList();
            bottomSheetDialog.show();
            lv.setOnItemClickListener((parent, viewList, position, id) -> {
                binding.loadingFloorEt.setText(array_list.get(position));
                bottomSheetDialog.dismiss();
            });
        });

        //UnLoading Floor
        binding.unloadingFloorEt.setOnClickListener(v -> {
            titleTv.setText(getString(R.string.floor_info));
            getFloorList();
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
        if (lang.equals("bn")){
            binding.minuteText.setText(CommonClass.toBanglaNumber(defaultProgress * 5) + " মিনিট");
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
                    binding.minuteText.setText(CommonClass.toBanglaNumber(minutes) + " মিনিট");
                }
                else {
                    binding.minuteText.setText(minutes + " minutes");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

    }

    private void getFloorList() {
        array_list.clear();
        array_list.add(getString(R.string.ground_floor));
        array_list.add(getString(R.string.second_floor));
        array_list.add(getString(R.string.third_floor));
        array_list.add(getString(R.string.fourth_floor));
        array_list.add(getString(R.string.fifth_floor));
        array_list.add(getString(R.string.sixth_floor));
        array_list.add(getString(R.string.seventh_floor));
        array_list.add(getString(R.string.eighth_floor));
        array_list.add(getString(R.string.ninth_floor));
        array_list.add(getString(R.string.tenth_floor));
        array_list.add(getString(R.string.tenth_plus_floor));
    }

    //Only Home shifting Then show  Room Requirement
    private void getRoomList() {

        array_list.clear();
        array_list.add(getString(R.string.one_room));
        array_list.add(getString(R.string.two_room));
        array_list.add(getString(R.string.three_room));
        array_list.add(getString(R.string.four_room));
        array_list.add(getString(R.string.six_room));
        array_list.add(getString(R.string.seven_room));
        array_list.add(getString(R.string.eight_room));
        array_list.add(getString(R.string.nine_room));
        array_list.add(getString(R.string.ten_room));
        array_list.add(getString(R.string.more_room));

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