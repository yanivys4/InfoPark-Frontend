package com.example.infopark.RESTApi;

public class ReportForm {
    private final String uniqueID;

    private final String fromA;

    private final String toA;

    private final String fromB;

    private final String toB;

    private final String maxHours;

    public ReportForm(String uniqueID, String fromA, String toA, String fromB, String toB, String maxHours) {
        this.uniqueID = uniqueID;
        this.fromA = fromA;
        this.toA = toA;
        this.fromB = fromB;
        this.toB = toB;
        this.maxHours = maxHours;
    }
}
