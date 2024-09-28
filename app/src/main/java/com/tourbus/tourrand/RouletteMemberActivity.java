package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import java.util.List;

public class RouletteMemberActivity extends AppCompatActivity {
    private int tourId;
    private TextView randomBtn;
    private Handler handler = new Handler(Looper.getMainLooper());
    private RecyclerView rouletteMemberRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<RMItem> rmItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette_member);

        TripPlan tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        tourId = tripPlan.getTourId();


        randomBtn = findViewById(R.id.randomBtn);
        randomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                randomBtn.setText("축당첨");
//                randomBtn.setBackgroundResource(R.drawable.teamback);
                String url = "https://api.tourrand.com/roulette_save";
                String data = String.valueOf(tourId);
                new Thread(() -> {
                    // DELETE 요청을 수행
                    String result = httpPostBodyConnection(url, data);
                    // 처리 결과 확인
                    handler = new Handler(Looper.getMainLooper());
                    if (handler != null) {
                        handler.post(() -> {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String winning = jsonObject.getString("nickname");
                                randomBtn.setText(winning);
                                randomBtn.setBackgroundResource(R.drawable.teamback);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            seeNetworkResult(result.toString());
                        });
                    }
                }).start();
            }
        });

        rouletteMemberRecyclerView = findViewById(R.id.randRecyclerView);
        rouletteMemberRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rouletteMemberRecyclerView.setLayoutManager(layoutManager);

        rmItemList = new ArrayList<>();
        rmItemList.add(new RMItem("장징징","4"));
        rmItemList.add(new RMItem("장징징","4"));
        rmItemList.add(new RMItem("장징징","4"));
        rmItemList.add(new RMItem("장징징","4"));

        adapter = new RMAdapter(rmItemList);
        rouletteMemberRecyclerView.setAdapter(adapter);
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
    private void parseAndAddItems(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray resultsArray = jsonObject.getJSONArray("results");

        rmItemList.clear();  // 기존 데이터를 초기화

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject resultObject = resultsArray.getJSONObject(i);
            int count = resultObject.getInt("count");
            String nickname = resultObject.getString("nickname");

            // RMItem 객체 생성 후 리스트에 추가
            RMItem item = new RMItem(nickname, String.valueOf(count));
            rmItemList.add(item);
        }

        // 어댑터에 데이터 변경을 알림
        adapter.notifyDataSetChanged();
    }
}