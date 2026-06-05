package com.krishibarirangpur.bdhelper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.customer.DistrictAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentAddServiceFormBinding;
import com.krishibarirangpur.bdhelper.utils.core.LocaleHelper;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.Arrays;
import java.util.List;

public class ServiceFormHelper {

    private final Context context;
    private final FragmentAddServiceFormBinding binding;

    public ServiceFormHelper(Context context, FragmentAddServiceFormBinding binding) {
        this.context = context;
        this.binding = binding;
    }

    public void hideAllLayouts() {
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

    public void showLayoutByCategory(String categoryId) {
        if (categoryId == null) return;
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
    public void showLayoutBySubCategory(String subCategoryId, String subCategoryName) {
        if (subCategoryId == null) return;
        switch (subCategoryId) {
            case MyUtils.SUB_CAR_ID:
            case MyUtils.SUB_MICROBUS_ID:
                binding.carAndMicroLL.setVisibility(View.VISIBLE);
                break;
            case MyUtils.SUB_AMBULANCE_ID:
                binding.ambulanceVanTv.setText(subCategoryName + " " + context.getString(R.string.types));
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

    public void setupSubCategoryUI(String subCategoryId) {
        if (subCategoryId == null) return;
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
    }

    public boolean validateFields(List<Pair<TextView, String>> fields) {
        for (Pair<TextView, String> field : fields) {
            if (TextUtils.isEmpty(field.second)) {
                setErrorWatcher(field.first, true);
                return false;
            }
        }
        return true;
    }

    public void setErrorWatcher(View view, boolean hasError) {
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

    public int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public interface OnItemSelectedListener {
        void onSelected(String item, int position);
    }

    public void popupMetroList(OnItemSelectedListener listener) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_recycleview, null);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        titleTv.setText(context.getString(R.string.select_metro));

        String[] districtListEng = MyUtils.METRO_LIST_ENG;
        String[] districtListBan = MyUtils.METRO_LIST_BAN;
        boolean isBangla = LocaleHelper.getLanguage(context).equals("bn");
        String[] displayList = isBangla ? districtListBan : districtListEng;

        DistrictAdapter adapter = new DistrictAdapter(Arrays.asList(displayList), (item, position) -> {
            String selectedItem = isBangla ? districtListEng[position] : item;
            listener.onSelected(selectedItem, position);
            binding.metroNameEt.setText(item);
            setErrorWatcher(binding.metroNameEt, false);
            bottomSheetDialog.dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                int heightPx = dpToPx(400);
                bottomSheet.getLayoutParams().height = heightPx;
                bottomSheet.requestLayout();
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(heightPx);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetDialog.show();
    }

    public void popupSerialCategory(OnItemSelectedListener listener) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_recycleview, null);
        bottomSheetDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.titleTv);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        titleTv.setText(context.getString(R.string.select_serial));

        String[] serialEng = MyUtils.SERIAL_ENG;
        String[] serialBan = MyUtils.SERIAL_BAN;
        boolean isBangla = LocaleHelper.getLanguage(context).equals("bn");
        String[] displayList = isBangla ? serialBan : serialEng;

        DistrictAdapter adapter = new DistrictAdapter(Arrays.asList(displayList), (item, position) -> {
            String selectedItem = isBangla ? serialEng[position] : item;
            listener.onSelected(selectedItem, position);
            binding.serialCategoryEt.setText(item);
            setErrorWatcher(binding.serialCategoryEt, false);
            bottomSheetDialog.dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        bottomSheetDialog.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                int heightPx = dpToPx(400);
                bottomSheet.getLayoutParams().height = heightPx;
                bottomSheet.requestLayout();
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(heightPx);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetDialog.show();
    }

    public void popupHarvesterCategory() {
        PopupMenu popupMenu = new PopupMenu(context, binding.harvesterCategoryEt, Gravity.END);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, context.getString(R.string.full_feed));
        popupMenu.getMenu().add(Menu.NONE, 1, 0, context.getString(R.string.half_feed));

        popupMenu.setOnMenuItemClickListener(item -> {
            binding.harvesterCategoryEt.setText(item.getTitle());
            setErrorWatcher(binding.harvesterCategoryEt, false);
            return false;
        });
        popupMenu.show();
    }

    public void popupLowBetSizeCategory() {
        PopupMenu popupMenu = new PopupMenu(context, binding.lowBedSizeEt, Gravity.END);
        String[] sz = {"১৪ ফিট", "১৬ ফিট", "১৮ ফিট", "২০ ফিট", "২৪ ফিট", "২৬ ফিট", "৪০ ফিট"};
        for (int i = 0; i < sz.length; i++) popupMenu.getMenu().add(Menu.NONE, i, 0, sz[i]);

        popupMenu.setOnMenuItemClickListener(item -> {
            binding.lowBedSizeEt.setText(item.getTitle());
            setErrorWatcher(binding.lowBedSizeEt, false);
            return false;
        });
        popupMenu.show();
    }

    public void popupAmbulanceCategory() {
        PopupMenu popupMenu = new PopupMenu(context, binding.ambulanceCategoryEt, Gravity.END);
        String[] categories = {"রেগুলার", "বেসিক লাইফ সাপোর্ট (BLS)", "অ্যাডভান্সড লাইফ সাপোর্ট (ALS)", "আইসিইউ অ্যাম্বুলেন্স"};
        for (int i = 0; i < categories.length; i++) popupMenu.getMenu().add(Menu.NONE, i, 0, categories[i]);

        popupMenu.setOnMenuItemClickListener(item -> {
            binding.ambulanceCategoryEt.setText(item.getTitle());
            setErrorWatcher(binding.ambulanceCategoryEt, false);
            return false;
        });
        popupMenu.show();
    }

    public void popupChargerVanType(String subCategoryId) {
        PopupMenu popupMenu = new PopupMenu(context, binding.ambulanceVanTypeEt, Gravity.END);
        if (MyUtils.SUB_CHARGER_VAN_ID.equals(subCategoryId)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "রেগুলার");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "যাত্রী বাহী");
        } else {
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "এসি অ্যাম্বুলেন্স");
            popupMenu.getMenu().add(Menu.NONE, 3, 0, "নন-এসি অ্যাম্বুলেন্স");
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            binding.ambulanceVanTypeEt.setText(item.getTitle());
            setErrorWatcher(binding.ambulanceVanTypeEt, false);
            return false;
        });
        popupMenu.show();
    }

    public void popupServiceSizeAndCapacity(String subCategoryName) {
        PopupMenu popupMenu = new PopupMenu(context, binding.serviceSizeAndCapacityEt, Gravity.END);
        if (subCategoryName == null) return;
        switch (subCategoryName) {
            case "ট্রাক": case "Truck":
                popupMenu.getMenu().add(Menu.NONE, 0, 0, context.getString(R.string.sixteen_feet_seven_half));
                popupMenu.getMenu().add(Menu.NONE, 1, 0, context.getString(R.string.eighteen_feet_fifteen_ton));
                popupMenu.getMenu().add(Menu.NONE, 2, 0, context.getString(R.string.twenty_feet_fifteen_ton));
                popupMenu.getMenu().add(Menu.NONE, 3, 0, context.getString(R.string.twenty_three_feet_twenty_five_ton));
                break;
            case "পিকাপ": case "Pickup":
                popupMenu.getMenu().add(Menu.NONE, 4, 0, context.getString(R.string.seven_feet_one_ton_open));
                popupMenu.getMenu().add(Menu.NONE, 5, 0, context.getString(R.string.eight_feet_one_ton_open));
                popupMenu.getMenu().add(Menu.NONE, 6, 0, context.getString(R.string.nine_feet_one_half_ton_open));
                popupMenu.getMenu().add(Menu.NONE, 7, 0, context.getString(R.string.ten_feet_one_half_ton_open));
                popupMenu.getMenu().add(Menu.NONE, 8, 0, context.getString(R.string.twelve_feet_two_ton_open));
                popupMenu.getMenu().add(Menu.NONE, 9, 0, context.getString(R.string.forteen_feet_three_half_ton_open));
                break;
            case "কাভার্ড ভ্যান": case "Covered Van":
                popupMenu.getMenu().add(Menu.NONE, 10, 0, context.getString(R.string.seven_feet_one_ton_covered));
                popupMenu.getMenu().add(Menu.NONE, 11, 0, context.getString(R.string.eight_feet_one_ton_open));
                popupMenu.getMenu().add(Menu.NONE, 12, 0, context.getString(R.string.nine_feet_one_half_ton_covered));
                popupMenu.getMenu().add(Menu.NONE, 13, 0, context.getString(R.string.ten_feet_one_half_ton_open));
                popupMenu.getMenu().add(Menu.NONE, 14, 0, context.getString(R.string.twelve_feet_two_ton_covered));
                popupMenu.getMenu().add(Menu.NONE, 15, 0, context.getString(R.string.forteen_feet_three_half_ton_covered));
                popupMenu.getMenu().add(Menu.NONE, 16, 0, context.getString(R.string.sixteen_feet_seven_half));
                popupMenu.getMenu().add(Menu.NONE, 17, 0, context.getString(R.string.eighteen_feet_fifteen_ton));
                popupMenu.getMenu().add(Menu.NONE, 18, 0, context.getString(R.string.twenty_feet_fifteen_ton));
                popupMenu.getMenu().add(Menu.NONE, 19, 0, context.getString(R.string.twenty_three_feet_fifteen_ton));
                break;
            case "লো বেড": case "Lo bet":
                popupMenu.getMenu().add(Menu.NONE, 20, 0, context.getString(R.string.forteen_feet));
                popupMenu.getMenu().add(Menu.NONE, 21, 0, context.getString(R.string.sixteen_feet));
                popupMenu.getMenu().add(Menu.NONE, 22, 0, context.getString(R.string.eighteen_feet));
                popupMenu.getMenu().add(Menu.NONE, 23, 0, context.getString(R.string.twenty_feet));
                popupMenu.getMenu().add(Menu.NONE, 24, 0, context.getString(R.string.twenty_four_feet));
                popupMenu.getMenu().add(Menu.NONE, 25, 0, context.getString(R.string.twenty_six_feet));
                popupMenu.getMenu().add(Menu.NONE, 26, 0, context.getString(R.string.fourty_feet));
                break;
            case "ট্রেইলর": case "Trailer":
                popupMenu.getMenu().add(Menu.NONE, 27, 0, context.getString(R.string.twenty_feet));
                popupMenu.getMenu().add(Menu.NONE, 28, 0, context.getString(R.string.fourty_feet));
                break;
            case "ফ্রিজার ভ্যান": case "Freezer Van":
                popupMenu.getMenu().add(Menu.NONE, 29, 0, context.getString(R.string.twelve_feet));
                popupMenu.getMenu().add(Menu.NONE, 30, 0, context.getString(R.string.forteen_feet));
                popupMenu.getMenu().add(Menu.NONE, 31, 0, context.getString(R.string.sixteen_feet));
                popupMenu.getMenu().add(Menu.NONE, 32, 0, context.getString(R.string.eighteen_feet));
                break;
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            binding.serviceSizeAndCapacityEt.setText(item.getTitle());
            setErrorWatcher(binding.serviceSizeAndCapacityEt, false);
            return false;
        });
        popupMenu.show();
    }

    public void popupCategory(TextView et, String subCategoryName) {
        PopupMenu popupMenu = new PopupMenu(context, et, Gravity.END);
        if (subCategoryName == null) return;
        switch (subCategoryName) {
            case "ট্রাক": case "Truck": case "পিকাপ": case "Pickup": popupMenu.getMenu().add(Menu.NONE, 0, 0, "খোলা গাড়ী"); break;
            case "কাভার্ড ভ্যান": case "Covered Van": popupMenu.getMenu().add(Menu.NONE, 1, 0, "কাভার্ড ভ্যান"); break;
            case "লো বেড": case "Lo bet": popupMenu.getMenu().add(Menu.NONE, 2, 0, "লো বেড"); break;
            case "ট্রেইলর": case "Trailer": popupMenu.getMenu().add(Menu.NONE, 3, 0, "ফ্লাট বেড"); break;
            case "ফ্রিজার ভ্যান": case "Freezer Van":
                popupMenu.getMenu().add(Menu.NONE, 4, 0, "আইসক্রীম"); popupMenu.getMenu().add(Menu.NONE, 5, 0, "মাছ/মাংস");
                popupMenu.getMenu().add(Menu.NONE, 6, 0, "খাবার"); popupMenu.getMenu().add(Menu.NONE, 7, 0, "ঔষধ");
                break;
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            et.setText(item.getTitle());
            setErrorWatcher(et, false);
            return false;
        });
        popupMenu.show();
    }

    public void popupEquipmentCapability(String subCategoryId) {
        PopupMenu popupMenu = new PopupMenu(context, binding.equipmentCapabilityEt, Gravity.END);
        if (MyUtils.SUB_EXCAVATOR_ID.equals(subCategoryId)) {
            popupMenu.getMenu().add(Menu.NONE, 1, 0, ".৩ সাইজ");
            popupMenu.getMenu().add(Menu.NONE, 2, 0, ".৫ সাইজ");
            popupMenu.getMenu().add(Menu.NONE, 3, 0, ".৭ সাইজ");
            popupMenu.getMenu().add(Menu.NONE, 4, 0, ".৯ সাইজ");
        } else if (MyUtils.SUB_DUMP_TRUCK_ID.equals(subCategoryId)) {
            String[] cfts = {"১২০ সিএফটি", "১৮০ সিএফটি", "২০০ সিএফটি", "২৫০ সিএফটি", "৩০০ সিএফটি", "৪০০ সিএফটি", "৫০০ সিএফটি", "৫৫০ সিএফটি", "৬০০ সিএফটি", "৬৫০ সিএফটি", "৭০০ সিএফটি", "৭৫০ সিএফটি", "৮০০ সিএফটি", "৮৫০ সিএফটি"};
            for (int i = 0; i < cfts.length; i++) popupMenu.getMenu().add(Menu.NONE, i + 5, 0, cfts[i]);
        } else if (MyUtils.SUB_TRACTOR_ID.equals(subCategoryId)) {
            popupMenu.getMenu().add(Menu.NONE, 19, 0, context.getString(R.string.sonalika_tractor));
            popupMenu.getMenu().add(Menu.NONE, 20, 0, context.getString(R.string.mahindra_tractor));
            popupMenu.getMenu().add(Menu.NONE, 21, 0, context.getString(R.string.yanmar_tractor));
            popupMenu.getMenu().add(Menu.NONE, 22, 0, context.getString(R.string.john_deere_tractor));
            popupMenu.getMenu().add(Menu.NONE, 23, 0, context.getString(R.string.new_holland_tractor));
            popupMenu.getMenu().add(Menu.NONE, 24, 0, context.getString(R.string.massey_ferguson_tractor));
            popupMenu.getMenu().add(Menu.NONE, 25, 0, context.getString(R.string.eicher_tractor));
            popupMenu.getMenu().add(Menu.NONE, 26, 0, context.getString(R.string.foton_tractor));
            popupMenu.getMenu().add(Menu.NONE, 27, 0, context.getString(R.string.force_motors_tractor));
        } else if (MyUtils.SUB_RICE_TRANSPLANTER_ID.equals(subCategoryId)) {
            popupMenu.getMenu().add(Menu.NONE, 28, 0, context.getString(R.string.riding_type));
            popupMenu.getMenu().add(Menu.NONE, 29, 0, context.getString(R.string.walking_type));
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            binding.equipmentCapabilityEt.setText(item.getTitle());
            if (id >= 19 && id <= 27) {
                binding.equipmentCategoryEt.setText("");
                String tractorTag = "";
                switch (id) {
                    case 19: tractorTag = context.getString(R.string.sonalika_tractor); break;
                    case 20: tractorTag = context.getString(R.string.mahindra_tractor); break;
                    case 21: tractorTag = context.getString(R.string.yanmar_tractor); break;
                    case 22: tractorTag = context.getString(R.string.john_deere_tractor); break;
                    case 23: tractorTag = context.getString(R.string.new_holland_tractor); break;
                    case 24: tractorTag = context.getString(R.string.massey_ferguson_tractor); break;
                    case 25: tractorTag = context.getString(R.string.eicher_tractor); break;
                    case 26: tractorTag = context.getString(R.string.foton_tractor); break;
                    case 27: tractorTag = context.getString(R.string.force_motors_tractor); break;
                }
                binding.equipmentCapabilityEt.setTag(tractorTag);
            }
            setErrorWatcher(binding.equipmentCapabilityEt, false);
            return false;
        });
        popupMenu.show();
    }

    public void popupEquipmentCategory(String subCategoryId) {
        PopupMenu popupMenu = new PopupMenu(context, binding.equipmentCategoryEt, Gravity.END);
        if (MyUtils.SUB_DUMP_TRUCK_ID.equals(subCategoryId)) {
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "৬ চাকা");
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "১০ চাকা");
        } else if (MyUtils.SUB_EXCAVATOR_ID.equals(subCategoryId)) {
            popupMenu.getMenu().add(Menu.NONE, 3, 0, "রেগুলার");
            popupMenu.getMenu().add(Menu.NONE, 4, 0, "লং বুম");
        } else if (MyUtils.SUB_TRACTOR_ID.equals(subCategoryId)) {
            String selectedCapacity = (String) binding.equipmentCapabilityEt.getTag();
            if (selectedCapacity != null) {
                if (selectedCapacity.equals(context.getString(R.string.sonalika_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 5, 0, context.getString(R.string.hp_fal_40));
                    popupMenu.getMenu().add(Menu.NONE, 6, 0, context.getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 7, 0, context.getString(R.string.hp_fal_60_4));
                } else if (selectedCapacity.equals(context.getString(R.string.mahindra_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 8, 0, context.getString(R.string.hp_fal_42));
                    popupMenu.getMenu().add(Menu.NONE, 9, 0, context.getString(R.string.hp_fal_45));
                    popupMenu.getMenu().add(Menu.NONE, 10, 0, context.getString(R.string.hp_fal_62));
                    popupMenu.getMenu().add(Menu.NONE, 11, 0, context.getString(R.string.hp_fal_71));
                } else if (selectedCapacity.equals(context.getString(R.string.yanmar_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 12, 0, context.getString(R.string.hp_fal_21));
                    popupMenu.getMenu().add(Menu.NONE, 13, 0, context.getString(R.string.hp_fal_26));
                    popupMenu.getMenu().add(Menu.NONE, 14, 0, context.getString(R.string.hp_fal_42));
                    popupMenu.getMenu().add(Menu.NONE, 15, 0, context.getString(R.string.hp_fal_46_4));
                    popupMenu.getMenu().add(Menu.NONE, 16, 0, context.getString(R.string.hp_fal_59));
                } else if (selectedCapacity.equals(context.getString(R.string.john_deere_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 17, 0, context.getString(R.string.hp_fal_35));
                    popupMenu.getMenu().add(Menu.NONE, 18, 0, context.getString(R.string.hp_fal_55));
                    popupMenu.getMenu().add(Menu.NONE, 19, 0, context.getString(R.string.hp_fal_113));
                } else if (selectedCapacity.equals(context.getString(R.string.new_holland_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 20, 0, context.getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 21, 0, context.getString(R.string.hp_fal_56));
                    popupMenu.getMenu().add(Menu.NONE, 22, 0, context.getString(R.string.hp_fal_60_5));
                } else if (selectedCapacity.equals(context.getString(R.string.massey_ferguson_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 23, 0, context.getString(R.string.hp_fal_50_3));
                    popupMenu.getMenu().add(Menu.NONE, 24, 0, context.getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 25, 0, context.getString(R.string.hp_fal_85));
                } else if (selectedCapacity.equals(context.getString(R.string.eicher_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 26, 0, context.getString(R.string.hp_fal_25));
                    popupMenu.getMenu().add(Menu.NONE, 27, 0, context.getString(R.string.hp_fal_36));
                    popupMenu.getMenu().add(Menu.NONE, 28, 0, context.getString(R.string.hp_fal_40));
                    popupMenu.getMenu().add(Menu.NONE, 29, 0, context.getString(R.string.hp_fal_45));
                    popupMenu.getMenu().add(Menu.NONE, 30, 0, context.getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 31, 0, context.getString(R.string.hp_fal_60_5));
                } else if (selectedCapacity.equals(context.getString(R.string.foton_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 32, 0, context.getString(R.string.hp_fal_24));
                    popupMenu.getMenu().add(Menu.NONE, 33, 0, context.getString(R.string.hp_fal_46_3));
                    popupMenu.getMenu().add(Menu.NONE, 34, 0, context.getString(R.string.hp_fal_50_4));
                    popupMenu.getMenu().add(Menu.NONE, 35, 0, context.getString(R.string.hp_fal_70));
                    popupMenu.getMenu().add(Menu.NONE, 36, 0, context.getString(R.string.hp_fal_90));
                } else if (selectedCapacity.equals(context.getString(R.string.force_motors_tractor))) {
                    popupMenu.getMenu().add(Menu.NONE, 37, 0, context.getString(R.string.hp_fal_27));
                    popupMenu.getMenu().add(Menu.NONE, 38, 0, context.getString(R.string.hp_fal_31));
                    popupMenu.getMenu().add(Menu.NONE, 39, 0, context.getString(R.string.hp_fal_40));
                    popupMenu.getMenu().add(Menu.NONE, 40, 0, context.getString(R.string.hp_fal_45));
                    popupMenu.getMenu().add(Menu.NONE, 41, 0, context.getString(R.string.hp_fal_50_4));
                }
            }
        } else if (MyUtils.SUB_RICE_TRANSPLANTER_ID.equals(subCategoryId)) {
            popupMenu.getMenu().add(Menu.NONE, 42, 0, context.getString(R.string.four_line_machine));
            popupMenu.getMenu().add(Menu.NONE, 43, 0, context.getString(R.string.six_Line_machine));
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            binding.equipmentCategoryEt.setText(item.getTitle());
            setErrorWatcher(binding.equipmentCategoryEt, false);
            return false;
        });
        popupMenu.show();
    }

    public void clearAllFields() {
        binding.metroNameEt.setText("");
        binding.serialCategoryEt.setText("");
        binding.serialNumberEt.setText("");
        binding.serviceSizeAndCapacityEt.setText("");
        binding.categoryOrYearEt.setText("");
        binding.carModelNumberEt.setText("");
        binding.carYearEt.setText("");
        binding.chargerVanNameEt.setText("");
        binding.ambulanceCategoryEt.setText("");
        binding.ambulanceVanTypeEt.setText("");
        binding.equipmentCapabilityEt.setText("");
        binding.equipmentCategoryEt.setText("");
        binding.harvesterNameEt.setText("");
        binding.harvesterCategoryEt.setText("");
        binding.teamLeaderNameEt.setText("");
        binding.teamMemberEt.setText("");
        binding.workAreaEt.setText("");
        binding.serviceSkillEt.setText("");
        binding.serviceExperienceEt.setText("");
        binding.serviceAreaEt.setText("");
        binding.lowBedSizeEt.setText("");
        binding.lowBedCategoryEt.setText("");
        binding.equipmentCapabilityEt.setTag(null);
    }
}
