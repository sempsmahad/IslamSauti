package com.example.myapplication.data

import com.example.myapplication.data.model.ServerResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SautiService {
//    @Multipart
//    @POST("upload.php")
//    fun uploadAudio(
//        @Part("name") name: RequestBody?,
//        @Part("date") date: RequestBody?,
//        @Part("topic") topic: RequestBody?,
//        @Part audio: MultipartBody.Part?
//    ): Call<Summon?>?

    @Multipart
    @POST("upload.php")
    fun createSummon(
        @Part("name") name: RequestBody?,
        @Part("date") date: RequestBody?,
        @Part("topic") topic: RequestBody?,
        @Part audio: MultipartBody.Part?
    ): Call<ServerResponse>

//    @GET("read.php")
//    fun readAudioList(): Call<GetResponse?>?

    @GET("read.php")
    fun getSummons(): Call<ServerResponse?>?

//    @POST("sessions")
//    fun createSession(@Body summon: Summon?): Call<ServerResponse>
}