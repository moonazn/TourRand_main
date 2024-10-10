package com.tourbus.tourrand;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoApiService {
    @GET("v1/directions")
    Call<KakaoResponse> getDrivingTime(
            @Header("Authorization") String authHeader,
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("waypoints") String waypoints,
            @Query("priority") String priority,
            @Query("car_fuel") String carFuel,
            @Query("car_hipass") boolean carHipass,
            @Query("alternatives") boolean alternatives,
            @Query("road_details") boolean roadDetails
    );
}
