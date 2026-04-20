package com.krishibarirangpur.bdhelper.utils.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.utils.CommonClass;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

public class SubmitPostBottomSheetDialog {

    public interface VoidCallback {
        void onClick();
    }

    @SuppressLint("SetTextI18n")
    public static void show(
            Fragment fragment,
            String categoryId,
            String subCategoryId,
            String subCategoryName,
            String quantity,
            String loadLocation,
            String unloadLocation,
            String rentLocation,
            String specificationCapacity,
            String specificationDuration,
            String specificationTypes,
            String description,
            String landArea,
            String dateTime,
            VoidCallback onSubmit
    ) {
        Context context = fragment.requireContext();
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.layout_submit_post);

        // Binding Views
        ImageView iconView = dialog.findViewById(R.id.iconImageViewSub);
        TextView size = dialog.findViewById(R.id.sizeCapacityTV);
        TextView count = dialog.findViewById(R.id.totalCountTV);
        TextView duration = dialog.findViewById(R.id.popupDurationTV);
        TextView product = dialog.findViewById(R.id.productTV);
        TextView sizeDef = dialog.findViewById(R.id.sizeCapacityDefTV);
        TextView countDef = dialog.findViewById(R.id.totalCountDefTV);
        TextView durationDef = dialog.findViewById(R.id.popupDurationDefTV);
        TextView productDef = dialog.findViewById(R.id.productDefTV);
        TextView detailsTV = dialog.findViewById(R.id.detailsTV);
        TextView time = dialog.findViewById(R.id.popupTimeTV);
        TextView submitBtn = dialog.findViewById(R.id.postSubmitBtn);

        RelativeLayout loadLocationRl = dialog.findViewById(R.id.loadLocationRl);
        RelativeLayout unloadLocationRl = dialog.findViewById(R.id.unloadLocationRl);
        RelativeLayout areaLocationRl = dialog.findViewById(R.id.areaLocationRl);

        TextView locationTv = dialog.findViewById(R.id.locationTv);
        TextView unloadLocationTv = dialog.findViewById(R.id.unloadLocationTv);
        TextView areaLocationTv = dialog.findViewById(R.id.areaLocationTv);

        TableRow landAreaNameTR = dialog.findViewById(R.id.landAreaNameTR);
        TextView landAreaTV = dialog.findViewById(R.id.landAreaTV);

        // ✅ Land Area (Harvester)
        if (landAreaNameTR != null) {
            boolean isHarvesterOrTractor = subCategoryId.equals(MyUtils.HARVESTER_MACHINE_ID) || subCategoryId.equals(MyUtils.SUB_TRACTOR_ID);
            landAreaNameTR.setVisibility(isHarvesterOrTractor ? View.VISIBLE : View.GONE);
            if (isHarvesterOrTractor && landAreaTV != null) {
                landAreaTV.setText(landArea + " " + fragment.getString(R.string.acres));
            }
        }

        // ✅ Toggle Location UI
        boolean isEquipmentOrLabor = categoryId.equals(MyUtils.EQUIPMENT_ID)
                || subCategoryId.equals(MyUtils.HARVESTER_MACHINE_ID)
                || categoryId.equals(MyUtils.SKILLED_LABOR_ID);

        if (loadLocationRl != null) loadLocationRl.setVisibility(isEquipmentOrLabor ? View.GONE : View.VISIBLE);
        if (unloadLocationRl != null) unloadLocationRl.setVisibility(isEquipmentOrLabor ? View.GONE : View.VISIBLE);
        if (areaLocationRl != null) areaLocationRl.setVisibility(isEquipmentOrLabor ? View.VISIBLE : View.GONE);

        // ✅ Setup Labels and Values Based on Category
        setupCategoryUI(fragment, categoryId, subCategoryId, subCategoryName, quantity, 
                        countDef, count, sizeDef, size, durationDef, duration, productDef);

        // ✅ Set Global Data
        if (locationTv != null) locationTv.setText(loadLocation);
        if (unloadLocationTv != null) unloadLocationTv.setText(unloadLocation);
        if (areaLocationTv != null) areaLocationTv.setText(rentLocation);
        if (size != null && specificationCapacity != null) size.setText(specificationCapacity);
        if (duration != null && specificationDuration != null) duration.setText(specificationDuration);
        if (product != null && specificationTypes != null) product.setText(specificationTypes);
        if (time != null) time.setText(dateTime);
        
        if (detailsTV != null) {
            detailsTV.setText(Replacement.ReplacementNumberInLocal(context, description));
        }

        // ✅ Set Icon
        if (iconView != null) {
            int iconRes = CommonClass.getIconForSubCategory(subCategoryId);
            if (iconRes != 0) {
                iconView.setImageDrawable(ContextCompat.getDrawable(context, iconRes));
            }
        }

        // ✅ Submit Click
        if (submitBtn != null) {
            submitBtn.setOnClickListener(v -> {
                dialog.dismiss();
                if (onSubmit != null) onSubmit.onClick();
            });
        }

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private static void setupCategoryUI(Fragment fragment, String categoryId, String subCategoryId,
                                        String subCategoryName, String quantity,
                                        TextView countDef, TextView count, TextView sizeDef, TextView size,
                                        TextView durationDef, TextView duration, TextView productDef) {
        
        Context context = fragment.getContext();

        if (categoryId.equals(MyUtils.HOME_SHIFTING_ID)) {
            if (sizeDef != null) sizeDef.setText(fragment.getString(R.string.shift_type_dot));
            if (countDef != null) countDef.setText(fragment.getString(R.string.size_dot));
            if (productDef != null) productDef.setText(fragment.getString(R.string.truck_access));

            if (count != null) count.setText(subCategoryName);
            if (durationDef != null) durationDef.setVisibility(View.GONE);
            if (duration != null) duration.setVisibility(View.GONE);
        } 
        else if (categoryId.equals(MyUtils.SKILLED_LABOR_ID)) {
            if (countDef != null) countDef.setText(subCategoryName + " " + fragment.getString(R.string.need_dot));
            if (count != null) count.setText(quantity);
            if (sizeDef != null) sizeDef.setText(fragment.getString(R.string.work_experience_dot));
            if (productDef != null) productDef.setText(fragment.getString(R.string.which_type_laborer_dot));
            
            if (durationDef != null) durationDef.setVisibility(View.GONE);
            if (duration != null) duration.setVisibility(View.GONE);

            if (!subCategoryId.equals(MyUtils.SUB_DRIVER_ID)) {
                if (sizeDef != null) sizeDef.setVisibility(View.GONE);
                if (size != null) size.setVisibility(View.GONE);
            }
        } 
        else {
            // Default Case for most categories
            if (countDef != null) countDef.setText(subCategoryName);
            if (count != null) count.setText(Replacement.ReplacementQtyToLocal(context, quantity));
            if (durationDef != null) durationDef.setText(fragment.getString(R.string.duration_dot));

            // Product Type Labeling
            if (productDef != null) {
                String typeSuffix = " " + fragment.getString(R.string.type_dot);
                if (categoryId.equals(MyUtils.EQUIPMENT_ID) || 
                    subCategoryId.equals(MyUtils.SUB_MICROBUS_ID) || 
                    subCategoryId.equals(MyUtils.SUB_AMBULANCE_ID) || 
                    subCategoryId.equals(MyUtils.SUB_DUMP_TRUCK_ID)) {
                    productDef.setText(subCategoryName + typeSuffix);
                } else if (subCategoryId.equals(MyUtils.HARVESTER_MACHINE_ID)) {
                    productDef.setText(fragment.getString(R.string.working) + typeSuffix);
                } else if (subCategoryId.equals(MyUtils.SUB_CAR_ID)) {
                    productDef.setText(fragment.getString(R.string.program) + typeSuffix);
                } else if (categoryId.equals(MyUtils.ROAD_TRANSPORT_ID)) {
                    if (subCategoryId.equals(MyUtils.SUB_CHARGER_VAN_ID) || subCategoryId.equals(MyUtils.SUB_LOW_BED_ID)) {
                        productDef.setText(fragment.getString(R.string.transport) + typeSuffix);
                    } else {
                        productDef.setText(fragment.getString(R.string.product_type));
                    }
                } else {
                    productDef.setText(fragment.getString(R.string.type_dot));
                }
            }

            // Size/Category Labeling
            if (sizeDef != null) {
                if (subCategoryId.equals(MyUtils.SUB_TRACTOR_ID)) {
                    sizeDef.setText(fragment.getString(R.string.tractor_brand));
                } else if (categoryId.equals(MyUtils.RENT_A_CAR_ID)) {
                    sizeDef.setText(fragment.getString(R.string.category_dot));
                } else {
                    sizeDef.setText(fragment.getString(R.string.size_dot));
                }
            }
        }
    }
}
