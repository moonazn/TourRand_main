package com.tourbus.tourrand;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    private static KakaoApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this,"{d71b70e03d7f7b494a72421fb46cba46}");
    }
}