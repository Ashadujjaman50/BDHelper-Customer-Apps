package com.dropshep.bdhelper.partner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.ActivityDashboardBinding;
import com.dropshep.bdhelper.fragment.HelpFragment;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.FirebaseMessaging.FCMTokenManager;
import com.dropshep.bdhelper.myUtils.NetworkUtils;
import com.dropshep.bdhelper.myUtils.NoInternetDialog;
import com.dropshep.bdhelper.myUtils.NotificationPermissionHelper;
import com.dropshep.bdhelper.myUtils.SharedPrefHelper;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partnerFragment.MainFragment;
import com.dropshep.bdhelper.partnerFragment.MoreFragment;
import com.dropshep.bdhelper.partnerFragment.RentFragment;

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

        //Token Update
        FCMTokenManager.updateFCMToken();
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
            binding.customBottomBar2.getMenu().findItem(R.id.home).setChecked(true);

            return true;
        }
    }

    public void refreshCurrentMenuItem() {
        int selectedItemId = binding.customBottomBar2.getSelectedItemId();
        binding.customBottomBar2.setSelectedItemId(selectedItemId);
    }
}
