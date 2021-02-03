package com.example.infopark.RESTApi;

public class ReportForm {
    private final String user;

    private final String fromA;

    private final String toA;

    private final String fromB;

    private final String tob;

    private final String maxHours;

    private final LatitudeLongitude latitudeLongitude;

    public ReportForm(String user, String fromA, String toA, String fromB, String toB, String maxHours, LatitudeLongitude latitudeLongitude) {
        this.user = user;
        this.fromA = fromA;
        this.toA = toA;
        this.fromB = fromB;
        this.tob = toB;
        this.maxHours = maxHours;
        this.latitudeLongitude = latitudeLongitude;
    }
}
