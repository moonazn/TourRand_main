package com.tourbus.tourrand;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomCustomRouletteActivity extends AppCompatActivity {
    private ArrayList<String> elements;
    private LuckyWheel luckyWheel;
    private ImageView back;
    private Button next;
    private String selected;
    private int targetIndex = -1;
    String planDate;
    String tour_name;
    int tourId;
    TripPlan tripPlan;
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

        tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        planDate = tripPlan.getTravelDate();
        tour_name = tripPlan.getTripName();
        tourId = tripPlan.getTourId();

        elements = getIntent().getStringArrayListExtra("elements");

        back = findViewById(R.id.back);
        next = findViewById(R.id.nextBtn);
        luckyWheel = findViewById(R.id.roulette);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RandomCustomRouletteActivity.this, CustomSetActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        if (elements != null && !elements.isEmpty()) {
            setupLuckyWheel();
            spinRoulette();
        } else {
            Toast.makeText(this, "후보를 입력해주세요", Toast.LENGTH_SHORT).show();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ServerCommunicationTask().execute();

                Intent intent = new Intent(RandomCustomRouletteActivity.this, RandomActivity.class);
                intent.putExtra("selected", selected);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void setupLuckyWheel() {
        List<WheelItem> wheelItems = new ArrayList<>();
        int colorIndex = 0;
        for (String element : elements) {
            int color = ContextCompat.getColor(this, COLOR_PALETTE[colorIndex % COLOR_PALETTE.length]);

            //luckyWheel.getWheelView().setWheelBackgoundWheel(Color.RED);

            Typeface customFont = ResourcesCompat.getFont(RandomCustomRouletteActivity.this,R.font.pyeongchang_regular);
            Bitmap bitmap = BitmapUtils.textAsBitmap(element, 100, Color.WHITE,customFont);
            wheelItems.add(new WheelItem(color, bitmap));
            colorIndex++;
        }
        luckyWheel.addWheelItems(wheelItems);

        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                if (targetIndex != -1 && targetIndex < elements.size()) {
                    String selectedElement = elements.get(targetIndex);
                    selected = selectedElement;
                    Toast.makeText(RandomCustomRouletteActivity.this, "축 당 첨" + selectedElement, Toast.LENGTH_LONG).show();
                    next.setVisibility(View.VISIBLE);
                    next.setText(selectedElement);
                } else {
                    Toast.makeText(RandomCustomRouletteActivity.this, "Invalid target index", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void spinRoulette() {
        if (elements == null || elements.isEmpty()) {
            return;
        }
        targetIndex = new Random().nextInt(elements.size());
        luckyWheel.rotateWheelTo(targetIndex + 1);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, RandomActivity.class);
        intent.putExtra("tripPlan",tripPlan);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

//    private class ServerCommunicationTask extends AsyncTask<Void, Void, Void> {
//        ProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(CustomRouletteActivity.this);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                Thread.sleep(3000); // 실제 서버 통신 코드로 대체
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//            Intent intent = new Intent(CustomRouletteActivity.this, PlanViewActivity.class);
//            intent.putExtra("selectedLocation", selectedLocation);
//            intent.putExtra("previousActivity", previousActivity);
//            startActivity(intent);
//            overridePendingTransition(0, 0);
//            finish();
//        }
//    }
}
