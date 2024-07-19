package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DstActivity extends AppCompatActivity {

    private ImageView back;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dst);

        back = findViewById(R.id.back);
        next = findViewById(R.id.nextBtn);

        // 이전 액티비티들에서 전달된 데이터 받기 (‼️이걸 여기서할지 / 로딩창에서할지)
        Intent intent = getIntent();
        boolean withAnimal = intent.getBooleanExtra("withAnimal", false);
        int planDate = intent.getIntExtra("planDate", 0);
        Location departure = intent.getParcelableExtra("departure");
        Location destination = intent.getParcelableExtra("destination");

        // PreTrip 객체 생성
        PreTrip preTrip = new PreTrip(withAnimal, planDate, departure, destination);

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

                // 서버 통신을 비동기적으로 실행
                new ServerCommunicationTask().execute();

            }
        });
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
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }
}