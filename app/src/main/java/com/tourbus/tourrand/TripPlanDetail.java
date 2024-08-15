package com.tourbus.tourrand;

public class TripPlanDetail {
    private int day, latitude, longitude;
    private String location, address;
    public TripPlanDetail(int day, String location, String address, int latitude, int longitude){
        this.day = day;
        this.location = location;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getDay() {return day;}
    public String getLocation() {return location;}
    public String getAddress() {return address;}
    public int getLatitude() {return latitude;}
    public int getLongitude() {return longitude;}

}
