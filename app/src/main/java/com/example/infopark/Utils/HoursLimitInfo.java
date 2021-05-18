package com.example.infopark.Utils;

import java.io.Serializable;

public class HoursLimitInfo implements Serializable {
    private final String maxHours;

    public String getMaxHours() {
        return maxHours;
    }

    public HoursLimitInfo(String maxHours) {
        this.maxHours = maxHours;
    }
}
