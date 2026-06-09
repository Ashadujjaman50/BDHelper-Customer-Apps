package com.krishibarirangpur.bdhelper.utils.sharedWidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.krishibarirangpur.bdhelper.adapter.shared.SliderAdapterAuto;
import com.krishibarirangpur.bdhelper.model.SlideImageModel;
import com.krishibarirangpur.bdhelper.utils.firebase.FirebaseCollectionTable;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BannerSliderManager {

    private final FirebaseFirestore db;
    private static final String PREF_NAME = "BannerCache";
    private final Gson gson;

    public BannerSliderManager() {
        db = FirebaseFirestore.getInstance();
        gson = new Gson();
    }


    // 🔹 For ImageSlider (Home Screens) - Optimized with Caching
    public void loadImageSlider(
            Context context,
            ImageSlider imageSlider,
            String audienceType,
            String placementType
    ) {
        String cacheKey = "Slider_" + audienceType + "_" + placementType;
        
        // 1. Load from Cache immediately
        List<SlideImageModel> cachedData = getCachedBanners(context, cacheKey);
        if (cachedData != null && !cachedData.isEmpty()) {
            displayInImageSlider(imageSlider, cachedData);
        }

        // 2. Fetch from Firestore (Server)
        db.collection(FirebaseCollectionTable.BANNERS_SLIDE)
                .whereIn("audience", java.util.Arrays.asList(audienceType, "Both"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<SlideImageModel> newList = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            SlideImageModel slide = doc.toObject(SlideImageModel.class);
                            if (slide != null && (slide.getPlacement().equals(placementType) || slide.getPlacement().equals("Both"))) {
                                newList.add(slide);
                            }
                        }

                        if (!newList.isEmpty()) {
                            saveCache(context, cacheKey, newList);
                            displayInImageSlider(imageSlider, newList);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("BannerSlider", "Error loading ImageSlider", e));
    }

    private void displayInImageSlider(ImageSlider imageSlider, List<SlideImageModel> list) {
        List<SlideModel> imageList = new ArrayList<>();
        for (SlideImageModel slide : list) {
            imageList.add(new SlideModel(slide.getImageUrl(), ScaleTypes.FIT));
        }
        imageSlider.setImageList(imageList);
        imageSlider.startSliding(5500);
    }


    // 🔹 For SliderView (Help Screen) - Optimized with Caching
    public void loadAutoSlider(
            Context context,
            SliderView sliderView,
            String audienceType,
            String placementType
    ) {
        String cacheKey = "AutoSlider_" + audienceType + "_" + placementType;
        ArrayList<SlideImageModel> slideList = new ArrayList<>();
        
        // Load initial data from cache
        List<SlideImageModel> cachedData = getCachedBanners(context, cacheKey);
        if (cachedData != null) {
            slideList.addAll(cachedData);
        }

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

        // Fetch fresh data from Firestore
        db.collection(FirebaseCollectionTable.BANNERS_SLIDE)
                .whereIn("audience", java.util.Arrays.asList(audienceType, "Both"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<SlideImageModel> newList = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            SlideImageModel slide = doc.toObject(SlideImageModel.class);
                            if (slide != null && (slide.getPlacement().equals(placementType) || slide.getPlacement().equals("Both"))) {
                                newList.add(slide);
                            }
                        }
                        if (!newList.isEmpty()) {
                            saveCache(context, cacheKey, newList);
                            adapter.updateList(newList);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("BannerSlider", "Error loading SliderView", e));
    }

    // 🔹 Caching Helpers
    private void saveCache(Context context, String key, List<SlideImageModel> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = gson.toJson(list);
        prefs.edit().putString(key, json).apply();
    }

    private List<SlideImageModel> getCachedBanners(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);
        if (json == null) return null;
        Type type = new TypeToken<ArrayList<SlideImageModel>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

