package com.krishibarirangpur.bdhelper.api;

import android.location.Location;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krishibarirangpur.bdhelper.utils.bothWidget.MyUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarikoiCacheHelper {

    private static final String TAG = "BarikoiCacheHelper";
    private static final String COLLECTION = "barikoi_cache";

    private FirebaseFirestore db;
    BarikoiApiService apiInterface;
    String address, area, city,subDistrict, district;

    public interface LocationCallback {
        void onSuccess(BarikoiResponse.Place place);
        void onError(String error);
    }

    public BarikoiCacheHelper() {
        db = FirebaseFirestore.getInstance();
        apiInterface = BarikoiClient.getClient();
    }

    public void getLocation(double lat, double lng, LocationCallback callback) {

        double range = 0.0001; // ~10 meter search box

        db.collection(COLLECTION)
                .whereGreaterThanOrEqualTo("lat", lat - range)
                .whereLessThanOrEqualTo("lat", lat + range)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        Double dbLat = doc.getDouble("lat");
                        Double dbLng = doc.getDouble("lng");

                        if (dbLat == null || dbLng == null) continue;

                        float[] result = new float[1];

                        Location.distanceBetween(
                                lat,
                                lng,
                                dbLat,
                                dbLng,
                                result
                        );

                        float distance = result[0];

                        if (distance <= 3) {

                            BarikoiResponse.Place place =
                                    doc.get("place", BarikoiResponse.Place.class);

                            if (place != null) {
                                Log.d(TAG, "Loaded from Firebase cache");
                                callback.onSuccess(place);
                                return;
                            }
                        }
                    }

                    // cache miss
                    Log.d(TAG, "Cache miss → calling Barikoi API");
                    callBarikoiApi(lat, lng, callback);

                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    private void callBarikoiApi(double lat, double lng, LocationCallback callback) {

        // এখানে আপনার Retrofit API call বসাবেন

        // Example pseudo code:

        Call<BarikoiResponse> call = apiInterface.getPlaceInfo(MyUtils.barikoi_api_key, lng, lat,
                true, // district
                true, // post_code
                true, // country
                true, // sub_district
                true, // union
                true, // pauroshova
                true, // location_type
                true, // division
                true, // address
                true, // area
                true // Bangla
        );

        call.enqueue(new Callback<BarikoiResponse>() {
            @Override
            public void onResponse(Call<BarikoiResponse> call, Response<BarikoiResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        BarikoiResponse barikoiResponse = response.body();

                        if (barikoiResponse != null && barikoiResponse.getPlace() != null) {
                            Log.d("API", "onResponse: " + barikoiResponse);
                            BarikoiResponse.Place place = response.body().getPlace();

                            saveToCache(lat, lng, place);

                            callback.onSuccess(place);
                        }
                        else {
                            Log.e("API", "Response body or place is null");
                            callback.onError("API response error");
                        }
                    } else {
                        Log.e("API Error", "Error code: " + response.code() );
                        callback.onError("API response error" + response.code());
                    }
                }
                catch (Exception e) {
                    Log.e("API", "Error processing response", e);
                    callback.onError(e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<BarikoiResponse> call, Throwable t) {
                // Handle network errors here
                Log.e("API Error", "Network error: " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });

    }

    private void saveToCache(double lat, double lng, BarikoiResponse.Place place) {

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