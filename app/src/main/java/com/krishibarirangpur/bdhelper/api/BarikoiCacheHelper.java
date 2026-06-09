package com.krishibarirangpur.bdhelper.api;

import android.location.Location;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class BarikoiCacheHelper {

    private static final String TAG = "LOCATION_SOURCE";
    private static final String COLLECTION = "barikoi_cache";

    private final FirebaseFirestore db;

    public interface LocationCallback {
        void onSuccess(BarikoiResponse.Place place);
        void onCacheMiss();
        void onError(String error);
    }

    public BarikoiCacheHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void getLocation(double lat, double lng, LocationCallback callback) {

        double range = 0.00015; // প্রায় ১৫-১৬ মিটার রেঞ্জ

        db.collection(COLLECTION)
                .whereGreaterThanOrEqualTo("lat", lat - range)
                .whereLessThanOrEqualTo("lat", lat + range)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean foundInCache = false;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Double dbLat = doc.getDouble("lat");
                        Double dbLng = doc.getDouble("lng");

                        if (dbLat == null || dbLng == null) continue;

                        // Longitude ফিল্টারিং
                        if (dbLng < lng - range || dbLng > lng + range) continue;

                        float[] result = new float[1];
                        Location.distanceBetween(lat, lng, dbLat, dbLng, result);

                        float distance = result[0];
                        Log.d(TAG, "Distance check: " + distance + "m");

                        if (distance <= 4) { // ৪ মিটারের মধ্যে হলে
                            BarikoiResponse.Place place = doc.get("place", BarikoiResponse.Place.class);

                            if (place != null) {
                                Log.d(TAG, "Loaded from Firestore cache (Found within 4m)");
                                callback.onSuccess(place);
                                foundInCache = true;
                                break;
                            }
                        }
                    }

                    if (!foundInCache) {
                        Log.d(TAG, "Cache miss (No results within 4m)");
                        callback.onCacheMiss();
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void saveToCache(double lat, double lng, BarikoiResponse.Place place) {
        Map<String, Object> data = new HashMap<>();
        data.put("lat", lat);
        data.put("lng", lng);
        
        if (place != null) {
            data.put("place", sanitizePlace(place));
        } else {
            data.put("place", null);
        }
        
        data.put("timestamp", System.currentTimeMillis());

        db.collection(COLLECTION)
                .add(data)
                .addOnSuccessListener(unused -> Log.d(TAG, "Location cached in Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Cache save failed in Firestore"));
    }

    private Map<String, Object> sanitizePlace(BarikoiResponse.Place place) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", place.getId());
        map.put("distance_within_meters", place.getDistance_within_meters());
        map.put("address", place.getAddress());
        map.put("area", place.getArea());
        map.put("city", place.getCity());
        map.put("postCode", place.getPostCode());
        map.put("address_bn", place.getAddress_bn());
        map.put("area_bn", place.getArea_bn());
        map.put("city_bn", place.getCity_bn());
        map.put("country", place.getCountry());
        map.put("division", place.getDivision());
        map.put("district", place.getDistrict());
        map.put("sub_district", place.getSub_district());
        map.put("pauroshova", place.getPauroshova());
        map.put("union", place.getUnion());
        map.put("location_type", place.getLocation_type());

        // Address Components
        Map<String, Object> addrComp = new HashMap<>();
        if (place.getAddress_components() != null) {
            addrComp.put("place_name", place.getAddress_components().getPlace_name());
            addrComp.put("house", place.getAddress_components().getHouse());
            addrComp.put("road", place.getAddress_components().getRoad());
        } else {
            addrComp.put("place_name", null);
            addrComp.put("house", null);
            addrComp.put("road", null);
        }
        map.put("address_components", addrComp);

        // Area Components
        Map<String, Object> areaComp = new HashMap<>();
        if (place.getArea_components() != null) {
            areaComp.put("area", place.getArea_components().getArea());
            areaComp.put("sub_area", place.getArea_components().getSub_area());
        } else {
            areaComp.put("area", null);
            areaComp.put("sub_area", null);
        }
        map.put("area_components", areaComp);

        return map;
    }
}
