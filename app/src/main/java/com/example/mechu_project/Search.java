package com.example.mechu_project;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class Search extends AppCompatActivity {

    ImageView search_search1;
    LinearLayout search_search;
    View black_line;

    EditText edittext;
    Button deleteAllButton; // 검색 버튼 추가
    ChipGroup chipGroup; // Chip을 추가할 ChipGroup 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_search1 = findViewById(R.id.search_search1);
        black_line = findViewById(R.id.black_line);
        edittext = findViewById(R.id.edittext);
        search_search = findViewById(R.id.search_search);
        deleteAllButton = findViewById(R.id.deleteAllButton);

        // 검색 버튼과 ChipGroup 연결
        chipGroup = findViewById(R.id.chip_group); // ChipGroup의 ID를 chip_group으로 가정

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

        // 검색 버튼 클릭 이벤트 리스너 추가
        search_search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = edittext.getText().toString(); // 사용자가 입력한 텍스트를 가져옵니다.
                if (!searchText.isEmpty()) {
                    Chip chip = new Chip(Search.this);
                    chip.setText(searchText);
                    chip.setCloseIconVisible(true);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chipGroup.removeView(chip); // Chip 삭제
                        }
                    });
                    chipGroup.addView(chip); // ChipGroup에 Chip 추가
                    edittext.setText(""); // EditText 초기화
                }
            }
        });
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipGroup.removeAllViews(); // ChipGroup 내의 모든 뷰(Chip)를 삭제합니다.
            }
        });


    }//oncreate
}//appcom
