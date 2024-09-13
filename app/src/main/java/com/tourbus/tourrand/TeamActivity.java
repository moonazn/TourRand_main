package com.tourbus.tourrand;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friends;

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


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 친구 목록 가져오기
                TalkApiClient.getInstance().friends((Friends friends, Throwable error) -> {
                    if (error != null) {
                        Toast.makeText(getApplicationContext(), "친구 목록 가져오기 실패", Toast.LENGTH_SHORT).show();
                    } else {
                        if (friends.getElements().size() > 0) {
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
                    }
                    return null;
                });
            }
        });



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
                // 선택된 친구를 이용해 추가 작업을 수행할 수 있음
            }
        });

        // 취소 버튼
        builder.setNegativeButton("취소", (dialog, id) -> dialog.dismiss());

        // 팝업 띄우기
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
