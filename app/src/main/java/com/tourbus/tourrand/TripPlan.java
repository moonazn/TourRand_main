package com.tourbus.tourrand;

import java.io.Serializable;
import java.util.List;

public class TripPlan implements Serializable {

    private String tripName;
    private String travelDate;
    private String dDay;
    private int tourId;
    private List<String> memberImages; // 멤버 이미지들의 리소스 ID 리스트

    public TripPlan(String tripName, String travelDate, String dDay, int tourId, List<String> memberImages) {
        this.tripName = tripName;
        this.travelDate = travelDate;
        this.dDay = dDay;
        this.tourId = tourId;
        this.memberImages = memberImages;
    }

    public TripPlan(String tripName, String travelDate, String dDay, int tourId) {
        this.tripName = tripName;
        this.travelDate = travelDate;
        this.dDay = dDay;
        this.tourId = tourId;
        // 멤버 리스트가 없는 경우, 디폴트 이미지 ID를 추가
        this.memberImages = null; // 또는 빈 리스트로 초기화해도 됨
    }

    public int getTourId() {
        return tourId;
    }

    public String getTripName() {
        return tripName;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public String getDDay() {
        return dDay;
    }

    public List<String> getMemberImages() {
        return memberImages;
    }
}
