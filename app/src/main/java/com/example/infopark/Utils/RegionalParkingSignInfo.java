package com.example.infopark.Utils;

import java.io.Serializable;

public class RegionalParkingSignInfo implements Serializable {
    private final String fromRegionalSign;
    private final String toRegionalSign;
    private final String parkingSignNumber;

    public RegionalParkingSignInfo(String fromRegionalSign, String toRegionalSign, String parkingSignNumber) {
        this.fromRegionalSign = fromRegionalSign;
        this.toRegionalSign = toRegionalSign;
        this.parkingSignNumber = parkingSignNumber;
    }

    public String getFromRegionalSign() {
        return fromRegionalSign;
    }

    public String getToRegionalSign() {
        return toRegionalSign;
    }

    public String getParkingSignNumber() {
        return parkingSignNumber;
    }
}
