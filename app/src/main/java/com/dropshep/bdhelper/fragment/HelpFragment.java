package com.dropshep.bdhelper.fragment;

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

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.dropshep.bdhelper.ChatActivity;
import com.dropshep.bdhelper.R;
import com.dropshep.bdhelper.user.ServiceRequestActivity;
import com.dropshep.bdhelper.databinding.FragmentHelpBinding;

import java.util.ArrayList;

public class HelpFragment extends Fragment {

    private FragmentHelpBinding binding;

    String userType;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userType = getArguments().getString("user_type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_help, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init views

        if (userType.equals("partner")){
            binding.reqServiceLl.setVisibility(View.GONE);
        }

        //Slider Image Load and View
        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.demo_help, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.demo_support, ScaleTypes.FIT));

        binding.imageSlider.setImageList(imageList);


        binding.chatLl.setOnClickListener(v -> {
            //Chat activity
            Intent intent = new Intent(requireActivity(), ChatActivity.class);
            intent.putExtra("adminID", "TZY5QogPv5M8iqXmM07tLqVC3by1");
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.talkLl.setOnClickListener(v -> {
            //Call to det dialer
            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel: " + "+8809666700722"));
            startActivity(intent);
        });

        binding.reqServiceLl.setOnClickListener(v -> {
            //Service Request
            Intent intent = new Intent(requireActivity(), ServiceRequestActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }
}