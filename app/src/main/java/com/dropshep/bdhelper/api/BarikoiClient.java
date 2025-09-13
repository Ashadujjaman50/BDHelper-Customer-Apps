package com.dropshep.bdhelper.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BarikoiClient {

    public static Retrofit retrofit = null;

    public static BarikoiApiService getClient(){

        if (retrofit == null){

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).build();

            Gson gson = new GsonBuilder().create();

            //Retrofit
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://barikoi.xyz/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }

        return retrofit.create(BarikoiApiService.class);

    }

}
