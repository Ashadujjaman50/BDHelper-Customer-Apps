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
import com.dropshep.bdhelper.databinding.ActivityTrendingCategoryBinding;
import com.dropshep.bdhelper.model.ModelSubCategory;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.ThemeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrendingCategoryActivity extends BaseActivity {

    private ActivityTrendingCategoryBinding binding;

    String categoryType;

    List<ModelSubCategory> modelSubCategoryList;
    private AdapterSubCategory subCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trending_category);

        //init views
        Intent intent = getIntent();
        categoryType = intent.getStringExtra("categoryType");

        if (categoryType.equals("Trending")){
            binding.titleTv.setText(getString(R.string.trending));
        }
        else if (categoryType.equals("Popular")){
            binding.titleTv.setText(getString(R.string.popular));
        }
        else {
            binding.titleTv.setText(getString(R.string.recommended));
        }

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        //Recyclerview Setup
        modelSubCategoryList = new ArrayList<>();
        binding.categoryRecyclerView.setHasFixedSize(true);
        subCategoryAdapter = new AdapterSubCategory(modelSubCategoryList, this);
        binding.categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.categoryRecyclerView.setAdapter(subCategoryAdapter);

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
        Intent intent = new Intent(TrendingCategoryActivity.this, AddressActivity.class);
        intent.putExtra(MyUtils.categoryId, subCategoryData[0]);
        intent.putExtra(MyUtils.subCategoryId, subCategoryData[1]);
        intent.putExtra(MyUtils.subCategoryName, subCategoryData[2]);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public Uri getURLForResource(int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getSubCategoryList() {
        // master list বানাও
        List<ModelSubCategory> allList = new ArrayList<>();

        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_TRUCK_ID, getString(R.string.truck), getURLForResource(R.drawable.ic_truck), false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_PICKUP_ID, getString(R.string.pickup), getURLForResource(R.drawable.ic_pickup), false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_COVERED_VAN_ID, getString(R.string.coveredvan), getURLForResource(R.drawable.ic_covered_van), false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_TRAILER_ID, getString(R.string.trailer), getURLForResource(R.drawable.ic_trailer), false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.LOW_BED_ID, getString(R.string.lo_bet), getURLForResource(R.drawable.ic_trailer), false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_FREEZER_VAN_ID, getString(R.string.freezervan), getURLForResource(R.drawable.ic_freezer_van), false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_CHARGER_VAN_ID, getString(R.string.charger_van), getURLForResource(R.drawable.ic_charger_van), false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_DUMP_TRUCK_ID, getString(R.string.dump_truck), getURLForResource(R.drawable.ic_dump_truck), false));

        allList.add(new ModelSubCategory(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_CAR_ID, getString(R.string.car), getURLForResource(R.drawable.ic_car), false));
        allList.add(new ModelSubCategory(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_MICROBUS_ID, getString(R.string.microbus), getURLForResource(R.drawable.ic_microbus), false));
        allList.add(new ModelSubCategory(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_AMBULANCE_ID, getString(R.string.ambulance), getURLForResource(R.drawable.ic_ambulance), false));

        allList.add(new ModelSubCategory(MyUtils.EQUIPMENT_ID, MyUtils.SUB_TRACTOR_ID, getString(R.string.tractor), getURLForResource(R.drawable.ic_tractor), false));
        allList.add(new ModelSubCategory(MyUtils.EQUIPMENT_ID, MyUtils.SUB_RICE_TRANSPLANTER_ID, getString(R.string.rice_transplanter), getURLForResource(R.drawable.ic_rice_transplanter), false));
        allList.add(new ModelSubCategory(MyUtils.EQUIPMENT_ID, MyUtils.SUB_EXCAVATOR_ID, getString(R.string.excavator), getURLForResource(R.drawable.ic_excavator), false));

        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_DRIVER_ID, getString(R.string.driver), getURLForResource(R.drawable.ic_driver), false));
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_MECHANIC_ID, getString(R.string.mechanic), getURLForResource(R.drawable.ic_mechanic), false));
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_ELECTRICIAN_ID, getString(R.string.electrician), getURLForResource(R.drawable.ic_electrician), false));
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_STOVE_TECHNICIAN_ID, getString(R.string.stove_mechanic), getURLForResource(R.drawable.ic_stove_technician), false));
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_PLUMBER_ID, getString(R.string.plumber), getURLForResource(R.drawable.ic_plumbing), false));

        // এখন random shuffle করো
        Collections.shuffle(allList);

        // প্রথম ৫–৬ টা item নাও (random count করতে চাইলে 5 + random.nextInt(2))
        int count;
        if (categoryType.equals("Trending")){
            count = 7 + (int)(Math.random() * 2); // ৭ বা ৮ item হবে
        }
        else if (categoryType.equals("Popular")){
            count = 6 + (int)(Math.random() * 2); // ৬ বা ৭ item হবে
        }
        else {
            count = 5 + (int)(Math.random() * 2); // ৫ বা ৬ item হবে
        }

        List<ModelSubCategory> selectedList = allList.subList(0, Math.min(count, allList.size()));

        // মেইন লিস্টে যোগ করো
        modelSubCategoryList.clear();
        modelSubCategoryList.addAll(selectedList);

        subCategoryAdapter.notifyDataSetChanged();
    }


}