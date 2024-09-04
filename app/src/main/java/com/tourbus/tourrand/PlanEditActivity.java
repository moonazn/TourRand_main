package com.tourbus.tourrand;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelManager;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import com.kakao.vectormap.route.RouteLine;
import com.kakao.vectormap.route.RouteLineLayer;
import com.kakao.vectormap.route.RouteLineOptions;
import com.kakao.vectormap.route.RouteLineSegment;
import com.kakao.vectormap.route.RouteLineStyle;
import com.kakao.vectormap.route.RouteLineStyles;
import com.kakao.vectormap.route.RouteLineStylesSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanEditActivity extends AppCompatActivity {
    private LabelLayer labelLayer;

    private RecyclerView daysRecyclerView;
    private RecyclerView placesRecyclerView;
    private DaysAdapter daysAdapter;
    private PlacesEditAdapter placesEditAdapter;
    private List<String> daysList;
    private Map<Integer, List<Place>> placesMap;
    private KakaoMap map;
    private MapView mapView;
    private LabelManager labelManager;

    private ExcelParser excelParser;
    private GeocodingUtils geocodingUtils;

    private ImageView scheduleList;
    private TextView navigateTextView;

    private static final int MAX_REROLL_COUNT = 3;
    private List<PlanViewActivity.Schedule> savedSchedules = new ArrayList<>();
    private int rerollCount = 1;
    String theme;
    String destination;

    private ImageView edit;
    private boolean isEditing = false;
    private Handler handler = new Handler();

    String url; String jsonData, result;
    String planDate;
    int day;
    private ArrayList<TripPlanDetail> tripPlanDetailList;
    private ArrayList<Location> locationArrayList = new ArrayList<>();
    Place departureDocument;
    String tour_name;
    String getData;
    TextView tripTitleEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_edit);

        TripPlan tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        planDate = tripPlan.getTravelDate();
        tour_name = tripPlan.getTripName();

        tripTitleEditText = findViewById(R.id.tripTitleEditText);

        KakaoMapSdk.init(this, "d71b70e03d7f7b494a72421fb46cba46");

        mapView = findViewById(R.id.map);

        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();
        url = "http://13.209.33.141:5000/tour_detail";
// JSON 문자열을 구성하기 위한 StringBuilder 사용
        StringBuilder data = new StringBuilder();

        data.append("{");
        data.append("\"user_id\":\"").append(userId).append("\",");
        data.append("\"planDate\":\"").append(planDate).append("\",");
        data.append("\"tour_name\":\"").append(tour_name).append("\"");
        data.append("}");

// 최종적으로 생성된 JSON 문자열
        jsonData = data.toString();

// jsonData를 서버에 전송
        new ServerCommunicationTask().execute();

//        new Thread(() -> {
//            String result = httpPostBodyConnection(url, jsonData);
//
//            if (handler != null) {
//                handler.post(() -> {
//                    seeNetworkResult(result);
//                    Log.d("Result", result);
//
//                    // 서버에서 받은 결과에 따라 idChecked 값 설정
//                    idChecked = Boolean.parseBoolean(result);
//                    Log.d("JoinActivity", String.valueOf(idChecked));
//                    // UI 업데이트는 UI 스레드에서 수행
//                    if(idChecked == true) {
//                        idCheckInfo.setText("사용 가능한 아이디입니다.");
//                        idCheckInfo.setTextColor(getResources().getColor(R.color.blue)); // blue 컬러를 정의하세요
//                    } else {
//                        idCheckInfo.setText("중복된 아이디입니다.");
//                        idCheckInfo.setTextColor(getResources().getColor(R.color.red)); // red 컬러를 정의하세요
//                    }
//                    idCheckInfo.setVisibility(View.VISIBLE);
//                });
//            } else {
//                // handler가 null일 경우의 예외 처리
//                Log.e("JoinActivity", "Handler is null and cannot post Runnable.");
//            }
//        }).start();

//        mapView.start(new MapLifeCycleCallback() {
//            @Override
//            public void onMapDestroy() {
//                // 지도 API가 정상적으로 종료될 때 호출됨
//            }
//
//            @Override
//            public void onMapError(Exception error) {
//                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
//            }
//        }, new KakaoMapReadyCallback() {
//            @Override
//            public void onMapReady(KakaoMap kakaoMap) {
//                // 인증 후 API가 정상적으로 실행될 때 호출됨
//                LabelStyles styles = kakaoMap.getLabelManager()
//                        .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker)));
//                LabelOptions options = LabelOptions.from(LatLng.from(37.394660, 127.111182))
//                        .setStyles(styles);
//                LabelLayer layer = kakaoMap.getLabelManager().getLayer();
//                Label label = layer.addLabel(options);
//                Label centerLabel = layer.addLabel(options);
//                LabelOptions options2 = LabelOptions.from(LatLng.from(37.5642135, 127.0016985))
//                        .setStyles(styles);
//                Label label2 = layer.addLabel(options2);
//
//                ImageView logo = findViewById(R.id.logo);
//                logo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        centerLabel.changeStyles(LabelStyles.from(LabelStyle.from(R.drawable.choonsik)));
//                        centerLabel.moveTo(LatLng.from(37.5642135,
//                                127.0016985), 8000);
//                    }
//                });
//
////                //춘식이 돌아댕기는 거
////                LatLng pos = kakaoMap.getCameraPosition().getPosition();
////                Label centerLabel = labelLayer.addLabel(LabelOptions.from("dotLabel", pos)
////                        .setStyles(LabelStyle.from(R.drawable.choonsik).setAnchorPoint(0.5f, 0.5f))
////                        .setRank(1));
////                LatLng currentPos = centerLabel.getPosition();
////                centerLabel.moveTo(LatLng.from(currentPos.getLatitude() + 0.0006,
////                        currentPos.getLongitude() + 0.0006), 800);
//
//
//
//                //핀 사이 선으로 표시
//                kakaoMap.getRouteLineManager();
//                RouteLineLayer routelayer = kakaoMap.getRouteLineManager().getLayer();
//
//                RouteLineStylesSet stylesSet = RouteLineStylesSet.from("blueStyles",
//                        RouteLineStyles.from(RouteLineStyle.from(10, Color.BLUE)));
//                RouteLineSegment segment = RouteLineSegment.from(Arrays.asList(
//                                LatLng.from(37.394660, 127.111182),
//                                LatLng.from(37.5642135, 127.0016985)))
//                        .setStyles(stylesSet.getStyles(0));
//                RouteLineOptions routeoptions = RouteLineOptions.from(segment)
//                        .setStylesSet(stylesSet);
//                RouteLine routeLine = routelayer.addRouteLine(routeoptions);
//
//
//            }
//        });

        excelParser = new ExcelParser();
        geocodingUtils = new GeocodingUtils();

        // 엑셀 파일 파싱
        try {
            InputStream inputStream = getAssets().open("locations.xlsx");
            Log.d("PlanViewActivity", "Excel file found and opened"); // 로그 추가

            excelParser.parseExcelFile(inputStream);
            Log.d("PlanViewActivity", "Excel file parsed successfully"); // 로그 추가

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 여행 일수 RecyclerView 설정
        daysRecyclerView = findViewById(R.id.daysRecyclerView);
//        daysList = new ArrayList<>();
//        // 예시로 7일치 데이터를 추가합니다.
//        for (int i = 1; i <= 7; i++) {
//            daysList.add(i + "일차");
//        }
//
//        daysAdapter = new DaysAdapter(daysList, position -> {
//            // 해당 일차의 여행 장소를 업데이트
//            updatePlacesList(position);
//        });
        daysRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        daysRecyclerView.setAdapter(daysAdapter);

        // 중앙 정렬을 위한 SnapHelper 추가
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(daysRecyclerView);

        // 여행 장소 RecyclerView 설정
        placesRecyclerView = findViewById(R.id.placesRecyclerView);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // 예시로 각 일차별 장소 데이터를 추가합니다.
//        placesMap = new HashMap<>();
//        for (int i = 1; i <= 7; i++) {
//            List<Place> placesList = new ArrayList<>();
//            placesList.add(new Place("장소 " + i + "-1", "주소 " + i + "-1"));
//            placesList.add(new Place("장소 " + i + "-2", "주소 " + i + "-2"));
//            placesList.add(new Place("장소 " + i + "-3", "주소 " + i + "-3"));
//            placesMap.put(i - 1, placesList);
//        }

        // 처음에 1일차의 장소를 표시
//        updatePlacesList(0); // 1일차 데이터를 로드




        edit = findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditing = !isEditing;  // 상태를 반전시킴

                // 현재 보이는 첫 번째 항목의 위치를 가져옴
                int currentDayPosition = ((LinearLayoutManager) daysRecyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();

                // 어댑터에 isEditing 상태 전달
                placesEditAdapter.setEditing(isEditing);

                if (isEditing) {
                    // Edit 모드로 전환
                    edit.setImageResource(R.drawable.save); // 완료 버튼 이미지로 변경
                } else {
                    // 수정 완료 후 원래 상태로 복귀
                    edit.setImageResource(R.drawable.edit); // 원래 edit 버튼 이미지로 변경
                    saveChanges();
                }

                // 현재 상태에 따라 아이템들을 다시 표시
                placesEditAdapter.notifyDataSetChanged();
            }
        });

    }

    private void saveChanges() {

        // 싱글톤 인스턴스 가져오기
        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();

        String url = "http://13.209.33.141:5000/update_itinerary";
//            String data = "{ \"user_id\" : \""+userId+"\", \"tour_name\" : \""+tripPlanDetailList.get(0).getTripName()+"\" , \"planDate\" : \""+tripPlanDetailList.get(0).getPlanDate()+"\", \"schedules\" : [{\""+tripPlanDetailList+"\"}] }";

        // JSON 문자열을 구성하기 위한 StringBuilder 사용
        StringBuilder data = new StringBuilder();

        data.append("{");
        data.append("\"user_id\":\"").append(userId).append("\",");
        data.append("\"tour_name\":\"").append(tripTitleEditText.getText()).append("\",");
        data.append("\"planDate\":\"").append(tripPlanDetailList.get(0).getPlanDate()).append("\",");
        data.append("\"schedules\":[");

// 각 TripPlanDetail 객체를 순회하며 JSON 형식으로 추가
        for (int i = 0; i < tripPlanDetailList.size(); i++) {
            TripPlanDetail detail = tripPlanDetailList.get(i);

            data.append("{");
            data.append("\"address\":\"").append(detail.getAddress()).append("\",");
            data.append("\"day\":\"").append(detail.getDay()).append("\",");
            data.append("\"latitude\":").append(detail.getLatitude()).append(",");
            data.append("\"location\":\"").append(detail.getLocation()).append("\",");
            data.append("\"longitude\":").append(detail.getLongitude());
            data.append("}");

            // 마지막 객체가 아닌 경우 쉼표 추가
            if (i < tripPlanDetailList.size() - 1) {
                data.append(",");
            }
        }

        data.append("]}");

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

        Log.d("PlanEditActivity", "saveChanges executed");
    }

    private void updatePlacesList(int day) {
        List<Place> placesList = placesMap.get(day);
        placesEditAdapter = new PlacesEditAdapter(placesList);
        placesEditAdapter = new PlacesEditAdapter(placesList, updatedPlacesList -> {
            // 데이터 변경 리스너 호출
            updateTripPlanDetailList(day, updatedPlacesList);
        });
        placesRecyclerView.setAdapter(placesEditAdapter);

        ItemMoveCallback callback = new ItemMoveCallback(placesEditAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(placesRecyclerView);

        // 어댑터에 현재 editing 상태 전달
        placesEditAdapter.setEditing(isEditing);
        placesEditAdapter.notifyDataSetChanged();
    }
//    private void updateTripPlanDetailList(int day, List<Place> updatedPlacesList) {
//        // tripPlanDetailList를 업데이트합니다.
//
//        Log.d("updateTripPlanDetailList", "updateTripPlanDetailList executed");
//        for (int i = 0; i < tripPlanDetailList.size(); i++) {
//            TripPlanDetail detail = tripPlanDetailList.get(i);
//            if (detail.getDay() == day) {
//                // 해당 일차의 장소를 업데이트합니다.
//                Place place = updatedPlacesList.get(i); // 어댑터의 리스트와 인덱스를 맞추세요.
//                detail.setLocation(place.getPlaceName());
//                detail.setAddress(place.getAddress());
//                detail.setLatitude(place.getLatitude());
//                detail.setLongitude(place.getLongitude());
//            }
//        }
//
//        Log.d("updateTripPlanDetailList", String.valueOf(updatedPlacesList));
//    }

    private void updateTripPlanDetailList(int day, List<Place> updatedPlacesList) {
        Log.d("updateTripPlanDetailList", "updateTripPlanDetailList executed");

        // 입력된 데이터 상태 로그
        Log.d("updateTripPlanDetailList", "Day: " + day);
        Log.d("updateTripPlanDetailList", "Updated Places List Size: " + updatedPlacesList.size());
        for (int j = 0; j < updatedPlacesList.size(); j++) {
            Place place = updatedPlacesList.get(j);
            Log.d("updateTripPlanDetailList", "Updated Place " + j + ": " + place.getPlaceName() + ", " + place.getAddress() + ", Lat: " + place.getLatitude() + ", Lon: " + place.getLongitude());
        }

        // 기존 tripPlanDetailList 상태 로그
        Log.d("updateTripPlanDetailList", "Original TripPlanDetailList Size: " + tripPlanDetailList.size());
        for (int i = 0; i < tripPlanDetailList.size(); i++) {
            TripPlanDetail detail = tripPlanDetailList.get(i);
            Log.d("updateTripPlanDetailList", "TripPlanDetail " + i + ": Day " + detail.getDay() + ", Location: " + detail.getLocation() + ", Address: " + detail.getAddress() + ", Lat: " + detail.getLatitude() + ", Lon: " + detail.getLongitude());
        }

        // 업데이트 과정: day가 일치하는 곳만 업데이트
        int updateIndex = 0;
        for (int i = 0; i < tripPlanDetailList.size(); i++) {
            TripPlanDetail detail = tripPlanDetailList.get(i);
            if (detail.getDay() == day && updateIndex < updatedPlacesList.size()) {
                Place place = updatedPlacesList.get(updateIndex);
                Log.d("updateTripPlanDetailList", "Updating TripPlanDetail for Day: " + day + ", Index: " + i);
                Log.d("updateTripPlanDetailList", "Before Update - Location: " + detail.getLocation() + ", Address: " + detail.getAddress() + ", Lat: " + detail.getLatitude() + ", Lon: " + detail.getLongitude());

                detail.setLocation(place.getPlaceName());
                detail.setAddress(place.getAddress());
                detail.setLatitude(place.getLatitude());
                detail.setLongitude(place.getLongitude());

                Log.d("updateTripPlanDetailList", "After Update - Location: " + detail.getLocation() + ", Address: " + detail.getAddress() + ", Lat: " + detail.getLatitude() + ", Lon: " + detail.getLongitude());

                updateIndex++;  // 다음 updatedPlacesList의 항목으로 이동
            }
        }

        // 최종 결과 로그
        Log.d("updateTripPlanDetailList", "Final TripPlanDetailList State:");
        for (int i = 0; i < tripPlanDetailList.size(); i++) {
            TripPlanDetail detail = tripPlanDetailList.get(i);
            Log.d("updateTripPlanDetailList", "TripPlanDetail " + i + ": Day " + detail.getDay() + ", Location: " + detail.getLocation() + ", Address: " + detail.getAddress() + ", Lat: " + detail.getLatitude() + ", Lon: " + detail.getLongitude());
        }
    }



    private void displaySchedule(ArrayList<TripPlanDetail> tripPlanDetailList) {
        // 일정 정보를 화면에 표시하는 로직을 여기에 구현합니다.
        setDataWithTripDetailList(tripPlanDetailList);

        tripTitleEditText.setText(tripPlanDetailList.get(0).getTripName());

        daysList = new ArrayList<>();

        for (int i = 1; i <= day; i++) {
            daysList.add(i + "일차");
        }

        daysAdapter = new DaysAdapter(daysList, position -> {
            // 해당 일차의 여행 장소를 업데이트
            updatePlacesList(position);
        });
        daysRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        daysRecyclerView.setAdapter(daysAdapter);

        // 중앙 정렬을 위한 SnapHelper 추가
        LinearSnapHelper snapHelper = new LinearSnapHelper();

        if (daysRecyclerView.getOnFlingListener() != null) {
            daysRecyclerView.setOnFlingListener(null);
        }

        snapHelper.attachToRecyclerView(daysRecyclerView);

        // 여행 장소 RecyclerView 설정

        // 처음에 1일차의 장소를 표시
        updatePlacesList(0); // 1일차 데이터를 로드

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }
            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                Log.e("개같이 멸망", "다시");
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {

                map = kakaoMap;
                if (labelLayer != null) {
                    labelLayer = null; //라벨 전부 제거..
                }
                labelLayer = kakaoMap.getLabelManager().getLayer();

                labelLayer.removeAll();

                // 인증 후 API가 정상적으로 실행될 때 호출됨
                LabelStyles styles = kakaoMap.getLabelManager()
                        .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker)));
                LabelOptions options = LabelOptions.from(LatLng.from(37.394660, 127.111182))
                        .setStyles(styles);
                LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                Label label = layer.addLabel(options);


                //핀 사이 선으로 표시
                kakaoMap.getRouteLineManager();
                RouteLineLayer routelayer = kakaoMap.getRouteLineManager().getLayer();

                RouteLineStylesSet stylesSet = RouteLineStylesSet.from("blueStyles",
                        RouteLineStyles.from(RouteLineStyle.from(10, Color.BLUE)));
                RouteLineSegment segment = RouteLineSegment.from(Arrays.asList(
                                LatLng.from(37.394660, 127.111182),
                                LatLng.from(37.5642135, 127.0016985)))
                        .setStyles(stylesSet.getStyles(0));
                RouteLineOptions routeoptions = RouteLineOptions.from(segment)
                        .setStylesSet(stylesSet);
                RouteLine routeLine = routelayer.addRouteLine(routeoptions);

//                setMapPlaces(locationArrayList, kakaoMap);

                if (locationArrayList.isEmpty()) {
                    Log.d("locationArrayList", "locationArrayList is null when kakaomap is ready");
                } else {
                    Log.d("locationArrayList", "locationArrayList is not null when kakaomap is ready");

                }

                for (int i=0; i<locationArrayList.size(); i++) {

                    Log.d("locationArrayList", "locationArrayList (" + i + ") - " + locationArrayList.get(i).getName());

                    options = LabelOptions.from(LatLng.from(locationArrayList.get(i).getLatitude(), locationArrayList.get(i).getLongitude()))
                            .setStyles(styles);
//                    labelLayer.addLabel(options);
//
//                    Log.d("setMapPlaces", "label added : " + locationArrayList.get(i).getName());
//                    if (labelLayer != null) {
//                        Log.d("setMapPlaces", "LabelLayer is not null.");
//                    } else {
//                        Log.e("setMapPlaces", "LabelLayer is null.");
//                    }

                    if (labelLayer != null) {
                        Log.d("locationArrayList", "check : " + locationArrayList.get(i).getLatitude());

                        labelLayer.addLabel(options);
                        Log.d("setMapPlaces", "label added : " + locationArrayList.get(i).getName());
                    } else {
                        Log.e("setMapPlaces", "LabelLayer is null.");
                    }

                    if (i > 0) {
                        kakaoMap.getRouteLineManager();

                        stylesSet = RouteLineStylesSet.from("blueStyles",
                                RouteLineStyles.from(RouteLineStyle.from(10, Color.BLUE)));


                        segment = RouteLineSegment.from(Arrays.asList(
                                        LatLng.from(locationArrayList.get(i-1).getLatitude(), locationArrayList.get(i-1).getLongitude()),
                                        LatLng.from(locationArrayList.get(i).getLatitude(), locationArrayList.get(i).getLongitude())))
                                .setStyles(stylesSet.getStyles(0));
                        routeoptions = RouteLineOptions.from(segment)
                                .setStylesSet(stylesSet);
                        routelayer.addRouteLine(routeoptions);
                    }
                }

//                if (locationArrayList.size() > 0) {
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(locationArrayList.get(0).getLatitude(), locationArrayList.get(0).getLongitude()));
//                    kakaoMap.moveCamera(cameraUpdate);
//
//                    Log.d("locationArrayList", "camera moved");
//                }
            }@Override
            public LatLng getPosition() {
                return LatLng.from(locationArrayList.get(0).getLatitude(), locationArrayList.get(0).getLongitude());
            }

            @Override
            public int getZoomLevel() {
                return 11;
            }
        });
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

    private void setDataWithTripDetailList(ArrayList<TripPlanDetail> tripPlanDetailList) {
        int idx = 0;
        placesMap = new HashMap<>();
        locationArrayList.clear();
        locationArrayList = new ArrayList<>();

        if (tripPlanDetailList != null) {
            for (TripPlanDetail detail : tripPlanDetailList) {

                day = detail.getDay();
                String location = detail.getLocation();
                String address = detail.getAddress();
                double longitude = detail.getLatitude();
                double latitude = detail.getLongitude();

                Log.d("day", String.valueOf(day));
                Log.d("location", location);
                Log.d("address", address);
                Log.d("latitude", String.valueOf(latitude));
                Log.d("longitude", String.valueOf(latitude));

//                locationArrayList.clear();

//

                locationArrayList.add(idx, new Location(location, latitude, longitude));
//                locationList.add(idx, new Location(location, latitude, longitude));

//                TextView placeName = findViewById(R.id.placeName);
//                placeName.setText(location);
                idx++;
            }

//            int lastIdx = 0;
//            for (int i = 1; i <= day; i++) {
//                List<Place> placesList = new ArrayList<>();
//                for(int index = lastIdx; index < tripPlanDetailList.size(); index++) {
//                    Log.d("PlanViewActivity", "index=" + index);
//                    Log.d("PlanViewActivity", String.valueOf(tripPlanDetailList.get(index).getDay()) + " : " + index);
//                    if (tripPlanDetailList.get(index).getDay() == i) {
//                        placesList.add(new Place(tripPlanDetailList.get(index).getLocation(), tripPlanDetailList.get(index).getAddress()));
//                        Log.d("PlanViewActivity", placesList.get(index).getPlaceName());
//                    } else {
//                        lastIdx = index-1;
//                        break;
//                    }
//                }
//                placesMap.put(i-1, placesList);
//            }

            int lastIdx = 0;
            for (int i = 1; i <= day; i++) {
                List<Place> placesList = new ArrayList<>();
                for (int index = lastIdx; index < tripPlanDetailList.size(); index++) {
                    Log.d("PlanViewActivity", "index=" + index);
                    Log.d("PlanViewActivity", String.valueOf(tripPlanDetailList.get(index).getDay()) + " : " + index);
                    if (tripPlanDetailList.get(index).getDay() == i) {
                        placesList.add(new Place(tripPlanDetailList.get(index).getLocation(), tripPlanDetailList.get(index).getAddress(), tripPlanDetailList.get(index).getLongitude(), tripPlanDetailList.get(index).getLatitude()));
                        Log.d("PlanViewActivity", tripPlanDetailList.get(index).getLocation());
                        Log.d("PlanViewActivity", String.valueOf(tripPlanDetailList.get(index).getLatitude()));
                        Log.d("PlanViewActivity", String.valueOf(tripPlanDetailList.get(index).getLongitude()));
                    } else {
                        lastIdx = index;
                        break;
                    }
                }
                placesMap.put(i-1, placesList); // i-1이 아니라 i를 사용하여 키를 맞추세요.
            }

// 만약 마지막 날에 남아 있는 장소가 있을 수 있으니, 마지막으로 `lastIdx`가 끝난 후 확인합니다.
            if (lastIdx < tripPlanDetailList.size()) {
                List<Place> remainingPlacesList = new ArrayList<>();
                for (int index = lastIdx; index < tripPlanDetailList.size(); index++) {
                    if (tripPlanDetailList.get(index).getDay() == day) {
                        remainingPlacesList.add(new Place(tripPlanDetailList.get(index).getLocation(), tripPlanDetailList.get(index).getAddress(), tripPlanDetailList.get(index).getLongitude(), tripPlanDetailList.get(index).getLatitude()));
                        Log.d("PlanViewActivity", tripPlanDetailList.get(index).getLocation());
                        Log.d("PlanViewActivity", String.valueOf(tripPlanDetailList.get(index).getLatitude()));
                        Log.d("PlanViewActivity", String.valueOf(tripPlanDetailList.get(index).getLongitude()));
                    }
                }
                if (!remainingPlacesList.isEmpty()) {
                    placesMap.put(day, remainingPlacesList);
                }
            }

        } else {
            Log.d("PlanViewActivity", "tripPlanDetailList null");
        }
    }

    private class ServerCommunicationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SuspiciousIndentation")
        @Override
        protected Void doInBackground(Void... voids) {
            // 서버와 통신
            result = httpPostBodyConnection(url, jsonData);
            handler.post(() -> {seeNetworkResult(result);
                if(result != null && !result.isEmpty())
                    tripPlanDetailList = parseTripPlanDetail(result);
            });// 실제 서버 통신 코드로 대체
            Log.d("함수 내 주소", url);
            Log.d("보낸 데이터 확인", jsonData);
            return null;
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
            Log.d("진짜 받은 거", returnData.toString());

            return returnData; // 네트워크 요청 결과를 반환
        }
        public void seeNetworkResult(String result) {
            // 네트워크 작업 완료 후
//            displaySchedule(newTripPlanDetailList);
//
//            savedTripPlans.add(newTripPlanDetailList);
//            rerollCount++;

            Log.d(result, "network");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            displaySchedule(tripPlanDetailList);

        }
    }
    public ArrayList<TripPlanDetail> parseTripPlanDetail(String json) {
        ArrayList<TripPlanDetail> TripPlanDetailList = new ArrayList<>();

        try {
            // JSON 전체 객체를 먼저 파싱합니다.
            JSONObject jsonObject = new JSONObject(json);

            // "details" 키에 있는 JSON 배열을 추출합니다.
            JSONArray detailsArray = jsonObject.getJSONArray("details");

            for (int i = 0; i < detailsArray.length(); i++) {
                JSONObject detailObject = detailsArray.getJSONObject(i);

                int day = 0;
                String location = null;
                String address = null;
                double latitude = 0.0;
                double longitude = 0.0;

                if (detailObject.has("day")) {
                    day = detailObject.getInt("day");
                    Log.d("몇일차", String.valueOf(day));
                }

                if (detailObject.has("location")) {
                    location = detailObject.getString("location");
                    Log.d("장소", location);
                }

                if (detailObject.has("address")) {
                    address = detailObject.getString("address");
                    Log.d("주소", address);
                }

                if (detailObject.has("latitude")) {
                    latitude = detailObject.getDouble("latitude");
                    Log.d("위도", String.valueOf(latitude));
                }

                if (detailObject.has("longitude")) {
                    longitude = detailObject.getDouble("longitude");
                    Log.d("경도", String.valueOf(longitude));
                }

                // TripPlanDetail 객체를 생성하고 리스트에 추가합니다.
                if (day != 0 && location != null && address != null && latitude != 0 && longitude != 0) {
                    TripPlanDetail tripPlanDetail;
                    if (departureDocument == null) {
                        tripPlanDetail = new TripPlanDetail(tour_name, "출발지역 없음", day, planDate, location, address, latitude, longitude);
                    } else {
                        tripPlanDetail = new TripPlanDetail(tour_name, departureDocument.getPlaceName(), day, planDate, location, address, latitude, longitude);
                    }
                    TripPlanDetailList.add(tripPlanDetail);
                } else {
                    Log.e("JSONError", "Missing key in JSON object: " + detailObject.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return TripPlanDetailList;
    }



}
