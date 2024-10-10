package com.tourbus.tourrand;

import android.os.Parcel;
import android.os.Parcelable;

public class PreTrip implements Parcelable {
    private boolean withAnimal;
    private int plandate;
    private Place departure;
    private Place destination;

    // 기본 생성자
    public PreTrip(boolean withAnimal, int plandate, Place departure, Place destination) {
        this.withAnimal = withAnimal;
        this.plandate = plandate;
        this.departure = departure;
        this.destination = destination;
    }

    // Parcel을 통해 객체를 복원하는 생성자
    protected PreTrip(Parcel in) {
        withAnimal = in.readByte() != 0; // boolean을 읽을 때는 readByte()를 사용
        plandate = in.readInt();
        departure = in.readParcelable(Location.class.getClassLoader());
        destination = in.readParcelable(Location.class.getClassLoader());
    }

    // Parcelable.Creator 구현
    public static final Creator<PreTrip> CREATOR = new Creator<PreTrip>() {
        @Override
        public PreTrip createFromParcel(Parcel in) {
            return new PreTrip(in);
        }

        @Override
        public PreTrip[] newArray(int size) {
            return new PreTrip[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeByte((byte) (withAnimal ? 1 : 0)); // boolean 값을 쓸 때는 writeByte()를 사용
        parcel.writeInt(plandate);
        parcel.writeParcelable(departure, flags);
        parcel.writeParcelable(destination, flags);
    }

    // Getter와 Setter 메소드들
    public boolean isWithAnimal() {
        return withAnimal;
    }

    public void setWithAnimal(boolean withAnimal) {
        this.withAnimal = withAnimal;
    }

    public int getPlandate() {
        return plandate;
    }

    public void setPlandate(int plandate) {
        this.plandate = plandate;
    }

    public Place getDeparture() {
        return departure;
    }

    public void setDeparture(Place departure) {
        this.departure = departure;
    }

    public Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }
}

