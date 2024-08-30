package com.example.linkstation.model;

import java.util.ArrayList;
import java.util.List;

public class CreateStationRequest {
    private String stationUrl;
    private String stationTitle;
    private String stationDescription;
    private String instagram;
    private String facebook;
    private String twitter;
    private String youtube;
    private boolean visibility;
    private Links links;

    // Getters and Setters for stationUrl
    public String getStationUrl() {
        return stationUrl;
    }

    public void setStationUrl(String stationUrl) {
        this.stationUrl = stationUrl;
    }

    // Getters and Setters for stationTitle
    public String getStationTitle() {
        return stationTitle;
    }

    public void setStationTitle(String stationTitle) {
        this.stationTitle = stationTitle;
    }

    // Getters and Setters for stationDescription
    public String getStationDescription() {
        return stationDescription;
    }

    public void setStationDescription(String stationDescription) {
        this.stationDescription = stationDescription;
    }

    // Getters and Setters for instagram
    public String getInstagram() {
        return instagram;
    }
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    // Getters and setters for facebook
    public String getFacebook(){
        return facebook;
    }
    public void setFacebook(String facebook){
        this.facebook = facebook;
    }

    // Getters and Setters for twitter
    public String getTwitter() {
        return twitter;
    }
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    // Getters and Setters for youtube
    public String getYoutube() {
        return youtube;
    }
    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    // Getters and Setters for visibility
    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    // Getters and Setters for links
    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    // Inner Links class with getters and setters
    public static class Links {
        private List<String> url = new ArrayList<>();
        private List<String> title = new ArrayList<>();
        private List<String> position = new ArrayList<>();

        // Getters and Setters for url
        public List<String> getUrl() {
            return url;
        }

        public void setUrl(List<String> url) {
            this.url = url;
        }

        // Getters and Setters for title
        public List<String> getTitle() {
            return title;
        }

        public void setTitle(List<String> title) {
            this.title = title;
        }

        // Getters and Setters for position
        public List<String> getPosition() {
            return position;
        }

        public void setPosition(List<String> position) {
            this.position = position;
        }
    }
}


