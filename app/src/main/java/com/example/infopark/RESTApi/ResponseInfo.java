package com.example.infopark.RESTApi;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * This class holds the information retrieved from the server in the getInfo via retrofit RESTApi.
 */
public class ResponseInfo extends ResponseMessage implements Parcelable {

    private final String fromSunThu;
    private final String toSunThu;
    private final String fromFri;
    private final String toFri;
    private final String maxHours;


    /**
     * Constructor for ResponseInfo instance.
     * @param in
     */
    protected ResponseInfo(Parcel in) {
        fromSunThu = in.readString();
        toSunThu = in.readString();
        fromFri = in.readString();
        toFri = in.readString();
        maxHours = in.readString();

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

    }
}
