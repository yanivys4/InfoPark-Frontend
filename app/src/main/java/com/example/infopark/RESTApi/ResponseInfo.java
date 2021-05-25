package com.example.infopark.RESTApi;

import com.example.infopark.Utils.HoursLimitInfo;
import com.example.infopark.Utils.RegionalParkingSignInfo;
import com.example.infopark.Utils.RegularSignInfo;
import com.example.infopark.Utils.UnloadingChargingInfo;
import com.example.infopark.Utils.WeightLimitInfo;

import java.io.Serializable;


/**
 * This class holds the information retrieved from the server in the getInfo via retrofit RESTApi.
 */
public class ResponseInfo extends ResponseMessage implements Serializable {

    private final RegularSignInfo regularSignInfo;
    private final HoursLimitInfo hoursLimitInfo;
    private final RegionalParkingSignInfo regionalParkingSignInfo;
    private final UnloadingChargingInfo unloadingChargingInfo;
    private final WeightLimitInfo weightLimitInfo;

    public ResponseInfo(RegularSignInfo regularSignInfo, HoursLimitInfo hoursLimitInfo, RegionalParkingSignInfo regionalParkingSignInfo, UnloadingChargingInfo unloadingChargingInfo, WeightLimitInfo weightLimitInfo) {
        this.regularSignInfo = regularSignInfo;
        this.hoursLimitInfo = hoursLimitInfo;
        this.regionalParkingSignInfo = regionalParkingSignInfo;
        this.unloadingChargingInfo = unloadingChargingInfo;
        this.weightLimitInfo = weightLimitInfo;
    }


    public RegularSignInfo getRegularSignInfo() {
        return regularSignInfo;
    }

    public HoursLimitInfo getHoursLimitInfo() {
        return hoursLimitInfo;
    }

    public RegionalParkingSignInfo getRegionalParkingSignInfo() {
        return regionalParkingSignInfo;
    }

    public UnloadingChargingInfo getUnloadingChargingInfo() {
        return unloadingChargingInfo;
    }

    public WeightLimitInfo getWeightLimitInfo() {
        return weightLimitInfo;
    }
}
