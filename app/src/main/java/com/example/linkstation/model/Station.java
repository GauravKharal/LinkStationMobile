package com.example.linkstation.model;

import java.io.Serializable;

public class Station implements Serializable {
    private final String stationURL;
    private final String stationTitle;
    private String stationDescription;

    public Station(String stationURL, String stationTitle) {
        this.stationURL = stationURL;
        this.stationTitle = stationTitle;
    }

    public String getStationURL() {
        return stationURL;
    }

    public String getStationTitle() {
        return stationTitle;
    }
}
