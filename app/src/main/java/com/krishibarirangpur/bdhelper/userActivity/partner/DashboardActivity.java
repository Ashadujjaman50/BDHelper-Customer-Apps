package com.krishibarirangpur.bdhelper.userActivity.partner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityDashboardBinding;
import com.krishibarirangpur.bdhelper.sharedFragment.HelpFragment;
import com.krishibarirangpur.bdhelper.model.BidSummary;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.CacheManager;
import com.krishibarirangpur.bdhelper.utils.FinanceCache;
import com.krishibarirangpur.bdhelper.utils.FinanceManager;
import com.krishibarirangpur.bdhelper.utils.network.NetworkUtils;
import com.krishibarirangpur.bdhelper.utils.network.NoInternetDialog;
import com.krishibarirangpur.bdhelper.utils.network.NotificationPermissionHelper;
import com.krishibarirangpur.bdhelper.utils.core.SharedPrefHelper;
import com.krishibarirangpur.bdhelper.utils.core.ThemeUtil;
import com.krishibarirangpur.bdhelper.userFragment.partner.navBarFragment.MainFragment;
import com.krishibarirangpur.bdhelper.userFragment.partner.navBarFragment.MoreFragment;
import com.krishibarirangpur.bdhelper.userFragment.partner.navBarFragment.RentFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding binding;
    private boolean doubleBackToExitPressedOnce = false;
    boolean backPress = false;
    private static final String KEY_FIRST_TIME_NOTIFICATION_REQUESTED = "first_time_notification_requested";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            NoInternetDialog internetDialog = new NoInternetDialog();
            internetDialog.show(getSupportFragmentManager(), "NoInternetDialog");
        }
        //Finance Info Preload (For MoreFragment)
        preloadFinanceSummary(); // ✅ preload once
        preloadVendorBidSummary(); // ✅ preload once


        //Post Notification Enable
        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(this);
        boolean alreadyAsked = sharedPrefHelper.getBoolean(KEY_FIRST_TIME_NOTIFICATION_REQUESTED, false);

        if (!alreadyAsked) {
            NotificationPermissionHelper.requestPermissionAndSubscribe(this);
            sharedPrefHelper.putBoolean(KEY_FIRST_TIME_NOTIFICATION_REQUESTED, true);
        }

        // শুধু তখনি ফ্র্যাগমেন্ট লোড হবে যদি savedInstanceState null হয়
        if (savedInstanceState == null) {
            backPress = true;
            replaceFragment(new MainFragment());
        } else {
            int selectedTabId = savedInstanceState.getInt("selected_tab", R.id.home);
            binding.customBottomBar2.setSelectedItemId(selectedTabId);
        }

        binding.customBottomBar2.inflateMenu(R.menu.nav_bottom_menu_partner);
        binding.customBottomBar2.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                replaceFragment(new MainFragment());
                backPress = true;
            } else if (itemId == R.id.nav_rent) {
                replaceFragment(new RentFragment());
                backPress = false;
            } else if (itemId == R.id.nav_help) {
                // HelpFragment এ data পাঠানোর জন্য bundle তৈরি করো
                Bundle bundle = new Bundle();
                bundle.putString("user_type", "partner");

                HelpFragment helpFragment = new HelpFragment();
                helpFragment.setArguments(bundle);

                replaceFragment(helpFragment);
                backPress = false;
            } else if (itemId ==R.id.nav_profile){
                replaceFragment(new MoreFragment());
                backPress = false;
            }
            return true;
        });


    }

    private void preloadFinanceSummary() {
        //FinanceCache.lastUpdated = System.currentTimeMillis();

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FinanceManager fm = new FinanceManager();

        fm.getPartnerFinanceSummary(currentUserId, (totalEarned, partnerReceivable, companyReceivable) -> {
            FinanceCache.totalEarned = totalEarned;
            FinanceCache.partnerReceivable = partnerReceivable;
            FinanceCache.companyReceivable = companyReceivable;
            FinanceCache.isLoaded = true;
            Log.d("FinanceCache", "✅ Preloaded finance summary");
        });
    }

    private void preloadVendorBidSummary() {
        String vendorId = FirebaseAuth.getInstance().getUid();
        long todayMillis = System.currentTimeMillis();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bidForOrder")
                .whereEqualTo("bidInfo.vendorId", vendorId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalCount = 0;
                    int successCount = 0;
                    int cancelCount = 0;

                    for (DocumentSnapshot doc : querySnapshot) {
                        totalCount++;

                        String status = doc.getString("bidInfo.status");
                        String rentTimeStr = doc.getString("orderInfo.rentTime");

                        long rentTime = 0L;
                        try {
                            rentTime = Long.parseLong(rentTimeStr != null ? rentTimeStr : "0");
                        } catch (NumberFormatException ignored) {}

                        //Log.d("preloadVendorBidSummary", "Status: "+status);
                        if ("confirmed".equals(status) || "done".equals(status)) {
                            successCount++;
                        } else if ("pending".equals(status) && rentTime < todayMillis) {
                            cancelCount++;
                        }
                    }

                    // ✅ Cache এ রাখো
                    BidSummary summary = new BidSummary(totalCount, successCount, cancelCount);
                    CacheManager.getInstance().setBidSummary(summary);

                    Log.d("BidSummaryCache", "✅ Cached: " + totalCount + " | Success: " + successCount + " | Cancel: " + cancelCount);
                })
                .addOnFailureListener(e -> Log.e("BidSummaryCache", "❌ Failed: " + e.getMessage()));
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    @Override
    protected boolean handleCustomBack() {
        if (backPress)  {
            // এখন nav_home এ আছো, এখন double back চেক করো
            if (doubleBackToExitPressedOnce) {
                return false; // অর্থাৎ exit করবে
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            return true; // ব্যাক হ্যান্ডেলড, বের হবে না
        }
        else {
            // অন্য tab থাকলে প্রথমে home-এ ফেরত যাও
            backPress = true;
            replaceFragment(new MainFragment());
            binding.customBottomBar2.getMenu().findItem(R.id.nav_home).setChecked(true);

            return true;
        }
    }

    public void refreshCurrentMenuItem() {
        int selectedItemId = binding.customBottomBar2.getSelectedItemId();
        binding.customBottomBar2.setSelectedItemId(selectedItemId);
    }
}
