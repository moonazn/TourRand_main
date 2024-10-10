package com.tourbus.tourrand;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InviteDialog extends Dialog {
    private String TourName;
    private String Nickname;
    private int tourId;
    private boolean isInviteStatus;
    private Handler handler;
    private HomeActivity homeActivity;

    public InviteDialog(@NonNull Context context, String TourName, String Nickname, int tourId, boolean isInviteStatus) {
        super(context);

        // Context가 HomeActivity인지 확인
        if (context instanceof HomeActivity) {
            this.homeActivity = (HomeActivity) context;
        } else {
            throw new ClassCastException("Context is not an instance of HomeActivity");
        }

        this.Nickname = Nickname;
        this.TourName = TourName;
        this.tourId = tourId;
        this.isInviteStatus = isInviteStatus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.invite_dialog);

        handler = new Handler(Looper.getMainLooper());

        TextView inviter = findViewById(R.id.invite);
        TextView inviteTour = findViewById(R.id.inviteTour);

        Button positiveBtn = findViewById(R.id.yesBtn);
        Button negativeBtn = findViewById(R.id.noBtn);

        inviter.setText(Nickname);
        inviteTour.setText(TourName);

        positiveBtn.setOnClickListener(v -> {
            String url = "https://api.tourrand.com/add";
            String data = "{ \"user_id\" : \"" + UserManager.getInstance().getUserId() + "\", \"tour_id\" : \"" + tourId + "\"}";

            new Thread(() -> {
                String result = httpPostBodyConnection(url, data);
                handler.post(() -> {
                    seeNetworkResult(result);
                    refreshHomeFragment();
                    dismiss(); // 다이얼로그 종료
                });
            }).start();
        });

        negativeBtn.setOnClickListener(v -> {
            String url = "https://api.tourrand.com/invite_delete";
            String data = "{ \"user_id\" : \"" + UserManager.getInstance().getUserId() + "\", \"tour_id\" : \"" + tourId + "\"}";

            new Thread(() -> {
                String result = httpPostBodyConnection(url, data);
                handler.post(() -> seeNetworkResult(result));
            }).start();
            Toast.makeText(getContext(), "초대가 거절되었습니다", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    private void refreshHomeFragment() {
        if (homeActivity != null) {
            FragmentManager fragmentManager = homeActivity.getSupportFragmentManager();
            HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(homeActivity);
            homeActivity.viewPager.setAdapter(adapter); // 어댑터 설정
            homeActivity.viewPager.setCurrentItem(0, false); // 첫 번째 프래그먼트로 이동
        }
    }


    public String httpPostBodyConnection(String urlData, String paramData) {
        StringBuilder returnData = new StringBuilder();
        BufferedReader br = null;
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlData);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] request_data = paramData.getBytes("utf-8");
                os.write(request_data);
            }

            int responseCode = conn.getResponseCode();
            Log.d("HTTP Response", "Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String responseData;
                while ((responseData = br.readLine()) != null) {
                    returnData.append(responseData);
                }
            } else {
                Log.e("HTTP Error", "Response Code: " + responseCode);
            }

        } catch (IOException e) {
            Log.e("HTTP Error", "Connection failed", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return returnData.toString();
    }

    public void seeNetworkResult(String result) {
        Log.d("Network Result", result);
    }
}
