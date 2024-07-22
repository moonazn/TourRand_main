package com.tourbus.tourrand;

public class Place {
    private String placeName;
    private String addressName;

    public Place(String placeName, String addressName) {
        this.placeName = placeName;
        this.addressName = addressName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return addressName;
    }
}
