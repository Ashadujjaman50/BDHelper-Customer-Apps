package com.dropshep.bdhelper.partner;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.ActivityAddServiceBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.ThemeUtil;
import com.dropshep.bdhelper.partnerFragment.AddServiceFormFragment;
import com.dropshep.bdhelper.partnerFragment.SelectServiceCategoryFragment;

import java.util.Objects;

public class AddServiceActivity extends BaseActivity {

    ActivityAddServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_service);

        String loadDefault = getIntent().getStringExtra("loadDefault");

        if ("selectCategory".equals(loadDefault) || Objects.equals(loadDefault, "")) {
            loadFragment(new SelectServiceCategoryFragment(), false);
        }
        else if ("selectAddService".equals(loadDefault)) {
            // প্রথমে Category fragment load করো
            loadFragment(new SelectServiceCategoryFragment(), false);
            // তারপর AddService fragment load করো (backstack এ যাবে)
            loadFragment(new AddServiceFormFragment(), true);
        }

        binding.backBtn.setOnClickListener(v -> checkBack());
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        var transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }


    public void checkBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // যদি backstack এ কিছু থাকে, তাহলে popBackStack করো
            getSupportFragmentManager().popBackStack();
        } else {
            // না থাকলে Activity শেষ করো
            finishOnBack();
        }
    }
}
