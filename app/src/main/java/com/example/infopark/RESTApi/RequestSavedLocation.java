package com.example.infopark.RESTApi;

public class RequestSavedLocation {
    private String uniqueID;
    private LatitudeLongitude requestedSavedLocation;

    public RequestSavedLocation(String uniqueID, LatitudeLongitude requestedSavedLocation) {
        this.uniqueID = uniqueID;
        this.requestedSavedLocation = requestedSavedLocation;
    }
}
