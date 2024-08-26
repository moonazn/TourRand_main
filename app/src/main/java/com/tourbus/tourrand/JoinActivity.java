package com.tourbus.tourrand;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {

    private Handler handler;

    private EditText idEditText;
    private EditText pwEditText;
    private EditText nicknameEditText;
    private TextView idCheckInfo;
    private TextView nicknameCheckInfo;
    private Button idCheckBtn;
    private Button joinBtn;

    private boolean isIdDuplicated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join); // XML 레이아웃 파일명

        idEditText = findViewById(R.id.idEditText);
        pwEditText = findViewById(R.id.pwEditText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        idCheckInfo = findViewById(R.id.idCheckInfo);
        nicknameCheckInfo = findViewById(R.id.nicknameCheckInfo);
        idCheckBtn = findViewById(R.id.idCheckBtn);
        joinBtn = findViewById(R.id.joinBtn);

        // 아이디 중복 확인 버튼 클릭 리스너
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredId = idEditText.getText().toString().trim();
                if (isIdDuplicated(enteredId)) {
                    idCheckInfo.setText("중복된 아이디입니다.");
                    idCheckInfo.setTextColor(getResources().getColor(R.color.red)); // red 컬러를 정의하세요
                    idCheckInfo.setVisibility(View.VISIBLE);
                } else {
                    idCheckInfo.setText("사용 가능한 아이디입니다.");
                    idCheckInfo.setTextColor(getResources().getColor(R.color.blue)); // blue 컬러를 정의하세요
                    idCheckInfo.setVisibility(View.VISIBLE);
                }
            }
        });

        // 닉네임 입력 필드 변화 감지
        nicknameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 이곳은 필요 없으므로 비워둡니다.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredNickname = s.toString().trim();
                if (isNicknameValid(enteredNickname)) {
                    nicknameCheckInfo.setText("사용 가능한 닉네임입니다.");
                    nicknameCheckInfo.setTextColor(getResources().getColor(R.color.blue)); // blue 컬러를 정의하세요
                    nicknameCheckInfo.setVisibility(View.VISIBLE);
                } else {
                    nicknameCheckInfo.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 이곳은 필요 없으므로 비워둡니다.
            }
        });

        // 가입 완료 버튼 클릭 리스너
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString().trim();
                String pw = pwEditText.getText().toString().trim();
                String nickname = nicknameEditText.getText().toString().trim();

                if (id.isEmpty() || pw.isEmpty() || nickname.isEmpty()) {
                    Toast.makeText(JoinActivity.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isIdDuplicated(id)) {
                    Toast.makeText(JoinActivity.this, "아이디가 중복되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isNicknameValid(nickname)) {
                    Toast.makeText(JoinActivity.this, "닉네임이 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 여기서 회원가입 요청을 서버에 보낼 수 있습니다.

                //url 회원가입 버전으로 바꾸기⭐️⭐️⭐️
                String url = "http://13.209.33.141:5000/login";
                String data = "{ \"id\" : \""+id+"\",\"pw\" : \""+pw+"\",\"nickname\":\""+nickname+"\" }"; //json 형식 데이터
                new Thread(() -> {
                    String result = httpPostBodyConnection(url, data);
                    // 처리 결과 확인
                    handler.post(() -> seeNetworkResult(result));
                }).start();

                Toast.makeText(JoinActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                finish(); // 액티비티 종료
            }
        });
    }

    // 아이디 중복 확인
    private boolean isIdDuplicated(String id) {

        //url 아이디 중복확인 버전으로 바꾸기⭐️⭐️⭐️

        String url = "http://13.209.33.141:5000/login";
        String data = "{ \"id\" : \""+id+"\" }"; //json 형식 데이터
        new Thread(() -> {
            String result = httpPostBodyConnection(url, data);
            // 처리 결과 확인
            handler.post(() -> checkIdDuplicateResult(result));
        }).start();

//        return isIdDuplicated;
        return false;
    }

    // 닉네임 유효성 검사 (간단한 예시)
    private boolean isNicknameValid(String nickname) {
        // 닉네임은 유효하다고 가정합니다.
        return true;
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

    public void checkIdDuplicateResult(String result) {

        isIdDuplicated = false; // 아이디 중복되지 않음 -> 사용 가능한 아이디

    }

    public void seeNetworkResult(String result) {

        Log.d("JoinActivity", "seeNetworkResult = join completed!");

    }
}
