package com.krishibarirangpur.bdhelper.userActivity.partner;

import android.os.Bundle;
import android.view.View;
import android.util.Log;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.ViewPagerOrderAdapter;
import com.krishibarirangpur.bdhelper.databinding.ActivityOrderRentBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class OrderRentActivity extends BaseActivity {

    private ActivityOrderRentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_rent);

        // Back button
        binding.backBtn.setOnClickListener(v -> finishOnBack());

        // Tabs style set
        binding.bidTabLayout.post(this::setupTabBackgrounds);

        // 🔥 Firestore থেকে subCategoryIds আগে লোড করবো
        loadSubCategoryIds();
    }

    private void loadSubCategoryIds() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("services")
                .whereEqualTo("serviceVerified", "verified")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> subCategoryIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String subCategoryId = doc.getString("subCategoryId");
                        if (subCategoryId != null) {
                            subCategoryIds.add(subCategoryId);
                        }
                    }

                    Log.d("OrderRentActivity", "Loaded SubCategoryIds: " + subCategoryIds);

                    // 🔥 Adapter এখনই সেট করা হবে
                    setupViewPager(subCategoryIds);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error: " + e.getMessage()));
    }

    private void setupViewPager(ArrayList<String> subCategoryIds) {
        // ViewPager Adapter
        ViewPagerOrderAdapter orderAdapter =
                new ViewPagerOrderAdapter(getSupportFragmentManager(), getLifecycle(), subCategoryIds);
        binding.bidViewPager.setAdapter(orderAdapter);

        // Add Tabs
        binding.bidTabLayout.addTab(binding.bidTabLayout.newTab().setText(getString(R.string.all_order_rent)));
        binding.bidTabLayout.addTab(binding.bidTabLayout.newTab().setText(getString(R.string.your_order_rent)));
        binding.bidViewPager.setUserInputEnabled(true);
        binding.bidViewPager.setSaveEnabled(false);

        // Initial tab backgrounds
        setupTabBackgrounds();

        // Tab Selected Listener
        binding.bidTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate().scaleX(1f).scaleY(1f).alpha(0.9f).setDuration(200).start();
                setPagerFragment(tab.getPosition());
                setupTabBackgrounds();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.view;
                tabView.animate().scaleX(0.9f).scaleY(0.9f).alpha(0.8f).setDuration(200).start();
                setupTabBackgrounds();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                setupTabBackgrounds();
            }
        });

        // ViewPager Page Change
        binding.bidViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.bidTabLayout.selectTab(binding.bidTabLayout.getTabAt(position));
            }
        });
    }

    private void setPagerFragment(int a) {
        binding.bidViewPager.setCurrentItem(a);
    }

    // Tab Backgrounds
    private void setupTabBackgrounds() {
        if (binding.bidTabLayout.getTabCount() != 2) return;

        TabLayout.Tab leftTab = binding.bidTabLayout.getTabAt(0);
        if (leftTab != null) leftTab.view.setBackgroundResource(R.drawable.left_tab_selector);

        TabLayout.Tab rightTab = binding.bidTabLayout.getTabAt(1);
        if (rightTab != null) rightTab.view.setBackgroundResource(R.drawable.right_tab_selector);
    }
}
