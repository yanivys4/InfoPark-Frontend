package com.example.infopark.Utils;

import java.io.Serializable;

public class RegularSignInfo implements Serializable {
    private final String fromSunThu;
    private final String toSunThu;
    private final String fromFri;
    private final String toFri;

    public RegularSignInfo(String fromSunThu, String toSunThu, String fromFri, String toFri) {
        this.fromSunThu = fromSunThu;
        this.toSunThu = toSunThu;
        this.fromFri = fromFri;
        this.toFri = toFri;
    }

    public String getFromSunThu() {
        return fromSunThu;
    }

    public String getToSunThu() {
        return toSunThu;
    }

    public String getFromFri() {
        return fromFri;
    }

    public String getToFri() {
        return toFri;
    }
}
