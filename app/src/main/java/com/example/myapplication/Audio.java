package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class Audio {
    @SerializedName("name")
    private String name;

    @SerializedName("date")
    private String date;

    @SerializedName("topic")
    private String topic;

    @SerializedName("response")
    private String response;

    public boolean error;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

