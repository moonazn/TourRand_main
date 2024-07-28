package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DepartureQActivity extends AppCompatActivity {

    private ImageView back;
    private Button nextBtn;
    Location departure;
    private ImageView searchIcon;
    private FindDepartureDialog findDepartureDialog;

    private TextView departureTextView;
    private Place departureDocument;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departure_qactivity);

        back = findViewById(R.id.back);
        nextBtn = findViewById(R.id.nextBtn);
        TextView noAnswer = findViewById(R.id.noAnswer);
        searchIcon = findViewById(R.id.searchIcon);
        departureTextView = findViewById(R.id.departureTextView);


        // 이전 액티비티에서 전달된 데이터 받기
        Intent intent = getIntent();
        boolean withAnimal = intent.getBooleanExtra("withAnimal", false);
        String planDate = intent.getStringExtra("planDate");


        findDepartureDialog = new FindDepartureDialog(this);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinishing() && !isDestroyed()) {
                    findDepartureDialog.show();
                }
            }
        });

        // 팝업에서 선택된 장소 정보를 받아오는 리스너
        findDepartureDialog.setOnItemClickListener(new FindDepartureDialog.OnItemClickListener() {
            @Override
            public void onItemClick(Place document) {
                if (document != null) {
                    departureDocument = document;
                    departure = new Location(departureDocument.getPlaceName(), 0, 0);
                    departureTextView.setText(document.getPlaceName());
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DepartureQActivity.this, DateQActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (departureDocument == null) {
                    if (noAnswer.getVisibility() != View.VISIBLE)
                        noAnswer.setVisibility(View.VISIBLE);

                    Animation shake = AnimationUtils.loadAnimation(DepartureQActivity.this, R.anim.shake_fast);
                    noAnswer.startAnimation(shake);

                } else {
                    Intent intent = new Intent(DepartureQActivity.this, DstActivity.class);
                    intent.putExtra("withAnimal", withAnimal);
                    intent.putExtra("planDate", planDate);
                    intent.putExtra("departureDocument", departureDocument);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }


            }
        });
    }
}