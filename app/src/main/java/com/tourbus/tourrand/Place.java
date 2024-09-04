package com.tourbus.tourrand;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Place implements Parcelable {
    private String placeName;
    private String addressName;
    private double latitude;
    private double longitude;
    private int day;

    public Place(String placeName, String addressName, double latitude, double longitude) {
        this.placeName = placeName;
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Place(String placeName, String addressName, double latitude, double longitude, int day) {
        this.placeName = placeName;
        this.addressName = addressName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.day = day;
    }

    public Place(String placeName, String addressName) {
        this.placeName = placeName;
        this.addressName = addressName;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    protected Place(Parcel in) {
        placeName = in.readString();
        addressName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return addressName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(placeName);
        dest.writeString(addressName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
