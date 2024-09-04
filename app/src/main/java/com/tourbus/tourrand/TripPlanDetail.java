package com.tourbus.tourrand;
import android.os.Parcel;
import android.os.Parcelable;

public class TripPlanDetail implements Parcelable {

    private String tripName;
    private String departure;
    private int day;
    private String planDate;
    private double latitude, longitude;
    private String location, address;
    public TripPlanDetail(String tripName, String departure, int day, String planDate, String location, String address, double latitude, double longitude){
        this.tripName = tripName;
        this.departure = departure;
        this.day = day;
        this.planDate = planDate;
        this.location = location;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripName() {return tripName;}

    public String getDeparture() {
        return departure;
    }
    public int getDay() {return day;}
    public String getPlanDate() {return planDate;}
    public String getLocation() {return location;}
    public String getAddress() {return address;}
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}
    protected TripPlanDetail(Parcel in) {
        tripName = in.readString();
        departure = in.readString();
        day = in.readInt();
        planDate = in.readString();
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
        parcel.writeString(tripName);
        parcel.writeString(departure);
        parcel.writeInt(day);
        parcel.writeString(planDate);
        parcel.writeString(location);
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

}
