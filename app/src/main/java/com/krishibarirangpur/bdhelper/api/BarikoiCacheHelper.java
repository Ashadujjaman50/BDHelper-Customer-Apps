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

        double range = 0.00015; // Increased range slightly for better coverage (~15-16 meters)

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

                        // Manual longitude filtering for efficiency
                        if (dbLng < lng - range || dbLng > lng + range) continue;

                        float[] result = new float[1];

                        Location.distanceBetween(
                                lat,
                                lng,
                                dbLat,
                                dbLng,
                                result
                        );

                        float distance = result[0];
                        Log.d(TAG, "Distance check: " + distance + "m");

                        if (distance <= 4) { // Updated to 4 meters as requested

                            BarikoiResponse.Place place =
                                    doc.get("place", BarikoiResponse.Place.class);

                            if (place != null) {
                                Log.d(TAG, "Loaded from Firebase cache (Found within 4m)");
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
        data.put("place", place);
        data.put("timestamp", System.currentTimeMillis());

        db.collection(COLLECTION)
                .add(data)
                .addOnSuccessListener(doc -> Log.d(TAG, "Location cached"))
                .addOnFailureListener(e -> Log.e(TAG, "Cache save failed"));
    }

}
