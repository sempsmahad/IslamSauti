package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    indices = [
        Index("id")],
    primaryKeys = ["name"]
)
data class Summon(
    val id: Int,

    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("topic")
    val topic: String? = null,

    @field:SerializedName("response")
    val response: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("path")
    val path: String? = null,

    var error: Boolean = false

)