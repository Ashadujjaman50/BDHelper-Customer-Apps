package com.dropshep.bdhelper.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BarikoiApiService {

    @GET("v2/api/search/reverse/geocode")
    Call<BarikoiResponse> getPlaceInfo(
            @Query("api_key") String apiKey,
            @Query("longitude") double longitude,
            @Query("latitude") double latitude,
            @Query("district") boolean district,
            @Query("post_code") boolean postCode,
            @Query("country") boolean country,
            @Query("sub_district") boolean subDistrict,
            @Query("union") boolean union,
            @Query("pauroshova") boolean pauroshova,
            @Query("location_type") boolean locationType,
            @Query("division") boolean division,
            @Query("address") boolean address,
            @Query("area") boolean area,
            @Query("bangla") boolean bangla
    );

    @GET("v2/api/search/autocomplete/place")
    Call<SearchAutoCompleteResponse> autocompletePlace(
            @Query("api_key") String apiKey,
            @Query("q") String query,
            @Query("bangla") boolean bangla
    );

}
