package com.krishibarirangpur.bdhelper.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.krishibarirangpur.bdhelper.ChatActivity;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.adapter.SliderAdapterAuto;
import com.krishibarirangpur.bdhelper.model.SlideImage;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.krishibarirangpur.bdhelper.user.ServiceRequestActivity;
import com.krishibarirangpur.bdhelper.databinding.FragmentHelpBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class HelpFragment extends Fragment {

    private FragmentHelpBinding binding;

    String userType;

    ArrayList<SlideImage> slideImageArrayList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        if (userType.equals("partner")){
            fetchSlides(view, "partnerHelp");
        }
        else {
            fetchSlides(view, "customerHelp");
        }


    }


    private void fetchSlides(View view, String slideType) {

        slideImageArrayList = new ArrayList<>();
        //SliderView
        SliderView sliderView;
        sliderView = view.findViewById(R.id.imageSlider);

        //SliderView
        SliderAdapterAuto sliderAdapter = new SliderAdapterAuto(getContext(), slideImageArrayList);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorVisibility(false);
        sliderView.setScrollTimeInSec(3);
        sliderView.setSliderAnimationDuration(1000);
        sliderView.setIndicatorEnabled(false);
        sliderView.startAutoCycle();
        sliderView.setAutoCycle(true);

        db.collection("slides")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error fetching slides", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<SlideImage> newList = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            SlideImage slide = doc.toObject(SlideImage.class);
                            if (slide != null && slide.getSlideType().equals(slideType)) {
                                newList.add(slide);
                            }
                        }

                        // Adapter update করা হচ্ছে, নতুন instance না নেয়া
                        sliderAdapter.updateList(newList);
                    }
                });

    }


}