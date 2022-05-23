package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class Summon {

    @SerializedName("title")
    String title;

    @SerializedName("_id")
    String server_id;

    @SerializedName("topic")
    String topic;

    @SerializedName("name")
    String name;

    @SerializedName("description")
    String description;

    @SerializedName("creation_date")
    String creation_date;

    @SerializedName("audio")
    String audio;

    @SerializedName("createdAt")
    String created_at;

    @SerializedName("updatedAt")
    String updated_att;

    public Summon() {
    }

    public Summon(String title, String server_id, String topic, String name, String description, String creation_date, String audio) {
        this.title         = title;
        this.server_id     = server_id;
        this.topic         = topic;
        this.name          = name;
        this.description   = description;
        this.creation_date = creation_date;
        this.audio         = audio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_att() {
        return updated_att;
    }

    public void setUpdated_att(String updated_att) {
        this.updated_att = updated_att;
    }
}
