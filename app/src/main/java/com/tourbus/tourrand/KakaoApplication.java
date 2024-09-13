package com.tourbus.tourrand;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    private static KakaoApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this,"{e211572ac7a98da2054d8a998e86a28a}");
    }
}