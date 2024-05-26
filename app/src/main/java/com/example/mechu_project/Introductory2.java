package com.example.mechu_project;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class Introductory2 extends AppCompatActivity {

    ImageView miniLogo, app_name, bar;
    LottieAnimationView lottie;
    Button letgo;
    TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory2);

        letgo = findViewById(R.id.letgo);
        miniLogo = findViewById(R.id.miniLogo);
        app_name = findViewById(R.id.appName);
        bar = findViewById(R.id.bar);
        lottie = findViewById(R.id.lottie);
        welcomeMessage = findViewById(R.id.welcomeMessage);

        // 애니메이션 설정
        bar.animate().translationY(-400).setDuration(700).setStartDelay(2800);
        app_name.animate().translationY(-400).setDuration(700).setStartDelay(2800);
        miniLogo.animate().translationY(-400).setDuration(700).setStartDelay(2800);
        lottie.animate().translationY(3000).setDuration(700).setStartDelay(2500);

        // 환영 메시지 설정
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = preferences.getString("user_name", "");
        welcomeMessage.setText(userName + "님, 환영합니다!");

        // 환영 메시지 애니메이션 설정
        welcomeMessage.setAlpha(0f);
        welcomeMessage.setVisibility(View.VISIBLE);

        welcomeMessage.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(welcomeMessage, "alpha", 0f, 1f);
                fadeIn.setDuration(1000);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.start();
            }
        }, 3500); // 모든 아이콘 애니메이션 이후에 시작

        letgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Introductory2.this, MainActivity.class);
                startActivity(intent);
                finish(); // Introductory2 액티비티를 종료
            }
        });
    }
}
