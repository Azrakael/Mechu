package com.example.mechu_project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class Search extends AppCompatActivity {
    ImageView search_search1;      //검색이미지
    LinearLayout search_search;     //검색이미지,edittext 부분을 담는 레이아웃
    View black_line;
    EditText edittext;
    Button deleteAllButton; // 검색 버튼 추가
    ChipGroup chipGroup; // Chip을 추가할 ChipGroup 선언

    private DatabaseHelper dbHelper;     //dbHelper 선언
    private static final String KEY_CHIPS = "chips";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_search1 = findViewById(R.id.search_search1);
        black_line = findViewById(R.id.black_line);
        edittext = findViewById(R.id.edittext);
        search_search = findViewById(R.id.search_search);
        deleteAllButton = findViewById(R.id.deleteAllButton);

        // DatabaseHelper 초기화
        dbHelper = new DatabaseHelper(this);


        // SharedPreferences 초기화 chip을저장하기 위한 부분(최근검색어 부분)
        sharedPreferences = getSharedPreferences("SearchPrefs", MODE_PRIVATE);

        // 검색 버튼과 ChipGroup 연결
        chipGroup = findViewById(R.id.chip_group);

        // black_line 초기 상태를 INVISIBLE로 설정
        black_line.setVisibility(View.INVISIBLE);

        // Load saved chips
        loadChips();

        search_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검은 선을 보이게 하기
                black_line.setVisibility(View.VISIBLE);

                // search_search1 이동 애니메이션
                search_search1.animate()
                        .translationX(850)
                        .translationY(30)
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
                                // edittext에 포커스 주기
                                edittext.requestFocus();
                                // 키보드 표시하기
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        })
                        .start();
            }
        });
        // 검색 버튼 클릭 이벤트 리스너 추가
        search_search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = edittext.getText().toString().trim();
                LinearLayout noResult = findViewById(R.id.noresult);
                DatabaseHelper dbHelper = new DatabaseHelper(Search.this);
                LinearLayout favoritesearch = findViewById(R.id.favoritesearch);

                //SharedPreferences에서 사용자 ID가져옴
                SharedPreferences userPrefs = getSharedPreferences("user_prefs",MODE_PRIVATE);
                String userId = userPrefs.getString("user_id",null);

                // 데이터베이스에서 음식 정보 가져오기
                Cursor cursor = dbHelper.getFoodInfo(searchText);

                if (cursor != null && cursor.getCount() > 0) {
                    // 검색 결과가 있는 경우
                    addChip(searchText);
                    saveChips();
                    edittext.setText("");

                    // 검색 기록 저장
                    if (userId != null) {
                        dbHelper.insertOrUpdateSearchRecord(userId, searchText);
                    }

                    Intent intent = new Intent(Search.this, SearchResult.class);
                    intent.putExtra("SEARCH_TERM", searchText);
                    startActivity(intent);
                } else {
                    // 검색 결과가 없는 경우
                    noResult.setVisibility(View.VISIBLE); // 메시지 보이기
                    favoritesearch.setVisibility(View.INVISIBLE); // 인기 검색어 안보이게
                }

                // 커서 닫기
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chipGroup.removeAllViews(); // ChipGroup 내의 모든 뷰(Chip)를 삭제합니다.
                saveChips(); // Save the empty state
            }
        });
    }


    //검색 결과를  chip에 저장해 검색 부분을 나갔다 들어와도 유지되게 하는 코드



    //칩을 그룹에 추가
    private void addChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip); // Chip 삭제
                saveChips(); // Save chips after removing one
            }
        });
        chipGroup.addView(chip); // ChipGroup에 Chip 추가
    }


    //chip을 저장
    private void saveChips() {
        Set<String> chipsSet = new HashSet<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chipsSet.add(chip.getText().toString());
        }
        sharedPreferences.edit().putStringSet(KEY_CHIPS, chipsSet).apply();
    }


    //저장된 칩을 가져옴
    private void loadChips() {
        Set<String> chipsSet = sharedPreferences.getStringSet(KEY_CHIPS, new HashSet<>());
        for (String chipText : chipsSet) {
            addChip(chipText);
        }
    }
}
