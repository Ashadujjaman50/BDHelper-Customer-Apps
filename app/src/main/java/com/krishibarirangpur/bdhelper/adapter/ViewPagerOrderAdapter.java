package com.krishibarirangpur.bdhelper.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.krishibarirangpur.bdhelper.userFragment.partner.BidAllOrderFragment;
import com.krishibarirangpur.bdhelper.userFragment.partner.BidByCategoryOrderFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerOrderAdapter extends FragmentStateAdapter {

    private final List<String> subCategoryIds;

    public ViewPagerOrderAdapter(@NonNull FragmentManager fragmentManager,
                                 @NonNull Lifecycle lifecycle,
                                 List<String> subCategoryIds) {
        super(fragmentManager, lifecycle);
        this.subCategoryIds = subCategoryIds != null ? subCategoryIds : new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("subCategoryIds", new ArrayList<>(subCategoryIds));

        Fragment fragment;
        if (position == 0) {
            fragment = new BidAllOrderFragment();
        } else {
            fragment = new BidByCategoryOrderFragment();
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
