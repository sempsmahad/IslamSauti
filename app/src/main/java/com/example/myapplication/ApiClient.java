package com.example.myapplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "https://emisomo.xyz/sauti/";
    public static Retrofit retrofit = null;

    public static Retrofit getApiClient(){
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
        if (retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(new OkHttpClient().newBuilder().connectTimeout(200, TimeUnit.SECONDS).readTimeout(200, TimeUnit.SECONDS).writeTimeout(200, TimeUnit.SECONDS).build()).
                    addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;

    }

}
