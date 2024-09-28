package com.tourbus.tourrand;

import static android.content.Intent.getIntent;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kakao.sdk.user.UserApiClient;

import org.apache.poi.ss.formula.functions.T;
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
    private Handler handler = new Handler();
    private int tourId;
    String getData;
    private String inviteTourName;
    private int inviteTourId;
    OnTripPlansReceivedListener listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home1, container, false);

        Log.d("홈프래그먼트","시작");
        // HomeActivity에서 Fragment를 다시 실행하는 코드
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//// HomeFragment1을 찾아서 제거하고 다시 추가
//        HomeFragment1 homeFragment1 = (HomeFragment1) fragmentManager.findFragmentByTag("f0"); // 0번 인덱스 Fragment의 태그
//        if (homeFragment1 != null) {
//            transaction.remove(homeFragment1);
//            transaction.commitNow(); // 즉시 commit
//            // 다시 Fragment를 추가
//            transaction = fragmentManager.beginTransaction();
//            transaction.add(R.id.fragment_container, new HomeFragment1(), "f0");
//            transaction.commit();
//        }


        Button test = rootView.findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), RouletteMember.class);
                Intent intent = new Intent(getActivity(), RandomCustomSetActivity.class);
                startActivity(intent);
            }
        });
        // RecyclerView 초기화
        recyclerView = rootView.findViewById(R.id.recycler_view_trip_plans);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        Bundle bundle = getArguments();
//        if(bundle !=null){
//            String inviteTourName = bundle.getString("inviteTourName");
//            String inviteNickname = bundle.getString("inviteNickname");
//            int inviteTourid = bundle.getInt("inviteTourId");
//            Log.d("홈프래그먼트",inviteTourName);
//            boolean isInviteStatus = bundle.getBoolean("isInviteState");
//
//            getActivity().runOnUiThread(() -> {
//                InviteDialog dialog = new InviteDialog(getActivity(), inviteTourName, inviteNickname, inviteTourid, isInviteStatus);
//                dialog.show();
//            });
//            //InviteDialog dialog = new InviteDialog(getActivity(), inviteTourName, inviteNickname,inviteTourid, isInviteStatus);
//
//            //dialog.show();
//
//        }else {
//            Log.d("홈프래그먼트", "Bundle is null");
//        }


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
        tripzero = rootView.findViewById(R.id.tripzero);

        connect(new OnTripPlansReceivedListener() {
            @Override
            public void onTripPlansReceived(List<TripPlan> tripPlans) {
                // tripPlans를 받아서 UI를 업데이트
                if (tripPlans != null && !tripPlans.isEmpty()) {
                    // 예: RecyclerView에 데이터 세팅
// 어댑터 초기화
                    Log.d("홈프래그먼트", "onCreate connect");
                    adapter = new TripPlanAdapter(getActivity(), tripPlans, HomeFragment1.this);
                    recyclerView.setAdapter(adapter);

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

                    }                } else {
                    // 데이터가 없을 때 처리
                    Log.d("MyFragment", "No trip plans received");
                }
            }
        });
//        requireActivity().runOnUiThread(() -> {
//            connect(listener);
//
//
//        });


//        Button move = rootView.findViewById(R.id.move);
//        move.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), TeamActivity.class);
//                startActivity(intent);
//            }
//        });



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
        logo = rootView.findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InviteDialog dialog = new InviteDialog(HomeActivity.class, "부산여행", "장징징",16, false);
//                dialog.show();
            }
        });

        return rootView;
    }
    private void updateUI() {

        if (tripPlans == null || tripPlans.isEmpty()) {
            tripzero.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tripzero.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                Log.e("updateUI", "adapter is null");
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
        Log.d("network", result);
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
                String memberImg = null;

                ArrayList<String> userImgs = new ArrayList<>();

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
                if (jsonObject.has("user_imgs")) {
                    JSONArray imgArray = jsonObject.getJSONArray("user_imgs");
                    for (int j = 0; j < imgArray.length(); j++) {
                        String imageUrl = imgArray.getString(j);
                        userImgs.add(imageUrl);
                        Log.d("유저 이미지", imageUrl);
                    }
                }
                if (tripName != null && travelDate != null && userImgs!=null) {
                    TripPlan tripPlan = new TripPlan(tripName, travelDate, getDday(travelDate), tourId,userImgs );
                    TripPlanList.add(tripPlan);
                } else if (tripName != null && travelDate != null) {
                    TripPlan tripPlan = new TripPlan(tripName, travelDate, getDday(travelDate), tourId );
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
    public void connect(OnTripPlansReceivedListener listener) {
        List<TripPlan> tripPlansHere = new ArrayList<>();
        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();
        Log.d("홈프래그먼트", "connect");

        String url = "https://api.tourrand.com/tour_list";
        String data = "{ \"user_id\" : \"" + userId + "\"}"; // json 형식 데이터

        new Thread(() -> {
            String result = httpPostBodyConnection(url, data);
            // 처리 결과 확인
            Handler handler2 = new Handler(Looper.getMainLooper());
            handler2.post(() -> {
                if (result != null && !result.isEmpty()) {
                    // tripPlans 초기화 및 데이터 파싱
                    tripPlansHere.clear();
                    tripPlansHere.addAll(parseTripPlan(result));

                    // 데이터 확인 로그
                    Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlansHere.size());
                    for (TripPlan plan : tripPlansHere) {
                        Log.d("TripPlan", "Plan: " + plan.getTripName() + ", Date: " + plan.getTravelDate());
                    }
                }
                seeNetworkResult(result);
                // 콜백 호출
                listener.onTripPlansReceived(tripPlansHere);
            });
        }).start();
    }

    // 콜백 인터페이스
    public interface OnTripPlansReceivedListener {
        void onTripPlansReceived(List<TripPlan> tripPlans);
    }

    public void deleteTripOnServer(int tripId, int position) {
        // 서버에 삭제 요청을 먼저 보냄
        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();

        String url = "https://api.tourrand.com/delete";

        // JSON 문자열을 구성
        String jsonData = String.format("{\"user_id\":\"%s\",\"tour_id\":\"%d\"}", userId, tripId);
        Log.d("data", jsonData);

        new Thread(() -> {
            // 서버에 요청 보내기
            String getData = httpPostBodyConnection(url, jsonData);

            // 요청 처리 결과 확인
            handler.post(() -> {
                // 응답 처리 및 새로운 TripPlan 리스트 파싱
                List<TripPlan> newTripPlan = parseTripPlan(getData);

                // 기존 adapter의 데이터 갱신
                tripPlans.clear(); // 기존 데이터 삭제
                tripPlans.addAll(newTripPlan); // 새로운 데이터 추가
                adapter = null; // 데이터 변경 알림
                adapter = new TripPlanAdapter(getActivity(), tripPlans, HomeFragment1.this);

                // RecyclerView의 상태 업데이트
                if (newTripPlan.isEmpty()) {
                    tripzero.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tripzero.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                recyclerView.setAdapter(adapter);

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
            System.out.println("D" + dDay);
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
//@Override
//public void onResume() {
//    super.onResume();
//
//    connect(new OnTripPlansReceivedListener() {
//
//        @Override
//        public void onTripPlansReceived(List<TripPlan> tripPlans) {
//            // tripPlans를 받아서 UI를 업데이트
//            if (tripPlans != null && !tripPlans.isEmpty()) {
//                // 예: RecyclerView에 데이터 세팅
//// 어댑터 초기화
//                adapter = new TripPlanAdapter(getActivity(), tripPlans, HomeFragment1.this);
//                recyclerView.setAdapter(adapter);
//                Log.d(" 홈프래그먼트", "onResume, connect");
//
//                updateUI();
//
//                if(tripPlans.size() == 0) {
//                    tripzero.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                } else {
//                    tripzero.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
//
//                    // Adapter 설정
//                    adapter = new TripPlanAdapter(getActivity(), tripPlans, HomeFragment1.this);
//
//                    // GridLayoutManager 설정
//                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//                    layoutManager.setOrientation(RecyclerView.VERTICAL);
//
//                    // RecyclerView에 LayoutManager와 Adapter 설정
//                    recyclerView.setLayoutManager(layoutManager);
//                    recyclerView.setAdapter(adapter);
//
//                }                } else {
//                // 데이터가 없을 때 처리
//                Log.d("MyFragment", "No trip plans received");
//            }
//        }
//    });
//}
}
