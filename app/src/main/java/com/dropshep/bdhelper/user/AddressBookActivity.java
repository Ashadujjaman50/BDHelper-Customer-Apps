package com.dropshep.bdhelper.user;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.ActivityAddressBookBinding;
import com.dropshep.bdhelper.myUtils.BaseActivity;
import com.dropshep.bdhelper.myUtils.ThemeUtil;

public class AddressBookActivity extends BaseActivity {

    private ActivityAddressBookBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_book);

        //Init views
        String controlType = getIntent().getStringExtra("controlType");
        if (controlType != null && controlType.equals("addressList")){
            //
        }
        else {
            //
        }

        binding.backBtn.setOnClickListener(v -> finishOnBack());

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}