package com.krishibarirangpur.bdhelper.userActivity.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.BuildConfig;
import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.api.BarikoiApiService;
import com.krishibarirangpur.bdhelper.api.BarikoiCacheHelper;
import com.krishibarirangpur.bdhelper.api.BarikoiClient;
import com.krishibarirangpur.bdhelper.api.BarikoiResponse;
import com.krishibarirangpur.bdhelper.databinding.ActivityMapLocationBinding;
import com.krishibarirangpur.bdhelper.utils.core.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.sharedWidget.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.core.ThemeHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapLocationActivity extends BaseActivity implements OnMapReadyCallback {
    private ActivityMapLocationBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private AutocompleteSessionToken token;

    List<Address> addressList;

    BarikoiApiService apiInterface;

    private final float DEFAULT_ZOOM = 16;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map_location);

        // Initialize map and Places API
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(this, BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(this);
        token = AutocompleteSessionToken.newInstance();
        apiInterface = BarikoiClient.getClient();

        // Set up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up UI listeners
        binding.backBtn.setOnClickListener(v -> finish());
        binding.rentLocationTv.setOnClickListener(v -> binding.materialSearchBar.openSearch());
        binding.continuePostBtn.setOnClickListener(v -> handleContinueButton());

        // Set up search bar
        setupSearchBar();
    }

    private void setupSearchBar() {

        binding.materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                binding.materialSearchBar.clearSuggestions();
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, handleGlobalSearch(text.toString()));
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    binding.materialSearchBar.clearSuggestions();
                    binding.materialSearchBar.closeSearch();
                }
            }
        });

        binding.materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest =
                        FindAutocompletePredictionsRequest.builder()
                                .setCountries(Arrays.asList("BD"))
                                .setTypesFilter(Arrays.asList(PlaceTypes.ADDRESS))
                                .setSessionToken(token)
                                .setQuery(s.toString())
                                .setLocationBias(mMap != null ? RectangularBounds.newInstance(
                                        mMap.getProjection().getVisibleRegion().latLngBounds) : null)
                                .build();

                placesClient.findAutocompletePredictions(predictionsRequest)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                                if (predictionsResponse != null) {
                                    predictionList = predictionsResponse.getAutocompletePredictions();
                                    List<String> suggestionsList = new ArrayList<>();
                                    for (int i = 0; i < predictionList.size(); i++) {
                                        AutocompletePrediction prediction = predictionList.get(i);
                                        suggestionsList.add(prediction.getFullText(null).toString());
                                    }
                                    binding.materialSearchBar.updateLastSuggestions(suggestionsList);
                                    if (!binding.materialSearchBar.isSuggestionsVisible()) {
                                        binding.materialSearchBar.showSuggestionsList();
                                    }
                                }
                            } else {
                                Log.e("PlacesAPI", "Prediction fetching failed: " + task.getException());
                                Toast.makeText(MapLocationActivity.this, "Failed to fetch suggestions", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) return;

                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = binding.materialSearchBar.getLastSuggestions().get(position).toString();
                binding.materialSearchBar.setText(suggestion);

                handler.postDelayed(() -> binding.materialSearchBar.clearSuggestions(), 1000);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(binding.materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);


                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
                    Place place = fetchPlaceResponse.getPlace();

                    LatLng latLngOfPlace = place.getLatLng();
                    if (latLngOfPlace != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM));
                        binding.rentLocationTv.setText(suggestion);
                        hideKeyboard(MapLocationActivity.this);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("PlacesAPI", "Place fetch failed: " + e.getMessage());
                    Toast.makeText(MapLocationActivity.this, "Place not found", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {}
        });

    }

    private boolean handleGlobalSearch(String queryText) {
        mMap.clear();

        if (!TextUtils.isEmpty(queryText)) {

            Geocoder geocoder = new Geocoder(MapLocationActivity.this);

            try {
                addressList = geocoder.getFromLocationName(queryText, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList != null && !addressList.isEmpty()) {

                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                binding.rentLocationTv.setText(queryText);

                hideKeyboard(MapLocationActivity.this);
            } else {
                Toast.makeText(MapLocationActivity.this, "Address not found", Toast.LENGTH_LONG).show();
            }

        }

        return false;
    }

    private void handleContinueButton() {
        String rentLocation = binding.rentLocationTv.getText().toString().trim();
        if (rentLocation.isEmpty()) {
            binding.rentLocationTv.setBackgroundResource(R.drawable.bg_edit_text);
            binding.rentLocationTv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    binding.rentLocationTv.setBackgroundResource(R.drawable.bg_edit_text);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("result", rentLocation);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        checkPermissionsAndSetupMap();
        setupMapListeners();
    }

    private void checkPermissionsAndSetupMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setupMapListeners() {
        mMap.setOnCameraIdleListener(() -> {
            LatLng pickerLatLng = mMap.getCameraPosition().target;
            if (pickerLatLng.latitude != 0 && pickerLatLng.longitude != 0) {
                loadDataList(pickerLatLng.latitude, pickerLatLng.longitude);
                loadDataWithBarikoy(pickerLatLng.latitude, pickerLatLng.longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(pickerLatLng)
                        .title("Selected Location")
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                getBitmapFromVectorDrawable(this, R.drawable.ic_location_select))));
            }
        });

        mMap.setOnMyLocationButtonClickListener(() -> {
            if (binding.materialSearchBar.isSuggestionsVisible()) {
                binding.materialSearchBar.clearSuggestions();
            }
            if (binding.materialSearchBar.isSearchOpened()) {
                binding.materialSearchBar.closeSearch();
            }
            return false;
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));

                loadDataList(location.getLatitude(), location.getLongitude());
                loadDataWithBarikoy(location.getLatitude(), location.getLongitude());

                mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                getBitmapFromVectorDrawable(this, R.drawable.ic_location_select))));
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String address, area, city,subDistrict, district;

    private void loadDataList(double lat, double lng) {

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

                            subDistrict = barikoiResponse.getPlace().getSub_district();
                            district = barikoiResponse.getPlace().getDistrict();

                            if (barikoiResponse.getPlace().getAddress_bn() != null) {
                                address = barikoiResponse.getPlace().getAddress_bn();
                            } else {
                                address = barikoiResponse.getPlace().getAddress();
                            }

                            if (barikoiResponse.getPlace().getArea_bn() != null) {
                                area = barikoiResponse.getPlace().getArea_bn();
                            } else {
                                area = barikoiResponse.getPlace().getArea();
                            }

                            if (barikoiResponse.getPlace().getCity_bn() != null) {
                                city = barikoiResponse.getPlace().getCity_bn();
                            } else {
                                city = barikoiResponse.getPlace().getCity();
                            }

                            boolean check = Replacement.cityCheck(city);

                            if (!TextUtils.isEmpty(address)) {
                                if (TextUtils.isEmpty(subDistrict)) {
                                    address = Replacement.checkAddress(address, area, district);
                                } else {
                                    address = Replacement.checkAddress(address, subDistrict, district);
                                }
                            }

                            String finalAddress;
                            if (!TextUtils.isEmpty(city) && check) {
                                if (TextUtils.isEmpty(subDistrict) || Objects.equals(area, subDistrict)) {
                                    finalAddress = address + ", " + area + ", " + city + " " + "সিটি";
                                } else {
                                    finalAddress = address + ", " + subDistrict + ", " + area + ", " + city + " " +  "সিটি";
                                }
                            } else {
                                if (!TextUtils.isEmpty(address)) {
                                    finalAddress = address + ", " + subDistrict + ", " + district;
                                } else {
                                    finalAddress = area + ", " + subDistrict + ", " + district;
                                }
                            }

                            String cleanString = Replacement.checkString(finalAddress);
                            binding.rentLocationTv.setText(Replacement.removeDuplicateAddressParts(cleanString));
                        }
                        else {
                            Log.e("API", "Response body or place is null");
                        }
                    } else {
                        Log.e("API Error", "Error code: " + response.code() );
                    }
                }
                catch (Exception e) {
                    Log.e("API", "Error processing response", e);
                    binding.rentLocationTv.setText(R.string.select_location); // fallback UI
                }

            }

            @Override
            public void onFailure(Call<BarikoiResponse> call, Throwable t) {
                // Handle network errors here
                Log.e("API Error", "Network error: " + t.getMessage());
            }
        });

    }


    //Only Save this Data in database
    private void loadDataWithBarikoy(double lat, double lng) {
        BarikoiCacheHelper helper = new BarikoiCacheHelper();

        helper.getLocation(lat, lng, new BarikoiCacheHelper.LocationCallback() {
            @Override
            public void onSuccess(BarikoiResponse.Place place) {
                // place নাল কি না চেক করা ভালো
                if (place != null) {
                    String address = place.getAddress_bn();

                    // অ্যাড্রেস নাল কি না চেক করে লগ করুন
                    if (address != null) {
                        Log.d("ADDRESS", address);
                        return;
                    }
                    Log.d("ADDRESS", "Barikoi Address Cache done");
                }
            }

            @Override
            public void onError(String error) {
                // এরর মেসেজ নাল কি না চেক করুন
                Log.e("ERROR", Objects.requireNonNullElse(error, "Unknown error occurred in Barikoi helper"));
            }
        });
    }

    private void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissionsAndSetupMap();
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



}
