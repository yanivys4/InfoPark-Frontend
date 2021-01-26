package com.example.infopark.RESTApi;

public class SavedLocation {

    private LatitudeLongitude savedLocation;

    public LatitudeLongitude getSavedLocation() {
        return savedLocation;
    }

    public SavedLocation(LatitudeLongitude savedLocation) {
        this.savedLocation = savedLocation;
    }
}
