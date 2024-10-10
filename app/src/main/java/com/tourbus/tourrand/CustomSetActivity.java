package com.tourbus.tourrand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class CustomSetActivity extends AppCompatActivity {

    private LinearLayout elementContainer;
    private Button addButton, nextButton;
    private ArrayList<String> elements;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_set);

        NotiDialog dialog = new NotiDialog(this);
        dialog.show();

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomSetActivity.this, QuestionActivity.class);
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

        Intent intent = new Intent(this, CustomRouletteActivity.class);
        intent.putStringArrayListExtra("elements", elements);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
