package com.tourbus.tourrand;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kakao.sdk.user.UserApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeFragment1 extends Fragment {

    private RecyclerView recyclerView;
    private TripPlanAdapter adapter;
    private List<TripPlan> tripPlans;
    private TextView shakeText1;
    private TextView shakeText2;
    private TextView plusBtn;
    private ImageView mypageBtn;
    private ImageView logo;

    private TextView tripzero;
    private Handler handler;
    private int tourId;
    String getData;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home1, container, false);

        // RecyclerView 초기화
        recyclerView = rootView.findViewById(R.id.recycler_view_trip_plans);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 임시 데이터 생성 (실제 데이터는 네트워크 요청 또는 로컬 DB에서 가져와야 함)
        tripPlans = new ArrayList<>();
//        tripPlans.add(new TripPlan("여행 이름 1", "2024-06-20 ~ 2024-07-01", "2"));
//        tripPlans.add(new TripPlan("여행 이름 2", "2024-06-20 ~ 2024-07-01", "30"));
//        tripPlans.add(new TripPlan("여행 이름 3", "2024-06-20 ~ 2024-07-01", "50"));
//        tripPlans.add(new TripPlan("여행 이름 4", "2024-06-20 ~ 2024-07-01", "90"));
//        tripPlans.add(new TripPlan("여행 이름 5", "2024-06-20 ~ 2024-07-01", "120"));
//        tripPlans.add(new TripPlan("여행 이름 5", "2024-06-20 ~ 2024-07-01", "120"));
//        tripPlans.add(new TripPlan("여행 이름 5", "2024-06-20 ~ 2024-07-01", "120"));
//        tripPlans.add(new TripPlan("여행 이름 5", "2024-06-20 ~ 2024-07-01", "120"));
//        tripPlans.add(new TripPlan("여행 이름 5", "2024-06-20 ~ 2024-07-01", "120"));

        // 어댑터 초기화
        adapter = new TripPlanAdapter(getActivity(), tripPlans, HomeFragment1.this);
        recyclerView.setAdapter(adapter);

        tripzero = rootView.findViewById(R.id.tripzero);
        updateUI();

        if(tripPlans.size() == 0) {
            tripzero.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tripzero.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Adapter 설정
            adapter = new TripPlanAdapter(getActivity(), tripPlans, HomeFragment1.this);

            // GridLayoutManager 설정
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(RecyclerView.VERTICAL);

            // RecyclerView에 LayoutManager와 Adapter 설정
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

        }

        // 애니메이션 설정 및 시작
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        shakeText1 = rootView.findViewById(R.id.shakeText1);
        shakeText2 = rootView.findViewById(R.id.shakeText2);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                new Handler().postDelayed(() -> {
                    shakeText1.startAnimation(shake);
                    shakeText2.startAnimation(shake);
                }, 1000); // 1초 딜레이 추가
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

//        System.out.println("email = " + SplashActivity.currentUser.getEmail());

        mypageBtn = rootView.findViewById(R.id.mypage);
        plusBtn = rootView.findViewById(R.id.plusBut);

        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyPageActivity.class);
                startActivity(intent);
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuestionActivity.class);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            }
        });
        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();
        logo = rootView.findViewById(R.id.logo);

        String url = "https://api.tourrand.com/tour_list";
        String data = "{ \"user_id\" : \""+userId+"\"}"; //json 형식 데이터

        new Thread(() -> {
            String result = httpPostBodyConnection(url, data);
            // 처리 결과 확인
            handler = new Handler(Looper.getMainLooper());
            if (handler != null) {
                handler.post(() -> {
                    if(result != null && !result.isEmpty()) {
                        // tripPlans 초기화 및 데이터 파싱
                        tripPlans.clear();
                        tripPlans.addAll(parseTripPlan(result));
//                                tripPlans = parseTripPlan(result);
//                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());

                        // 데이터 확인 로그
                        Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());
                        for (TripPlan plan : tripPlans) {
                            Log.d("TripPlan", "Plan: " + plan.getTripName() + ", Date: " + plan.getTravelDate());
                        }

                    } else {
                        Log.e("Error", "Result is null or empty");
                    }
                    seeNetworkResult(result);
                });
            }
        }).start();
//        logo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "https://api.tourrand.com/tour_list";
//                String data = "{ \"user_id\" : \""+userId+"\"}"; //json 형식 데이터
//
//                new Thread(() -> {
//                    String result = httpPostBodyConnection(url, data);
//                    // 처리 결과 확인
//                    handler = new Handler(Looper.getMainLooper());
//                    if (handler != null) {
//                        handler.post(() -> {
//                            if(result != null && !result.isEmpty()) {
//                                // tripPlans 초기화 및 데이터 파싱
//                                tripPlans.clear();
//                                tripPlans.addAll(parseTripPlan(result));
////                                tripPlans = parseTripPlan(result);
////                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());
//
//                                // 데이터 확인 로그
//                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());
//                                for (TripPlan plan : tripPlans) {
//                                    Log.d("TripPlan", "Plan: " + plan.getTripName() + ", Date: " + plan.getTravelDate());
//                                }
//
//                                // UI 갱신
//                                updateUI();
//                            } else {
//                                Log.e("Error", "Result is null or empty");
//                            }
//                            seeNetworkResult(result);
//                        });
//                    }
//                }).start();
//
//            }
//        });

        return rootView;
    }
    private void updateUI() {
        if (tripPlans.isEmpty()) {
            tripzero.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tripzero.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
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



        // UI 갱신
        updateUI();
        Log.d("updateUI","실행완");
    }
    public ArrayList<TripPlan> parseTripPlan(String json) {
        ArrayList<TripPlan> TripPlanList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String tripName = null;
                String travelDate = null;

                if (jsonObject.has("tour_name")) {
                    tripName = jsonObject.getString("tour_name");
                    Log.d("여행이름", tripName);
                }

                if (jsonObject.has("tour_id")) {
                    tourId = jsonObject.getInt("tour_id");
                    Log.d("여행아이디", String.valueOf(tourId));
                }

                if (jsonObject.has("planDate")) {
                    travelDate = jsonObject.getString("planDate");
                    Log.d("여행 날짜", travelDate);
                }

                if (tripName != null && travelDate != null) {
                    TripPlan tripPlan = new TripPlan(tripName, travelDate, getDday(travelDate), tourId);
                    TripPlanList.add(tripPlan);
                } else {
                    Log.e("JSONError", "Missing key in JSON object: " + jsonObject.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return TripPlanList;
    }
    public void deleteTripOnServer(int tripId, int position) {
        adapter.removeItem(position);

        // 싱글톤 인스턴스 가져오기
        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();

        String url = "https://api.tourrand.com/delete";
//            String data = "{ \"user_id\" : \""+userId+"\", \"tour_name\" : \""+tripPlanDetailList.get(0).getTripName()+"\" , \"planDate\" : \""+tripPlanDetailList.get(0).getPlanDate()+"\", \"schedules\" : [{\""+tripPlanDetailList+"\"}] }";

        // JSON 문자열을 구성하기 위한 StringBuilder 사용
        StringBuilder data = new StringBuilder();

        data.append("{");
        data.append("\"user_id\":\"").append(userId).append("\",");
        data.append("\"tour_id\":\"").append(tripId).append("\"");
        data.append("}");

        // 최종적으로 생성된 JSON 문자열
        String jsonData = data.toString();

        // jsonData를 서버에 전송
        Log.d("data", jsonData);
        new Thread(() -> {
            getData = httpPostBodyConnection(url, jsonData);
            // 처리 결과 확인
            handler.post(() -> {
                seeNetworkResult(getData);
            });
        }).start();
    }
    private String getDday(String planDate){
// 입력 문자열
        String dateRange = planDate;

        // 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 시작 날짜와 끝 날짜 분리
        String[] dates = dateRange.split("~");
        LocalDate startDate = LocalDate.parse(dates[0].trim(), formatter);
        LocalDate endDate = LocalDate.parse(dates[1].trim(), formatter);

        // 현재 날짜 가져오기
        LocalDate currentDate = LocalDate.now();

        // D-day 계산
        long dDay = ChronoUnit.DAYS.between(currentDate, startDate);
        String dDaytoString  = String.valueOf(dDay);

        if (currentDate.isBefore(startDate)) {
            System.out.println("D-" + dDay);
        } else if (currentDate.isAfter(endDate)) {
            System.out.println("기간이 종료되었습니다.");
        } else {
            System.out.println("기간 중입니다. (" + dDay + "일 지남)");
        }

        return dDaytoString;
    }

//    public ArrayList<TripPlan> parseTripPlan(String json) {
//        ArrayList<TripPlan> TripPlanList = new ArrayList<>();
//
//        try {
//            JSONArray jsonArray = new JSONArray(json);
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                String tripName = null;
//                String travelDate = null;
//
//                if (jsonObject.has("tour_name")) {
//                    tripName = jsonObject.getString("tour_name").toString();
//                    Log.d("여행이름", tripName);
//                }
//
//                if (jsonObject.has("planDate")) {
//                    travelDate = jsonObject.getString("planDate").toString();
//                    Log.d("주소", travelDate);
//                }
//
//
//                if (tripName != null && travelDate != null) {
//                    TripPlan TripPlan = new TripPlan(tripName, travelDate,"3");
//                    tripPlans.add(TripPlan);
//
////                    Intent intent = new Intent(DstActivity.this, PlanViewActivity.class);
////                    intent.putParcelableArrayListExtra("TripPlanDetailList", TripPlanDetailList);
//
//                    //Log.d("맞나?", TripPlanDetailList.toString());
//                } else {
//                    Log.e("JSONError", "Missing key in JSON object: " + jsonObject.toString());
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return TripPlanList;
//    }
}
