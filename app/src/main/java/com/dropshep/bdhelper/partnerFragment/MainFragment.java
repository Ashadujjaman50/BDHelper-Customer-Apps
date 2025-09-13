package com.dropshep.bdhelper.partnerFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dropshep.bdhelper.ChatActivity;
import com.dropshep.bdhelper.NotificationActivity;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.databinding.FragmentMainBinding;


public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init views

        //notification  Activity
        binding.notificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NotificationActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        binding.contactUsText.setOnClickListener(v -> {
            //Chat Activity
            Intent intent = new Intent(requireActivity(), ChatActivity.class);
            intent.putExtra("adminID", "MxAvxVfrmCOeIvZZ8sh8W8XTAU23");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


    }
}