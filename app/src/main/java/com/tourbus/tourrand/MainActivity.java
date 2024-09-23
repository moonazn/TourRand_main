package com.tourbus.tourrand;

import static android.util.Base64.encodeToString;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ImageView kakaoLoginButton;
    private Handler handler;
    private  String inputText;

    private EditText idEditText, pwEditText;
    private Button loginBtn;
    private TextView join;
    private UserManager userManager;
    boolean isLoginFinish = true;
    private String inviteTourName;
    private int inviteTourId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("getKeyHash", ""+getKeyHash(MainActivity.this));

        kakaoLoginButton = findViewById(R.id.btn_kakao_login);

        handler = new Handler(Looper.getMainLooper());

        Function2<OAuthToken,Throwable, Unit> callback =new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            // 콜백 메서드 ,
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                Log.e(TAG,"CallBack Method");
                //oAuthToken != null 이라면 로그인 성공
                if(oAuthToken!=null){
                    // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                    updateKakaoLoginUi();

                }else {
                    //로그인 실패
                    String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
                    Log.e(TAG, "invoke: login fail - " + errorMessage);
                    showLoginFailedDialog("로그인 실패: " + errorMessage);

                }

                return null;
            }
        };
//        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
////                finish();
//
//                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)){
//                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, callback);
//                }else{
//                    // 카카오톡이 설치되어 있지 않다면
//                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback);
//                }
//            }
//
//
//        });

        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                finish();
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
                        @Override
                        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                            if (oAuthToken != null) {
                                // 로그인 성공
                                updateKakaoLoginUi();
                            } else {
                                // 카카오톡 로그인 실패 시, 카카오 계정 로그인을 시도
                                String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
                                Log.e(TAG, "KakaoTalk login failed: " + errorMessage);
                                UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback);
                            }
                            return null;
                        }
                    });
                } else {
                    // 카카오톡이 설치되어 있지 않다면
                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, callback);
                }
            }
        });


        idEditText = findViewById(R.id.idEditText);
        pwEditText = findViewById(R.id.pwEditText);
        loginBtn = findViewById(R.id.loginBtn);
        join = findViewById(R.id.join);

        TextView loginInfoText = findViewById(R.id.loginCheckInfo);

//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String id = idEditText.getText().toString();
//                String pw = pwEditText.getText().toString();
//
//                if(isLoginValid(id, pw)) {
//
//                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    finish();
//
//                } else {
//                    loginInfoText.setVisibility(View.VISIBLE);
//                    Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_fast);
//                    loginInfoText.startAnimation(shake);
//                }
//            }
//        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString().trim();
                String pw = pwEditText.getText().toString().trim();

                String url = "https://api.tourrand.com/login";
                String data = "{ \"id\" : \""+id+"\",\"password\" : \""+pw+"\" }"; //json 형식 데이터


                new Thread(() -> {
                    String result = httpPostBodyConnection(url, data);
                    // 처리 결과 확인
                    handler.post(() -> {
                        seeNetworkResult(result);
                        try {
                            // JSON 문자열을 JSONObject로 변환
                            JSONObject jsonObject = new JSONObject(result);

                            // "userId" 키에 해당하는 값 추출
                            String userId = jsonObject.getString("id");

                            if(userId.equals("로그인 실패")){
                                loginInfoText.setVisibility(View.VISIBLE);
                                Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_fast);
                                loginInfoText.startAnimation(shake);
                            }else if(userId.equals("사용자가 존재하지 않음")){
                                loginInfoText.setVisibility(View.VISIBLE);
                                Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_fast);
                                loginInfoText.startAnimation(shake);
                            }else {
                                // 싱글톤 인스턴스 가져오기
                                userManager = UserManager.getInstance();

                                Toast.makeText(MainActivity.this, userId+"님, 환영합니다!", Toast.LENGTH_SHORT).show();
                                // 값 저장하기
                                userManager.setUserId(userId);
                                userManager.setUserNickname(userId);
                                Log.d("userId",userId);

                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }

                            // 추출한 userId 출력
                            System.out.println("User ID: " + userId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }).start();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JoinActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        ConstraintLayout background = findViewById(R.id.background);
        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard(); // 터치 시 키보드를 숨김
                }
                return true; // 이벤트 처리 완료
            }
        });

    }

//    private boolean isLoginValid(String id, String pw){
//
//        //url 자체로그인 버전으로 바꾸기⭐️⭐️⭐️
//        String url = "http://13.209.33.141:4000/login";
//        String data = "{ \"id\" : \""+id+"\",\"password\" : \""+pw+"\" }"; //json 형식 데이터
//        new Thread(() -> {
//            String result = httpPostBodyConnection(url, data);
//            // 처리 결과 확인
//            handler.post(() -> appLoginNetworkResult(result));
//            try {
//                // JSON 문자열을 JSONObject로 변환
//                JSONObject jsonObject = new JSONObject(result);
//
//                // "userId" 키에 해당하는 값 추출
//                String userId = jsonObject.getString("id");
//
//                if (userId.equals("로그인 실패")) {
//                    return false;
//                } else {
//                    return true;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//    }

    private void showLoginFailedDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("로그인 실패")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public void updateKakaoLoginUi() {

        // 로그인 여부에 따른 UI 설정
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {

                if (user != null) {

                    SplashActivity.currentUser = new AppUser(user.getId().toString(),
                            user.getKakaoAccount().getProfile().getNickname(),
                            user.getKakaoAccount().getEmail(),
                            user.getKakaoAccount().getProfile().getProfileImageUrl());
                    // 유저의 아이디
                    Log.d("id", "invoke: id =" + user.getId());
                    // 유저의 이메일
                    Log.d("email", "invoke: email =" + user.getKakaoAccount().getEmail());
                    // 유저의 닉네임
                    Log.d("name", "invoke: nickname =" + user.getKakaoAccount().getProfile().getNickname());

//                    //유저 프로필 사진
//                    Glide.with(profileImage).load(user.getKakaoAccount().
//                            getProfile().getProfileImageUrl()).circleCrop().into(profileImage);
                    Log.d("사진 url", user.getKakaoAccount().
                            getProfile().getProfileImageUrl());

//                    id.setText(user.getId().toString());

                    Log.d(TAG, "invoke: profile = " + user.getKakaoAccount().getProfile().getThumbnailImageUrl());

                    String url = "https://api.tourrand.com/kakao_login";
//                    //10자리 숫자/이메일/이름/프로필 사진 주소
                    inputText = user.getId().toString()+"^^"+user.getKakaoAccount().getEmail().toString()+"^^"+
                            user.getKakaoAccount().getProfile().getNickname()+"^^"+user.getKakaoAccount().getProfile().getThumbnailImageUrl().toString();

                    userManager = UserManager.getInstance();
                    String id = user.getId().toString();
                    String email = user.getKakaoAccount().getEmail().toString();
                    String nickname = user.getKakaoAccount().getProfile().getNickname();
                    String user_img = user.getKakaoAccount().getProfile().getThumbnailImageUrl().toString();
                    userManager.setUserId(id);
                   // Log.d("id값", "카카오ID " + user.getId().toString());
                    //String data = "{ \"content\" : \""+inputText+"\" }";; //json 형식 데이터
                    String data = "{ \"id\" : \""+id+"\",\"email\" : \""+email+"\",\"nickname\":\""+nickname+"\",\"user_img\":\""+user_img+"\" }"; //json 형식 데이터

                    Log.d("json 변환", data);
                    new Thread(() -> {
                        String result = httpPostBodyConnection(url, data);
                        // 처리 결과 확인
                        handler.post(() ->{
                            try {
                                // JSON 문자열을 JSONObject로 변환
                                JSONObject jsonObject = new JSONObject(result);

                                // 공통된 값 처리 (nickname)
                                String get_nickname = jsonObject.getString("nickname");
                                UserManager.getInstance().setUserNickname(get_nickname);

                                // "invite"가 있는지 체크하여 처리
                                if (jsonObject.has("invite")) {
                                    String invite = jsonObject.getString("invite");
                                    // invite에 대한 로직 처리
                                    System.out.println("Invite: " + invite);
                                }
                                // "tour_id"가 있는지 체크하여 처리
                                if (jsonObject.has("tour_id")) {
                                    inviteTourId = jsonObject.getInt("tour_id");
                                    inviteTourName = jsonObject.getString("tour_name");
                                    // tour에 대한 로직 처리
                                    System.out.println("Tour ID: " + inviteTourId);
                                    System.out.println("Tour Name: " + inviteTourName);
                                }

                                // 공통 처리
                                System.out.println("Nickname: " + nickname);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            seeNetworkResult(result);
                                });
                    }).start();

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    if(inviteTourName !=null && inviteTourId!=0){
                        intent.putExtra("inviteTourName", inviteTourName);
                        intent.putExtra("inviteTourId", inviteTourId);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();

                } else {


                    kakaoLoginButton.setVisibility(View.VISIBLE);

                }
                return null;
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
        Log.d(result, "network");
    }

    public void appLoginNetworkResult(String result) {

        UserManager currentUser = UserManager.getInstance();
        currentUser.setUserNickname("realuser");
        currentUser.setUserId("realuserid");

        isLoginFinish = true;
    }
    public static String getKeyHash(final Context context){
        PackageManager pm = context.getPackageManager();
        try{
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if(packageInfo == null)
                return null;
            for(Signature signature : packageInfo.signatures){
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                }catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }

            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}

