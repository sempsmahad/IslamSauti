package com.example.myapplication.model;

public class RealAudio {

    String name;
    String date;
    String topic;
    String url;
    int    id;


    public RealAudio(RealAudio ad) {
        this.name  = ad.getName();
        this.date  = ad.getDate();
        this.topic = ad.getTopic();
        this.url   = ad.getUrl();
        this.id    = ad.getId();
    }

    public RealAudio() {
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
