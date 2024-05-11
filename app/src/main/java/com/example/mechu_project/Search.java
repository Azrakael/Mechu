package com.example.mechu_project;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.ViewGroup.LayoutParams; // LayoutParams 임포트 추가
public class Search extends AppCompatActivity {

    ImageView search_search1;
    LinearLayout search_search;
    View black_line;

    EditText edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_search1 = findViewById(R.id.search_search1);
        black_line = findViewById(R.id.black_line);
        edittext = findViewById(R.id.edittext);
        search_search = findViewById(R.id.search_search);

        // black_line 초기 상태를 INVISIBLE로 설정
        black_line.setVisibility(View.INVISIBLE);

        search_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검은 선을 보이게 하기
                black_line.setVisibility(View.VISIBLE);

                // search_search1 이동 애니메이션
                search_search1.animate()
                        .translationX(850)
                        .setDuration(500)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();

                // black_line 초기 위치 설정 및 초기 길이 설정
                black_line.setTranslationX(-850);
                ViewGroup.LayoutParams params = black_line.getLayoutParams();
                params.width = 850; // 최종 길이를 미리 설정
                black_line.setLayoutParams(params);

                // black_line 이동 애니메이션
                black_line.animate()
                        .translationX(0) // 최종 위치로 이동
                        .setDuration(500)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // 애니메이션 종료 시 검은 선 숨기기
                                black_line.setVisibility(View.INVISIBLE);
                                // edittext 보이게 하기
                                edittext.setVisibility(View.VISIBLE);
                            }
                        })
                        .start();
            }
        });
    }
}
