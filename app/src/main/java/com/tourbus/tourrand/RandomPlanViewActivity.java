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
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
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

import retrofit2.Call;
import retrofit2.http.GET;

public class RandomPlanViewActivity extends AppCompatActivity {
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

    private ExcelParser excelParser;
    private GeocodingUtils geocodingUtils;

    private ImageView scheduleList;
    private TextView navigateTextView;

    private static final int MAX_REROLL_COUNT = 4;
    private ArrayList<ArrayList<TripPlanDetail>> savedTripPlans = new ArrayList<>();
    private int rerollCount = 1;
    String destination;
    boolean withAnimal;

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
    int tripLength;
    Place departureDocument;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_plan_view);

        handler = new Handler();
        KakaoMapSdk.init(this, "e211572ac7a98da2054d8a998e86a28a");

        Intent intent = getIntent();
        tripPlanDetailList = getIntent().getParcelableArrayListExtra("TripPlanDetailList");
        tripLength = getIntent().getIntExtra("tripLength", 1);

        withAnimal = getIntent().getBooleanExtra("withAnimal",withAnimal);

        if(tripLength > 10 || withAnimal == true) {
            ConstraintLayout constraintLayout = findViewById(R.id.nono);

            constraintLayout.setVisibility(View.VISIBLE);

            TextView toMainTextView = findViewById(R.id.toMain);

            toMainTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RandomPlanViewActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            });

        }
        if (tripPlanDetailList != null) {
            updateThemeText(tripPlanDetailList.get(0).getTripName());

        }
        Log.d("반려동물동반여부", String.valueOf(withAnimal));

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
                LabelOptions options;
                LabelLayer layer = kakaoMap.getLabelManager().getLayer();


                //핀 사이 선으로 표시
                kakaoMap.getRouteLineManager();
                RouteLineLayer routelayer = kakaoMap.getRouteLineManager().getLayer();

                RouteLineStylesSet stylesSet = RouteLineStylesSet.from("blueStyles",
                        RouteLineStyles.from(RouteLineStyle.from(10, Color.BLUE)));

                RouteLineSegment segment;
                RouteLineOptions routeoptions;
//                setMapPlaces(locationArrayList, kakaoMap);

                if (locationArrayList.isEmpty()) {
                    Log.d("locationArrayList", "locationArrayList is null when kakaomap is ready");
                } else {
                    Log.d("locationArrayList", "locationArrayList is not null when kakaomap is ready");

                }

                for (int i=0; i<locationArrayList.size(); i++) {

                    Log.d("locationArrayList", "locationArrayList (i) - " + locationArrayList.get(i).getName());

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

        //000000
        setDataWithTripDetailList(tripPlanDetailList);

        savedTripPlans.add(tripPlanDetailList);

        String result = intent.getParcelableExtra("result");

        String previousActivity = intent.getStringExtra("previousActivity");
        Log.d("previousActivity", previousActivity);

        scheduleList = findViewById(R.id.scheduleList);

        excelParser = new ExcelParser();
        geocodingUtils = new GeocodingUtils();

//        // 엑셀 파일 파싱
//        try {
//            InputStream inputStream = getAssets().open("locations.xlsx");
//            // Log.d("PlanViewActivity", "Excel file found and opened"); // 로그 추가
//
//            excelParser.parseExcelFile(inputStream);
//            //Log.d("PlanViewActivity", "Excel file parsed successfully"); // 로그 추가
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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

        // 처음에 1일차의 장소를 표시
        updatePlacesList(0); // 1일차 데이터를 로드

        placesAdapter.setOnItemClickListener(place -> {

            Log.d("placesdapter", "place touched : " + place.getPlaceName());
            for (int i=0; i<tripPlanDetailList.size(); i++) {
                if (place.getPlaceName() != tripPlanDetailList.get(i).getLocation()) {
                    Log.d("placesdapter", "not equal : " + tripPlanDetailList.get(i).getLocation());

                    continue;
                } else {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(tripPlanDetailList.get(i).getLatitude(), tripPlanDetailList.get(i).getLongitude()));
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

                url = "https://api.tourrand.com/second_route";
                data = "{\"day\" : \""+tripLength + "\" }";

                // 서버 통신을 비동기적으로 실행
                new ServerCommunicationTask().execute();

            } else {
                Toast.makeText(RandomPlanViewActivity.this, "다시 돌리기 횟수를 초과했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        saveBut.setOnClickListener(v -> {
            // 싱글톤 인스턴스 가져오기
            UserManager userManager = UserManager.getInstance();
            String userId = userManager.getUserId();

            String url = "https://api.tourrand.com/confirmed";

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

            Intent homeIntent = new Intent(RandomPlanViewActivity.this, HomeActivity.class);
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

    private void updateThemeText(String location) {

        Log.d("updateThemeText", "updateThemeText executed");
        TextView themaText = findViewById(R.id.themaText);
        themaText.setText("안타깝게도 선택한 지역에서 생성할 수 있는 일정이 없어요. 대신 유저님에게 더 적절한 지역을 찾았어요! "+ location + "은 어떠세요?");
        Log.d("updateThemeText내의 setThemeText","실행완: " + location);
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
                        placesList.add(new Place(tripPlanDetailList.get(index).getLocation(), tripPlanDetailList.get(index).getAddress(), tripPlanDetailList.get(index).getLatitude(), tripPlanDetailList.get(index).getLongitude()));
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
                        remainingPlacesList.add(new Place(tripPlanDetailList.get(index).getLocation(), tripPlanDetailList.get(index).getAddress(), tripPlanDetailList.get(index).getLatitude(), tripPlanDetailList.get(index).getLongitude()));
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

            updateThemeText(TripPlanDetailList.get(index).getTheme());
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
//                LabelOptions options = LabelOptions.from(LatLng.from(37.394660, 127.111182))
//                        .setStyles(styles);
                LabelOptions options;
                LabelLayer layer = kakaoMap.getLabelManager().getLayer();
//                Label label = layer.addLabel(options);


                //핀 사이 선으로 표시
                kakaoMap.getRouteLineManager();
                RouteLineLayer routelayer = kakaoMap.getRouteLineManager().getLayer();

                RouteLineStylesSet stylesSet = RouteLineStylesSet.from("blueStyles",
                        RouteLineStyles.from(RouteLineStyle.from(10, Color.BLUE)));
//                RouteLineSegment segment = RouteLineSegment.from(Arrays.asList(
//                                LatLng.from(37.394660, 127.111182),
//                                LatLng.from(37.5642135, 127.0016985)))
//                        .setStyles(stylesSet.getStyles(0));
//                RouteLineOptions routeoptions = RouteLineOptions.from(segment)
//                        .setStylesSet(stylesSet);
//                RouteLine routeLine = routelayer.addRouteLine(routeoptions);

                RouteLineSegment segment;
                RouteLineOptions routeoptions;
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
        Log.d("network", result);
    }

    private class ServerCommunicationTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 로딩 다이얼로그 표시
            progressDialog = new ProgressDialog(RandomPlanViewActivity.this);
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
                if(result != null && !result.isEmpty()) {
//                    tripPlanDetailList = parseTripPlanDetail(result);
                    newTripPlanDetailList = parseTripPlanDetail(result);
                    updateThemeText(destination);

                    displaySchedule(newTripPlanDetailList);

                    savedTripPlans.add(newTripPlanDetailList);
                }

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
            updateThemeText(destination);

            Log.d("network", result);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 로딩 다이얼로그 종료
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                if (newTripPlanDetailList != null)
                    newTripPlanDetailList.get(rerollCount-1).setTheme(destination);

                Log.d("다시돌리기", destination);


                rerollCount++;
            } else {
                new ServerCommunicationTask().execute();
            }
        }
    }
    public ArrayList<TripPlanDetail> parseTripPlanDetail(String json) {
        ArrayList<TripPlanDetail> tripPlanDetailList = new ArrayList<>();

        try {
            // Parse the main JSON object
            JSONObject jsonObject = new JSONObject(json);

            // Extract the "destination" field
            if (jsonObject.has("destination")) {
                destination = jsonObject.getString("destination");
                Log.d("Destination", destination);
            } else {
                Log.e("JSONError", "No destination found in JSON.");
                return tripPlanDetailList; // Exit early if no destination is found
            }

            // Parse the "itinerary" array
            if (jsonObject.has("itinerary")) {
                JSONArray jsonArray = jsonObject.getJSONArray("itinerary");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject itineraryObject = jsonArray.getJSONObject(i);

                    int day = itineraryObject.optInt("day", 0); // Default to 0 if not found
                    String location = itineraryObject.optString("location", null); // Default to null if not found
                    String address = itineraryObject.optString("address", null); // Default to null if not found
                    double latitude = itineraryObject.optDouble("latitude", 0.0); // Default to 0.0 if not found
                    double longitude = itineraryObject.optDouble("longitude", 0.0); // Default to 0.0 if not found

                    // Log values for debugging
                    Log.d("Day", String.valueOf(day));
                    Log.d("Location", location);
                    Log.d("Address", address);
                    Log.d("Latitude", String.valueOf(latitude));
                    Log.d("Longitude", String.valueOf(longitude));

                    // Ensure all necessary data is available before creating the TripPlanDetail object
                    if (day != 0 && location != null && address != null && latitude != 0 && longitude != 0) {
                        // Ensure tripPlanDetailList is not empty and contains at least one valid entry

                        TripPlanDetail tripPlanDetail = new TripPlanDetail(
                                destination,
                                day,
                                savedTripPlans.get(0).get(0).getPlanDate(), // Make sure to handle this correctly
                                location,
                                address,
                                latitude,
                                longitude
                        );
                        tripPlanDetail.setTheme(destination); // Assuming theme is set based on location
                        tripPlanDetailList.add(tripPlanDetail);

                            // Log the TripPlanDetailList for debugging
                        Log.d("TripPlanDetailList", tripPlanDetailList.toString());

                    } else {
                        Log.e("JSONError", "Missing key in JSON object: " + itineraryObject.toString());
                    }
                }
            } else {
                Log.e("JSONError", "No itinerary found in JSON.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException", "Error parsing JSON: " + e.getMessage());
        }

        return tripPlanDetailList;
    }


}
