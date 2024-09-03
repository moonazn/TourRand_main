package com.tourbus.tourrand;

import android.graphics.Color;
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
    private Button saveBut, rerollBut;
    private KakaoMap kakaoMap;
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

    public class ScheduleResponse {
        public String destination;
        public Map<Integer, List<Place>> placesMap;
    }

    private ImageView edit;
    private boolean isEditing = false;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_edit);

        TripPlan tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        String user_id = UserManager.getInstance().getUserNickname();
        String planDate = tripPlan.getTravelDate();
        String tour_name = tripPlan.getTripName();

        KakaoMapSdk.init(this, "d71b70e03d7f7b494a72421fb46cba46");

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
                Label centerLabel = layer.addLabel(options);
                LabelOptions options2 = LabelOptions.from(LatLng.from(37.5642135, 127.0016985))
                        .setStyles(styles);
                Label label2 = layer.addLabel(options2);

                ImageView logo = findViewById(R.id.logo);
                logo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        centerLabel.changeStyles(LabelStyles.from(LabelStyle.from(R.drawable.choonsik)));
                        centerLabel.moveTo(LatLng.from(37.5642135,
                                127.0016985), 8000);
                    }
                });

//                //춘식이 돌아댕기는 거
//                LatLng pos = kakaoMap.getCameraPosition().getPosition();
//                Label centerLabel = labelLayer.addLabel(LabelOptions.from("dotLabel", pos)
//                        .setStyles(LabelStyle.from(R.drawable.choonsik).setAnchorPoint(0.5f, 0.5f))
//                        .setRank(1));
//                LatLng currentPos = centerLabel.getPosition();
//                centerLabel.moveTo(LatLng.from(currentPos.getLatitude() + 0.0006,
//                        currentPos.getLongitude() + 0.0006), 800);



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
        Log.d("PlanEditActivity", "saveChanges executed");
    }

    private void updatePlacesList(int day) {
        List<Place> placesList = placesMap.get(day);
        placesEditAdapter = new PlacesEditAdapter(placesList);
        placesRecyclerView.setAdapter(placesEditAdapter);

        ItemMoveCallback callback = new ItemMoveCallback(placesEditAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(placesRecyclerView);

        // 어댑터에 현재 editing 상태 전달
        placesEditAdapter.setEditing(isEditing);
        placesEditAdapter.notifyDataSetChanged();
    }


}
