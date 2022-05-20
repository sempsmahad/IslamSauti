package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class ServerResponse(
    @SerializedName("audios")
    var audios: List<RealAudio?>? = null
)