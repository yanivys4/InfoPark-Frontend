package com.example.infopark.RESTApi;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponseInfo implements Parcelable {

    private final String fromSunThu;
    private final String toSunThu;
    private final String fromFri;
    private final String toFri;
    private final String maxHours;
    private final boolean success;

    protected ResponseInfo(Parcel in) {
        fromSunThu = in.readString();
        toSunThu = in.readString();
        fromFri = in.readString();
        toFri = in.readString();
        maxHours = in.readString();
        success = in.readByte() != 0;
    }

    public static final Creator<ResponseInfo> CREATOR = new Creator<ResponseInfo>() {
        @Override
        public ResponseInfo createFromParcel(Parcel in) {
            return new ResponseInfo(in);
        }

        @Override
        public ResponseInfo[] newArray(int size) {
            return new ResponseInfo[size];
        }
    };

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

    public String getMaxHours(){
        return maxHours;
    }

    public boolean getSuccess() {
        return success;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromSunThu);
        dest.writeString(toSunThu);
        dest.writeString(fromFri);
        dest.writeString(toFri);
        dest.writeString(maxHours);
        dest.writeByte((byte) (success ? 1 : 0));
    }
}
