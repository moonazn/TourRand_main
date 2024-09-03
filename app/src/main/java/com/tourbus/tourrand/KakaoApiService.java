package com.tourbus.tourrand;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoApiService {
    @GET("/v2/local/search/keyword.json")
    Call<KakaoResponse> getDrivingTime(
            @Header("Authorization") String apiKey,
            @Query("origin") String origin,
            @Query("destination") String destination
    );
}
