package com.krishibarirangpur.bdhelper.userActivity.customer;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityRentFormBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.userFragment.customer.requirementFragment.HomesShiftingFormFragment;
import com.krishibarirangpur.bdhelper.userFragment.customer.requirementFragment.LoadUnloadFormFragment;
import com.krishibarirangpur.bdhelper.userFragment.customer.requirementFragment.RentLocationFormFragment;
import com.krishibarirangpur.bdhelper.userFragment.customer.requirementFragment.SkilledLaborFormFragment;

public class RentFormActivity extends BaseActivity {

    private ActivityRentFormBinding binding;
    String categoryId, subCategoryId, subCategoryName;
    String loadLocation = "", unloadLocation="", rentLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rent_form);

        //init views
        categoryId = getIntent().getStringExtra(MyUtils.categoryId);
        subCategoryId = getIntent().getStringExtra(MyUtils.subCategoryId);
        loadLocation = getIntent().getStringExtra(MyUtils.loadLocation);
        unloadLocation = getIntent().getStringExtra(MyUtils.unloadLocation);
        rentLocation = getIntent().getStringExtra(MyUtils.rentLocation);

        //show subCategoryName TV
        subCategoryName = getIntent().getStringExtra(MyUtils.subCategoryName);
        binding.titleTv.setText(subCategoryName);

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        //Bun
        // Load Specific category Rent Location or Load / Unload Location Fragment- এ
        Fragment targetFragment;
        switch (categoryId) {
            case MyUtils.HARVESTER_MACHINE_ID:
            case MyUtils.EQUIPMENT_ID:
                targetFragment = new RentLocationFormFragment();
                break;
            case MyUtils.SKILLED_LABOR_ID:
                targetFragment = new SkilledLaborFormFragment();
                break;
            case MyUtils.HOME_SHIFTING_ID:
                targetFragment = new HomesShiftingFormFragment();
                break;
            default:
                targetFragment = new LoadUnloadFormFragment();
                break;
        }

        //Bundle pack এ data fragment - এ পাঠানো হচ্ছে
        Bundle bundle = new Bundle();
        bundle.putString(MyUtils.categoryId, categoryId);
        bundle.putString(MyUtils.subCategoryId, subCategoryId);
        bundle.putString(MyUtils.subCategoryName, subCategoryName);
        bundle.putString(MyUtils.loadLocation, loadLocation);
        bundle.putString(MyUtils.unloadLocation, unloadLocation);
        bundle.putString(MyUtils.rentLocation, rentLocation);
        targetFragment.setArguments(bundle);

        loadFragment(targetFragment);


    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}