package com.example.infopark.Utils;

import java.io.Serializable;

public class UnloadingChargingInfo implements Serializable {
    private final String fromUnloadingCharging;
    private final String toUnloadingCharging;

    public UnloadingChargingInfo(String fromUnloadingCharging, String toUnloadingCharging) {
        this.fromUnloadingCharging = fromUnloadingCharging;
        this.toUnloadingCharging = toUnloadingCharging;
    }

    public String getFromUnloadingCharging() {
        return fromUnloadingCharging;
    }

    public String getToUnloadingCharging() {
        return toUnloadingCharging;
    }
}
