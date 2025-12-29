package com.krishibarirangpur.bdhelper.partner;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.ActivityAddServiceBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;
import com.krishibarirangpur.bdhelper.partnerFragment.AddServiceFormFragment;
import com.krishibarirangpur.bdhelper.partnerFragment.SelectServiceCategoryFragment;

import java.util.Objects;

public class AddServiceActivity extends BaseActivity {

    ActivityAddServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_service);

        String loadDefault = getIntent().getStringExtra("loadDefault");

        if ("selectCategory".equals(loadDefault) || Objects.equals(loadDefault, "")) {
            // ✅ শুধুমাত্র ক্যাটাগরি সিলেক্ট করতে যাবে
            loadFragment(new SelectServiceCategoryFragment(), false);

        }
        else if ("selectAddService".equals(loadDefault)) {
            // ✅ প্রথমে category fragment লোড (optional)
            loadFragment(new SelectServiceCategoryFragment(), false);

            // ✅ intent থেকে ডাটা নিয়ে bundle এ পাঠানো
            String categoryId = getIntent().getStringExtra(MyUtils.categoryId);
            String subCategoryId = getIntent().getStringExtra(MyUtils.subCategoryId);
            String subCategoryName = getIntent().getStringExtra(MyUtils.subCategoryName);

            Bundle bundle = new Bundle();
            bundle.putString(MyUtils.categoryId, categoryId);
            bundle.putString(MyUtils.subCategoryId, subCategoryId);
            bundle.putString(MyUtils.subCategoryName, subCategoryName);

            AddServiceFormFragment fragment = new AddServiceFormFragment();
            fragment.setArguments(bundle);

            // ✅ এখন সরাসরি AddServiceFormFragment এ যাবে
            loadFragment(fragment, false);
        }

        binding.backBtn.setOnClickListener(v -> checkBack());
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        var transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public void checkBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finishOnBack();
        }
    }
}
