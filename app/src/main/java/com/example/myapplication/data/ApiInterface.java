package com.example.myapplication.data;

import com.example.myapplication.data.model.Summon;
import com.example.myapplication.data.model.GetResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("upload.php")
    Call<Summon> uploadAudio(@Part("name") RequestBody name, @Part("date") RequestBody date, @Part("topic") RequestBody topic, @Part MultipartBody.Part audio);
//    Call<Audio> uploadAudio(@Part("name") RequestBody name, @Part("date") RequestBody date, @Part("topic") RequestBody topic, @Part("audio\"; filename=\"audio.mp3\"") RequestBody audio);

    @GET("read.php")
    Call<GetResponse> readAudioList();
}
