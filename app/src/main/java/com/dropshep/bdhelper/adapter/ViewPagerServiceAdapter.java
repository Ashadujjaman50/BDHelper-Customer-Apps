package com.dropshep.bdhelper.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dropshep.bdhelper.partnerFragment.AllServiceFragment;
import com.dropshep.bdhelper.partnerFragment.NeedDocumentFragment;
import com.dropshep.bdhelper.userFragment.AllRequirementPostFragment;
import com.dropshep.bdhelper.userFragment.CurrentRequirementPostFragment;

public class ViewPagerServiceAdapter extends FragmentStateAdapter {
    public ViewPagerServiceAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerServiceAdapter(FragmentManager fm, Lifecycle lifecycle){
        super(fm, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new AllServiceFragment();
        }
        return new NeedDocumentFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
