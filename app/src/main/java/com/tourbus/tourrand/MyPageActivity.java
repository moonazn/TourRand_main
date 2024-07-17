package com.tourbus.tourrand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.sdk.user.UserApiClient;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MyPageActivity extends AppCompatActivity {

    private TextView name, email;
    private ImageView profileImg,backToHomeBtn;
    private TextView logout;

    boolean isPopupOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        name = findViewById(R.id.welcome);
        email = findViewById(R.id.email);
        profileImg = findViewById(R.id.profileImg);

        name.setText(SplashActivity.currentUser.getName() + "님, 환영합니다!");
        email.setText("이메일 : " + SplashActivity.currentUser.getEmail());

        Glide.with(profileImg).load(SplashActivity.currentUser.getProfileImage())
                .circleCrop().into(profileImg);


        logout = findViewById(R.id.logout);
        backToHomeBtn = findViewById(R.id.backToHomeBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        SplashActivity.currentUser.deleteUser();
                        System.out.println("currentUser deleted");


                        Intent intent = new Intent(MyPageActivity.this, HomeActivity.class);
                        startActivity(intent);
//                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        finish();

                        return null;
                    }
                });
            }
        });
        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, HomeFragment1.class);
                startActivity(intent);
            }
        });
    }
}