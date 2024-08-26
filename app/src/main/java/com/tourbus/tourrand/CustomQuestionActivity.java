package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.ProgressDialog;
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

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CustomQuestionActivity extends AppCompatActivity {

    private ImageView back;
    private Button nextBtn;
    private SingleSelectToggleGroup withAnimalToggle;
    boolean withAnimal, ischecked;

    private String planDate;
    private TextView tripDateTextView;

    private int tripLength;

    String selectedLocation, previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_question);

        back = findViewById(R.id.back);
        nextBtn = findViewById(R.id.nextBtn);
        withAnimalToggle = findViewById(R.id.withAnimal);
        TextView noAnswer = findViewById(R.id.noAnswer);

        tripDateTextView = findViewById(R.id.tripDateTextView);
        ImageView calendarIcon = findViewById(R.id.calendarIcon);


        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });

        // 이전 액티비티에서 전달된 데이터 받기
        Intent intent = getIntent();
        withAnimal = intent.getBooleanExtra("withAnimal", false);
        selectedLocation = intent.getStringExtra("selectedLocation");
        previousActivity = intent.getStringExtra("previousActivity");

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
                Intent intent = new Intent(CustomQuestionActivity.this, CustomRouletteActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ischecked != true || planDate == null) {
                    if (noAnswer.getVisibility() != View.VISIBLE)
                        noAnswer.setVisibility(View.VISIBLE);

                    Animation shake = AnimationUtils.loadAnimation(CustomQuestionActivity.this, R.anim.shake_fast);
                    noAnswer.startAnimation(shake);

                } else {
//                    Intent intent = new Intent(CustomQuestionActivity.this, DateQActivity.class);
//                    intent.putExtra("withAnimal", withAnimal);
//                    startActivity(intent);
//                    overridePendingTransition(0, 0);
//                    finish();

                    new ServerCommunicationTask().execute();
                }

            }
        });
    }

    private class ServerCommunicationTask extends AsyncTask<Void, Void, Void> {
        android.app.ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CustomQuestionActivity.this);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
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
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Intent intent = new Intent(CustomQuestionActivity.this, PlanViewActivity.class);
            intent.putExtra("withAnimal", withAnimal);
            intent.putExtra("selectedLocation", selectedLocation);
            intent.putExtra("previousActivity", previousActivity);
            intent.putExtra("planDate", planDate);
            intent.putExtra("tripLength", tripLength);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
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

                // Calculate the difference in days
                long startDateMillis = selection.first;
                long endDateMillis = selection.second;
                long diffInMillis = endDateMillis - startDateMillis;
                int numberOfDays = (int) TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1;

                tripLength = numberOfDays;

                planDate = startDateStr + " ~ " + endDateStr;
                tripDateTextView.setText(planDate);
            }
        });

        dateRangePicker.show(getSupportFragmentManager(), "date_range_picker");
    }
}