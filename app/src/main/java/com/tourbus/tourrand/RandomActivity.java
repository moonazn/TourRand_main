package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RandomActivity extends AppCompatActivity {
    TripPlan tripPlan;
    String planDate;
    String tour_name;
    int tourId;

    private Button random_one, random_custom, random_youtube;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        tripPlan = (TripPlan) getIntent().getSerializableExtra("tripPlan");
        planDate = tripPlan.getTravelDate();
        tour_name = tripPlan.getTripName();
        tourId = tripPlan.getTourId();

        random_one = findViewById(R.id.random_one);
        random_custom = findViewById(R.id.random_custom);
        random_youtube = findViewById(R.id.random_youtube);

        random_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RandomActivity.this, RouletteMemberActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        random_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RandomActivity.this, RandomCustomSetActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        random_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostRequest("힐링");
            }
        });

        // bottom.xml에서 ImageView 찾기
        ImageView editPageIcon = findViewById(R.id.editPage);
        ImageView weatherPageIcon = findViewById(R.id.weatherPage);
        ImageView randomPageIcon = findViewById(R.id.randomPage);
        ImageView groupPageIcon = findViewById(R.id.groupPage);



        // 현재 화면에 해당하는 아이콘의 이미지 변경
        randomPageIcon.setImageResource(R.drawable.random_on);
        editPageIcon.setImageResource(R.drawable.edit_home_off);
        weatherPageIcon.setImageResource(R.drawable.weather_off);
        groupPageIcon.setImageResource(R.drawable.group_off);

        weatherPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RandomActivity.this, WeatherActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        editPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RandomActivity.this, PlanEditActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        groupPageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RandomActivity.this, TeamActivity.class);
                intent.putExtra("tripPlan",tripPlan);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
    private void sendPostRequest(String theme) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("https://api.tourrand.com/youtube");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Create JSON payload
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("theme", theme);

                // Write JSON payload to the output stream
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // Process response
                    processResponse(response.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }


    private void processResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String url = jsonResponse.getString("url");
            Log.d("processResponse", url);
            openYoutubeLink(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openYoutubeLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Check if there is an app that can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.d("openYoutubeLink", "No application can handle this request.");
                // Open in a web browser if no app can handle the request
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        } else {
            Log.d("openYoutubeLink", "No application can handle this request even for a browser.");
            // Open in a web browser if no app can handle the request
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RandomActivity.this, PlanEditActivity.class);
        intent.putExtra("tripPlan",tripPlan);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }







}