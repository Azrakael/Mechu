package com.example.mechu_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // 하트 클릭시 색이 채워지는 에니메이션 추가.효과
    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;

    // 탭 상수 정의
    private static final int TAB_HOME = R.id.tab_home;
    private static final int TAB_SEARCHING = R.id.tab_searching;
    private static final int TAB_MECHU = R.id.tab_mechu;
    private static final int TAB_SETTINGS = R.id.tab_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 클릭시 하트가 채워지는 부분 지속시간,
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator); //바운스 효과

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == TAB_HOME) {
                    // 홈 화면으로 이동
                    // Intent homeIntent = new Intent(MainActivity.this, Home.class);
                    // startActivity(homeIntent);
                    return true;
                } else if (item.getItemId() == TAB_SEARCHING) {
                    // 검색 화면으로 이동
                     Intent searchIntent = new Intent(MainActivity.this, Search.class);
                     startActivity(searchIntent);
                    return true;
                } else if (item.getItemId() == TAB_MECHU) {
                    // 메뉴추천 화면으로 이동
                    Intent menuRecommendationIntent = new Intent(MainActivity.this, Recommend.class);
                    startActivity(menuRecommendationIntent);
                    return true;
                } else if (item.getItemId() == TAB_SETTINGS) {
                    // 내정보 화면으로 이동
                     Intent profileIntent = new Intent(MainActivity.this, Profile.class);
                     startActivity(profileIntent);
                    return true;
                }
                return false;
            }
        });
     }

    // 각 버튼의 클릭 이벤트 처리
    public void onFavoriteButtonClick(View view) {
        // 클릭한 버튼에 애니메이션 적용
        view.startAnimation(scaleAnimation);
    }
}
