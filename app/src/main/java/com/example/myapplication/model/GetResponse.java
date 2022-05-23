package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetResponse {
    @SerializedName("summons")
    ArrayList<Summon> summons;

    public ArrayList<Summon> getSummons() {
        return summons;
    }

    public void setSummons(ArrayList<Summon> summons) {
        this.summons = summons;
    }
}
