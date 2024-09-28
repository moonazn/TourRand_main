package com.tourbus.tourrand;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InviteDialog extends Dialog {
    private String TourName;
    private String Nickname;
    private int tourId;
    private boolean isInviteStatus;
    private Handler handler;
    private HomeActivity homeActivity;

    public InviteDialog(@NonNull Context context, String TourName, String Nickname, int tourId, boolean isInviteStatus) {
        super(context);

        // 여기서 Context가 HomeActivity로 캐스팅 가능한지 확인
        if (context instanceof HomeActivity) {
            this.homeActivity = (HomeActivity) context;
        } else {
            throw new ClassCastException("Context is not an instance of HomeActivity");
        }

        this.Nickname = Nickname;
        this.TourName = TourName;
        this.tourId = tourId;
        this.isInviteStatus = isInviteStatus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.invite_dialog);

        handler = new Handler(Looper.getMainLooper());

        TextView inviter = findViewById(R.id.invite);
        TextView inviteTour = findViewById(R.id.inviteTour);

        Button positiveBtn = findViewById(R.id.yesBtn);
        Button negativeBtn = findViewById(R.id.noBtn);

        inviter.setText(Nickname);
        inviteTour.setText(TourName);


        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.tourrand.com/add";
                String data = "{ \"user_id\" : \""+UserManager.getInstance().getUserId()+"\",\"tour_id\" : \""+tourId+"\"}"; //json 형식 데이터

                new Thread(() -> {
                    String result = httpPostBodyConnection(url, data);
                    // 처리 결과 확인
                    handler.post(() -> {
                        seeNetworkResult(result);
//                        FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
//                        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                        transaction.replace(R.id.fragment_container, new HomeFragment1());
//                        transaction.commit();

//                        if (getOwnerActivity() instanceof HomeActivity) {
//                            ((HomeActivity) getOwnerActivity()).viewPager.setCurrentItem(0, false);
//                        } else {
//                            Log.e("InviteDialog", "Owner Activity is not an instance of HomeActivity");
//                        }
                        // HomeActivity 인스턴스가 존재할 경우, viewPager 처리
                        if (homeActivity != null) {
                            Log.d("여기다","여기다");
                            homeActivity.viewPager.setCurrentItem(0, false);
                        } else {
                            Log.e("InviteDialog", "HomeActivity instance is null");
                        }
                    });
                }).start();
                dismiss();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.tourrand.com/invite_delete";
                String data = "{ \"user_id\" : \""+UserManager.getInstance().getUserId()+"\",\"tour_id\" : \""+tourId+"\"}"; //json 형식 데이터

                new Thread(() -> {
                    String result = httpPostBodyConnection(url, data);
                    // 처리 결과 확인
                    handler.post(() -> {
                        seeNetworkResult(result);

                    });
                }).start();
                Toast.makeText(getContext(),"힝",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

    }
//    private String conncetAgain(String againUrl, String againData){
//        againUrl = "https://api.tourrand.com/tour_list";
//        againData = "{ \"user_id\" : \""+UserManager.getInstance().getUserId()+"\"}"; //json 형식 데이터
//        String finalAgainUrl = againUrl;
//        String finalAgainData = againData;
//        new Thread(() -> {
//            String result = httpPostBodyConnection(finalAgainUrl, finalAgainData);
//            // 처리 결과 확인
//            handler = new Handler(Looper.getMainLooper());
//            if (handler != null) {
//                handler.post(() -> {
//                    if(result != null && !result.isEmpty()) {
//                        // tripPlans 초기화 및 데이터 파싱
//                        tripPlans.clear();
//                        tripPlans.addAll(parseTripPlan(result));
////                                tripPlans = parseTripPlan(result);
////                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());
//
//                        // 데이터 확인 로그
//                        Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());
//                        for (TripPlan plan : tripPlans) {
//                            Log.d("TripPlan", "Plan: " + plan.getTripName() + ", Date: " + plan.getTravelDate());
//                        }
//
//                    } else {
//                        Log.e("Error", "Result is null or empty");
//                    }
//                    seeNetworkResult(result);
//                });
//            }
//        }).start();
//
//    }
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
