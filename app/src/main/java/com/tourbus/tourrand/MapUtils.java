package com.tourbus.tourrand;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class MapUtils {

    public static void showRoute(Context context, Location startLocation, ExcelParser.Location endLocation) {
        String url = "kakaomap://route?sp=" + startLocation.getLatitude() + "," + startLocation.getLongitude() +
                "&ep=" + endLocation.latitude + "," + endLocation.longitude + "&by=CAR";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            System.out.println("exeption is : " + e);
            // 카카오맵 설치 페이지로 이동
            String marketUrl = "market://details?id=net.daum.android.map";
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl));
            context.startActivity(intent);
        }

    }
}
