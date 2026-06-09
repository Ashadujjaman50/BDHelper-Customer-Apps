package com.krishibarirangpur.bdhelper.userActivity.partner;

import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.ViewPagerOrderAdapter;
import com.krishibarirangpur.bdhelper.databinding.ActivityOrderRentBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;

import java.util.ArrayList;

public class OrderRentActivity extends BaseActivity {

    private ActivityOrderRentBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_rent);

        binding.backBtn.setOnClickListener(v -> finishOnBack());

        loadSubCategoryIds();
    }

    private void loadSubCategoryIds() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        db.collection(FirebaseCollectionTable.USERS).document(userId).collection(FirebaseCollectionTable.SERVICES)
                .whereEqualTo("serviceVerified", "verified")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> subCategoryIds = new ArrayList<>();
                    ArrayList<String> categoryIds = new ArrayList<>();
                    ArrayList<String> sizeAndCapacities = new ArrayList<>();
                    ArrayList<String> categoryAndYears = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String subId = doc.getString("subCategoryId");
                        if (subId != null) {
                            subCategoryIds.add(subId);
                            categoryIds.add(doc.getString("categoryId"));
                            
                            Object specsObj = doc.get("specs");
                            if (specsObj instanceof java.util.Map) {
                                java.util.Map<String, Object> specs = (java.util.Map<String, Object>) specsObj;
                                sizeAndCapacities.add((String) specs.get("sizeAndCapacity"));
                                categoryAndYears.add((String) specs.get("categoryAndYear"));
                            } else {
                                sizeAndCapacities.add(null);
                                categoryAndYears.add(null);
                            }
                        }
                    }
                    Log.d("OrderRentLog", "Total verified services: " + subCategoryIds.size());
                    setupViewPager(subCategoryIds, categoryIds, sizeAndCapacities, categoryAndYears);
                })
                .addOnFailureListener(e -> Log.e("OrderRent", "Error: " + e.getMessage()));
    }

    private void setupViewPager(ArrayList<String> subCategoryIds, ArrayList<String> categoryIds, ArrayList<String> sizeAndCapacities, ArrayList<String> categoryAndYears) {
        ViewPagerOrderAdapter adapter = new ViewPagerOrderAdapter(getSupportFragmentManager(), getLifecycle(), subCategoryIds, categoryIds, sizeAndCapacities, categoryAndYears);
        binding.bidViewPager.setAdapter(adapter);
        binding.bidViewPager.setSaveEnabled(false);

        // TabLayout with ViewPager2 linking
        new TabLayoutMediator(binding.bidTabLayout, binding.bidViewPager, (tab, position) ->
                tab.setText(position == 0 ? getString(R.string.your_order_rent) : getString(R.string.all_order_rent))).attach();

        setupTabStyle();

        binding.bidTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.view.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(200).start();
                updateTabBackgrounds();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.view.animate().scaleX(0.95f).scaleY(0.95f).alpha(0.8f).setDuration(200).start();
            }

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupTabStyle() {
        binding.bidTabLayout.post(this::updateTabBackgrounds);
    }

    private void updateTabBackgrounds() {
        if (binding.bidTabLayout.getTabCount() < 2) return;
        TabLayout.Tab left = binding.bidTabLayout.getTabAt(0);
        TabLayout.Tab right = binding.bidTabLayout.getTabAt(1);
        if (left != null) left.view.setBackgroundResource(R.drawable.left_tab_selector);
        if (right != null) right.view.setBackgroundResource(R.drawable.right_tab_selector);
    }
}
