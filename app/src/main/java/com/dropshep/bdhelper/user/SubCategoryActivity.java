package com.dropshep.bdhelper.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dropshep.bdhelper.Interface.OnItemClickListener;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.AdapterSubCategory;
import com.dropshep.bdhelper.databinding.ActivitySubCategoryBinding;
import com.dropshep.bdhelper.model.ModelSubCategory;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryActivity extends BaseActivity {

    private ActivitySubCategoryBinding binding;

    private String categoryId, categoryName;

    List<ModelSubCategory> modelSubCategoryList;
    private AdapterSubCategory subCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_category);

        //init views
        categoryId = getIntent().getStringExtra(MyUtils.categoryId);
        categoryName = getIntent().getStringExtra(MyUtils.categoryName);

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        binding.titleTv.setText(categoryName);

        modelSubCategoryList = new ArrayList<>();
        subCategoryAdapter = new AdapterSubCategory(modelSubCategoryList, this);
        binding.subCategoryRV.setLayoutManager(new GridLayoutManager(this, 2));
        binding.subCategoryRV.setAdapter(subCategoryAdapter);

        //load model subcategory data
        getSubCategoryList();

        subCategoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Click
                ModelSubCategory clickedmodelSubCategory = modelSubCategoryList.get(position);
                String[] subCategoryData = {clickedmodelSubCategory.getCategoryId(), clickedmodelSubCategory.getSubCategoryId(), clickedmodelSubCategory.getSubCategoryName()};
                openServiceActivity(subCategoryData);
            }

            @Override
            public void onShowItemClick(int position) {

            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });

    }

    private void openServiceActivity(String[] subCategoryData) {
        Intent intent = new Intent(SubCategoryActivity.this, AddressActivity.class);
        intent.putExtra(MyUtils.categoryId, subCategoryData[0]);
        intent.putExtra(MyUtils.subCategoryId, subCategoryData[1]);
        intent.putExtra(MyUtils.subCategoryName, subCategoryData[2]);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getSubCategoryList() {
        switch (categoryId) {
            case MyUtils.ROAD_TRANSPORT_ID:
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_TRUCK_ID, getString(R.string.truck), R.drawable.ic_truck, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_PICKUP_ID, getString(R.string.pickup), R.drawable.ic_pickup, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_COVERED_VAN_ID, getString(R.string.coveredvan), R.drawable.ic_covered_van, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_TRAILER_ID, getString(R.string.trailer), R.drawable.ic_trailer, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_FREEZER_VAN_ID, getString(R.string.freezervan), R.drawable.ic_freezer_van, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_CHARGER_VAN_ID, getString(R.string.charger_van), R.drawable.ic_charger_van, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_DUMP_TRUCK_ID, getString(R.string.dump_truck), R.drawable.ic_dump_truck, false));
                break;

            case MyUtils.RENT_A_CAR_ID:
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_CAR_ID, getString(R.string.car), R.drawable.ic_car, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_MICROBUS_ID, getString(R.string.microbus), R.drawable.ic_microbus, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_AMBULANCE_ID, getString(R.string.ambulance), R.drawable.ic_ambulance, false));
                break;

            case MyUtils.EQUIPMENT_ID:
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_TRACTOR_ID, getString(R.string.tractor), R.drawable.ic_tractor, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_RICE_TRANSPLANTER_ID, getString(R.string.rice_transplanter), R.drawable.ic_rice_transplanter, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_EXCAVATOR_ID, getString(R.string.excavator), R.drawable.ic_excavator, false));
                break;

            case MyUtils.SKILLED_LABOR_ID:
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_DRIVER_ID, getString(R.string.driver), R.drawable.ic_driver, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_MECHANIC_ID, getString(R.string.mechanic), R.drawable.ic_mechanic, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_ELECTRICIAN_ID, getString(R.string.electrician), R.drawable.ic_electrician, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_STOVE_TECHNICIAN_ID, getString(R.string.stove_mechanic), R.drawable.ic_stove_technician, false));
                modelSubCategoryList.add(new ModelSubCategory(categoryId, MyUtils.SUB_PLUMBER_ID, getString(R.string.plumber), R.drawable.ic_plumbing, false));
                break;
        }

        subCategoryAdapter.notifyDataSetChanged();
    }

}