package com.tourbus.tourrand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_view);

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
            placesList.add(new Place("장소 " + i + "-3", "주소 " + i + "-3"));
            placesList.add(new Place("장소 " + i + "-3", "주소 " + i + "-3"));
            placesList.add(new Place("장소 " + i + "-3", "주소 " + i + "-3"));
            placesMap.put(i - 1, placesList);
        }

        // 처음에 1일차의 장소를 표시
        updatePlacesList(0); // 1일차 데이터를 로드


        rerollBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(PlanViewActivity.this, AnimalQActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//                finish();
            }
        });

        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanViewActivity.this, HomeFragment1.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

    }

    private void updatePlacesList(int day) {
        List<Place> placesList = placesMap.get(day);
        placesAdapter = new PlacesAdapter(placesList);
        placesRecyclerView.setAdapter(placesAdapter);
    }
}
