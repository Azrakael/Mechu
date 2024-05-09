package com.example.mechu_project;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.ViewGroup.LayoutParams; // LayoutParams 임포트 추가

public class Search extends AppCompatActivity {

    LinearLayout search_search;
    View black_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_search = findViewById(R.id.search_search);
        black_line = findViewById(R.id.black_line);

        search_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검은 선을 보이게 하기
                black_line.setVisibility(View.VISIBLE);

                // search_search 이동 애니메이션
                search_search.animate()
                        .translationX(850)
                        .setDuration(500)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();

                // black_line 초기 위치 설정 및 초기 길이 설정
                black_line.setTranslationX(-850);
                final LayoutParams params = black_line.getLayoutParams();
                params.width = 0; // 초기 길이를 0으로 설정
                black_line.setLayoutParams(params);

                // black_line 이동 및 길이 늘리기 애니메이션
                black_line.animate()
                        .translationX(0) // 최종 위치로 이동
                        .setDuration(500)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // 애니메이션 종료 시 길이 조정
                                params.width = 850; // 최종 길이 설정
                                black_line.setLayoutParams(params);
                            }
                        })
                        .start();
            }
        });
    }
}
