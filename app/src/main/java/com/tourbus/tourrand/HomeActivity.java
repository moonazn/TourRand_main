package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public ViewPager2 viewPager;

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
}