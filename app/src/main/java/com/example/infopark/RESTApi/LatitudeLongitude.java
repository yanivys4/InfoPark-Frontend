package com.example.infopark.RESTApi;

public class LatitudeLongitude {

    private double latitude;
    private double longitude;

    public LatitudeLongitude(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
