package com.example.myapplication;

import java.util.ArrayList;

public class VerticalListmodel {

    ArrayList<RealAudio> audios;
    String name;
    String topic;
    String date;

    public VerticalListmodel(ArrayList<RealAudio> audios, String name, String topic, String date) {
        this.audios = audios;
        this.name = name;
        this.topic = topic;
        this.date = date;
    }

    public ArrayList<RealAudio> getAudios() {
        return audios;
    }

    public void setAudios(ArrayList<RealAudio> audios) {
        this.audios = audios;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
