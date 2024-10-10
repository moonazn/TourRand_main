package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {

    TextView locationTextView, ptyTextView, popTextView, tmpTextView, wsdTextView;
    String planDate;
    String tour_name;
    int tourId;
    Handler handler;
    String getData;
    String cloth = null;
    TripPlan tripPlan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // bottom.xml에서 ImageView 찾기
        ImageView editPageIcon = findViewById(R.id.editPage);
        ImageView weatherPageIcon = findViewById(R.id.weatherPage);
        ImageView randomPageIcon = findViewById(R.id.randomPage);
        ImageView groupPageIcon = findViewById(R.id.groupPage);

        // 현재 화면에 해당하는 아이콘의 이미지 변경
        weatherPageIcon.setImageResource(R.drawable.weather_on);
        editPageIcon.setImageResource(R.drawable.edit_home_off);
        randomPageIcon.setImageResource(R.drawable.random_off);
        groupPageIcon.setImageResource(R.drawable.group_off);

        tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        planDate = tripPlan.getTravelDate();
        tour_name = tripPlan.getTripName();
        tourId = tripPlan.getTourId();

        locationTextView = findViewById(R.id.locationTextView);
        ptyTextView = findViewById(R.id.ptyTextView);
        popTextView = findViewById(R.id.popTextView);
        tmpTextView = findViewById(R.id.tmpTextView);
        wsdTextView = findViewById(R.id.wsdTextView);

        groupPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, TeamActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        editPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, PlanEditActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        randomPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, RandomActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });



        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();

        String url = "https://api.tourrand.com/weather";
// JSON 문자열을 구성하기 위한 StringBuilder 사용
        StringBuilder data = new StringBuilder();

        data.append("{");
        data.append("\"user_id\":\"").append(userId).append("\",");
        data.append("\"tour_id\":\"").append(tourId).append("\"");
        data.append("}");

        // 최종적으로 생성된 JSON 문자열
        String jsonData = data.toString();
        new Thread(() -> {
            String result = httpPostBodyConnection(url, jsonData);
            // 처리 결과 확인
            handler = new Handler(Looper.getMainLooper());
            if (handler != null) {
                handler.post(() -> {
                    if(result != null && !result.isEmpty()) {
                        // tripPlans 초기화 및 데이터 파싱
                        parseWeatherData(result);
//                                tripPlans = parseTripPlan(result);
//                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());

                        setClothData();
                    } else {
                        Log.e("Error", "Result is null or empty");
                    }
                    seeNetworkResult(result);
                });
            }
        }).start();
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
    public void parseWeatherData(String json) {
        try {
            // JSON 전체 객체를 먼저 파싱합니다.
            JSONObject jsonObject = new JSONObject(json);

            String pty = null;
            String pop = null;
            String tmp = null;
            String wsd = null;
            String sky = null;
            String location = null;

            if (jsonObject.has("강수 형태(PTY)")) {
                pty = jsonObject.getString("강수 형태(PTY)");
                Log.d("강수 형태(PTY)", pty);
            }

            if (jsonObject.has("강수 확률(POP)")) {
                pop = jsonObject.getString("강수 확률(POP)");
                Log.d("강수 확률(POP)", pop);
            }

            if (jsonObject.has("지역")) {
                location = jsonObject.getString("지역");
                Log.d("지역", location);
            }

            if (jsonObject.has("기온(TMP)")) {
                tmp = jsonObject.getString("기온(TMP)");
                Log.d("기온(TMP)", tmp);
            }

            if (jsonObject.has("오늘의 옷차림 추천")) {
                cloth = jsonObject.getString("오늘의 옷차림 추천");
                Log.d("오늘의 옷차림 추천", cloth);
            }

            if (jsonObject.has("풍속(WSD)")) {
                wsd = jsonObject.getString("풍속(WSD)");
                Log.d("풍속(WSD)", wsd);
            }

            if (jsonObject.has("하늘 상태(SKY)")) {
                sky = jsonObject.getString("하늘 상태(SKY)");
                Log.d("하늘 상태(SKY)", sky);
            }

            ptyTextView.setText("강수 형태: " + pty);
            popTextView.setText("강수 확률: " + pop);
            tmpTextView.setText(tmp);
            wsdTextView.setText("풍속: " + wsd);
            locationTextView.setText("여행 지역: "+location);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setClothData() {
        List<String> keywords = Arrays.asList(cloth.split(","));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

//        int numberOfColumns = 3; // 열의 개수를 원하는 대로 설정하세요.
//        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
//
//// 간격을 설정할 ItemDecoration 추가
//        int spacing = 8; // 간격을 dp로 설정
//        boolean includeEdge = true; // 가장자리 포함 여부
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(numberOfColumns, spacing, includeEdge));
//
//
//        KeywordAdapter adapter = new KeywordAdapter(keywords);
//        recyclerView.setAdapter(adapter);

// 2개의 열로 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        KeywordAdapter adapter = new KeywordAdapter(keywords);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(WeatherActivity.this, PlanEditActivity.class);
        intent.putExtra("tripPlan",tripPlan);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}