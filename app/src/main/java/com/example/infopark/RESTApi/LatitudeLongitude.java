package com.example.infopark.RESTApi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class holds the simplest representation of geographical location - Latitude and Longitude.
 * (the LatLng and Location are more complex and the purpose of this class is to save the location
 * in a simple way that will fit also the backend.)
 */
public class LatitudeLongitude  {

    private double latitude;
    private double longitude;

    /**
     * This function constructs a new instance of LatitudeLongitude.
     * @param latitude the latitude
     * @param longitude the longitude
     */
    public LatitudeLongitude(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    //============================================================================================

    /**
     * Getter method for latitude.
     * @return the latitude value.
     */
    public double getLatitude() {
        return latitude;
    }
    //============================================================================================

    /**
     * Getter method for longitude.
     * @return the longitude value.
     */
    public double getLongitude() {
        return longitude;
    }
    //============================================================================================

    /**
     * Setter method for latitude and longitude.
     * @param latitude the latitude to set.
     * @param longitude the longitude to set.
     */
    public void setLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //============================================================================================
}
