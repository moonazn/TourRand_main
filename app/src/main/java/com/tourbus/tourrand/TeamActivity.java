package com.tourbus.tourrand;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friends;
import com.kakao.sdk.user.UserApiClient;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

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
                                // 로그인 성공 후 친구 목록 가져오기
                                fetchFriendsList();
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
//        TripPlan tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
//        planDate = tripPlan.getTravelDate();
//        tour_name = tripPlan.getTripName();
//        tourId = tripPlan.getTourId();
//        weatherPageIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TeamActivity.this, WeatherActivity.class);
//                intent.putExtra("tripPlan",tripPlan);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//                finish();
//            }
//        });
//        editPageIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TeamActivity.this, PlanEditActivity.class);
//                intent.putExtra("tripPlan",tripPlan);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//                finish();
//            }
//        });
//        randomPageIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(TeamActivity.this, RandomActivity.class);
//                intent.putExtra("tripPlan",tripPlan);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//                finish();
//            }
//        });
    }
    // 친구 목록 가져오는 함수 (친구 목록을 불러와 멀티피커로 표시)
    private void fetchFriendsList() {
        TalkApiClient.getInstance().friends((Friends friends, Throwable error) -> {
            if (error != null) {
                Toast.makeText(getApplicationContext(), "친구 목록 가져오기 실패" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("친구목록 가져오기 실패", error.getMessage());
            } else if (friends != null && friends.getElements().size() > 0) {
                for (int i = 0; i < friends.getElements().size(); i++) {
                    Friend friend = (Friend) friends.getElements().get(i);
                    String friendNickname = friend.getNickname();
                    String friendUuid = friend.getUuid(); // UUID도 가져올 수 있음
                    String friendProfileImg = friend.getUserProfileImg();
                    friendList.add(new Friend(friendNickname, friendUuid,friendProfileImg));
                }
                // 멀티피커 팝업 띄우기
                showMultiPickerDialog();
            } else {
                Toast.makeText(getApplicationContext(), "친구 목록이 없습니다.", Toast.LENGTH_SHORT).show();
            }
            return null;
        });
    }
//    private void fetchFriendsList() {
//        TalkApiClient.getInstance().friends((Friends friends, Throwable error) -> {
//            if (error != null) {
//                // 동의가 완료되지 않았거나 API 호출 실패
//                Toast.makeText(getApplicationContext(), "친구 목록 가져오기 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            } else {
//                // 친구 목록 처리 로직
//                if (friends.getElements().size() > 0) {
//                    // 성공적으로 친구 목록을 가져온 경우
//                    for (Friend friend : friends.getElements()) {
//                        String friendName = friend.getNickname();
//                        String friendUuid = friend.getUuid();
//                        String friendProfileImg = friend.getUserProfileImg();
//                        friendList.add(new Friend(friendName, friendUuid, friendProfileImg));
//                    }
//                    showMultiPickerDialog(); // 멀티피커 다이얼로그 띄우기
//                } else {
//                    Toast.makeText(getApplicationContext(), "친구 목록이 없습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//            return null;
//        });
//    }

    private void showMultiPickerDialog() {
        // 친구 이름 배열 생성
        String[] friendNames = new String[friendList.size()];
        for (int i = 0; i < friendList.size(); i++) {
            friendNames[i] = friendList.get(i).getNickname();
        }

        // 체크 여부를 저장할 배열 초기화
        checkedItems = new boolean[friendList.size()];

        // AlertDialog 멀티 선택 팝업 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("친구 선택");
        builder.setMultiChoiceItems(friendNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                // 선택된 항목 관리
                if (isChecked) {
                    selectedFriends.add(friendList.get(indexSelected).getNickname());
                } else if (selectedFriends.contains(friendList.get(indexSelected).getNickname())) {
                    selectedFriends.remove(friendList.get(indexSelected).getNickname());
                }
            }
        });

        // 확인 버튼
        builder.setPositiveButton("확인", (dialog, id) -> {
            if (selectedFriends.isEmpty()) {
                Toast.makeText(getApplicationContext(), "선택된 친구가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "선택된 친구: " + selectedFriends, Toast.LENGTH_SHORT).show();
                // 선택된 친구를 이용해 추가 작업을 수행할 수 있음 + 서버로 통신
            }
        });

        // 취소 버튼
        builder.setNegativeButton("취소", (dialog, id) -> dialog.dismiss());

        // 팝업 띄우기
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
