package com.tourbus.tourrand;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.poi.hpsf.Decimal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DstActivity extends AppCompatActivity {

    private ImageView back;
    private Button next, reroll;
    private TextView dst1, dst2, dst3;
    private TextView selectedTextView = null;
    private String selectedLocation = null;
    private TextView noanswer;
    Place departureDocument;
    boolean withAnimal;
    public String withAnimaltoString, mainTheme;
    private String result,data;
    String previousActivity = "CustomRouletteActivity";
    private String url;
    private Handler handler = new Handler(Looper.getMainLooper());
    public ArrayList<TripPlanDetail> TripPlanDetailList ;
    String planDate;
    int tripLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dst);

        back = findViewById(R.id.back);
        next = findViewById(R.id.nextBtn);
        reroll = findViewById(R.id.rerollBtn);
        noanswer = findViewById(R.id.noAnswer);
        dst1 = findViewById(R.id.dst1);
        dst2 = findViewById(R.id.dst2);
        dst3 = findViewById(R.id.dst3);

        // 이전 액티비티들에서 전달된 데이터 받기
        Intent intent = getIntent();
        withAnimal = intent.getBooleanExtra("withAnimal", false);
        planDate = intent.getStringExtra("planDate");
        departureDocument = intent.getParcelableExtra("departureDocument");
//        destination = intent.getParcelableExtra("destination");
        tripLength = intent.getIntExtra("tripLength", 1);


        if(withAnimal == true){
            withAnimaltoString = "반려동물";
        }else{
            withAnimaltoString = "인간만";
        }




//        // PreTrip 객체 생성
//        PreTrip preTrip = new PreTrip(withAnimal, tripLength, departureDocument, selectedLocation);

        // 랜덤으로 목적지 3개 선택하여 TextView에 표시
        displayRandomDestinations();

        // TextView 클릭 리스너 설정
        View.OnClickListener destinationClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDestinationSelection((TextView) view);
            }
        };

        dst1.setOnClickListener(destinationClickListener);
        dst2.setOnClickListener(destinationClickListener);
        dst3.setOnClickListener(destinationClickListener);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DstActivity.this, DepartureQActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation == null) {
                    if (noanswer.getVisibility() != View.VISIBLE)
                        noanswer.setVisibility(View.VISIBLE);

                    Animation shake = AnimationUtils.loadAnimation(DstActivity.this, R.anim.shake_fast);
                    noanswer.startAnimation(shake);

                } else {
                    //반려동물이랑 같이 갈 때
                    if(withAnimaltoString.equals("반려동물")){
                        Log.d("반려동물 동반 여부", withAnimaltoString);
                        url = "http://13.209.33.141:5000/pet";
                        data = "{\"planDate\" : \""+tripLength+"\",\"destination\":\"" + selectedLocation+"\" }";; //json 형식 데이터

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
                            url = "http://13.209.33.141:5000/ecotourism";
                            data = "{\"planDate\" : \""+tripLength+"\",\"destination\":\""+selectedLocation+"\" }";
                        } else if(mainTheme.equals("생태관광")){
                            url = "http://13.209.33.141:5000/ecotourism";
                            data = "{\"planDate\" : \""+tripLength+"\",\"destination\":\""+selectedLocation+"\" }";
                        } else {
                            //위 지역임에도 불구하고 생태관광이 안 나왔을 때
                            url = "http://13.209.33.141:5000/route";
                            data = "{\"planDate\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";
                        }
                    } else if(selectedLocation.equals("경상남도 고성")){
                        selectedLocation = "경_고성";
                        mainTheme = chooseTheme();
                        url = "http://13.209.33.141:5000/route";
                        data = "{\"planDate\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";
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
                                    url = "http://13.209.33.141:5000/camping";
                                    data = "{\"destination\":\""+selectedLocation+"\" }";
                                } else if (selectedLocation.equals("경상남도 고성")) {
                                    selectedLocation = "경_고성";
                                    url = "http://13.209.33.141:5000/camping";
                                    data = "{\"destination\":\""+selectedLocation+"\" }";
                                } else {
                                    //고성 아닌데 캠핑인 것들
                                    url = "http://13.209.33.141:5000/camping";
                                    data = "{\"destination\":\""+selectedLocation+"\" }";
                                }
                            } else{
                                //여행 이틀만 가는데 캠핑 안 나왔을 때
                                mainTheme = chooseTheme();
                                url = "http://13.209.33.141:5000/route";
                                data = "{\"planDate\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";
                            }
                        }
                        //반려동물 + 캠핑 미포함
                        mainTheme = chooseTheme();
                        url = "http://13.209.33.141:5000/route";
                        data = "{\"planDate\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";
                        //data = "{\"planDate\" : \""+tripLength+"\",\"mainTheme\" : \"문화\",\"destination\":\""+selectedLocation+"\" }";

                        Log.d("데이터 보낸 거", data);


                    }
                    // 서버 통신을 비동기적으로 실행
                    new ServerCommunicationTask().execute();
                }
            }
        });

        reroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRandomDestinations();
            }
        });
    }

    private void displayRandomDestinations() {
        List<String> destinations = Arrays.asList(
                "서울", "강원도 고성", "속초", "양양", "인제", "양구", "강릉", "동해", "삼척", "태백",
                "정선", "평창", "홍천", "횡성", "원주", "영월", "화천", "철원", "춘천", "연천",
                "포천", "가평", "양평", "여주", "이천", "광주", "남양주", "하남", "구리", "의정부",
                "양주", "동두천", "파주", "고양", "김포", "부천", "광명", "시흥", "안산", "군포",
                "안양", "의왕", "과천", "성남", "수원", "화성", "평택", "안성", "용인", "오산",
                "단양", "제천", "충주", "음성", "진천", "괴산", "증평", "청주", "보은", "옥천",
                "영동", "대전", "세종", "천안", "아산", "예산", "당진", "서산", "태안", "홍성",
                "청양", "보령", "부여", "논산", "금산", "서천", "계룡", "공주", "울진", "봉화",
                "영주", "예천", "문경", "상주", "안동", "영양", "영덕", "청송", "포항", "경주",
                "영천", "군위", "의성", "구미", "김천", "칠곡", "성주", "고령", "대구", "경산",
                "청도", "거창", "함양", "산청", "합천", "창녕", "밀양", "울산", "의령", "함안",
                "김해", "부산", "창원", "하동", "진주", "사천", "경상남도 고성", "거제", "남해", "통영",
                "양산", "군산", "익산", "완주", "진안", "무주", "장수", "전주", "김제", "임실",
                "남원", "순창", "정읍", "부안", "고창", "영광", "장성", "담양", "곡성", "구례",
                "광양", "순천", "여수", "고흥", "보성", "화순", "광주광역시", "함평", "나주",
                "무안", "신안", "목포", "영암", "장흥", "강진", "해남", "완도", "진도", "제주도",
                "인천", "강화도", "영종도"
        );

        // 리스트를 섞어서 랜덤으로 3개 선택
        Collections.shuffle(destinations);
        dst1.setText(destinations.get(0));
        dst2.setText(destinations.get(1));
        dst3.setText(destinations.get(2));
    }
    public String chooseTheme(){
        String [] theme = {"레저","역사","문화","자연","힐링"}; //캠핑, 생태관광, 반려동물 미포함

        Random random = new Random();
        int index = random.nextInt(theme.length);

        mainTheme = theme[index];



        return mainTheme;
    }

    private void handleDestinationSelection(TextView selectedView) {
        if (selectedTextView != null) {
            selectedTextView.setBackgroundResource(R.drawable.round_rectangle); // 선택되지 않은 배경
        }

        selectedTextView = selectedView;
        selectedLocation = selectedTextView.getText().toString(); // 선택된 지역 이름 저장
        selectedTextView.setBackgroundResource(R.drawable.round_selected_rectangle); // 선택된 배경
    }

    private class ServerCommunicationTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 로딩 다이얼로그 표시
            progressDialog = new ProgressDialog(DstActivity.this);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 서버와 통신 (여기서는 예시로 Thread.sleep을 사용)
            result = httpPostBodyConnection(url, data);
            handler.post(() -> {seeNetworkResult(result);
                if(result != null && !result.isEmpty())
                    TripPlanDetailList = parseTripPlanDetail(result);
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
            Log.d(result, "network");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 로딩 다이얼로그 종료
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            // 다음 화면으로 전환
            Intent intent = new Intent(DstActivity.this, PlanViewActivity.class);

            intent.putParcelableArrayListExtra("TripPlanDetailList", TripPlanDetailList);

            intent.putExtra("withAnimal", withAnimal);
            intent.putExtra("mainTheme", mainTheme);
            intent.putExtra("selectedLocation", selectedLocation);
            intent.putExtra("departureDocument", departureDocument);
            intent.putExtra("previousActivity", previousActivity);
            intent.putExtra("tripLength", tripLength);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
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

                if (day != 0 && location != null && address != null && latitude !=0 && longitude !=0) {
                    TripPlanDetail TripPlanDetail = new TripPlanDetail(selectedLocation, departureDocument.getPlaceName(), day, planDate, location, address,latitude,longitude);
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
}
