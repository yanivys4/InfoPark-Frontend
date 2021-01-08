package com.example.infopark.RESTApi;

public class LatLng {

    private final float latitude;
    private final float longitude;

    public LatLng(float latitude, float longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
