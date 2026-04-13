package com.krishibarirangpur.bdhelper.userActivity.partner;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityBidBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;
import com.krishibarirangpur.bdhelper.sharedFragment.BidEquipmentFragment;
import com.krishibarirangpur.bdhelper.sharedFragment.BidHomeShiftingFragment;
import com.krishibarirangpur.bdhelper.sharedFragment.BidSkilledLaborFragment;
import com.krishibarirangpur.bdhelper.sharedFragment.BidTransportFragment;
import com.krishibarirangpur.bdhelper.userFragment.partner.BidFragment;

public class BidActivity extends BaseActivity {

    private ActivityBidBinding binding;

    String bidAction, user_type, orderId, categoryId, subCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bid);

        // init views
        bidAction = getIntent().getStringExtra(MyUtils.bidAction);
        user_type = getIntent().getStringExtra(MyUtils.USER_TYPE);

        //header title
        String title = "";

        if ("partner".equals(user_type)) {
            title = switch (bidAction) {
                case "new" -> getString(R.string.bid);
                case "confirmed" -> getString(R.string.accepted_bid);
                case "pending" -> getString(R.string.pending_bid);
                default -> title;
            };
        }
        else {
            title = getString(R.string.bidding);
        }

        binding.titleTv.setText(title);


        if (bidAction != null && bidAction.equals("new")) {
            // Get data from intent
            orderId = getIntent().getStringExtra(MyUtils.orderId);
            categoryId = getIntent().getStringExtra(MyUtils.categoryId);
            subCategoryId = getIntent().getStringExtra(MyUtils.subCategoryId);

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
                bundle.putString("user_type", user_type);
                bundle.putString(MyUtils.orderId, orderId);
                bundle.putString(MyUtils.subCategoryId, subCategoryId);
                fragment.setArguments(bundle);

                // Load fragment
                loadFragment(fragment);
            }
        }
        else {
            Fragment fragment = new BidFragment();
            Bundle bundle = new Bundle();
            bundle.putString(MyUtils.bidAction, bidAction);
            bundle.putString("user_type", user_type);
            fragment.setArguments(bundle);
            loadFragment(fragment);
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