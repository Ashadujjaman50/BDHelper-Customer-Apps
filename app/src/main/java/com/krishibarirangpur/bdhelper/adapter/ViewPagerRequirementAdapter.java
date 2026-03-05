package com.krishibarirangpur.bdhelper.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.krishibarirangpur.bdhelper.userFragment.customer.AllRequirementPostFragment;
import com.krishibarirangpur.bdhelper.userFragment.customer.CurrentRequirementPostFragment;

public class ViewPagerRequirementAdapter extends FragmentStateAdapter {
    public ViewPagerRequirementAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerRequirementAdapter(FragmentManager fm, Lifecycle lifecycle){
        super(fm, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new CurrentRequirementPostFragment();
        }
        return new AllRequirementPostFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
