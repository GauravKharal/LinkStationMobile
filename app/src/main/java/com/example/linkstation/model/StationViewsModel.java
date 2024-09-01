package com.example.linkstation.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class StationViewsModel implements Serializable {
    private String statusCode, message;
    private boolean success;

    @SerializedName("data")
    private List<StationViewsModel.Data> data; // Change from a single Data object to a List of Data objects

    // Getters and Setters
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<StationViewsModel.Data> getData() {
        return data;
    }

    public void setData(List<StationViewsModel.Data> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Data implements Serializable {
        @SerializedName("totalViews")
        private int totalViews;

        @SerializedName("station")
        private StationModel.Data.Station station;

        public StationModel.Data.Station getStation() {
            return station;
        }
        public void setStation(StationModel.Data.Station station) {
            this.station = station;
        }

        public int getTotalViews() {
            return totalViews;
        }

        public void setTotalViews(int totalViews) {
            this.totalViews = totalViews;
        }
    }
}
