package com.example.infopark.RESTApi;

public class RequestSavedLocation {
    private String email;
    private LatitudeLongitude savedLocation;

    public RequestSavedLocation(String email, LatitudeLongitude savedLocation) {
        this.email = email;
        this.savedLocation = savedLocation;
    }
}
