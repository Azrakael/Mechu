package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;

public class IntroductoryActivity extends AppCompatActivity {

    ImageView logo,app_name,bar;
    LottieAnimationView lottie;
    LinearLayout login_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);

        logo = findViewById(R.id.logo);
        app_name = findViewById(R.id.app_name);
        bar = findViewById(R.id.bar);
        lottie = findViewById(R.id.lottie);
        login_layout = findViewById(R.id.login_layout);
        //로그인창 초기투명도
        login_layout.setAlpha(0f);

        bar.animate().translationY(-1100).setDuration(700).setStartDelay(2800);
        app_name.animate().translationY(-1100).setDuration(700).setStartDelay(2800);
        logo.animate().translationY(-1100).setDuration(700).setStartDelay(2800);
        lottie.animate().translationY(3000).setDuration(700).setStartDelay(2500);

        login_layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                login_layout.setVisibility(View.VISIBLE);
                login_layout.animate().alpha(1f).setDuration(500).start();
            }
        }, 3400); // 3.4초 후에 실행

    }
}