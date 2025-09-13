package com.dropshep.bdhelper.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dropshep.bdhelper.userFragment.AllRequirementPostFragment;
import com.dropshep.bdhelper.userFragment.CurrentRequirementPostFragment;

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
