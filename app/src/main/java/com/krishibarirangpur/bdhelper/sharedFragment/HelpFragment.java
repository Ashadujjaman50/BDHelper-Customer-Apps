package com.krishibarirangpur.bdhelper.sharedFragment;

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

import com.google.firebase.firestore.ListenerRegistration;
import com.krishibarirangpur.bdhelper.sharedActivity.ChatActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.userActivity.customer.ServiceRequestActivity;
import com.krishibarirangpur.bdhelper.databinding.FragmentHelpBinding;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.BannerSliderManager;
import com.smarteist.autoimageslider.SliderView;


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        if (userType.equals("partner")){
            fetchSlides(view, "Partner");
        }
        else {
            fetchSlides(view, "Customer");
        }


        binding.chatLl.setOnClickListener(v -> {
            //Chat activity
            Intent intent = new Intent(requireActivity(), ChatActivity.class);
            intent.putExtra("adminID", MyUtils.CHAT_ADMIN_ID);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        binding.talkLl.setOnClickListener(v -> {
            //Call to det dialer
            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel: " + MyUtils.HOTLINE_NUMBER));
            startActivity(intent);
        });

        binding.reqServiceLl.setOnClickListener(v -> {
            //Service Request
            Intent intent = new Intent(requireActivity(), ServiceRequestActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


    }


    private void fetchSlides(View view, String audienceType) {
        SliderView sliderView = view.findViewById(R.id.imageSlider);
        BannerSliderManager manager = new BannerSliderManager();
        manager.loadAutoSlider(
                getContext(),
                sliderView,
                audienceType,
                "Help");

    }

    public void onDestroy() {
        super.onDestroy();
    }

}