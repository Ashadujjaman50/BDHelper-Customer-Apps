package com.dropshep.bdhelper.partner;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.ActivityBidBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.MyUtils;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partnerFragment.BidEquipmentFragment;
import com.dropshep.bdhelper.partnerFragment.BidHomeShiftingFragment;
import com.dropshep.bdhelper.partnerFragment.BidSkilledLaborFragment;
import com.dropshep.bdhelper.partnerFragment.BidTransportFragment;

public class BidActivity extends BaseActivity {

    private ActivityBidBinding binding;

    String bidAction, orderId, categoryId, subCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bid);

        // init views
        bidAction = getIntent().getStringExtra(MyUtils.bidAction);
        orderId = getIntent().getStringExtra(MyUtils.orderId);
        categoryId = getIntent().getStringExtra(MyUtils.categoryId);
        subCategoryId = getIntent().getStringExtra(MyUtils.subCategoryId);

        if (bidAction != null && bidAction.equals("new")) {
            Fragment fragment = null;

            switch (categoryId) {
                case MyUtils.ROAD_TRANSPORT_ID:
                case MyUtils.RENT_A_CAR_ID:
                    fragment = new BidTransportFragment();
                    break;
                case MyUtils.EQUIPMENT_ID:
                case MyUtils.HARVESTER_MACHINE_ID:
                    fragment = new BidEquipmentFragment();
                    break;
                case MyUtils.HOME_SHIFTING_ID:
                    fragment = new BidHomeShiftingFragment();
                    break;
                case MyUtils.SKILLED_LABOR_ID:
                    fragment = new BidSkilledLaborFragment();
                    break;
            }

            if (fragment != null) {
                // Pass data using Bundle
                Bundle bundle = new Bundle();
                bundle.putString(MyUtils.orderId, orderId);
                bundle.putString(MyUtils.subCategoryId, subCategoryId);
                fragment.setArguments(bundle);

                // Load fragment
                loadFragment(fragment);
            }
        }


        binding.backBtn.setOnClickListener(v -> finishOnBack());

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}