package com.tourbus.tourrand;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    int tourId;
    double latitude, longtitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_edit);

        TripPlan tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        planDate = tripPlan.getTravelDate();
        tour_name = tripPlan.getTripName();
        tourId = tripPlan.getTourId();

// bottom.xml에서 ImageView 찾기
        ImageView editPageIcon = findViewById(R.id.editPage);
        ImageView weatherPageIcon = findViewById(R.id.weatherPage);
        ImageView randomPageIcon = findViewById(R.id.randomPage);
        ImageView groupPageIcon = findViewById(R.id.groupPage);

        // 현재 화면에 해당하는 아이콘의 이미지 변경
        editPageIcon.setImageResource(R.drawable.edit_home_on);
        weatherPageIcon.setImageResource(R.drawable.weather_off);
        randomPageIcon.setImageResource(R.drawable.random_off);
        groupPageIcon.setImageResource(R.drawable.group_off);

        TextView toHome = findViewById(R.id.toHome);

        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanEditActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        weatherPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanEditActivity.this, WeatherActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        randomPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanEditActivity.this, RandomActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        groupPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanEditActivity.this, TeamActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });


        tripTitleEditText = findViewById(R.id.tripTitleEditText);
        tripTitleEditText.setEnabled(false);

        KakaoMapSdk.init(this, "e211572ac7a98da2054d8a998e86a28a");

        mapView = findViewById(R.id.map);

        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();
        url = "https://api.tourrand.com/tour_detail";
// JSON 문자열을 구성하기 위한 StringBuilder 사용
        StringBuilder data = new StringBuilder();

        data.append("{");
        data.append("\"user_id\":\"").append(userId).append("\",");
        data.append("\"tour_id\":\"").append(tourId).append("\"");
        data.append("}");

// 최종적으로 생성된 JSON 문자열
        jsonData = data.toString();

// jsonData를 서버에 전송
        new ServerCommunicationTask().execute();

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
                    tripTitleEditText.setEnabled(true); // EditText를 수정 가능 상태로 변경
                    edit.setImageResource(R.drawable.save); // 완료 버튼 이미지로 변경
                } else {
                    // 수정 완료 후 원래 상태로 복귀
                    tripTitleEditText.setEnabled(false);

                    edit.setImageResource(R.drawable.edit); // 원래 edit 버튼 이미지로 변경
                    saveChanges();
                }

                // 현재 상태에 따라 아이템들을 다시 표시
                placesEditAdapter.notifyDataSetChanged();
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private void getGeoDataByAddress(final String completeAddress, final GeoDataCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            private Double latitude;
            private Double longitude;

            @Override
            protected Void doInBackground(Void... params) {
                Log.d("getGeoDataByAddress", "getGeoDataByAddress executed");
                try {
                    String API_KEY = "AIzaSyCD4wiVWqJJAq1ipj5VdS4CXVG7ulEswkE";
                    String surl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(completeAddress, "UTF-8") + "&key=" + API_KEY;
                    URL url = new URL(surl);
                    InputStream is = url.openConnection().getInputStream();

                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null) {
                        responseStrBuilder.append(inputStr);
                    }

                    JSONObject jo = new JSONObject(responseStrBuilder.toString());
                    JSONArray results = jo.getJSONArray("results");
                    if (results.length() > 0) {
                        JSONObject jsonObject = results.getJSONObject(0);
                        latitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        longitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    }
                } catch (Exception e) {
                    Log.d("getGeoDataByAddress", String.valueOf(e));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // UI 업데이트는 메인 스레드에서 처리해야 하므로, 콜백을 사용
                if (callback != null && latitude != null && longitude != null) {

                    Log.d("GeoCoding", "Getting geo data for latitude: " + latitude);
                    callback.onGeoDataReceived(latitude, longitude);
                    Log.d("GeoCoding", "Geo data received: Lat " + latitude + ", Lon " + longitude);

                }
            }
        }.execute();
    }



    private void saveChanges() {

        // 싱글톤 인스턴스 가져오기
        UserManager userManager = UserManager.getInstance();
        String userId = userManager.getUserId();

        String url = "https://api.tourrand.com/update_itinerary";
//            String data = "{ \"user_id\" : \""+userId+"\", \"tour_name\" : \""+tripPlanDetailList.get(0).getTripName()+"\" , \"planDate\" : \""+tripPlanDetailList.get(0).getPlanDate()+"\", \"schedules\" : [{\""+tripPlanDetailList+"\"}] }";

        // JSON 문자열을 구성하기 위한 StringBuilder 사용
        StringBuilder data = new StringBuilder();

        data.append("{");
        data.append("\"user_id\":\"").append(userId).append("\",");
        data.append("\"tour_id\":").append(tourId).append(",");
        data.append("\"tour_name\":\"").append(tripTitleEditText.getText()).append("\",");
        data.append("\"planDate\":\"").append(planDate).append("\",");
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
            placesEditAdapter.notifyDataSetChanged();
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
//        Log.d("updateTripPlanDetailList", "updateTripPlanDetailList executed");
//
//        // 입력된 데이터 상태 로그
////        Log.d("updateTripPlanDetailList", "Day: " + day);
//        Log.d("updateTripPlanDetailList", "Updated Places List Size: " + updatedPlacesList.size());
//        for (int j = 0; j < updatedPlacesList.size(); j++) {
//            Place place = updatedPlacesList.get(j);
//            if (place.getLatitude() == 0.0) {
//
//                new Thread(() -> {
//                    getGeoDataByAddress(place.getAddress());
//
//                    // Handler를 사용하여 UI 스레드에서 후속 작업 실행
//                    new Handler(Looper.getMainLooper()).post(() -> {
//                        place.setLatitude(latitude);
//                        place.setLongitude(longtitude);
//                        Log.d("getGeoDataByAddress", String.valueOf(longtitude) + latitude);
//                    });
//                }).start();
//            }
//            Log.d("updateTripPlanDetailList", "Updated Place " + j + ": " + place.getPlaceName() + ", " + place.getAddress() + ", Lat: " + place.getLatitude() + ", Lon: " + place.getLongitude() + ", Day: " + place.getDay());
//        }
//
//
//        // 기존 tripPlanDetailList 상태 로그
//        Log.d("updateTripPlanDetailList", "Original TripPlanDetailList Size: " + tripPlanDetailList.size());
//        for (int i = 0; i < tripPlanDetailList.size(); i++) {
//            TripPlanDetail detail = tripPlanDetailList.get(i);
//            Log.d("updateTripPlanDetailList", "TripPlanDetail " + i + ": Day " + detail.getDay() + ", Location: " + detail.getLocation() + ", Address: " + detail.getAddress() + ", Lat: " + detail.getLatitude() + ", Lon: " + detail.getLongitude());
//        }
//
//        // 기존 항목 삭제
//        tripPlanDetailList.removeIf(detail -> detail.getDay() == day+1);
//        Log.d("updateTripPlanDetailList", "day: " + String.valueOf(day));
//
//
//        // 새로운 항목 추가
//        for (Place place : updatedPlacesList) {
//            place.setDay(day+1);
//
//            Log.d("updateTripPlanDetailList", "place.getDay(): " + place.getDay());
//            TripPlanDetail newDetail = new TripPlanDetail();
//            newDetail.setDay(day+1);
//            newDetail.setLocation(place.getPlaceName());
//            newDetail.setAddress(place.getAddress());
//            newDetail.setLongitude(place.getLatitude());
//            newDetail.setLatitude(place.getLongitude());
//
//            tripPlanDetailList.add(newDetail);
//
//
//        }
//
//        // day 기준으로 TripPlanDetailList를 정렬 (오름차순)
//        Collections.sort(tripPlanDetailList, new Comparator<TripPlanDetail>() {
//            @Override
//            public int compare(TripPlanDetail o1, TripPlanDetail o2) {
//                return Integer.compare(o1.getDay(), o2.getDay());
//            }
//        });
//
//        // 최종 결과 로그
//        Log.d("updateTripPlanDetailList", "Final TripPlanDetailList State:");
//        for (int i = 0; i < tripPlanDetailList.size(); i++) {
//            TripPlanDetail detail = tripPlanDetailList.get(i);
//            Log.d("updateTripPlanDetailList", "TripPlanDetail " + i + ": Day " + detail.getDay() + ", Location: " + detail.getLocation() + ", Address: " + detail.getAddress() + ", Lat: " + detail.getLatitude() + ", Lon: " + detail.getLongitude());
//        }
//
//    }

    public interface GeoDataCallback {
        void onGeoDataReceived(double latitude, double longitude);
    }



    private void updateTripPlanDetailList(int day, List<Place> updatedPlacesList) {
        Log.d("updateTripPlanDetailList", "updateTripPlanDetailList executed");
        Log.d("updateTripPlanDetailList", "Updated Places List Size: " + updatedPlacesList.size());

        // 일자별로 기존 장소를 그룹화
        Map<Integer, List<TripPlanDetail>> dayToDetailMap = new HashMap<>();
        for (TripPlanDetail detail : tripPlanDetailList) {
            if (!dayToDetailMap.containsKey(detail.getDay())) {
                dayToDetailMap.put(detail.getDay(), new ArrayList<>());
            }
            dayToDetailMap.get(detail.getDay()).add(detail);
        }

        // 기존 장소 삭제
        tripPlanDetailList.removeIf(detail -> detail.getDay() == day + 1);
        Log.d("updateTripPlanDetailList", "Removed details for day: " + day);

        // 지오코딩 작업을 마친 후 새로운 장소를 추가하기 위한 리스트
        List<TripPlanDetail> newDetails = new ArrayList<>();
        List<Place> placesToProcess = new ArrayList<>(updatedPlacesList);

        // 위도/경도가 없는 장소가 있다면 처리
        processGeoCodingForPlaces(day, placesToProcess, newDetails, dayToDetailMap);
    }

    private void processGeoCodingForPlaces(int day, List<Place> placesToProcess, List<TripPlanDetail> newDetails, Map<Integer, List<TripPlanDetail>> dayToDetailMap) {
        if (placesToProcess.isEmpty()) {
            // 모든 장소에 대한 처리가 완료되면 최종 리스트를 업데이트
            tripPlanDetailList.addAll(newDetails);

            // day 기준으로 TripPlanDetailList를 정렬 (오름차순)
            Collections.sort(tripPlanDetailList, (o1, o2) -> Integer.compare(o1.getDay(), o2.getDay()));

            // 최종 결과 로그
            Log.d("updateTripPlanDetailList", "Final TripPlanDetailList State:");
            for (int i = 0; i < tripPlanDetailList.size(); i++) {
                TripPlanDetail detail = tripPlanDetailList.get(i);
                Log.d("updateTripPlanDetailList", "TripPlanDetail " + i + ": Day " + detail.getDay() + ", Location: " + detail.getLocation() + ", Address: " + detail.getAddress() + ", Lat: " + detail.getLatitude() + ", Lon: " + detail.getLongitude());
            }

            // RecyclerView 어댑터 업데이트
            // (여기서 어댑터에 리스트 변경 사항을 반영)
            return;
        }

        Place place = placesToProcess.remove(0);
        if (place.getLatitude() == 0.0 || place.getLongitude() == 0.0) {
            // 지오코딩 처리 필요
            getGeoDataByAddress(place.getAddress(), (latitude, longitude) -> {
                place.setLatitude(latitude);
                place.setLongitude(longitude);
                Log.d("GeoCoding", "GeoData updated for place: " + place.getPlaceName());
                addPlaceToList(place, day, dayToDetailMap, newDetails);
                // 다음 장소로 이동
                processGeoCodingForPlaces(day, placesToProcess, newDetails, dayToDetailMap);
            });
        } else {
            // 위도/경도가 이미 있는 경우 바로 추가
            addPlaceToList(place, day, dayToDetailMap, newDetails);
            // 다음 장소로 이동
            processGeoCodingForPlaces(day, placesToProcess, newDetails, dayToDetailMap);
        }
    }

    // 장소를 리스트에 추가하는 메서드 분리
    private void addPlaceToList(Place place, int day, Map<Integer, List<TripPlanDetail>> dayToDetailMap, List<TripPlanDetail> newDetails) {
        TripPlanDetail newDetail = new TripPlanDetail();
        newDetail.setDay(day + 1);
        newDetail.setLocation(place.getPlaceName());
        newDetail.setAddress(place.getAddress());
        newDetail.setLongitude(place.getLongitude());
        newDetail.setLatitude(place.getLatitude());

        // 기존 장소 리스트에서 위치를 찾거나 추가
        List<TripPlanDetail> detailsForDay = dayToDetailMap.getOrDefault(day + 1, new ArrayList<>());
        int index = getIndexForPlace(detailsForDay, place);
        if (index != -1) {
            // 기존 장소를 업데이트
            detailsForDay.set(index, newDetail);
        } else {
            // 새로운 장소를 추가
            detailsForDay.add(newDetail);
        }

        newDetails.add(newDetail);
    }



    // 기존 장소 리스트에서 위치를 찾는 메서드
    private int getIndexForPlace(List<TripPlanDetail> detailsForDay, Place place) {
        for (int i = 0; i < detailsForDay.size(); i++) {
            TripPlanDetail detail = detailsForDay.get(i);
            // 장소 이름이나 주소 등을 통해 위치를 찾을 수 있습니다.
            if (detail.getLocation().equals(place.getPlaceName()) || detail.getAddress().equals(place.getAddress())) {
                return i;
            }
        }
        return -1; // 위치를 찾지 못한 경우
    }



    private void displaySchedule(ArrayList<TripPlanDetail> tripPlanDetailList) {
        // 일정 정보를 화면에 표시하는 로직을 여기에 구현합니다.
        setDataWithTripDetailList(tripPlanDetailList);

        //⭐⭐⭐⭐⭐⭐⭐여기를 바꿔야하는디
        tripTitleEditText.setText(tripPlanDetailList.get(0).getTripName());
        Log.d("표시되는 투어네임", tripPlanDetailList.get(0).getTripName());

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
            Log.d("TAG2", returnData.toString());
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
        Log.d("network",result);
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
                Log.d("latitude", String.valueOf(latitude));
                Log.d("longitude", String.valueOf(longitude));

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

            Log.d("network", result);
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
            tour_name = jsonObject.getString("tour_name");
            Log.d("파싱 코드 내 투어네임", tour_name);

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
                        tripPlanDetail = new TripPlanDetail(tour_name, day, planDate, location, address, latitude, longitude);
                    } else {
                        tripPlanDetail = new TripPlanDetail(tour_name, day, planDate, location, address, latitude, longitude);
                    }
                    Log.d("tripPlan삽입 투어네임", tour_name);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PlanEditActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }



}
