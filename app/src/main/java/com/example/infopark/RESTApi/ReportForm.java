package com.example.infopark.RESTApi;

import com.example.infopark.Utils.HoursLimitInfo;
import com.example.infopark.Utils.RegionalParkingSignInfo;
import com.example.infopark.Utils.RegularSignInfo;
import com.example.infopark.Utils.UnloadingChargingInfo;
import com.example.infopark.Utils.WeightLimitInfo;

/**
 * This class holds the report data used to send as a request to the server via retrofit RESTApi.
 */
public class ReportForm {
    private final String uniqueID;
    private final RegularSignInfo regularSignInfo;
    private final HoursLimitInfo hoursLimitInfo;
    private final RegionalParkingSignInfo regionalParkingSignInfo;
    private final UnloadingChargingInfo unloadingChargingInfo;
    private final WeightLimitInfo weightLimitInfo;

    /**
     *
     * @param uniqueID
     * @param regularSignInfo
     * @param hoursLimitInfo
     * @param regionalParkingSignInfo
     * @param unloadingChargingInfo
     * @param weightLimitInfo
     */
    public ReportForm(String uniqueID, RegularSignInfo regularSignInfo, HoursLimitInfo hoursLimitInfo,
                      RegionalParkingSignInfo regionalParkingSignInfo, UnloadingChargingInfo unloadingChargingInfo,
                      WeightLimitInfo weightLimitInfo) {
        this.uniqueID = uniqueID;
        this.regularSignInfo = regularSignInfo;
        this.hoursLimitInfo = hoursLimitInfo;
        this. regionalParkingSignInfo = regionalParkingSignInfo;
        this.unloadingChargingInfo = unloadingChargingInfo;
        this.weightLimitInfo = weightLimitInfo;
    }
}
