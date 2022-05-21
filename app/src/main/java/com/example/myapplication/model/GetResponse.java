package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResponse {
    @SerializedName("audios")
    List<RealAudio> audios;


}
