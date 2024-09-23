package com.tourbus.tourrand;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.friend.client.PickerClient;
import com.kakao.sdk.friend.model.OpenPickerFriendRequestParams;
import com.kakao.sdk.friend.model.PickerOrientation;
import com.kakao.sdk.friend.model.ViewAppearance;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friends;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

public class TeamActivity extends AppCompatActivity {

    String planDate;
    String tour_name;
    int tourId;
    TextView userTxt;
    ImageView plus;
    private UserManager userManager = UserManager.getInstance();
    private List<Friend> friendList = new ArrayList<>();
    private boolean[] checkedItems;
    private List<String> selectedFriends;
    public interface PickerCallback {
        void onResult(List<User> selectedUsers, Throwable error);
    }
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        handler = new Handler(Looper.getMainLooper());

        selectedFriends = new ArrayList<>();

        // Kakao SDK 초기화
        KakaoSdk.init(this, "e211572ac7a98da2054d8a998e86a28a");
        // bottom.xml에서 ImageView 찾기
        ImageView editPageIcon = findViewById(R.id.editPage);
        ImageView weatherPageIcon = findViewById(R.id.weatherPage);
        ImageView randomPageIcon = findViewById(R.id.randomPage);
        ImageView groupPageIcon = findViewById(R.id.groupPage);

        // 현재 화면에 해당하는 아이콘의 이미지 변경
        groupPageIcon.setImageResource(R.drawable.group_on);
        editPageIcon.setImageResource(R.drawable.edit_home_off);
        weatherPageIcon.setImageResource(R.drawable.weather_off);
        randomPageIcon.setImageResource(R.drawable.random_off);

        userTxt = findViewById(R.id.userTxt);
        plus = findViewById(R.id.teamPlus);
        userTxt.setText(userManager.getUserNickname());

//        // plus 버튼 클릭 리스너
//        plus.setOnClickListener(v -> {
//            // 카카오톡 또는 카카오 계정 로그인이 되어 있는지 확인
//            UserApiClient.getInstance().me((user, error) -> {
//                if (error != null) {
//                    // 로그인하지 않은 경우 카카오 계정으로 로그인 시도
//                    UserApiClient.getInstance().loginWithKakaoAccount(this, (OAuthToken token, Throwable loginError) -> {
//                        if (loginError != null) {
//                            Toast.makeText(getApplicationContext(), "로그인 실패: " + loginError.getMessage(), Toast.LENGTH_SHORT).show();
//                        } else if (token != null) {
//                            // 로그인 성공 후 친구 목록 가져오기
//                            fetchFriendsList();
//                        }
//                        return null;
//                    });
//                } else {
//                    // 이미 로그인된 경우 바로 친구 목록 가져오기
//                    fetchFriendsList();
//                }
//            });
//        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 카카오톡 또는 카카오 계정 로그인이 되어 있는지 확인
                UserApiClient.getInstance().me((user, error) -> {
                    if (error != null) {
                        // 로그인하지 않은 경우 카카오 계정으로 로그인 시도
                        UserApiClient.getInstance().loginWithKakaoAccount(TeamActivity.this, (OAuthToken token, Throwable loginError) -> {
                            if (loginError != null) {
                                Toast.makeText(getApplicationContext(), "로그인 실패: " + loginError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else if (token != null) {
                                // 로그인 성공 후 추가 동의 요청
                                requestAdditionalConsent();
                            }
                            return null;
                        });
                    } else {
                        // 이미 로그인된 경우 바로 친구 목록 가져오기
                        fetchFriendsList();
                    }
                    return null;
                });
            }
        });


//친구 불러오기 할 때만 주석하기
        TripPlan tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        planDate = tripPlan.getTravelDate();
        tour_name = tripPlan.getTripName();
        tourId = tripPlan.getTourId();
        weatherPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamActivity.this, WeatherActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        editPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamActivity.this, PlanEditActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        randomPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamActivity.this, RandomActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    // 추가 동의 요청 함수
    private void requestAdditionalConsent() {
        // 필요한 동의 항목에 대한 요청을 실행 (예: 친구 목록에 대한 추가 동의)
        UserApiClient.getInstance().loginWithKakaoAccount(TeamActivity.this, (OAuthToken token, Throwable error) -> {
            if (error != null) {
                Log.e("Kakao", "추가 동의 실패: " + error.getMessage());
            } else if (token != null) {
                // 추가 동의가 완료되었으면 친구 목록 가져오기 시작
                fetchFriendsList();
            }
            return null;
        });
    }
    // onActivityResult 함수 추가
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 카카오 로그인 또는 추가 동의 결과 처리
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // 추가 동의가 완료되었을 때 친구 목록을 가져옴
            fetchFriendsList();
        }
    }
    private void fetchFriendsList() {
        TalkApiClient.getInstance().friends((Friends friends, Throwable error) -> {
            if (error != null) {
                Toast.makeText(getApplicationContext(), "친구 목록 가져오기 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("친구목록 가져오기 실패", error.getMessage());
            } else if (friends != null && friends.getElements().size() > 0) {
                // Kakao SDK의 Friend 객체 목록을 자신의 Friend 객체 목록으로 변환
                List<Friend> myFriends = new ArrayList<>();
                for (Object element : friends.getElements()) {
                    // 객체를 Kakao SDK의 Friend로 캐스팅
                    if (element instanceof com.kakao.sdk.talk.model.Friend) {
                        com.kakao.sdk.talk.model.Friend kakaoFriend = (com.kakao.sdk.talk.model.Friend) element;

                        // 자신의 Friend 객체 생성 및 데이터 설정
                        Friend myFriend = new Friend();
                        myFriend.setNickname(kakaoFriend.getProfileNickname());
                        myFriend.setUuid(String.valueOf(kakaoFriend.getId())); // UUID도 설정
                        myFriend.setProfileImage(kakaoFriend.getProfileThumbnailImage());
                        myFriends.add(myFriend);
                        Log.d("초대된 친구의 id", myFriend.getUuid());
                    }
                }
                // 변환된 친구 목록 사용 (myFriends)
                friendList.clear(); // 기존 목록 초기화
                friendList.addAll(myFriends); // 새 친구 목록 추가

                OpenPickerFriendRequestParams openPickerFriendRequestParams = new OpenPickerFriendRequestParams(
                        "친구 초대하기", // 타이틀
                        ViewAppearance.AUTO, // 화면 모드
                        PickerOrientation.AUTO, // 화면 방향
                        true, // 검색 기능 사용 여부
                        true, // 인덱스뷰 사용 여부
                        true, // 즐겨찾기 친구 표시 여부
                        true // 선택한 친구 표시 여부
//                        5, // 최대 선택 가능 친구 수
//                        1 // 최소 선택 가능 친구 수
                );
                Friend myFriend = new Friend();
                TripPlan tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
                PickerClient.getInstance().selectFriends(
                        TeamActivity.this,
                        openPickerFriendRequestParams,
                        (selectedUsers, er) -> {
                            if (er != null) {
                                Log.e(TAG, "친구 선택 실패", er);
                            } else {
                                String url = "https://api.tourrand.com/invite";
                                String data = "{ \"user_id\" : \""+myFriend.getUuid()+"\",\"tour_id\" : \""+tourId+"\"}"; //json 형식 데이터
                                new Thread(() -> {
                                    String result = httpPostBodyConnection(url, data);
                                    // 처리 결과 확인
                                    handler.post(() ->{
                                        seeNetworkResult(result);
                                    });
                                }).start();
                                Log.d(TAG, "친구 선택 성공 " + selectedUsers);
                            }
                            return Unit.INSTANCE;
                        }
                );


            } else {
                Toast.makeText(getApplicationContext(), "친구 목록이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            return null;
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

}
