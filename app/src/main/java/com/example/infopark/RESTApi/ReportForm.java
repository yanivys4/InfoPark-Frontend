package com.example.infopark.RESTApi;

/**
 * This class holds the report data used to send as a request to the server via retrofit RESTApi.
 */
public class ReportForm {
    private final String uniqueID;

    private final String fromA;

    private final String toA;

    private final String fromB;

    private final String toB;

    private final String maxHours;

    /**
     * This function constructs a new report form.
     * @param uniqueID the user unique id
     * @param fromA indicate the FROM time for SUN-THU days
     * @param toA indicate the TO time for SUN-THU days
     * @param fromB indicate the FROM time for FRI day
     * @param toB indicate the TO time for FRI day
     * @param maxHours indicate the maximum parking hours
     */
    public ReportForm(String uniqueID, String fromA, String toA, String fromB, String toB, String maxHours) {
        this.uniqueID = uniqueID;
        this.fromA = fromA;
        this.toA = toA;
        this.fromB = fromB;
        this.toB = toB;
        this.maxHours = maxHours;
    }
}
