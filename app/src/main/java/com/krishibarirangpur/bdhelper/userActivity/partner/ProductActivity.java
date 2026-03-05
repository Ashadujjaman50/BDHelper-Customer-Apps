package com.krishibarirangpur.bdhelper.userActivity.partner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.ViewPagerProductAdapter;
import com.krishibarirangpur.bdhelper.databinding.ActivityProductBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.google.android.material.tabs.TabLayout;

public class ProductActivity extends BaseActivity {

    private ActivityProductBinding binding;

    String productType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product);

        // Post call ensures tabs are inflated
        binding.tabLayout.post(this::setupTabBackgrounds);

        //init views
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        //call Btn
        binding.callNowBtn.setOnClickListener(v -> {
            //Call to det dialer
            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel: " + MyUtils.HOTLINE_NUMBER));
            startActivity(intent);
        });


        FragmentManager fm = getSupportFragmentManager();
        ViewPagerProductAdapter sa = new ViewPagerProductAdapter(fm, getLifecycle());
        binding.viewPager.setAdapter(sa);

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("আমার অর্ডারগুলো"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ব্যাটারি অর্ডার"));
        binding.viewPager.setUserInputEnabled(true);
        binding.viewPager.setSaveEnabled(false);

        // Set initial tab backgrounds (Left and Right)
        setupTabBackgrounds();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(0.9f)
                        .setDuration(200)
                        .start();

                setPagerFragment(tab.getPosition());

                // Update background for selected/unselected
                setupTabBackgrounds();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .alpha(0.8f)
                        .setDuration(200)
                        .start();

                // Update background for selected/unselected
                setupTabBackgrounds();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Update background for selected/unselected
                setupTabBackgrounds();
            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

        //Init get
        productType = getIntent().getStringExtra("productType");



    }

    public void setPagerFragment(int a) {
        binding.viewPager.setCurrentItem(a);
    }

    // 🔥 Left & Right tab background setup
    private void setupTabBackgrounds() {
        if (binding.tabLayout.getTabCount() != 2) return;

        TabLayout.Tab leftTab = binding.tabLayout.getTabAt(0);
        if (leftTab != null) leftTab.view.setBackgroundResource(R.drawable.left_tab_selector);

        TabLayout.Tab rightTab = binding.tabLayout.getTabAt(1);
        if (rightTab != null) rightTab.view.setBackgroundResource(R.drawable.right_tab_selector);
    }

}