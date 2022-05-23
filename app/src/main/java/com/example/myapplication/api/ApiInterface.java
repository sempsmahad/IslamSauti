package com.example.myapplication.api;

import com.example.myapplication.model.Audio;
import com.example.myapplication.model.GetResponse;
import com.example.myapplication.model.Summon;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("summons/uploads")
    Call<Summon> uploadAudio(
            @Part("title") RequestBody title,
            @Part("topic") RequestBody topic,
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("creation_date") RequestBody date,
            @Part MultipartBody.Part audio);

    @GET("summons")
    Call<GetResponse> readAudioList();
}
