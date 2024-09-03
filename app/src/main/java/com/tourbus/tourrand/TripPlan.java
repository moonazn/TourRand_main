package com.tourbus.tourrand;

import java.io.Serializable;
import java.util.List;

public class TripPlan implements Serializable {

    private String tripName;
    private String travelDate;
    private String dDay;
    private List<Integer> memberImages; // 멤버 이미지들의 리소스 ID 리스트

    public TripPlan(String tripName, String travelDate, String dDay, List<Integer> memberImages) {
        this.tripName = tripName;
        this.travelDate = travelDate;
        this.dDay = dDay;
        this.memberImages = memberImages;
    }

    public TripPlan(String tripName, String travelDate, String dDay) {
        this.tripName = tripName;
        this.travelDate = travelDate;
        this.dDay = dDay;
        // 멤버 리스트가 없는 경우, 디폴트 이미지 ID를 추가
        this.memberImages = null; // 또는 빈 리스트로 초기화해도 됨
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

    public List<Integer> getMemberImages() {
        return memberImages;
    }
}
