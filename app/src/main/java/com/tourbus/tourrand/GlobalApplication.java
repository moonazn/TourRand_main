package com.tourbus.tourrand;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Kakao SDK 초기화
        KakaoSdk.init(this, "d71b70e03d7f7b494a72421fb46cba46");
        System.out.println("GlobalApplication class is executed.");
    }
}