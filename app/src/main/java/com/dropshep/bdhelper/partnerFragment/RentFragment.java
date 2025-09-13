package com.dropshep.bdhelper.partnerFragment;

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

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.ViewPagerServiceAdapter;
import com.dropshep.bdhelper.databinding.FragmentRentBinding;
import com.google.android.material.tabs.TabLayout;

public class RentFragment extends Fragment {

    private FragmentRentBinding binding;

    public RentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_rent, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //View Fragment Courser
        FragmentManager fm = getChildFragmentManager();
        ViewPagerServiceAdapter serviceAdapter = new ViewPagerServiceAdapter(fm, getLifecycle());
        binding.rentViewPager.setAdapter(serviceAdapter);
        binding.rentTabLayout.addTab(binding.rentTabLayout.newTab().setText(getString(R.string.all_service)));
        binding.rentTabLayout.addTab(binding.rentTabLayout.newTab().setText(getString(R.string.need_document)));
        binding.rentViewPager.setUserInputEnabled(true);
        binding.rentViewPager.setSaveEnabled(false);

        binding.rentTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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


        binding.rentViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.rentTabLayout.selectTab(binding.rentTabLayout.getTabAt(position));
            }
        });

    }

    private void setPagerFragment(int a) {
        binding.rentViewPager.setCurrentItem(a);
    }
}