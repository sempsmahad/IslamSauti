package com.example.myapplication.data

import com.example.myapplication.data.model.GetResponse
import com.example.myapplication.data.model.Summon
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SautiService {
    @Multipart
    @POST("upload.php")
    fun uploadAudio(
        @Part("name") name: RequestBody?,
        @Part("date") date: RequestBody?,
        @Part("topic") topic: RequestBody?,
        @Part audio: MultipartBody.Part?
    ): Call<Summon?>?

    //    Call<Audio> uploadAudio(@Part("name") RequestBody name, @Part("date") RequestBody date, @Part("topic") RequestBody topic, @Part("audio\"; filename=\"audio.mp3\"") RequestBody audio);
    @GET("read.php")
    fun readAudioList(): Call<GetResponse?>?
}