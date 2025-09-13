package com.dropshep.bdhelper.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.ActivityMainBinding;
import com.dropshep.bdhelper.fragment.HelpFragment;
import com.dropshep.bdhelper.FirebaseMessaging.FCMTokenManager;
import com.dropshep.bdhelper.myUtils.NotificationPermissionHelper;
import com.dropshep.bdhelper.myUtils.SharedPrefHelper;
import com.dropshep.bdhelper.userFragment.HomeFragment;
import com.dropshep.bdhelper.userFragment.PastPostFragment;
import com.dropshep.bdhelper.userFragment.ProfileFragment;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.NetworkUtils;
import com.dropshep.bdhelper.myUtils.NoInternetDialog;
import com.dropshep.bdhelper.myUtils.ThemeUtil;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private boolean doubleBackToExitPressedOnce = false;
    boolean backPress = false;
    private static final String KEY_FIRST_TIME_NOTIFICATION_REQUESTED = "first_time_notification_requested";


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Show no internet dialog if offline
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
            replaceFragment(new HomeFragment());
        } else {
            int selectedTabId = savedInstanceState.getInt("selected_tab", R.id.home);
            binding.customBottomBar.setSelectedItemId(selectedTabId);
        }

        binding.customBottomBar.inflateMenu(R.menu.nav_bottom_menu);
        binding.customBottomBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
                backPress = true;
            } else if (itemId == R.id.rentPost) {
                replaceFragment(new PastPostFragment());
                backPress = false;
            } else if (itemId == R.id.help) {
                // HelpFragment এ data পাঠানোর জন্য bundle তৈরি করো
                Bundle bundle = new Bundle();
                bundle.putString("user_type", "customer");

                HelpFragment helpFragment = new HelpFragment();
                helpFragment.setArguments(bundle);

                replaceFragment(helpFragment);
                backPress = false;
            } else if (itemId ==R.id.profile){
                replaceFragment(new ProfileFragment());
                backPress = false;
            }
            return true;
        });

        //Token Update
        FCMTokenManager.updateFCMToken();

    }


    // ফ্র্যাগমেন্ট রিপ্লেস করে
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

    @Override
    protected boolean handleCustomBack() {
        if (backPress) {
            // এখন home এ আছো, এখন double back চেক করো
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
            replaceFragment(new HomeFragment());
            binding.customBottomBar.getMenu().findItem(R.id.home).setChecked(true);

            return true;
        }

    }

    // বাইরের ফ্র্যাগমেন্ট/এক্টিভিটি থেকে BottomBar রিফ্রেশ করতে চাইলে ব্যবহার করো
    public void refreshCurrentMenuItem() {
        int selectedItemId = binding.customBottomBar.getSelectedItemId();
        binding.customBottomBar.setSelectedItemId(selectedItemId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NotificationPermissionHelper.onRequestPermissionsResult(this, requestCode, grantResults);
    }


}
