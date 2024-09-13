package com.tourbus.tourrand;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class GeocodingUtils {

    private static final String KAKAO_API_KEY = "fe49cf8c95a5f7436ec71df81036c51e";

    public static CompletableFuture<Location> geocodeAsync(String address) {
        return CompletableFuture.supplyAsync(() -> {
            OkHttpClient client = new OkHttpClient();
            String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "KakaoAK " + KAKAO_API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONObject document = jsonObject.getJSONArray("documents").getJSONObject(0);
                double longitude = document.getJSONObject("address").getDouble("x");
                double latitude = document.getJSONObject("address").getDouble("y");
                return new Location(address, latitude, longitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
