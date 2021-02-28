package com.example.infopark.RESTApi;

/**
 * This class holds the report data used to send as a request to the server via retrofit RESTApi.
 */
public class ReportForm {
    private final String uniqueID;

    private final String fromSunThu;

    private final String toSunThu;

    private final String fromFri;

    private final String toFri;

    private final String maxHours;

    /**
     * This function constructs a new report form.
     * @param uniqueID the user unique id
     * @param fromSunThu indicate the FROM time for SUN-THU days
     * @param toSunThu indicate the TO time for SUN-THU days
     * @param fromFri indicate the FROM time for FRI day
     * @param toFri indicate the TO time for FRI day
     * @param maxHours indicate the maximum parking hours
     */
    public ReportForm(String uniqueID, String fromSunThu, String toSunThu, String fromFri, String toFri, String maxHours) {
        this.uniqueID = uniqueID;
        this.fromSunThu = fromSunThu;
        this.toSunThu = toSunThu;
        this.fromFri = fromFri;
        this.toFri = toFri;
        this.maxHours = maxHours;
    }
}
