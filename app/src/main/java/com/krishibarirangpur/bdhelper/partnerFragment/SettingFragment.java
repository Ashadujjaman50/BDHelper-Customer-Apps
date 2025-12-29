package com.krishibarirangpur.bdhelper.partnerFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krishibarirangpur.bdhelper.EditProfileActivity;
import com.krishibarirangpur.bdhelper.NIDPhotoActivity;
import com.krishibarirangpur.bdhelper.PermissionActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.databinding.FragmentSettingBinding;
import com.krishibarirangpur.bdhelper.utils.LogoutHelper;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.krishibarirangpur.bdhelper.utils.NotificationPermissionHelper;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views

        //Account Info
        binding.accountInfoRL.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            intent.putExtra("user_type", "partner");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        //document  upload
        binding.documentRL.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NIDPhotoActivity.class);
            intent.putExtra("user_type", "partner");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        //Notification Permission
        binding.notificationRl.setOnClickListener(v -> {
            NotificationPermissionHelper.showPermissionDialog(requireActivity());
        });

        //Goto Permission Activity
        binding.permissionRl.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PermissionActivity.class);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        //Terms And Condition
        binding.termsConditionRl.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.trems_conditions_partner_url));
            v.getContext().startActivity(intent);
        });

        //Privacy Policy
        binding.privacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyUtils.privacy_policy_url));
            v.getContext().startActivity(intent);
        });

        //Logout Current User
        binding.logOutRl.setOnClickListener(v -> {
            //Call LogoutHelper.class
            LogoutHelper.logoutUser(requireActivity());
        });
    }


}