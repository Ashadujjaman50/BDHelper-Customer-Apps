package com.krishibarirangpur.bdhelper.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.krishibarirangpur.bdhelper.R;
import com.krishibarirangpur.bdhelper.api.BarikoiApiService;
import com.krishibarirangpur.bdhelper.api.BarikoiClient;
import com.krishibarirangpur.bdhelper.api.SearchAutoCompleteResponse;
import com.krishibarirangpur.bdhelper.databinding.ActivityAddressBinding;
import com.krishibarirangpur.bdhelper.utils.BaseActivity;
import com.krishibarirangpur.bdhelper.utils.MyUtils;
import com.krishibarirangpur.bdhelper.utils.Replacement;
import com.krishibarirangpur.bdhelper.utils.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends BaseActivity {

    private ActivityAddressBinding binding;

    String categoryId, subCategoryId, subCategoryName;
    boolean selectCategory = false, selectedPoint = false, editOn = false;
    private boolean isFromDivision = false;
    final List<String> placeNames = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    BarikoiApiService apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // থিম আগে সেট কর
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address);

        //init views
        categoryId = getIntent().getStringExtra(MyUtils.categoryId);
        subCategoryId = getIntent().getStringExtra(MyUtils.subCategoryId);
        subCategoryName = getIntent().getStringExtra(MyUtils.subCategoryName);

        binding.titleTv.setText(subCategoryName);

        binding.backBtn.setOnClickListener(v -> finishOnBack());


        //loading point Default Focus
        //binding.loadLocationTV.setBackgroundResource(R.drawable.bg_edit_text_primary);
        showDefaultFields();

        binding.getRoot().postDelayed(() -> {
            if (categoryId.equals(MyUtils.SKILLED_LABOR_ID) || categoryId.equals(MyUtils.HARVESTER_MACHINE_ID) || categoryId.equals(MyUtils.EQUIPMENT_ID)) {
                focusView(binding.rentLocationTv);
            } else {
                focusView(binding.loadLocationTV);
            }
        }, 300); // Slight delay ensures layout is fully loaded


        binding.continuePostBtn.setOnClickListener(v -> {
            if (categoryId.equals(MyUtils.SKILLED_LABOR_ID) || categoryId.equals(MyUtils.HARVESTER_MACHINE_ID) || categoryId.equals(MyUtils.EQUIPMENT_ID)) {
                String rentLocation = binding.rentLocationTv.getText().toString().trim();
                if (TextUtils.isEmpty(rentLocation)){
                    emptyFocusView(binding.rentLocationTv);
                }
                else {
                    gotoNextActivity("", "", rentLocation);
                }
            }
            else {
                String loadLocation = binding.loadLocationTV.getText().toString().trim();
                String unloadLocation = binding.unloadLocationTV.getText().toString().trim();
                if (TextUtils.isEmpty(loadLocation)){
                    emptyFocusView(binding.loadLocationTV);
                }
                else if (TextUtils.isEmpty(unloadLocation)) {
                    emptyFocusView(binding.unloadLocationTV);
                }
                else {
                    gotoNextActivity(loadLocation, unloadLocation, "");
                }
            }
        });

    }


    @SuppressLint("ClickableViewAccessibility")
    private void showDefaultFields() {

        // Category অনুযায়ী UI toggle
        boolean isSpecialCategory = categoryId.equals(MyUtils.SKILLED_LABOR_ID) ||
                categoryId.equals(MyUtils.HARVESTER_MACHINE_ID) ||
                categoryId.equals(MyUtils.EQUIPMENT_ID);

        selectCategory = isSpecialCategory;
        binding.loadingPointRl.setVisibility(isSpecialCategory ? View.GONE : View.VISIBLE);
        binding.unloadingPointRl.setVisibility(isSpecialCategory ? View.GONE : View.VISIBLE);
        binding.rentPointRl.setVisibility(isSpecialCategory ? View.VISIBLE : View.GONE);

        // ✅ Focus handler
        setFocusHandler(binding.loadLocationTV, false);
        setFocusHandler(binding.unloadLocationTV, true);
        setFocusHandler(binding.rentLocationTv, false);

        // ✅ Clear button handler
        setClearHandler(binding.loadingPointClear, binding.loadLocationTV, R.string.loading_point_address, false);
        setClearHandler(binding.unloadingPointClear, binding.unloadLocationTV, R.string.unloading_point_address, true);
        setClearHandler(binding.rentPointClear, binding.rentLocationTv, R.string.select_location, false);

        // ✅ Address search adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeNames);
        binding.searchListView.setAdapter(adapter);

        // Item select
        binding.searchListView.setOnItemClickListener((parent, view, position, id) -> {
            String address = adapter.getItem(position);
            if (address != null) {
                editOn = false;
                adapter.clear();
                String finalAddress = Replacement.removeDuplicateAddressParts(address);

                if (selectCategory) {
                    binding.rentLocationTv.setText(finalAddress);
                    binding.rentPointClear.setVisibility(View.VISIBLE);
                    gotoNextActivity("", "", finalAddress);
                } else {
                    if (selectedPoint) {
                        binding.unloadLocationTV.setText(finalAddress);
                        binding.unloadingPointClear.setVisibility(View.VISIBLE);

                        if (TextUtils.isEmpty(binding.loadLocationTV.getText())) {
                            requestFocus(binding.loadLocationTV, binding.unloadLocationTV);
                            selectedPoint = false;
                        } else {
                            gotoNextActivity(binding.loadLocationTV.getText().toString(),
                                    binding.unloadLocationTV.getText().toString(), "");
                        }
                    } else {
                        binding.loadLocationTV.setText(finalAddress);
                        binding.loadingPointClear.setVisibility(View.VISIBLE);

                        if (TextUtils.isEmpty(binding.unloadLocationTV.getText())) {
                            requestFocus(binding.unloadLocationTV, binding.loadLocationTV);
                            selectedPoint = true;
                        } else {
                            gotoNextActivity(binding.loadLocationTV.getText().toString(),
                                    binding.unloadLocationTV.getText().toString(), "");
                        }
                    }
                }
            }
        });

        // ✅ Add text watcher to all fields
        addSearchWatcher(binding.loadLocationTV);
        addSearchWatcher(binding.unloadLocationTV);
        addSearchWatcher(binding.rentLocationTv);

        // ✅ Division button
        binding.divisionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AreaLocationActivity.class);
            launchIntent(intent);
        });

        // ✅ Map button
        binding.mapBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapLocationActivity.class);
            intent.putExtra(MyUtils.subCategoryName, subCategoryName);
            launchIntent(intent);
        });

        // ✅ Address book
        binding.addressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressBookActivity.class);
            intent.putExtra("controlType", "addressList");
            intent.putExtra("isPicker", true);
            launchIntent(intent);
        });
    }

    /** ---------- Helper Methods ---------- */
    // ফিল্ড ফোকাস ম্যানেজার
    @SuppressLint("ClickableViewAccessibility")
    private void setFocusHandler(EditText field, boolean isUnload) {
        field.setOnTouchListener((v, event) -> {
            selectedPoint = isUnload;
            editOn = true;
            focusView(field);
            return false;
        });
    }

    // Clear বাটন ম্যানেজার
    private void setClearHandler(View clearBtn, EditText field, int hintRes, boolean isUnload) {
        clearBtn.setOnClickListener(v -> {
            selectedPoint = isUnload;
            editOn = true;
            field.setText("");
            field.setHint(hintRes);
            clearBtn.setVisibility(View.GONE);
            focusView(field);
        });
    }

    // TextWatcher অ্যাড করা
    private void addSearchWatcher(EditText field) {
        field.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFromDivision) {
                    isFromDivision = false;
                    return;
                }
                if (editOn) {
                    String query = s.toString().trim();
                    if (query.length() > 2) {
                        binding.loadingBar.setVisibility(View.GONE);
                        searchAutoComplete(query);
                    } else if (!TextUtils.isEmpty(query)) {
                        binding.loadingBar.setVisibility(View.VISIBLE);
                    } else {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        binding.loadingBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    // Intent launcher (rent/load/unload এক জায়গায়)
    private void launchIntent(Intent intent) {
        if (selectCategory) {
            rentLocationResultLauncher.launch(intent);
        } else {
            if (selectedPoint) unloadLocationResultLauncher.launch(intent);
            else loadLocationResultLauncher.launch(intent);
        }
    }

    // Focus shifting helper
    private void requestFocus(EditText toFocus, EditText toClearHighlight) {
        toFocus.requestFocus();
        toFocus.setBackgroundResource(R.drawable.bg_edit_text_primary);
        toClearHighlight.setBackgroundResource(R.drawable.bg_edit_text);
    }


    private void gotoNextActivity(String loadLocation, String unloadLocation, String rentLocation) {
        //Complete Address pickup Then go to next Activity
        Intent intent = new Intent(AddressActivity.this, RentFormActivity.class);
        intent.putExtra(MyUtils.categoryId,categoryId);
        intent.putExtra(MyUtils.subCategoryId,subCategoryId);
        intent.putExtra(MyUtils.subCategoryName,subCategoryName);
        intent.putExtra(MyUtils.loadLocation,loadLocation);
        intent.putExtra(MyUtils.unloadLocation,unloadLocation);
        intent.putExtra(MyUtils.rentLocation,rentLocation);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private void searchAutoComplete(String query) {
        adapter.clear();

        apiInterface = BarikoiClient.getClient();

        Call<SearchAutoCompleteResponse> call = apiInterface.autocompletePlace(MyUtils.barikoi_api_key,query, true);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SearchAutoCompleteResponse> call, @NonNull Response<SearchAutoCompleteResponse> response) {
                if (response.isSuccessful()) {
                    SearchAutoCompleteResponse autoCompleteResponse = response.body();
                    try {
                        if (autoCompleteResponse != null) {
                            List<SearchAutoCompleteResponse.Places> places = autoCompleteResponse.getPlaces();
                            //Handle the list of place here
                            for (SearchAutoCompleteResponse.Places place : places) {
                                if (!TextUtils.isEmpty(place.getAddress_bn())) {
                                    Log.d("Place", "Address BD: " + place.getAddress_bn());
                                    placeNames.add(place.getAddress_bn());
                                }
                                //placeNames.add(place.getAddress());
                                //saveAutoCompleteAddress(place);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Log.d("API", "onResponse: " + e.getMessage());
                    }
                } else {
                    Log.e("API Error", "Failed to fetch data");
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchAutoCompleteResponse> call, @NonNull Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });
    }

    private final ActivityResultLauncher<Intent> loadLocationResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Intent data = result.getData();
                            if (data != null) {
                                isFromDivision = true;  // 👈 ADD THIS LINE

                                String locationData = data.getStringExtra("result");
                                binding.loadLocationTV.setText(locationData);
                                binding.loadingPointClear.setVisibility(View.VISIBLE);

                                String getSheck = binding.unloadLocationTV.getText().toString();
                                if (TextUtils.isEmpty(getSheck)) {
                                    binding.unloadLocationTV.requestFocus();
                                    selectedPoint = true;
                                    binding.loadLocationTV.setBackgroundResource(R.drawable.bg_edit_text);
                                    binding.unloadLocationTV.setBackgroundResource(R.drawable.bg_edit_text_primary);
                                } else {
                                    //goto next activity
                                    String loadLocation = binding.loadLocationTV.getText().toString();
                                    String unloadLocation = binding.unloadLocationTV.getText().toString();
                                    gotoNextActivity(loadLocation, unloadLocation, "");
                                }
                            }
                            else {
                                binding.loadLocationTV.setHint(R.string.loading_point_address);
                            }
                        }
                    });

    private final ActivityResultLauncher<Intent> unloadLocationResultLauncher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Intent data = result.getData();
                            if (data != null) {
                                isFromDivision = true;  // 👈 ADD THIS LINE

                                String locationData = data.getStringExtra("result");
                                binding.unloadLocationTV.setText(locationData);
                                binding.unloadingPointClear.setVisibility(View.VISIBLE);

                                String getSheck = binding.loadLocationTV.getText().toString();
                                if (TextUtils.isEmpty(getSheck)) {
                                    binding.loadLocationTV.requestFocus();
                                    selectedPoint = false;
                                    binding.unloadLocationTV.setBackgroundResource(R.drawable.bg_edit_text);
                                    binding.loadLocationTV.setBackgroundResource(R.drawable.bg_edit_text_primary);
                                } else {
                                    //goto next activity
                                    String loadLocation = binding.loadLocationTV.getText().toString();
                                    String unloadLocation = binding.unloadLocationTV.getText().toString();
                                    gotoNextActivity(loadLocation, unloadLocation, "");
                                }
                            } else {
                                binding.unloadLocationTV.setHint(R.string.unloading_point_address);
                            }
                        }
                    });
    private final ActivityResultLauncher<Intent> rentLocationResultLauncher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            Intent data = result.getData();
                            if (data != null) {
                                String locationData = data.getStringExtra("result");
                                binding.rentLocationTv.setText(locationData);
                                binding.rentPointClear.setVisibility(View.VISIBLE);

                                //goto next activity
                                String rentLocation = binding.rentLocationTv.getText().toString();
                                gotoNextActivity("", "", rentLocation);
                            } else {
                                binding.rentLocationTv.setHint(R.string.select_location);
                            }
                        }
                    });

    private void focusView(View view) {
        view.requestFocus();
        view.setBackgroundResource(R.drawable.bg_edit_text_primary);

        binding.loadLocationTV.setBackgroundResource(view == binding.loadLocationTV ? R.drawable.bg_edit_text_primary : R.drawable.bg_edit_text);
        binding.unloadLocationTV.setBackgroundResource(view == binding.unloadLocationTV ? R.drawable.bg_edit_text_primary : R.drawable.bg_edit_text);
        binding.rentLocationTv.setBackgroundResource(view == binding.rentLocationTv ? R.drawable.bg_edit_text_primary : R.drawable.bg_edit_text);


        //showKeyboard(view);
    }

    private void emptyFocusView(View view) {
        view.requestFocus();
        view.setBackgroundResource(R.drawable.bg_edit_text_error);

        binding.loadLocationTV.setBackgroundResource(view == binding.loadLocationTV ? R.drawable.bg_edit_text_error : R.drawable.bg_edit_text);
        binding.unloadLocationTV.setBackgroundResource(view == binding.unloadLocationTV ? R.drawable.bg_edit_text_error : R.drawable.bg_edit_text);
        binding.rentLocationTv.setBackgroundResource(view == binding.rentLocationTv ? R.drawable.bg_edit_text_error : R.drawable.bg_edit_text);


        //showKeyboard(view);
    }



}