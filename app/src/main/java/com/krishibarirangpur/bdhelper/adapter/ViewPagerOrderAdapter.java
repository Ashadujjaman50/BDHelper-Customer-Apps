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

    private final ArrayList<String> subCategoryIds;

    public ViewPagerOrderAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<String> subCategoryIds) {
        super(fragmentManager, lifecycle);
        this.subCategoryIds = subCategoryIds != null ? new ArrayList<>(subCategoryIds) : new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = (position == 0) ? new BidByCategoryOrderFragment() : new BidAllOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("subCategoryIds", subCategoryIds);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}