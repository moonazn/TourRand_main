package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DstActivity extends AppCompatActivity {

    private ImageView back;
    private Button next, reroll;
    private TextView dst1, dst2, dst3;
    private TextView selectedTextView = null;
    private String selectedLocation = null;
    private TextView noanswer;
    Place departureDocument;
    boolean withAnimal;
    public String fin, withAnimaltoString;

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
        int planDate = intent.getIntExtra("planDate", 0);
        departureDocument = intent.getParcelableExtra("departureDocument");
        Place destination = intent.getParcelableExtra("destination");

        if(withAnimal == true){
            withAnimaltoString = "반려동물";
        }else{
            withAnimaltoString = "인간만";
        }




        // PreTrip 객체 생성
        PreTrip preTrip = new PreTrip(withAnimal, planDate, departureDocument, destination);

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
                "서울", "고성", "속초", "양양", "인제", "양구", "강릉", "동해", "삼척", "태백",
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
                "김해", "부산", "창원", "하동", "진주", "사천", "고성", "거제", "남해", "통영",
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
            try {
                Thread.sleep(3000); // 실제 서버 통신 코드로 대체
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
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
            intent.putExtra("withAnimal", withAnimal);
            intent.putExtra("selectedLocation", selectedLocation);
            intent.putExtra("departureDocument", departureDocument);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }
}
