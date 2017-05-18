package com.fypj.icreative.model;

/**
 * Created by User on 6/20/2016.
 */
public class SpeedCamLocationModel {
    private String description;
    private String lat;
    private String lon;

    public SpeedCamLocationModel() {
    }

    public SpeedCamLocationModel(String description, String lat, String lon) {
        this.description = description;
        this.lat = lat;
        this.lon = lon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
