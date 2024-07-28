package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomRouletteActivity extends AppCompatActivity {
    private ArrayList<String> elements;
    private LuckyWheel luckyWheel;

    private ImageView back;

    private int targetIndex = -1; // 초기화

    private static final int[] COLOR_PALETTE = {
            R.color.random1,
            R.color.random2,
            R.color.random3,
            R.color.random4,
            R.color.random5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_roulette);

        elements = getIntent().getStringArrayListExtra("elements");

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomRouletteActivity.this, CustomSetActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        luckyWheel = findViewById(R.id.roulette);

        if (elements != null && !elements.isEmpty()) {
            setupLuckyWheel();
        } else {
            Toast.makeText(this, "No elements found", Toast.LENGTH_SHORT).show();
        }
        spinRoulette();


    }

    private void setupLuckyWheel() {
        List<WheelItem> wheelItems = new ArrayList<>();
        int colorIndex = 0;
        for (String element : elements) {
            // COLOR_PALETTE에서 순서대로 색상 선택
            int color = ContextCompat.getColor(this, COLOR_PALETTE[colorIndex % COLOR_PALETTE.length]);
            Bitmap bitmap = BitmapUtils.textAsBitmap(element, 30, Color.WHITE);
            wheelItems.add(new WheelItem(color, bitmap));
            colorIndex++;
        }
        luckyWheel.addWheelItems(wheelItems);

        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                if (targetIndex != -1 && targetIndex < elements.size()) {
                    // 결과 표시 로직
                    String selectedElement = elements.get(targetIndex - 1);
                    Toast.makeText(CustomRouletteActivity.this, "Selected element: " + selectedElement, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CustomRouletteActivity.this, "Invalid target index", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void spinRoulette() {
        targetIndex = new Random().nextInt(elements.size());
        luckyWheel.rotateWheelTo(targetIndex);
    }
}
