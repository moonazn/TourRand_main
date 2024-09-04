package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new HomeViewPagerAdapter(this));

        // 넘어온 Intent에 따라 특정 프래그먼트로 이동
        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
        if ("homeFragment1".equals(fragmentToLoad)) {
            viewPager.setCurrentItem(0, false); // HomeFragment1의 위치를 0번째로 가정
        }
    }
}