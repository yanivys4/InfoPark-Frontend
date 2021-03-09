package com.example.infopark.RESTApi;

/**
 * This class contains saved location response that got from the server in
 * getSavedLocation vid retrofit.
 */
public class SavedLocation {

    private LatitudeLongitude responseSavedLocation;

    public LatitudeLongitude getSavedLocation() {
        return responseSavedLocation;
    }

}
