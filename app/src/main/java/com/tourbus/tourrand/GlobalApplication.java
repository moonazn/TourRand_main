package com.tourbus.tourrand;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Kakao SDK 초기화
        KakaoSdk.init(this, "e211572ac7a98da2054d8a998e86a28a");
        System.out.println("GlobalApplication class is executed.");
    }
}