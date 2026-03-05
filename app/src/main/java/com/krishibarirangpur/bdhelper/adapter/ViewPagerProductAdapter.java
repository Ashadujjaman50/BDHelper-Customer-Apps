package com.krishibarirangpur.bdhelper.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.krishibarirangpur.bdhelper.userFragment.partner.productFragment.BatteryOrderFragment;
import com.krishibarirangpur.bdhelper.userFragment.partner.productFragment.MyBatteryOrderFragment;

public class ViewPagerProductAdapter extends FragmentStateAdapter {
    public ViewPagerProductAdapter(@NonNull FragmentManager  fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Hardcoded in this order, you'll want to use lists and make sure the titles match
        if (position == 0) {
            return new MyBatteryOrderFragment();
        }
        return new BatteryOrderFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
