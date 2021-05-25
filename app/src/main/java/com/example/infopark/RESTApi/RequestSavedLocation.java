package com.example.infopark.RESTApi;

/**
 * This class holds the data used to send in a request to the server related to
 * saved location(get and set) via retrofit RESTApi.
 */
public class RequestSavedLocation {
    private String uniqueID;
    private LatitudeLongitude requestedSavedLocation;

    /**
     * Constructor or RequestSavedLocation instance
     * @param uniqueID the unique id of the user
     * @param requestedSavedLocation the location used for the set saved location action.
     */
    public RequestSavedLocation(String uniqueID, LatitudeLongitude requestedSavedLocation) {
        this.uniqueID = uniqueID;
        this.requestedSavedLocation = requestedSavedLocation;
    }
}
