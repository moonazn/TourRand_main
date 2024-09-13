package com.tourbus.tourrand;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class SplashActivity extends AppCompatActivity {

    private static final double SPLASH_TIME_SEC = 2.5;
    private boolean isLoginChecked = false;
    private boolean isTimerFinished = false;
    static AppUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // VideoView 설정
        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash720);
        videoView.setVideoURI(videoUri);

        // 비디오 재생 시작
        videoView.start();

        // 로그인 상태를 확인하여 적절한 화면으로 이동
        checkLoginStatus();

        // 타이머 시작
        startTimer(SPLASH_TIME_SEC);
    }

    private void checkLoginStatus() {
        // 로그인 상태 확인
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                isLoginChecked = true;
                if (isTimerFinished) {
                    // 로그인 상태 확인이 완료되었고, 타이머도 완료된 경우
                    if (user != null) {
                        moveToActivity(HomeActivity.class);
                    } else {
                        moveToActivity(MainActivity.class);
                    }
                }
                return null;
            }
        });
    }

    private void startTimer(double sec) {
        int delayMillis = (int) (sec * 1000); // 초를 밀리초로 변환
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isTimerFinished = true;
                if (isLoginChecked) {
                    // 타이머가 완료되었고, 로그인 상태 확인도 완료된 경우
                    UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(User user, Throwable throwable) {
                            if (user != null) {

                                UserManager userManager = UserManager.getInstance();
                                userManager.setUserNickname(user.getKakaoAccount().getProfile().getNickname());
                                userManager.setUserId(user.getId().toString());
                                userManager.getUserNickname();
                                Log.d("닉네임",userManager.getUserNickname());
                                userManager.getUserId();
                                userManager.setUserProfileImg(user.getKakaoAccount().getProfile().getProfileImageUrl());
                                userManager.getUserProfileImg();
                                moveToActivity(HomeActivity.class);
                            } else {
                                moveToActivity(MainActivity.class);
                            }
                            return null;
                        }
                    });
                }
            }
        }, delayMillis); // sec초 정도 딜레이를 준 후 시작
    }

    private void moveToActivity(Class<?> activityClass) {
        // 새로운 Intent 생성
        Intent intent = new Intent(getApplicationContext(), activityClass);

        // Intent에 명시된 액티비티로 이동
        startActivity(intent);
        overridePendingTransition(0, 0);

        // 현재 액티비티 종료
        finish();
    }
}
