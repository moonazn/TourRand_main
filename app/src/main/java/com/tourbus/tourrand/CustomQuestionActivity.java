package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CustomQuestionActivity extends AppCompatActivity {
    private String result,data;
    private String url;
    private ImageView back;
    private Button nextBtn;
    private SingleSelectToggleGroup withAnimalToggle;
    boolean withAnimal, ischecked;
    private Handler handler = new Handler(Looper.getMainLooper());
    public ArrayList<TripPlanDetail> TripPlanDetailList ;
    private String planDate;
    private TextView tripDateTextView;
    private int tripLength;
    private String mainTheme;
    private LinearLayout dateLinear;
    String selectedLocation, previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_question);

        back = findViewById(R.id.back);
        nextBtn = findViewById(R.id.nextBtn);
        withAnimalToggle = findViewById(R.id.withAnimal);
        TextView noAnswer = findViewById(R.id.noAnswer);
        dateLinear = findViewById(R.id.dateLinear);
        dateLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });

        tripDateTextView = findViewById(R.id.tripDateTextView);
        ImageView calendarIcon = findViewById(R.id.calendarIcon);


        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });

        // 이전 액티비티에서 전달된 데이터 받기
        Intent intent = getIntent();
        withAnimal = intent.getBooleanExtra("withAnimal", false);
        selectedLocation = intent.getStringExtra("selectedLocation");
        previousActivity = intent.getStringExtra("previousActivity");

        withAnimalToggle.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                ischecked = true;
                if (checkedId == R.id.choice_yes)
                    withAnimal = true;
                else
                    withAnimal = false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomQuestionActivity.this, CustomRouletteActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ischecked != true || planDate == null) {
                    if (noAnswer.getVisibility() != View.VISIBLE)
                        noAnswer.setVisibility(View.VISIBLE);

                    Animation shake = AnimationUtils.loadAnimation(CustomQuestionActivity.this, R.anim.shake_fast);
                    noAnswer.startAnimation(shake);

                } else {
//                    Intent intent = new Intent(CustomQuestionActivity.this, DateQActivity.class);
//                    intent.putExtra("withAnimal", withAnimal);
//                    startActivity(intent);
//                    overridePendingTransition(0, 0);
//                    finish();
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
                    } else{
                        //반려동물 + 캠핑 미포함
                        mainTheme = chooseTheme();
                        url = "https://api.tourrand.com/route";
                        data = "{\"day\" : \""+tripLength+"\",\"mainTheme\" : \""+mainTheme+"\",\"destination\":\""+selectedLocation+"\" }";

                        Log.d("데이터 보낸 거", data);
                    }

                    new ServerCommunicationTask().execute();
                }

            }
        });
    }
    public String chooseTheme(){
        String [] theme = {"레저","역사","문화","자연","힐링"}; //캠핑, 생태관광, 반려동물 미포함

        Random random = new Random();
        int index = random.nextInt(theme.length);
        mainTheme = theme[index];
        return mainTheme;
    }

    private class ServerCommunicationTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CustomQuestionActivity.this);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
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
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            Intent intent = new Intent(CustomQuestionActivity.this, PlanViewActivity.class);
            TripPlanDetailList.get(0).setTheme(mainTheme);
            intent.putParcelableArrayListExtra("TripPlanDetailList", TripPlanDetailList);

            intent.putExtra("withAnimal", withAnimal);
            intent.putExtra("selectedLocation", selectedLocation);
            intent.putExtra("previousActivity", previousActivity);
            intent.putExtra("planDate", planDate);
            intent.putExtra("tripLength", tripLength);
            intent.putExtra("mainTheme", mainTheme);

            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    private void showDateRangePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        builder.setCalendarConstraints(constraintsBuilder.build());
        final MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                String startDateStr = sdf.format(selection.first);
                String endDateStr = sdf.format(selection.second);

                // Calculate the difference in days
                long startDateMillis = selection.first;
                long endDateMillis = selection.second;
                long diffInMillis = endDateMillis - startDateMillis;
                int numberOfDays = (int) TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1;

                tripLength = numberOfDays;

                planDate = startDateStr + " ~ " + endDateStr;
                tripDateTextView.setText(planDate);
            }
        });

        dateRangePicker.show(getSupportFragmentManager(), "date_range_picker");
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
                    TripPlanDetail TripPlanDetail = new TripPlanDetail(selectedLocation, day, planDate, location, address,latitude,longitude);
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
}