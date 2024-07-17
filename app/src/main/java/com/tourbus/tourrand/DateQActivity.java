package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateQActivity extends AppCompatActivity {

    private ImageView back;
    private Button nextBtn;

    private String planDate;
    private TextView tripDateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_qactivity);

        back = findViewById(R.id.back);
        nextBtn = findViewById(R.id.nextBtn);
        tripDateTextView = findViewById(R.id.tripDateTextView);
        ImageView calendarIcon = findViewById(R.id.calendarIcon);
        TextView noAnswer = findViewById(R.id.noAnswer);


        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });

        // 이전 액티비티에서 전달된 데이터 받기
        Intent intent = getIntent();
        boolean withAnimal = intent.getBooleanExtra("withAnimal", false);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DateQActivity.this, AnimalQActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (planDate == null) {
                    if (noAnswer.getVisibility() != View.VISIBLE)
                        noAnswer.setVisibility(View.VISIBLE);

                    Animation shake = AnimationUtils.loadAnimation(DateQActivity.this, R.anim.shake_fast);
                    noAnswer.startAnimation(shake);

                } else {
                    Intent intent = new Intent(DateQActivity.this, DepartureQActivity.class);
                    intent.putExtra("withAnimal", withAnimal);
                    intent.putExtra("planDate", planDate);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }

            }
        });
    }

    private void showDateRangePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        builder.setCalendarConstraints(constraintsBuilder.build());
        final MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                String startDateStr = sdf.format(selection.first);
                String endDateStr = sdf.format(selection.second);

                planDate = startDateStr + " ~ " + endDateStr;
                tripDateTextView.setText(planDate);
            }
        });

        dateRangePicker.show(getSupportFragmentManager(), "date_range_picker");
    }
}