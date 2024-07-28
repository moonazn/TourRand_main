package com.tourbus.tourrand;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Place implements Parcelable {
    private String placeName;
    private String addressName;

    public Place(String placeName, String addressName) {
        this.placeName = placeName;
        this.addressName = addressName;
    }

    protected Place(Parcel in) {
        placeName = in.readString();
        addressName = in.readString();
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

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return addressName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(placeName);
        dest.writeString(addressName);
    }
}
