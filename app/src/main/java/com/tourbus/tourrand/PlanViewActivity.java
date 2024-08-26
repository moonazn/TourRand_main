package com.tourbus.tourrand;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

import org.w3c.dom.Text;

import java.io.InputStream;
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

    private RecyclerView daysRecyclerView;
    private RecyclerView placesRecyclerView;
    private DaysAdapter daysAdapter;
    private PlacesAdapter placesAdapter;
    private List<String> daysList;
    private Map<Integer, List<Place>> placesMap;
    private Button saveBut, rerollBut;
    private KakaoMap kakaoMap;
    private MapView mapView;
    private LabelManager labelManager;

    private ExcelParser excelParser;
    private GeocodingUtils geocodingUtils;

    private ImageView scheduleList;
    private TextView navigateTextView;

    private static final int MAX_REROLL_COUNT = 3;
    private List<Schedule> savedSchedules = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_view);

        KakaoMapSdk.init(this, "d71b70e03d7f7b494a72421fb46cba46");

        Intent intent = getIntent();
        tripPlanDetailList = getIntent().getParcelableArrayListExtra("TripPlanDetailList");
        getTheme = getIntent().getStringExtra("mainTheme");
        updateThemeText(getTheme);


        if (tripPlanDetailList != null) {
            for (TripPlanDetail detail : tripPlanDetailList) {
                int day = detail.getDay();
                String location = detail.getLocation();
                String address = detail.getAddress();
                double latitude = detail.getLatitude();
                double longitude = detail.getLongitude();
                Log.d("day", String.valueOf(day));
                Log.d("location", location);
                Log.d("address", address);
                Log.d("latitude", String.valueOf(latitude));
                Log.d("longitude", String.valueOf(latitude));

                TextView placeName = findViewById(R.id.placeName);
                placeName.setText(location);
                Log.d("변경해라", location);

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
                    }
                }, new KakaoMapReadyCallback() {
                    @Override
                    public void onMapReady(KakaoMap kakaoMap) {
                        // 인증 후 API가 정상적으로 실행될 때 호출됨
                        LabelStyles styles = kakaoMap.getLabelManager()
                                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker)));
                        LabelOptions options = LabelOptions.from(LatLng.from(latitude, longitude))
                                .setStyles(styles);
                        LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                        Label label = layer.addLabel(options);
                        Label centerLabel = layer.addLabel(options);
                        LabelOptions options2 = LabelOptions.from(LatLng.from(latitude, longitude))
                                .setStyles(styles);
                        Label label2 = layer.addLabel(options2);

                        ImageView logo = findViewById(R.id.logo);
//                        logo.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                centerLabel.changeStyles(LabelStyles.from(LabelStyle.from(R.drawable.choonsik)));
//                                centerLabel.moveTo(LatLng.from(latitude,
//                                        longitude), 8000);
//                            }
//                        });
//
//                        //핀 사이 선으로 표시
//                        kakaoMap.getRouteLineManager();
//                        RouteLineLayer routelayer = kakaoMap.getRouteLineManager().getLayer();
//
//                        RouteLineStylesSet stylesSet = RouteLineStylesSet.from("blueStyles",
//                                RouteLineStyles.from(RouteLineStyle.from(10, Color.BLUE)));
//                        RouteLineSegment segment = RouteLineSegment.from(Arrays.asList(
//                                        LatLng.from(latitude, longitude),
//                                        LatLng.from(latitude, longitude)))
//                                .setStyles(stylesSet.getStyles(0));
//                        RouteLineOptions routeoptions = RouteLineOptions.from(segment)
//                                .setStylesSet(stylesSet);
//                        RouteLine routeLine = routelayer.addRouteLine(routeoptions);


                    }
                });

                // 데이터를 사용하여 작업 수행
                // 예: 로그 출력
                System.out.println("Day: " + day + ", Location: " + location + ", Address: " + address + ", Latitude: " + latitude + ", Longitude: " + longitude);
            }
        }

        String result = intent.getParcelableExtra("result");
        TextView semiTheme = findViewById(R.id.themaSemiText);
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
            Place departureDocument = intent.getParcelableExtra("departureDocument");
            destination = intent.getStringExtra("selectedLocation");

            if (withAnimal) {
                theme = "반려동물";
            } else {
                theme = generateRandomTheme();
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
//        mapView = findViewById(R.id.map);
//        mapView.start(new MapLifeCycleCallback() {
//            @Override
//            public void onMapDestroy() {
//                // 지도 API가 정상적으로 종료될 때 호출됨
//            }
//            @Override
//            public void onMapError(Exception error) {
//                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
//                Log.e("개같이 멸망", "다시");
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
        // 예시로 7일치 데이터를 추가합니다.
        for (int i = 1; i <= 7; i++) {
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
        placesMap = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            List<Place> placesList = new ArrayList<>();
            placesList.add(new Place("장소 " + i + "-1", "주소 " + i + "-1"));
            placesList.add(new Place("장소 " + i + "-2", "주소 " + i + "-2"));
            placesList.add(new Place("장소 " + i + "-3", "주소 " + i + "-3"));
            placesMap.put(i - 1, placesList);
        }

        // 처음에 1일차의 장소를 표시
        updatePlacesList(0); // 1일차 데이터를 로드

        scheduleList.setOnClickListener(v -> showPopupMenu(v));

        rerollBut.setOnClickListener(v -> {
            if (rerollCount < MAX_REROLL_COUNT) {
                rerollCount++;
                rerollBut.setText("다시 돌리기 (" + rerollCount + "/" + MAX_REROLL_COUNT);
                if (!withAnimal) {
                    theme = generateRandomTheme();
                }
//                rerollSchedule();
                updateThemeText(theme);
            } else {
                Toast.makeText(PlanViewActivity.this, "다시 돌리기 횟수를 초과했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        saveBut.setOnClickListener(v -> {
            Intent homeIntent = new Intent(PlanViewActivity.this, HomeFragment1.class);
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

    private void rerollSchedule() {
        // 서버에서 랜덤 일정 데이터 받아오기
        apiService.getRandomSchedule().enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ScheduleResponse scheduleResponse = response.body();

                    // 새로운 일정 저장
                    String newTheme = withAnimal ? "반려동물" : generateRandomTheme();
                    Schedule newSchedule = new Schedule(newTheme, scheduleResponse.destination, scheduleResponse.placesMap);
                    savedSchedules.add(newSchedule);
                    rerollCount++;

                    // 새로운 일정 데이터로 업데이트
                    updatePlanView(newSchedule);
                } else {
                    Toast.makeText(PlanViewActivity.this, "일정을 받아오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                Toast.makeText(PlanViewActivity.this, "서버와 통신하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateRandomTheme() {
        Random random = new Random();
        int index = random.nextInt(THEMES.length);
        return THEMES[index];
    }

    private void updateThemeText(String theme) {
        TextView themaText = findViewById(R.id.themaText);
        themaText.setText("이번 여행의 테마는 " + theme + "입니다!");
    }

    private void updatePlanView(Schedule schedule) {
        // 테마와 목적지를 업데이트
        TextView themaText = findViewById(R.id.themaText);
        themaText.setText("이번 여행의 테마는 " + schedule.theme + "입니다!");

        // 목적지를 업데이트
        this.destination = schedule.destination;

        // 여행 장소를 업데이트
        this.placesMap = new HashMap<>(schedule.placesMap);
        updatePlacesList(0); // 첫 번째 일차 데이터를 로드
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
        if (index < savedSchedules.size()) {
            Schedule schedule = savedSchedules.get(index);
            displaySchedule(schedule);
        } else {
            Toast.makeText(this, "해당 일정이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displaySchedule(Schedule schedule) {
        // 일정 정보를 화면에 표시하는 로직을 여기에 구현합니다.
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
}
