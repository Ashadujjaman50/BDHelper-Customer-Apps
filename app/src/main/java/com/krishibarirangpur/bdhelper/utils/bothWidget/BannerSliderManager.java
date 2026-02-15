package com.krishibarirangpur.bdhelper.utils.bothWidget;

import android.content.Context;
import android.util.Log;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.krishibarirangpur.bdhelper.adapter.SliderAdapterAuto;
import com.krishibarirangpur.bdhelper.model.SlideImage;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class BannerSliderManager {

    private final FirebaseFirestore db;

    public BannerSliderManager() {
        db = FirebaseFirestore.getInstance();
    }

    // 🔹 For ImageSlider (Home Screens)
    public void loadImageSlider(
            ImageSlider imageSlider,
            String audienceType,
            String placementType
    ) {

        List<SlideModel> imageList = new ArrayList<>();

        db.collection("bannersSlide")
                .orderBy("order", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        SlideImage slide = doc.toObject(SlideImage.class);

                        if (slide != null
                                && (slide.getAudience().equals(audienceType)
                                || slide.getAudience().equals("Both"))
                                && (slide.getPlacement().equals(placementType)
                                || slide.getPlacement().equals("Both"))) {

                            imageList.add(new SlideModel(
                                    slide.getImageUrl(),
                                    ScaleTypes.FIT
                            ));
                        }
                    }

                    imageSlider.setImageList(imageList);
                    imageSlider.startSliding(2000);

                })
                .addOnFailureListener(e ->
                        Log.e("BannerSlider", "Error loading ImageSlider", e)
                );
    }


    // 🔹 For SliderView (Help Screen)
    public ListenerRegistration loadAutoSlider(
            Context context,
            SliderView sliderView,
            String audienceType,
            String placementType
    ) {

        ArrayList<SlideImage> slideList = new ArrayList<>();
        SliderAdapterAuto adapter = new SliderAdapterAuto(context, slideList);

        sliderView.setSliderAdapter(adapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorVisibility(false);
        sliderView.setScrollTimeInSec(3);
        sliderView.setSliderAnimationDuration(1000);
        sliderView.setIndicatorEnabled(false);
        sliderView.startAutoCycle();
        sliderView.setAutoCycle(true);

        return db.collection("bannersSlide")
                .orderBy("order", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {

                    if (e != null) {
                        Log.e("BannerSlider", "Error loading SliderView", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {

                        List<SlideImage> newList = new ArrayList<>();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            SlideImage slide = doc.toObject(SlideImage.class);

                            if (slide != null
                                    && (slide.getAudience().equals(audienceType)
                                    || slide.getAudience().equals("Both"))
                                    && (slide.getPlacement().equals(placementType)
                                    || slide.getPlacement().equals("Both"))) {

                                newList.add(slide);
                            }
                        }

                        adapter.updateList(newList);
                    }
                });
    }
}

