package com.example.myapplication.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResponse {
    @SerializedName("audios")
    List<RealAudio> audios;

    public List<RealAudio> getAudios() {
        return audios;
    }

    public void setAudios(List<RealAudio> audios) {
        this.audios = audios;
    }
}
