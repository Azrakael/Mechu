package com.example.mechu_project;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SearchResult extends AppCompatActivity {

    ImageView search_search1, backButton1, foodImageView;
    EditText resultText;
    TextView foodNameTextView, calorieTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        resultText = findViewById(R.id.ResultText);
        search_search1 = findViewById(R.id.search_search1);
        backButton1 = findViewById(R.id.backButton1);
        foodImageView = findViewById(R.id.food_img);
        foodNameTextView = findViewById(R.id.food_name);
        calorieTextView = findViewById(R.id.calorie);

        // Intent에서 검색어 데이터 받아오기 (처음 Activity 시작 시)
        String initialSearchTerm = getIntent().getStringExtra("SEARCH_TERM");
        if (initialSearchTerm != null) {
            resultText.setText(initialSearchTerm);
            resultText.setVisibility(View.VISIBLE);
            getFoodInfo(initialSearchTerm); // 검색어에 따라 음식 정보를 표시

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
                Intent it = new Intent(SearchResult.this, MainActivity.class);
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

        // 음식 정보 표시
        getFoodInfo(searchTerm);
    }
    // 음식 정보를 가져와서 UI에 표시하는 메서드
    private void getFoodInfo(String searchText) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getFoodInfo(searchText);

        if (cursor != null && cursor.moveToFirst()) {
            // 이미지 설정
            String imageName = cursor.getString(cursor.getColumnIndex("food_img"));
            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            if (resId != 0) {
                ImageView foodImageView = findViewById(R.id.food_img);
                foodImageView.setImageResource(resId);
            }
        }
            // 음식 이름 설정
            String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
            TextView foodNameTextView = findViewById(R.id.food_name);
            foodNameTextView.setText(foodName);

            // 칼로리 설정
            int calorie = cursor.getInt(cursor.getColumnIndex("calorie"));
            TextView calorieTextView = findViewById(R.id.calorie);
            calorieTextView.setText(calorie + "kcal");

            cursor.close(); // 커서를 닫아줍니다.
        }
    }

