package com.tourbus.tourrand;

public class LocalSearchDocument {
    private String placeName;
    private String addressName;

    public LocalSearchDocument(String placeName, String addressName) {
        this.placeName = placeName;
        this.addressName = addressName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddressName() {
        return addressName;
    }
}
