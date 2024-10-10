package com.tourbus.tourrand;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.kakao.sdk.friend.model.SelectedUser;
import com.kakao.sdk.friend.model.ViewAppearance;

import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friends;
import com.kakao.sdk.template.model.DefaultTemplate;
import com.kakao.sdk.template.model.Link;
import com.kakao.sdk.template.model.TextTemplate;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private RecyclerView teamRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<TeamItem> teamItem;
    TripPlan tripPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        handler = new Handler(Looper.getMainLooper());

        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        teamRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        teamRecyclerView.setLayoutManager(layoutManager);

        //친구 불러오기 할 때만 주석하기
        tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        planDate = tripPlan.getTravelDate();
        tour_name = tripPlan.getTripName();
        tourId = tripPlan.getTourId();

        teamItem = new ArrayList<>();
//        teamItem.add(new TeamItem("이연진"));
//        teamItem.add(new TeamItem("송지연"));
//        teamItem.add(new TeamItem("김재균"));
        adapter = new TeamAdapter(teamItem);
        teamRecyclerView.setAdapter(adapter);



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

        String url = "https://api.tourrand.com/checkteam";
        String data = "{ \"user_id\" : \""+UserManager.getInstance().getUserId()+"\",\"tour_id\" : \""+tourId+"\"}"; //json 형식 데이터

        new Thread(() -> {
            // DELETE 요청을 수행
            String result = httpPostBodyConnection(url, data);
            // 처리 결과 확인
            handler = new Handler(Looper.getMainLooper());
            if (handler != null) {
                handler.post(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        boolean isMemberCheck = jsonObject.getBoolean("message");
                        if(isMemberCheck){

                            parseJsonResponse(result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    seeNetworkResult(result.toString());
                });
            }
        }).start();

        plus = findViewById(R.id.teamPlus);
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
    public ArrayList<TeamItem> parseTeamMember(String json) {
        ArrayList<TeamItem> teamItems = new ArrayList<>();

        try {
            // 전체 JSON 데이터는 JSONObject로 파싱
            JSONObject jsonObject = new JSONObject(json);
            boolean isMemberCheck = jsonObject.getBoolean("message");

            // JSONObject에서 "data" 필드를 JSONArray로 추출
            JSONArray jsonArray = jsonObject.getJSONArray("member");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject postObject = jsonArray.getJSONObject(i);

                String member = null;

                if (postObject.has("member")) {
                    member = postObject.getString("member");
                    Log.d("member", member);
                }

                if (member !=null) {
                    TeamItem teamItem = new TeamItem(member);
                    teamItems.add(teamItem);
                } else {
                    Log.e("JSONError", "Missing key in JSON object: " + postObject.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return teamItems;
    }
    public void parseJsonResponse(String jsonResponse) {
        List<TeamItem> teamList = new ArrayList<>();

        try {
            // JSON 문자열을 JSONObject로 변환
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // "member" 배열 가져오기
            JSONArray memberArray = jsonObject.getJSONArray("member");

            // 각 멤버 이름을 TeamItem 리스트에 추가
            for (int i = 0; i < memberArray.length(); i++) {
                String memberName = memberArray.getString(i);
                TeamItem teamItem = new TeamItem(memberName);
                teamList.add(teamItem);
            }

            // 여기서 어댑터에 teamList를 전달해서 리사이클러뷰에 적용 가능
            adapter = new TeamAdapter(teamList);
            teamRecyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        myFriend.setId(String.valueOf(kakaoFriend.getId())); // ID도 설정
                        myFriend.setProfileImage(kakaoFriend.getProfileThumbnailImage());
                        myFriend.setUuid(kakaoFriend.getUuid());
                        myFriends.add(myFriend);
                        Log.d("초대된 친구의 id", myFriend.getId());
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
                TripPlan tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
                PickerClient.getInstance().selectFriends(
                        TeamActivity.this,
                        openPickerFriendRequestParams,
                        (selectedUsers, er) -> {
                            if (er != null) {
                                Log.e(TAG, "친구 선택 실패", er);
                            } else {
                                Log.d("selectedUsers 타입 확인", selectedUsers.getClass().getName());
                                if(selectedUsers!=null){
                                    // selectedUsers에서 선택된 친구 리스트를 가져옵니다.
                                    List<SelectedUser> selectedFriendList = selectedUsers.getUsers(); // getFriends()는 예시입니다. SDK 문서에서 실제 메서드를 확인하세요.

                                    if (selectedFriendList != null && !selectedFriendList.isEmpty()) {

                                        // 선택된 친구들의 ID와 닉네임을 수집하는 부분
                                        JSONArray friendArray = new JSONArray();

                                        for (SelectedUser selectedFriend : selectedFriendList) {
                                            JSONObject friendObject = new JSONObject();
                                            try {
                                                friendObject.put("user_id", String.valueOf(selectedFriend.getId()));
                                                //friendObject.put("nickname", selectedFriend.getProfileNickname());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            friendArray.put(friendObject);
                                        }

                                        Log.d("친구 리스트", "선택된 친구들: " + friendArray.toString());

                                        String url = "https://api.tourrand.com/invite";
                                        // 초대하는 사람의 ID가 invite_id
                                        String data = "{ \"users\" : " + friendArray.toString() + ", \"tour_id\" : \"" + tourId + "\", \"nickname\" : \"" + UserManager.getInstance().getUserNickname() + "\" }"; // JSON 형식 데이터

                                        new Thread(() -> {
                                            String result = httpPostBodyConnection(url, data);
                                            // 처리 결과 확인
                                            handler.post(() -> {
                                                seeNetworkResult(result);
                                            });
                                        }).start();

                                        Log.d(TAG, "친구 선택 성공 " + selectedUsers);

                                        // Kakao 메시지 보내기 함수 호출 (여러 친구에게 메시지를 보내려면 이 부분도 수정해야 할 수 있음)
                                        for (SelectedUser selectedFriend : selectedFriendList) {
                                            sendKakaoMessage(selectedFriend);
                                        }
                                    }
                                }
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
    private void sendKakaoMessage(SelectedUser selectedUser) {
        String templateId = "112266"; // 사용하고자 하는 템플릿 ID
        String friendUUid = selectedUser.getUuid();
        if(friendUUid == null || friendUUid.isEmpty()){
            Log.e("UUID 오류", "친구의 UUID가 null거나 빈 값 입니다.");
            Toast.makeText(getApplicationContext(), "친구의 UUID를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> receiverUuids = new ArrayList<>();
        receiverUuids.add(friendUUid);
        // 템플릿 파라미터 설정 (Kakao Developer Console에서 정의한 변수에 맞게 데이터 설정)
        Map<String, String> templateArgs = new HashMap<>();
        templateArgs.put("${userNickname}", "값1");
        templateArgs.put("${tourname}", "값2");

        TalkApiClient.getInstance().sendCustomMessage(receiverUuids, Long.parseLong(templateId), templateArgs, (result, error) -> {
            if (error != null) {
                // 에러 발생 시 처리
                Toast.makeText(getApplicationContext(), "메시지 보내기 실패: " + error, Toast.LENGTH_SHORT).show();
                Log.e("카카오 메시지 보내기 실패", Log.getStackTraceString(error));
            } else {
                // 성공적으로 메시지 보냄
                Toast.makeText(getApplicationContext(), "초대 완료!", Toast.LENGTH_SHORT).show();
                Log.d("카카오 메시지 전송", "성공적으로 메시지를 보냈습니다.");
            }
        return Unit.INSTANCE;
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
        Log.d("network",result );
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PlanEditActivity.class);
        intent.putExtra("tripPlan",tripPlan);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
