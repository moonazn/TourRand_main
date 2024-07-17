package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class DstActivity extends AppCompatActivity {

    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dst);

        back = findViewById(R.id.back);

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
    }
}