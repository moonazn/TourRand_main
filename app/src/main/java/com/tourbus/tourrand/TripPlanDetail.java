package com.tourbus.tourrand;
import android.os.Parcel;
import android.os.Parcelable;

public class TripPlanDetail implements Parcelable {
    private int day;
    private double latitude, longitude;
    private String location, address;
    public TripPlanDetail(int day, String location, String address, double latitude, double longitude){
        this.day = day;
        this.location = location;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getDay() {return day;}
    public String getLocation() {return location;}
    public String getAddress() {return address;}
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}
    protected TripPlanDetail(Parcel in) {
        day = in.readInt();
        location = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }
    public static final Creator<TripPlanDetail> CREATOR = new Creator<TripPlanDetail>() {
        @Override
        public TripPlanDetail createFromParcel(Parcel in) {
            return new TripPlanDetail(in);
        }

        @Override
        public TripPlanDetail[] newArray(int size) {
            return new TripPlanDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(day);
        parcel.writeString(location);
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

}
