package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public ViewPager2 viewPager;
    private long lastTimeBackPressed = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("홈액티비티", "시작");

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new HomeViewPagerAdapter(this));

        Intent intent = getIntent();
        if(intent !=null){
            String inviteTourName = intent.getStringExtra("inviteTourName");
            String inviteNickname = intent.getStringExtra("inviteNickname");
            int inviteTourid = intent.getIntExtra("inviteTourId",0);
            if(inviteNickname!=null){
                InviteDialog dialog = new InviteDialog(this, inviteTourName, inviteNickname, inviteTourid, true);
                dialog.show();
            }
        //Log.d("홈프래그먼트",inviteTourName);

        }


        // 넘어온 Intent에 따라 특정 프래그먼트로 이동
        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
        if ("homeFragment1".equals(fragmentToLoad)) {
            Log.d("홈액티비티","인텐트");
            viewPager.setCurrentItem(0, false); // HomeFragment1의 위치를 0번째로 가정

        }
    }
    @Override
    public void onBackPressed() {
        // 2초 이내에 백버튼을 두 번 누르면 앱 종료
        if (System.currentTimeMillis() > lastTimeBackPressed + 2000) {
            lastTimeBackPressed = System.currentTimeMillis();
            // "뒤로가기 버튼을 한번 더 누르면 종료됩니다" 메시지 출력
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 백버튼을 두 번 눌렀다면 토스트 메시지를 종료하고 앱을 종료
        if (System.currentTimeMillis() <= lastTimeBackPressed + 2000) {
            //if (backToast != null) backToast.cancel();  // 토스트 메시지 종료
            super.onBackPressed();  // 앱 종료
        }
    }
}