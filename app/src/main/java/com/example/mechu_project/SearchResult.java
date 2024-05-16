package com.example.mechu_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchResult extends AppCompatActivity {

    ImageView search_search1 ,backButton1;
    EditText resultText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        resultText = findViewById(R.id.ResultText);
        search_search1 = findViewById(R.id.search_search1);
        backButton1 = findViewById(R.id.backButton1);


        // Intent에서 검색어 데이터 받아오기 (처음 Activity 시작 시)
        String initialSearchTerm = getIntent().getStringExtra("SEARCH_TERM");
        if (initialSearchTerm != null) {
            resultText.setText(initialSearchTerm);
            resultText.setVisibility(View.VISIBLE);
        }

        // 검색 아이콘 클릭 이벤트 처리
        search_search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newSearchTerm = resultText.getText().toString();

                // 새로운 검색어로 화면 업데이트
                updateSearchResult(newSearchTerm);
            }
        });
        backButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(SearchResult.this , MainActivity.class);
                startActivity(it);
            }
        });
    }

    // 검색 결과 업데이트 메서드
    private void updateSearchResult(String searchTerm) {
        // 여기에 실제 검색 로직을 구현합니다.
        // 예: 서버 API 호출, 데이터베이스 조회 등

        // 검색 결과를 화면에 표시 (예시)
        resultText.setText(searchTerm); // EditText에 새로운 검색어 표시
    }
}