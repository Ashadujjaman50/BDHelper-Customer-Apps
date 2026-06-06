package com.krishibarirangpur.bdhelper.userActivity.customer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.shared.AdapterSubCategory;
import com.krishibarirangpur.bdhelper.databinding.ActivityTrendingCategoryBinding;
import com.krishibarirangpur.bdhelper.model.SubCategoryModel;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrendingCategoryActivity extends BaseActivity {

    private ActivityTrendingCategoryBinding binding;

    String categoryType;

    List<SubCategoryModel> subCategoryModelList;
    private AdapterSubCategory subCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trending_category);

        //init views
        Intent intent = getIntent();
        categoryType = intent.getStringExtra("categoryType");

        if (MyUtils.HOME_CATEGORY_TRENDING.equals(categoryType)){
            binding.titleTv.setText(getString(R.string.trending));
        }
        else if (MyUtils.HOME_CATEGORY_POPULAR.equals(categoryType)){
            binding.titleTv.setText(getString(R.string.popular));
        }
        else {
            binding.titleTv.setText(getString(R.string.recommended));
        }

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        //Recyclerview Setup
        subCategoryModelList = new ArrayList<>();
        binding.categoryRecyclerView.setHasFixedSize(true);
        subCategoryAdapter = new AdapterSubCategory(subCategoryModelList, this);
        binding.categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.categoryRecyclerView.setAdapter(subCategoryAdapter);

        //load model subcategory data
        getSubCategoryList();

        subCategoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Click
                SubCategoryModel clickedmodelSubCategoryModel = subCategoryModelList.get(position);
                String[] subCategoryData = {clickedmodelSubCategoryModel.getCategoryId(), clickedmodelSubCategoryModel.getSubCategoryId(), clickedmodelSubCategoryModel.getSubCategoryName()};
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
        Intent intent = new Intent(TrendingCategoryActivity.this, AddressActivity.class);
        intent.putExtra(MyUtils.categoryId, subCategoryData[0]);
        intent.putExtra(MyUtils.subCategoryId, subCategoryData[1]);
        intent.putExtra(MyUtils.subCategoryName, subCategoryData[2]);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getSubCategoryList() {
        // master list বানাও
        List<SubCategoryModel> allList = new ArrayList<>();

        // Road Transport
        allList.add(new SubCategoryModel(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_TRUCK_ID, getString(R.string.truck), R.drawable.ic_truck, false));
        allList.add(new SubCategoryModel(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_PICKUP_ID, getString(R.string.pickup), R.drawable.ic_pickup, false));
        allList.add(new SubCategoryModel(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_COVERED_VAN_ID, getString(R.string.coveredvan), R.drawable.ic_covered_van, false));
        allList.add(new SubCategoryModel(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_TRAILER_ID, getString(R.string.trailer), R.drawable.ic_trailer, false));
        allList.add(new SubCategoryModel(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_LOW_BED_ID, getString(R.string.lo_bet), R.drawable.ic_low_bed, false));
        allList.add(new SubCategoryModel(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_FREEZER_VAN_ID, getString(R.string.freezervan), R.drawable.ic_freezer_van, false));
        allList.add(new SubCategoryModel(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_CHARGER_VAN_ID, getString(R.string.charger_van), R.drawable.ic_charger_van, false));
        allList.add(new SubCategoryModel(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_DUMP_TRUCK_ID, getString(R.string.dump_truck), R.drawable.ic_dump_truck, false));

        // Rent A Car
        allList.add(new SubCategoryModel(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_CAR_ID, getString(R.string.car), R.drawable.ic_car, false));
        allList.add(new SubCategoryModel(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_MICROBUS_ID, getString(R.string.microbus), R.drawable.ic_microbus, false));
        allList.add(new SubCategoryModel(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_AMBULANCE_ID, getString(R.string.ambulance), R.drawable.ic_ambulance, false));

        // Equipment
        allList.add(new SubCategoryModel(MyUtils.EQUIPMENT_ID, MyUtils.SUB_TRACTOR_ID, getString(R.string.tractor), R.drawable.ic_tractor, false));
        allList.add(new SubCategoryModel(MyUtils.EQUIPMENT_ID, MyUtils.SUB_RICE_TRANSPLANTER_ID, getString(R.string.rice_transplanter), R.drawable.ic_rice_transplanter, false));
        allList.add(new SubCategoryModel(MyUtils.EQUIPMENT_ID, MyUtils.SUB_EXCAVATOR_ID, getString(R.string.excavator), R.drawable.ic_excavator, false));

        // Skilled Labor
        allList.add(new SubCategoryModel(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_DRIVER_ID, getString(R.string.driver), R.drawable.ic_driver, false));
        allList.add(new SubCategoryModel(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_MECHANIC_ID, getString(R.string.mechanic), R.drawable.ic_mechanic, false));
        allList.add(new SubCategoryModel(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_ELECTRICIAN_ID, getString(R.string.electrician), R.drawable.ic_electrician, false));
        allList.add(new SubCategoryModel(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_STOVE_TECHNICIAN_ID, getString(R.string.stove_mechanic), R.drawable.ic_stove_technician, false));
        allList.add(new SubCategoryModel(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_PLUMBER_ID, getString(R.string.plumber), R.drawable.ic_plumbing, false));


        // এখন random shuffle করো
        Collections.shuffle(allList);

        // প্রথম ৫–৬ টা item নাও (random count করতে চাইলে 5 + random.nextInt(2))
        int count;
        if (MyUtils.HOME_CATEGORY_TRENDING.equals(categoryType)){
            count = 7 + (int)(Math.random() * 2); // ৭ বা ৮ item হবে
        }
        else if (MyUtils.HOME_CATEGORY_POPULAR.equals(categoryType)){
            count = 6 + (int)(Math.random() * 2); // ৬ বা ৭ item হবে
        }
        else {
            count = 5 + (int)(Math.random() * 2); // ৫ বা ৬ item হবে
        }

        List<SubCategoryModel> selectedList = allList.subList(0, Math.min(count, allList.size()));

        // মেইন লিস্টে যোগ করো
        subCategoryModelList.clear();
        subCategoryModelList.addAll(selectedList);

        subCategoryAdapter.notifyDataSetChanged();
    }


}