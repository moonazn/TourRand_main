package com.tourbus.tourrand;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanViewActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_view);

        KakaoMapSdk.init(this, "d71b70e03d7f7b494a72421fb46cba46");

        // 이전 액티비티들에서 전달된 데이터 받기
        Intent intent = getIntent();
        Place departureDocument = intent.getParcelableExtra("departureDocument");
        String destination = intent.getStringExtra("selectedLocation");

        mapView = findViewById(R.id.map);

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }

            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {
                // 인증 후 API가 정상적으로 실행될 때 호출됨
                LabelStyles styles = kakaoMap.getLabelManager()
                        .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.marker)));
                LabelOptions options = LabelOptions.from(LatLng.from(37.394660, 127.111182))
                        .setStyles(styles);
                LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                Label label = layer.addLabel(options);
                LabelOptions options2 = LabelOptions.from(LatLng.from(37.5642135, 127.0016985))
                        .setStyles(styles);
                Label label2 = layer.addLabel(options2);
            }
        });

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

        TextView navigateTextView = findViewById(R.id.fromSrcToDst);

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

        rerollBut.setOnClickListener(v -> {
            // 현재는 다음 액티비티로 전환하지 않음
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
}
