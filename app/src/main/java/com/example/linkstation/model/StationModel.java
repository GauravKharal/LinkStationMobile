package com.example.linkstation.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class StationModel implements Serializable {
    private String statusCode, message;
    private boolean success;

    @SerializedName("data")
    private Data data;

    // Getters and Setters
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccessful() {
        return success;
    }

    public void setSuccessful(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data implements Serializable {
        @SerializedName("stations")
        private List<Station> stations;

        @SerializedName("station")
        private Station station;


        @SerializedName("links")
        private List<Link> links;

        // Instead of Uri, use File for image paths
        @SerializedName("linkImages")
        private List<File> linkImages;

        public Station getStation() {
            return station;
        }

        public void setStation(Station station) {
            this.station = station;
        }

        // Getters and Setters
        public List<Station> getStations() {
            return stations;
        }

        public void setStations(List<Station> stations) {
            this.stations = stations;
        }

        public List<Link> getLinks() {
            return links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }

        public List<File> getLinkImages() {
            return linkImages;
        }

        public void setLinkImages(List<File> linkImages) {
            this.linkImages = linkImages;
        }

        public static class Station implements Serializable {
            @SerializedName("url")
            private String stationUrl;

            @SerializedName("title")
            private String stationTitle;

            @SerializedName("description")
            private String stationDescription;

            // Use File instead of Uri
            @SerializedName("stationImage")
            private File stationImage;

            @SerializedName("image")
            private String image;

            @SerializedName("instagram")
            private String instagram;

            @SerializedName("twitter")
            private String twitter;

            @SerializedName("facebook")
            private String facebook;

            @SerializedName("youtube")
            private String youtube;

            @SerializedName("visibility")
            private boolean visibility;

            @SerializedName("views")
            private int views;

            @SerializedName("clicks")
            private int clicks;

            @SerializedName("shares")
            private int shares;

            // Constructor
            public Station(String stationUrl, String stationTitle, String stationDescription, File stationImage, String image, boolean visibility, String instagram, String twitter, String facebook, String youtube) {
                this.stationUrl = stationUrl;
                this.stationTitle = stationTitle;
                this.stationDescription = stationDescription;
                this.stationImage = stationImage;
                this.image = image;
                this.visibility = visibility;
                this.instagram = instagram;
                this.twitter = twitter;
                this.facebook = facebook;
                this.youtube = youtube;
            }

            // Getters and Setters
            public String getStationUrl() {
                return stationUrl;
            }

            public void setStationUrl(String stationUrl) {
                this.stationUrl = stationUrl;
            }

            public String getStationTitle() {
                return stationTitle;
            }

            public void setStationTitle(String stationTitle) {
                this.stationTitle = stationTitle;
            }

            public String getStationDescription() {
                return stationDescription;
            }

            public void setStationDescription(String stationDescription) {
                this.stationDescription = stationDescription;
            }

            public File getStationImage() {
                return stationImage;
            }

            public void setStationImage(File stationImage) {
                this.stationImage = stationImage;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public boolean isVisibility() {
                return visibility;
            }

            public void setVisibility(boolean visibility) {
                this.visibility = visibility;
            }

            public String getInstagram() {
                return instagram;
            }

            public void setInstagram(String instagram) {
                this.instagram = instagram;
            }

            public String getTwitter() {
                return twitter;
            }

            public void setTwitter(String twitter) {
                this.twitter = twitter;
            }

            public String getFacebook() {
                return facebook;
            }

            public void setFacebook(String facebook) {
                this.facebook = facebook;
            }

            public String getYoutube() {
                return youtube;
            }

            public void setYoutube(String youtube) {
                this.youtube = youtube;
            }

            public int getViews() {
                return views;
            }

            public void setViews(int views) {
                this.views = views;
            }

            public void setClicks(int clicks) {
                this.clicks = clicks;
            }

            public int getClicks() {
                return clicks;
            }

            public int getShares() {
                return shares;
            }

            public void setShares(int shares) {
                this.shares = shares;
            }

        }

        public static class Link implements Serializable {
            @SerializedName("url")
            private String url;

            @SerializedName("title")
            private String title;

            // Use File instead of Uri
            @SerializedName("linkImage")
            private File linkImage;

            @SerializedName("position")
            private int position;

            public Link(String url, String title, File linkImage, int position) {
                this.url = url;
                this.title = title;
                this.linkImage = linkImage;
                this.position = position;
            }

            // Getters and Setters
            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public File getLinkImage() {
                return linkImage;
            }

            public void setLinkImage(File linkImage) {
                this.linkImage = linkImage;
            }

            public int getPosition() {
                return position;
            }

            public void setPosition(int position) {
                this.position = position;
            }
        }
    }
}
