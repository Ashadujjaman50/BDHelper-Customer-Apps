package com.dropshep.bdhelper.partner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.denzcoskun.imageslider.adapters.ViewPagerAdapter;
import com.dropshep.bdhelper.ChatActivity;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.adapter.ViewPagerProductAdapter;
import com.dropshep.bdhelper.databinding.ActivityProductBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
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

        //init views
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        //call Btn
        binding.callNowBtn.setOnClickListener(v -> {
            //Call to det dialer
            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel: " + "+8809666700722"));
            startActivity(intent);
        });


        FragmentManager fm = getSupportFragmentManager();
        ViewPagerProductAdapter sa = new ViewPagerProductAdapter(fm, getLifecycle());
        binding.viewPager.setAdapter(sa);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("আমার অর্ডারগুলো"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ব্যাটারি অর্ডার"));
        binding.viewPager.setUserInputEnabled(true);
        binding.viewPager.setSaveEnabled(false);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setPagerFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

}