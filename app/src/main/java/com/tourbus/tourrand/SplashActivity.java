package com.tourbus.tourrand;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.VideoView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class SplashActivity extends AppCompatActivity {

    private static final double SPLASH_TIME_SEC = 2.5;
    private boolean isLoginChecked = false;
    private boolean isTimerFinished = false;
    static AppUser currentUser;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler = new Handler(Looper.getMainLooper());

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
                                String url = "https://api.tourrand.com/kakao_login";
                                String data = "{ \"id\" : \""+userManager.getUserId()+"\",\"nickname\":\""+userManager.getUserNickname()+"\",\"user_img\":\""+userManager.getUserProfileImg()+"\",\"email\" : \""+user.getKakaoAccount().getEmail().toString()+"\" }"; //json 형식 데이터
                                new Thread(() -> {
                                    String result = httpPostBodyConnection(url, data);
                                    // 처리 결과 확인
                                    handler.post(() ->{
                                        try {
                                            // JSON 문자열을 JSONObject로 변환
                                            JSONObject jsonObject = new JSONObject(result);

                                            // 공통된 값 처리 (nickname)
                                            String get_nickname = jsonObject.getString("nickname");
                                            UserManager.getInstance().setUserNickname(get_nickname);
                                            String isInviteCheck = jsonObject.getString("invite");
                                            Log.d("초대여부 체크-스", isInviteCheck);
                                            if(isInviteCheck.equals("초대 있음")){
                                                String inviteTourName = jsonObject.getString("tour_name");
                                                int inviteTourId = jsonObject.getInt("tour_id");
                                                String inviteNickname = jsonObject.getString("invite_nickname");
                                                boolean isInviteState = jsonObject.getBoolean("isInviteState");

//                                                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//                                                intent.putExtra("inviteTourName",inviteTourName);
//                                                intent.putExtra("inviteTourId",inviteTourId);
//                                                intent.putExtra("inviteNickname",inviteNickname);
//                                                intent.putExtra("isInviteState",isInviteState);

                                                // Intent로 HomeActivity에 값 전달
                                                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                                intent.putExtra("inviteTourName", inviteTourName);
                                                intent.putExtra("inviteTourId", inviteTourId);
                                                intent.putExtra("inviteNickname", inviteNickname);
                                                intent.putExtra("isInviteState", isInviteState);
                                                startActivity(intent);
                                                finish();

//                                                Fragment fragment = new HomeFragment1();
//                                                Bundle bundle = new Bundle();
//                                                Log.d("스플래시inviteTourName",inviteTourName);
//                                                Log.d("isInviteState", String.valueOf(isInviteState));
//                                                bundle.putString("inviteTourName", inviteTourName);
//                                                bundle.putInt("inviteTourId", inviteTourId);
//                                                bundle.putString("inviteNickname", inviteNickname);
//                                                bundle.putBoolean("isInviteState", isInviteState);
//                                                fragment.setArguments(bundle);
                                                // FragmentManager를 사용해 프래그먼트를 추가 또는 교체
//                                                FragmentManager fragmentManager = getSupportFragmentManager();
//                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                                fragmentTransaction.replace(R.id.fragment_container, fragment);  // fragment_container는 실제 레이아웃 ID로 교체해야 합니다.
//                                                fragmentTransaction.commit();
                                            }

                                            // 공통 처리
                                           // System.out.println("Nickname: " + nickname);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        seeNetworkResult(result);
                                    });
                                }).start();
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
    public String httpPostBodyConnection(String UrlData, String ParamData) {
        // 이전과 동일한 네트워크 연결 코드를 그대로 사용합니다.
        // 백그라운드 스레드에서 실행되기 때문에 메인 스레드에서는 문제가 없습니다.

        String totalUrl = "";
        totalUrl = UrlData.trim().toString();

        //http 통신을 하기위한 객체 선언 실시
        URL url = null;
        HttpURLConnection conn = null;

        //http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
        String responseData = "";
        BufferedReader br = null;
        StringBuffer sb = null;

        //메소드 호출 결과값을 반환하기 위한 변수
        String returnData = "";


        try {
            //파라미터로 들어온 url을 사용해 connection 실시
            url = null;
            url = new URL(totalUrl);
            conn = null;
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8"); //post body json으로 던지기 위함
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); //OutputStream을 사용해서 post body 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte request_data[] = ParamData.getBytes("utf-8");
                Log.d("TAGGG",request_data.toString());
                os.write(request_data);
                //os.close();
            } catch (Exception e) {
                Log.d("TAG3","여기다");
                e.printStackTrace();
            }

            //http 요청 실시
            conn.connect();
            System.out.println("http 요청 방식 : " + "POST BODY JSON");
            System.out.println("http 요청 타입 : " + "application/json");
            System.out.println("http 요청 주소 : " + UrlData);
            System.out.println("http 요청 데이터 : " + ParamData);
            System.out.println("");

            //http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
            }

            //메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
            returnData = sb.toString();
            Log.d("TAG2", returnData);
            //http 요청 응답 코드 확인 실시
            String responseCode = String.valueOf(conn.getResponseCode());
            System.out.println("http 응답 코드 : " + responseCode);
            System.out.println("http 응답 데이터 : " + returnData);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //http 요청 및 응답 완료 후 BufferedReader를 닫아줍니다
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnData; // 네트워크 요청 결과를 반환
    }
    public void seeNetworkResult(String result) {
        // 네트워크 작업 완료 후
        Log.d(result, "network");
    }
}
