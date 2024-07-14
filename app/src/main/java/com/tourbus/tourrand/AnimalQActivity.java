package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

public class AnimalQActivity extends AppCompatActivity {

    //개쩌네 진심

    private ImageView back;
    private Button nextBtn;
    private SingleSelectToggleGroup withAnimalToggle;
    boolean withAnimal = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_qactivity);

        back = findViewById(R.id.back);
        nextBtn = findViewById(R.id.nextBtn);
        withAnimalToggle = findViewById(R.id.withAnimal);

        withAnimalToggle.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (checkedId == R.id.choice_yes)
                    withAnimal = true;
                else
                    withAnimal = false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnimalQActivity.this, QuestionActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnimalQActivity.this, DepartureQActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

    }
}