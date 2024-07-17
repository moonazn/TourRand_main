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

import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

public class AnimalQActivity extends AppCompatActivity {

    private ImageView back;
    private Button nextBtn;
    private SingleSelectToggleGroup withAnimalToggle;
    boolean withAnimal, ischecked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_qactivity);

        back = findViewById(R.id.back);
        nextBtn = findViewById(R.id.nextBtn);
        withAnimalToggle = findViewById(R.id.withAnimal);
        TextView noAnswer = findViewById(R.id.noAnswer);

        withAnimalToggle.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                ischecked = true;
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

                if (ischecked != true) {
                    if (noAnswer.getVisibility() != View.VISIBLE)
                        noAnswer.setVisibility(View.VISIBLE);

                    Animation shake = AnimationUtils.loadAnimation(AnimalQActivity.this, R.anim.shake_fast);
                    noAnswer.startAnimation(shake);

                } else {
                    Intent intent = new Intent(AnimalQActivity.this, DateQActivity.class);
                    intent.putExtra("withAnimal", withAnimal);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }

            }
        });

    }
}