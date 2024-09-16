package com.tourbus.tourrand;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;
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
import org.w3c.dom.Text;

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
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public class PlanViewActivity extends AppCompatActivity {
    private LabelLayer labelLayer;
    String getData;

    private RecyclerView daysRecyclerView;
    private RecyclerView placesRecyclerView;
    private DaysAdapter daysAdapter;
    private PlacesAdapter placesAdapter;
    private List<String> daysList;
    private Map<Integer, List<Place>> placesMap;
    private Button saveBut, rerollBut;
    private KakaoMap map;
    private MapView mapView;
    private LabelManager labelManager;

    private ExcelParser excelParser;
    private GeocodingUtils geocodingUtils;

    private ImageView scheduleList;
    private TextView navigateTextView;

    private static final int MAX_REROLL_COUNT = 4;
    private ArrayList<ArrayList<TripPlanDetail>> savedTripPlans = new ArrayList<>();
    private int rerollCount = 1;
    String theme, getTheme;
    String destination;
    boolean withAnimal;
    private static final String[] THEMES = {"레저", "역사", "캠핑", "문화", "자연", "힐링", "생태관광", "쇼핑"};

    private ApiService apiService;

    public interface ApiService {
        @GET("getRandomSchedule")
        Call<ScheduleResponse> getRandomSchedule();
    }

    // ScheduleResponse 클래스 정의
    public class ScheduleResponse {
        public String destination;
        public Map<Integer, List<Place>> placesMap;
    }
    private ArrayList<TripPlanDetail> tripPlanDetailList;
    private ArrayList<Location> locationArrayList = new ArrayList<>();
    private Handler handler;
    private String result,data;
    int day;
    private String url;
    public ArrayList<TripPlanDetail> newTripPlanDetailList;
    String selectedLocation;
    int tripLength;
    String mainTheme;
    Place departureDocument;
    TextView semiTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_view);

        handler = new Handler();
        KakaoMapSdk.init(this, "e211572ac7a98da2054d8a998e86a28a");

        Intent intent = getIntent();
        tripPlanDetailList = getIntent().getParcelableArrayListExtra("TripPlanDetailList");
        getTheme = getIntent().getStringExtra("mainTheme");
        updateThemeText(getTheme);
        selectedLocation = getIntent().getStringExtra("selectedLocation");
        String tour_name = selectedLocation + getTheme + "여행";
        tripLength = getIntent().getIntExtra("tripLength", 1);

        withAnimal = getIntent().getBooleanExtra("withAnimal",withAnimal);

        Log.d("반려동물동반여부", String.valueOf(withAnimal));
        semiTheme = findViewById(R.id.themaSemiText);

        setThemeText(getTheme, semiTheme);

        mapView = findViewById(R.id.map);
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }
            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                Log.e("개같이 멸망", "다시");
                Log.e("개같이 멸망", "error: " + error);
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {

                map = kakaoMap;
                labelLayer = kakaoMap.getLabelManager().getLayer();

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

                    Log.d("locationArrayList", "locationArrayList (i) - " + locationArrayList.get(i).getName());

                    options = LabelOptions.from(LatLng.from(locationArrayList.get(i).getLongitude(), locationArrayList.get(i).getLatitude()))
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
                                        LatLng.from(locationArrayList.get(i-1).getLongitude(), locationArrayList.get(i-1).getLatitude()),
                                        LatLng.from(locationArrayList.get(i).getLongitude(), locationArrayList.get(i).getLatitude())))
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
                return LatLng.from(locationArrayList.get(0).getLongitude(), locationArrayList.get(0).getLatitude());
            }

            @Override
            public int getZoomLevel() {
                return 11;
            }
        });

        //000000
        setDataWithTripDetailList(tripPlanDetailList);

        savedTripPlans.add(tripPlanDetailList);

        String result = intent.getParcelableExtra("result");

        //semiTheme.setText(result.toString());

        String previousActivity = intent.getStringExtra("previousActivity");
        Log.d("previousActivity", previousActivity);

        if ("CustomRouletteActivity".equals(previousActivity)) {
            destination = intent.getStringExtra("selectedLocation");
            // NavigateTextView 숨기기
            navigateTextView = findViewById(R.id.fromSrcToDst);
            navigateTextView.setVisibility(View.GONE);
        } else {
            withAnimal = intent.getBooleanExtra("withAnimal", false);
            departureDocument = intent.getParcelableExtra("departureDocument");
            destination = intent.getStringExtra("selectedLocation");

            if (withAnimal) {
                theme = "반려동물";
            } else {
                theme = chooseTheme();
            }

            navigateTextView = findViewById(R.id.fromSrcToDst);
            navigateTextView.setOnClickListener(v -> {
                String startAddress = departureDocument.getAddress();
                ExcelParser.Location endLocation = excelParser.getLocation(destination);

                Log.d("PlanViewActivity", "Start Address: " + startAddress);
                Log.d("PlanViewActivity", "Destination: " + destination);
                Log.d("PlanViewActivity", "End Location: " + (endLocation != null ? endLocation.latitude + ", " + endLocation.longitude : "null"));

                geocodingUtils.geocodeAsync(startAddress).thenAccept(startLocation -> {
                    if (startLocation != null && endLocation != null) {
                        Log.d("PlanViewActivity", "Start Location: " + startLocation.getLatitude() + ", " + startLocation.getLongitude());

                        runOnUiThread(() -> {
                            MapUtils.showRoute(PlanViewActivity.this, startLocation, endLocation);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Log.e("PlanViewActivity", "Geocoding failed or end location is null.");
                        });
                    }
                });
            });
        }

        scheduleList = findViewById(R.id.scheduleList);

        excelParser = new ExcelParser();
        geocodingUtils = new GeocodingUtils();

        // 엑셀 파일 파싱
        try {
            InputStream inputStream = getAssets().open("locations.xlsx");
           // Log.d("PlanViewActivity", "Excel file found and opened"); // 로그 추가

            excelParser.parseExcelFile(inputStream);
            //Log.d("PlanViewActivity", "Excel file parsed successfully"); // 로그 추가

        } catch (Exception e) {
            e.printStackTrace();
        }

        saveBut = findViewById(R.id.saveBut);
        rerollBut = findViewById(R.id.rerollBut);

        // 여행 일수 RecyclerView 설정
        daysRecyclerView = findViewById(R.id.daysRecyclerView);
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
        snapHelper.attachToRecyclerView(daysRecyclerView);

        // 여행 장소 RecyclerView 설정
        placesRecyclerView = findViewById(R.id.placesRecyclerView);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 예시로 각 일차별 장소 데이터를 추가합니다.
//        placesMap = new HashMap<>();
//        for(int index = 0; index < tripPlanDetailList.size(); index++) {
//            if (tripPlanDetailList.get(index).getDay() == index+1) {
//
//            }
//
//        }
//        for (int i = 1; i <= 7; i++) {
//            List<Place> placesList = new ArrayList<>();
//            placesList.add(new Place("장소 " + i + "-1", "주소 " + i + "-1"));
//            placesList.add(new Place("장소 " + i + "-2", "주소 " + i + "-2"));
//            placesList.add(new Place("장소 " + i + "-3", "주소 " + i + "-3"));
//            placesMap.put(i - 1, placesList);
//        }

        // 처음에 1일차의 장소를 표시
        updatePlacesList(0); // 1일차 데이터를 로드

        placesAdapter.setOnItemClickListener(place -> {

            Log.d("placesdapter", "place touched : " + place.getPlaceName());
            for (int i=0; i<tripPlanDetailList.size(); i++) {
                if (place.getPlaceName() != tripPlanDetailList.get(i).getLocation()) {
                    Log.d("placesdapter", "not equal : " + tripPlanDetailList.get(i).getLocation());

                    continue;
                } else {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(tripPlanDetailList.get(i).getLongitude(), tripPlanDetailList.get(i).getLatitude()));
                    map.moveCamera(cameraUpdate);

                    Log.d("placesdapter", "camera moved to " + tripPlanDetailList.get(i).getLocation());
                    break;
                }
            }
        });

        scheduleList.setOnClickListener(v -> showPopupMenu(v));

        rerollBut.setOnClickListener(v -> {
            if (rerollCount < MAX_REROLL_COUNT) {
                rerollCount++;
                if (rerollCount > 3) {
                    rerollCount = 3;
                }
                rerollBut.setText("다시 돌리기 (" + rerollCount + "/" + 3 + ")");
                Log.d("testsibal", String.valueOf(tripPlanDetailList.get(0).getDay()));
                Log.d("testsibal", String.valueOf(tripLength));

                //반려동물이랑 같이 갈 때
                if(withAnimal == true){
                    Log.d("반려동물 동반 여부", "반려동물");
                    url = "https://api.tourrand.com/pet";
                    data = "{\"day\" : \""+tripLength+"\",\"destination\":\"" + selectedLocation+"\" }";; //json 형식 데이터

                } else if (selectedLocation .equals("안산") || selectedLocation.equals("파주") || selectedLocation.equals("광주") || selectedLocation.equals("안양") || selectedLocation.equals("의왕") ||
                        selectedLocation.equals("시흥") || selectedLocation.equals("가평") || selectedLocation.equals("남양주") || selectedLocation.equals("연천") || selectedLocation.equals("평창") ||
                        selectedLocation.equals("속초") || selectedLocation.equals("태백") || selectedLocation.equals("원주") || selectedLocation.equals("양구") || selectedLocation.equals("포천") ||
                        selectedLocation.equals("강릉") || selectedLocation.equals("홍천") || selectedLocation.equals("정선") || selectedLocation.equals("삼척") || selectedLocation.equals("울산") ||
                        selectedLocation.equals("부산") || selectedLocation.equals("대전") || selectedLocation.equals("인천") ||selectedLocation.equals("강원도 고성")|| selectedLocation.equals("서울")  ) {
                    String [] theme = {"레저","역사","문화","자연","힐링","생태관광"}; //생태포함

                    Random random = new Random();
                    int index = random.nextInt(theme.length);
                    mainTheme = theme[index];

                    if(selectedLocation.equals("강원도 고성") && mainTheme.equals("생태관광")){
                        selectedLocation ="강_고성";
                        url = "https://api.tourrand.com/ecotourism";
                        data = "{\"day\" : \""+tripLength+"\",\"destination\":\""+selectedLocation+"\" }";
                    } else if(mainTheme.equals("생태관광")){
                        url = "https://api.tourrand.com/ecotourism";
                        data = "{\"day\" : \""+tripLength+"\",\"destination\":\""+selectedLocation+"\" }";
                    } else {
                        //위 지역임에도 불구하고 생태관광이 안 나왔을 때
                        url = "https://api.tourrand.com/route";
                        data = "{\"day\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";
                    }
                } else if(selectedLocation.equals("경상남도 고성")){
                    selectedLocation = "경_고성";
                    mainTheme = chooseTheme();
                    url = "https://api.tourrand.com/route";
                    data = "{\"day\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";
                }
                else{
                    //반려동물 미포함
                    if (tripLength==2){
                        String [] theme = {"레저","역사","문화","자연","힐링","캠핑"}; //캠핑 포함

                        Random random = new Random();
                        int index = random.nextInt(theme.length);
                        mainTheme = theme[index];

                        if(mainTheme.equals("캠핑")){
                            if(selectedLocation.equals("강원도 고성")){
                                selectedLocation = "강_고성";
                                url = "https://api.tourrand.com/camping";
                                data = "{\"destination\":\""+selectedLocation+"\" }";
                            } else if (selectedLocation.equals("경상남도 고성")) {
                                selectedLocation = "경_고성";
                                url = "https://api.tourrand.com/camping";
                                data = "{\"destination\":\""+selectedLocation+"\" }";
                            } else {
                                //고성 아닌데 캠핑인 것들
                                url = "https://api.tourrand.com/camping";
                                data = "{\"destination\":\""+selectedLocation+"\" }";
                            }
                        } else{
                            //여행 이틀만 가는데 캠핑 안 나왔을 때
                            mainTheme = chooseTheme();
                            url = "https://api.tourrand.com/route";
                            data = "{\"day\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";
                        }
                    }
                    //반려동물 + 캠핑 미포함
                    mainTheme = chooseTheme();
                    url = "https://api.tourrand.com/route";
                    data = "{\"day\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";
                    //data = "{\"planDate\" : \""+tripLength+"\",\"mainTheme\" : \"문화\",\"destination\":\""+selectedLocation+"\" }";

                    Log.d("데이터 보낸 거", data);


                }
                // 서버 통신을 비동기적으로 실행
                new ServerCommunicationTask().execute();

//                if (!withAnimal) {
//                    theme = generateRandomTheme();
//                }
//                rerollSchedule();
                updateThemeText(mainTheme);
            } else {
                Toast.makeText(PlanViewActivity.this, "다시 돌리기 횟수를 초과했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        saveBut.setOnClickListener(v -> {
            // 싱글톤 인스턴스 가져오기
            UserManager userManager = UserManager.getInstance();
            String userId = userManager.getUserId();

            String url = "https://api.tourrand.com/confirmed";
//            String data = "{ \"user_id\" : \""+userId+"\", \"tour_name\" : \""+tripPlanDetailList.get(0).getTripName()+"\" , \"planDate\" : \""+tripPlanDetailList.get(0).getPlanDate()+"\", \"schedules\" : [{\""+tripPlanDetailList+"\"}] }";

            // JSON 문자열을 구성하기 위한 StringBuilder 사용
            StringBuilder data = new StringBuilder();

            data.append("{");
            data.append("\"user_id\":\"").append(userId).append("\",");
            data.append("\"destination\":\"").append(tripPlanDetailList.get(0).getTripName()).append("\",");
            data.append("\"tour_name\":\"").append(tripPlanDetailList.get(0).getTripName() + "여행").append("\",");
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

//            Intent homeIntent = new Intent(PlanViewActivity.this, HomeFragment1.class);
//            startActivity(homeIntent);
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//            finish();

            Intent homeIntent = new Intent(PlanViewActivity.this, HomeActivity.class);
            homeIntent.putExtra("fragmentToLoad", "homeFragment1");
            startActivity(homeIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();

        });
    }

    private void updatePlacesList(int day) {
        List<Place> placesList = placesMap.get(day);
        placesAdapter = new PlacesAdapter(placesList);
        placesRecyclerView.setAdapter(placesAdapter);

    }

//    private void setMapPlaces(ArrayList<Location> locationArrayList, KakaoMap kakaoMap) {
//
//
//        LabelStyles styles = kakaoMap.getLabelManager()
//                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker)));
//
//        for (int i=0; i<locationArrayList.size(); i++) {
//
//            LabelOptions options = LabelOptions.from(LatLng.from(locationArrayList.get(i).getLatitude(), locationArrayList.get(i).getLongitude()))
//                    .setStyles(styles);
//            labelLayer = kakaoMap.getLabelManager().getLayer();
//            labelLayer.addLabel(options);
//
//            Log.d("setMapPlaces", "label added : " + locationArrayList.get(i).getName());
//            if (labelLayer != null) {
//                Log.d("setMapPlaces", "LabelLayer is not null.");
//            } else {
//                Log.e("setMapPlaces", "LabelLayer is null.");
//            }
//
//            if (i > 0) {
//                kakaoMap.getRouteLineManager();
//                RouteLineLayer routelayer = kakaoMap.getRouteLineManager().getLayer();
//
//                RouteLineStylesSet stylesSet = RouteLineStylesSet.from("blueStyles",
//                        RouteLineStyles.from(RouteLineStyle.from(10, Color.BLUE)));
//                RouteLineSegment segment = RouteLineSegment.from(Arrays.asList(
//                                LatLng.from(locationArrayList.get(i-1).getLatitude(), locationArrayList.get(i-1).getLongitude()),
//                                LatLng.from(locationArrayList.get(i).getLatitude(), locationArrayList.get(i).getLongitude())))
//                        .setStyles(stylesSet.getStyles(0));
//                RouteLineOptions routeoptions = RouteLineOptions.from(segment)
//                        .setStylesSet(stylesSet);
//                RouteLine routeLine = routelayer.addRouteLine(routeoptions);
//            }
//        }
//    }

//    private void rerollSchedule() {
//        // 서버에서 랜덤 일정 데이터 받아오기
//        apiService.getRandomSchedule().enqueue(new Callback<ScheduleResponse>() {
//            @Override
//            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ScheduleResponse scheduleResponse = response.body();
//
//                    // 새로운 일정 저장
//                    String newTheme = withAnimal ? "반려동물" : generateRandomTheme();
//                    Schedule newSchedule = new Schedule(newTheme, scheduleResponse.destination, scheduleResponse.placesMap);
//                    savedTripPlans.add(newSchedule);
//                    rerollCount++;
//
//                    // 새로운 일정 데이터로 업데이트
//                    updatePlanView(newSchedule);
//                } else {
//                    Toast.makeText(PlanViewActivity.this, "일정을 받아오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
//                Toast.makeText(PlanViewActivity.this, "서버와 통신하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private String generateRandomTheme() {
//        Random random = new Random();
//        int index = random.nextInt(THEMES.length);
//        return THEMES[index];
//    }

    public String chooseTheme(){
        String [] theme = {"레저","역사","문화","자연","힐링"}; //캠핑, 생태관광, 반려동물 미포함

        Random random = new Random();
        int index = random.nextInt(theme.length);

        mainTheme = theme[index];



        return mainTheme;
    }

    private void updateThemeText(String theme) {

        if(withAnimal == true)
            theme = "반려동물";

        if(theme == null || theme == "null") {
            theme = "반려동물";
        }
        TextView themaText = findViewById(R.id.themaText);
        themaText.setText("이번 여행의 테마는 " + theme + "입니다!");
        setThemeText(theme, semiTheme);
    }

//    private void updatePlanView(Schedule schedule) {
//        // 테마와 목적지를 업데이트
//        TextView themaText = findViewById(R.id.themaText);
//        themaText.setText("이번 여행의 테마는 " + schedule.theme + "입니다!");
//
//
//        // 목적지를 업데이트
//        this.destination = schedule.destination;
//
//        // 여행 장소를 업데이트
//        this.placesMap = new HashMap<>(schedule.placesMap);
//        updatePlacesList(0); // 첫 번째 일차 데이터를 로드
//    }

    private void setDataWithTripDetailList(ArrayList<TripPlanDetail> tripPlanDetailList) {
        int idx = 0;
        TextView themaText = findViewById(R.id.themaText);
        if(themaText.getText() == "이번 여행의 테마는 null입니다!") {
            themaText.setText("이번 여행의 테마는 " + tripPlanDetailList.get(0).getTheme() + "입니다!");
        }
        setThemeText(theme, semiTheme);

        placesMap = new HashMap<>();
        locationArrayList.clear();
        locationArrayList = new ArrayList<>();

        if (tripPlanDetailList != null) {
            for (TripPlanDetail detail : tripPlanDetailList) {

                day = detail.getDay();
                String location = detail.getLocation();
                String address = detail.getAddress();
                double latitude = detail.getLatitude();
                double longitude = detail.getLongitude();

                Log.d("day", String.valueOf(day));
                Log.d("location", location);
                Log.d("address", address);
                Log.d("latitude", String.valueOf(longitude));
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

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.schedule_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.schedule1) {
            showSchedule(0); // 첫 번째 저장된 일정을 로드
            return true;
        } else if (id == R.id.schedule2) {
            showSchedule(1); // 두 번째 저장된 일정을 로드
            return true;
        } else if (id == R.id.schedule3) {
            showSchedule(2); // 세 번째 저장된 일정을 로드
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSchedule(int index) {
        if (index < savedTripPlans.size()) {
            ArrayList<TripPlanDetail> TripPlanDetailList = savedTripPlans.get(index);

            displaySchedule(TripPlanDetailList);
        } else {
            Toast.makeText(this, "해당 일정이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displaySchedule(ArrayList<TripPlanDetail> tripPlanDetailList) {
        // 일정 정보를 화면에 표시하는 로직을 여기에 구현합니다.
        setDataWithTripDetailList(tripPlanDetailList);
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

                    options = LabelOptions.from(LatLng.from(locationArrayList.get(i).getLongitude(), locationArrayList.get(i).getLatitude()))
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
                                        LatLng.from(locationArrayList.get(i-1).getLongitude(), locationArrayList.get(i-1).getLatitude()),
                                        LatLng.from(locationArrayList.get(i).getLongitude(), locationArrayList.get(i).getLatitude())))
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
                return LatLng.from(locationArrayList.get(0).getLongitude(), locationArrayList.get(0).getLongitude());
            }

            @Override
            public int getZoomLevel() {
                return 11;
            }
        });
    }

    public static class Schedule {
        String theme;
        String destination;
        Map<Integer, List<Place>> placesMap;

        public Schedule(String theme, String destination, Map<Integer, List<Place>> placesMap) {
            this.theme = theme;
            this.destination = destination;
            this.placesMap = new HashMap<>(placesMap); // 깊은 복사
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
    }

    private class ServerCommunicationTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 로딩 다이얼로그 표시
            progressDialog = new ProgressDialog(PlanViewActivity.this);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @SuppressLint("SuspiciousIndentation")
        @Override
        protected Void doInBackground(Void... voids) {
            // 서버와 통신
            result = httpPostBodyConnection(url, data);
            handler.post(() -> {seeNetworkResult(result);
                if(result != null && !result.isEmpty())
                    tripPlanDetailList = parseTripPlanDetail(result);
                    newTripPlanDetailList = parseTripPlanDetail(result);
            });// 실제 서버 통신 코드로 대체
            Log.d("함수 내 주소", url);
            Log.d("보낸 데이터 확인", data);
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
            // 로딩 다이얼로그 종료
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();

                displaySchedule(newTripPlanDetailList);

                savedTripPlans.add(newTripPlanDetailList);
                rerollCount++;
            }
        }
    }
    public ArrayList<TripPlanDetail> parseTripPlanDetail(String json) {
        ArrayList<TripPlanDetail> TripPlanDetailList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int day = 0;
                String location = null;
                String address = null;
                double latitude = 0.0;
                double longitude = 0.0;

                if (jsonObject.has("day")) {
                    day = jsonObject.getInt("day");
                    Log.d("몇일차", String.valueOf(day));
                }

                if (jsonObject.has("location")) {
                    location = jsonObject.getString("location").toString();
                    Log.d("장소", location);
                }

                if (jsonObject.has("address")) {
                    address = jsonObject.getString("address").toString();
                    Log.d("주소", address);
                }
                if (jsonObject.has("latitude")) {
                    latitude = jsonObject.getDouble("latitude");
                    Log.d("위도", String.valueOf(latitude));
                }
                if (jsonObject.has("longitude")) {
                    longitude = jsonObject.getDouble("longitude");
                    Log.d("경도", String.valueOf(longitude));
                }

                TripPlanDetail TripPlanDetail;
                if (day != 0 && location != null && address != null && latitude !=0 && longitude !=0) {
                    if (departureDocument == null) {
                        TripPlanDetail = new TripPlanDetail(selectedLocation, day, tripPlanDetailList.get(0).getPlanDate(), location, address,latitude,longitude);
                    } else {
                        TripPlanDetail = new TripPlanDetail(selectedLocation, day, tripPlanDetailList.get(0).getPlanDate(), location, address,latitude,longitude);
                    }
                    TripPlanDetail.setTheme(mainTheme);
                    TripPlanDetailList.add(TripPlanDetail);

//                    Intent intent = new Intent(DstActivity.this, PlanViewActivity.class);
//                    intent.putParcelableArrayListExtra("TripPlanDetailList", TripPlanDetailList);

                    //Log.d("맞나?", TripPlanDetailList.toString());
                } else {
                    Log.e("JSONError", "Missing key in JSON object: " + jsonObject.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return TripPlanDetailList;
    }

    private void setThemeText(String getTheme, TextView semiTheme) {

        semiTheme = findViewById(R.id.themaSemiText);

        if(getTheme == null) {
            Log.d("setThemeText", "getTheme is null");
            semiTheme.setText("단조로운 일상에서 벗어나 투어랜드와 함꼐 색다른 여행을 떠나보세요!");
            return;
        } else if (semiTheme == null) {
            Log.d("setThemeText", "semiTheme is null");
        }

        switch (getTheme){
            case "힐링":
                semiTheme.setText("이번 여행은 마음을 편안하게 만들어줄 것입니다. "+selectedLocation+"의 푸르른 자연과 아름다운 풍경을 만끽하며 즐거운 여행을 떠나보세요!");
                break;
            case "레저":
                semiTheme.setText("굳어있던 몸을 움직일 시간입니다😄 다양한 액티비티를 즐기며, 몸과 마음을 재충전해보세요!");
                break;
            case "역사":
                semiTheme.setText("역사를 잊은 민족에게 미래란 없다! 과거의 이야기가 숨 쉬는 이곳에서, 역사의 발자취를 따라 여행하며 시간을 거슬러 올라가 보세요.");
                break;
            case "문화":
                semiTheme.setText("다채로운 문화가 어우러진 "+selectedLocation+"에서, 지역 특유의 전통과 예술을 깊이 있게 체험해보세요.");
                break;
            case "자연":
                semiTheme.setText("일상의 번잡함을 내려놓고 마음껏 자연의 품에 안겨보세요. 맑은 공기와 푸른 경관이 선사하는 평온함을 만끽할 수 있습니다.");
                break;
            case "생태관광":
                semiTheme.setText("청정 자연을 보호하며 즐길수 있는 생태관광! \n환경을 생각하는 여행으로 지구와 함께 숨 쉬어보세요");
                break;
            case "캠핑":
                semiTheme.setText("별빛 가득한 하늘 아래 캠핑을 즐기며, 자연 속에서 소박한 행복을 만끽해보세요.");
                break;
            case "반려동물":
                semiTheme.setText("인생의 동반자인 반려동물과 즐거운 여행을 떠나보세요. 몸과 마음을 리프레쉬 할 수 있는 즐거운 경험이 될 것 입니다.");
                break;
            default:
                semiTheme.setText("단조로운 일상에서 벗어나 투어랜드와 함꼐 색다른 여행을 떠나보세요!");
                break;
        }
    }
}
