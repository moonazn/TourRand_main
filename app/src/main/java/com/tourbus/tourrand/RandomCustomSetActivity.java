package com.tourbus.tourrand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RandomCustomSetActivity extends AppCompatActivity {

    private LinearLayout elementContainer;
    private Button addButton, nextButton;
    private ArrayList<String> elements;
    private ImageView back;
    String planDate;
    String tour_name;
    int tourId;
    TripPlan tripPlan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_custom_set);

        tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        planDate = tripPlan.getTravelDate();
        tour_name = tripPlan.getTripName();
        tourId = tripPlan.getTourId();

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RandomCustomSetActivity.this, RandomActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });


        elementContainer = findViewById(R.id.elementContainer);
        addButton = findViewById(R.id.addButton);
        nextButton = findViewById(R.id.nextButton);
        elements = new ArrayList<>();

        addButton.setOnClickListener(v -> addElementField());
        nextButton.setOnClickListener(v -> goToSecondActivity());
    }

    private void addElementField() {
        EditText editText = new EditText(this);
        editText.setHint("여행지 후보 입력");
        elementContainer.addView(editText);
    }

    private void goToSecondActivity() {
        for (int i = 0; i < elementContainer.getChildCount(); i++) {
            View view = elementContainer.getChildAt(i);
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                String element = editText.getText().toString().trim();
                if (!element.isEmpty()) {
                    elements.add(element);
                }
            }
        }

        Intent intent = new Intent(this, RandomCustomRouletteActivity.class);
        intent.putStringArrayListExtra("elements", elements);
        intent.putExtra("tripPlan",tripPlan);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
