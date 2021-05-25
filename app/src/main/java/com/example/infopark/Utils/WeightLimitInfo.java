package com.example.infopark.Utils;

import java.io.Serializable;

public class WeightLimitInfo implements Serializable {
    private final String maxWeight;

    public String getMaxWeight() {
        return maxWeight;
    }

    public WeightLimitInfo(String maxWeight) {
        this.maxWeight = maxWeight;
    }
}
