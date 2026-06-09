package com.krishibarirangpur.bdhelper.adapter;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.krishibarirangpur.bdhelper.userFragment.partner.BidAllOrderFragment;
import com.krishibarirangpur.bdhelper.userFragment.partner.BidByCategoryOrderFragment;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerOrderAdapter extends FragmentStateAdapter {

    private final ArrayList<String> subCategoryIds;
    private final ArrayList<String> categoryIds;
    private final ArrayList<String> sizeAndCapacities;
    private final ArrayList<String> categoryAndYears;

    public ViewPagerOrderAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, 
                                ArrayList<String> subCategoryIds, ArrayList<String> categoryIds, 
                                ArrayList<String> sizeAndCapacities, ArrayList<String> categoryAndYears) {
        super(fragmentManager, lifecycle);
        this.subCategoryIds = subCategoryIds;
        this.categoryIds = categoryIds;
        this.sizeAndCapacities = sizeAndCapacities;
        this.categoryAndYears = categoryAndYears;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = (position == 0) ? new BidByCategoryOrderFragment() : new BidAllOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("subCategoryIds", subCategoryIds);
        bundle.putStringArrayList("categoryIds", categoryIds);
        bundle.putStringArrayList("sizeAndCapacities", sizeAndCapacities);
        bundle.putStringArrayList("categoryAndYears", categoryAndYears);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
