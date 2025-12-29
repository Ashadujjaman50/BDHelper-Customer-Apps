package com.krishibarirangpur.bdhelper.partnerFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krishibarirangpur.bdhelper.Interface.OnItemClickListener;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.AdapterSubCategory;
import com.krishibarirangpur.bdhelper.databinding.FragmentSelectServiceCategoryBinding;
import com.krishibarirangpur.bdhelper.model.ModelSubCategory;
import com.krishibarirangpur.bdhelper.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;


public class SelectServiceCategoryFragment extends Fragment {

    private FragmentSelectServiceCategoryBinding binding;

    List<ModelSubCategory> modelSubCategoryList;
    private AdapterSubCategory subCategoryAdapter;


    public SelectServiceCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_select_service_category, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init vieww
        //Recyclerview Setup
        modelSubCategoryList = new ArrayList<>();
        binding.categoryRv.setHasFixedSize(true);
        subCategoryAdapter = new AdapterSubCategory(modelSubCategoryList, getContext());
        binding.categoryRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.categoryRv.setAdapter(subCategoryAdapter);

        //load model subcategory data
        getSubCategoryList();

        subCategoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Click
                ModelSubCategory clickedmodelSubCategory = modelSubCategoryList.get(position);
                String[] subCategoryData = {clickedmodelSubCategory.getCategoryId(), clickedmodelSubCategory.getSubCategoryId(), clickedmodelSubCategory.getSubCategoryName()};
                openServiceFragment(subCategoryData);
            }

            @Override
            public void onShowItemClick(int position) {

            }

            @Override
            public void onDeleteItemClick(int position) {

            }
        });
    }

    private void openServiceFragment(String[] subCategoryData) {
        AddServiceFormFragment fragment = new AddServiceFormFragment();

        Bundle bundle = new Bundle();
        bundle.putString(MyUtils.categoryId, subCategoryData[0]);
        bundle.putString(MyUtils.subCategoryId, subCategoryData[1]);
        bundle.putString(MyUtils.subCategoryName, subCategoryData[2]);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment) // তোমার container id বসাও
                .addToBackStack(null)
                .commit();
    }



    @SuppressLint("NotifyDataSetChanged")
    private void getSubCategoryList() {
        // master list বানাও
        List<ModelSubCategory> allList = new ArrayList<>();

        // Road Transport
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_TRUCK_ID, getString(R.string.truck), R.drawable.ic_truck, false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_PICKUP_ID, getString(R.string.pickup), R.drawable.ic_pickup, false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_COVERED_VAN_ID, getString(R.string.coveredvan), R.drawable.ic_covered_van, false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_TRAILER_ID, getString(R.string.trailer), R.drawable.ic_trailer, false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_LOW_BED_ID, getString(R.string.lo_bet), R.drawable.ic_low_bed, false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_FREEZER_VAN_ID, getString(R.string.freezervan), R.drawable.ic_freezer_van, false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_CHARGER_VAN_ID, getString(R.string.charger_van), R.drawable.ic_charger_van, false));
        allList.add(new ModelSubCategory(MyUtils.ROAD_TRANSPORT_ID, MyUtils.SUB_DUMP_TRUCK_ID, getString(R.string.dump_truck), R.drawable.ic_dump_truck, false));

        // Rent A Car
        allList.add(new ModelSubCategory(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_CAR_ID, getString(R.string.car), R.drawable.ic_car, false));
        allList.add(new ModelSubCategory(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_MICROBUS_ID, getString(R.string.microbus), R.drawable.ic_microbus, false));
        allList.add(new ModelSubCategory(MyUtils.RENT_A_CAR_ID, MyUtils.SUB_AMBULANCE_ID, getString(R.string.ambulance), R.drawable.ic_ambulance, false));

        //Harvester
        allList.add(new ModelSubCategory(MyUtils.HARVESTER_MACHINE_ID, MyUtils.HARVESTER_MACHINE_ID, getString(R.string.harvester), R.drawable.ic_harvester, false));

        // Equipment
        allList.add(new ModelSubCategory(MyUtils.EQUIPMENT_ID, MyUtils.SUB_TRACTOR_ID, getString(R.string.tractor), R.drawable.ic_tractor, false));
        allList.add(new ModelSubCategory(MyUtils.EQUIPMENT_ID, MyUtils.SUB_RICE_TRANSPLANTER_ID, getString(R.string.rice_transplanter), R.drawable.ic_rice_transplanter, false));
        allList.add(new ModelSubCategory(MyUtils.EQUIPMENT_ID, MyUtils.SUB_EXCAVATOR_ID, getString(R.string.excavator), R.drawable.ic_excavator, false));

        //Home shifting
        allList.add(new ModelSubCategory(MyUtils.HOME_SHIFTING_ID, MyUtils.HOME_SHIFTING_ID, getString(R.string.home_office_shifting), R.drawable.ic_home_shift, false));

        // Skilled Labor
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_DRIVER_ID, getString(R.string.driver), R.drawable.ic_driver, false));
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_MECHANIC_ID, getString(R.string.mechanic), R.drawable.ic_mechanic, false));
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_ELECTRICIAN_ID, getString(R.string.electrician), R.drawable.ic_electrician, false));
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_STOVE_TECHNICIAN_ID, getString(R.string.stove_mechanic), R.drawable.ic_stove_technician, false));
        allList.add(new ModelSubCategory(MyUtils.SKILLED_LABOR_ID, MyUtils.SUB_PLUMBER_ID, getString(R.string.plumber), R.drawable.ic_plumbing, false));



        // মেইন লিস্টে যোগ করো
        modelSubCategoryList.clear();
        modelSubCategoryList.addAll(allList);

        subCategoryAdapter.notifyDataSetChanged();
    }
}