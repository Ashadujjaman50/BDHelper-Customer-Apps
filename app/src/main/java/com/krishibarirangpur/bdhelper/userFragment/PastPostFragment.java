package com.krishibarirangpur.bdhelper.userFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.ViewPagerRequirementAdapter;
import com.krishibarirangpur.bdhelper.databinding.FragmentPastPostBinding;
import com.google.android.material.tabs.TabLayout;

public class PastPostFragment extends Fragment {

    private FragmentPastPostBinding binding;

    public PastPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_past_post, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        ViewPagerRequirementAdapter sa = new ViewPagerRequirementAdapter(fm, getLifecycle());
        binding.requirementViewPager.setAdapter(sa);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.current_rent_post)));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.all_rent_post)));
        binding.requirementViewPager.setUserInputEnabled(true);
        binding.requirementViewPager.setSaveEnabled(false);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .alpha(1f)
                        .setDuration(200)
                        .start();

                setPagerFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(0.8f)
                        .setDuration(200)
                        .start();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional: Do something on reselection
            }
        });


        binding.requirementViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

    }

    public void setPagerFragment(int a) {
        binding.requirementViewPager.setCurrentItem(a);
    }

}