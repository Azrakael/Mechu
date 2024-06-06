package com.example.mechu_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class RecommendedItemActivity extends AppCompatActivity {

    private TextView recommendationTitle, foodNameTextView, calorieTextView;
    private ImageView foodImageView, logoImage, backbutton;
    private Button refreshButton;
    private DatabaseHelper databaseHelper;
    private boolean isSecondRecommendation = false;
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_item);

        // 뷰 초기화
        recommendationTitle = findViewById(R.id.recommendationTitle);
        foodNameTextView = findViewById(R.id.foodNameTextView);
        calorieTextView = findViewById(R.id.calorieTextView);
        foodImageView = findViewById(R.id.foodImageView);
        refreshButton = findViewById(R.id.refreshButton);
        logoImage = findViewById(R.id.logoImage);
        backbutton = findViewById(R.id.backButton);

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        String tagType = intent.getStringExtra("TAG_TYPE");
        String tagValue = intent.getStringExtra("TAG_VALUE");

        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RecommendedItemActivity.this,MainActivity.class);
                startActivity(it);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RecommendedItemActivity.this, Recommend.class);
                // 현재 tagType을 인텐트에 추가
                it.putExtra("TAG_TYPE", tagType);
                it.putExtra("SHOW_OPTIONS", true); // 옵션을 보여주기 위해 추가
                offset = 0; // offset 값을 0으로 초기화
                startActivity(it);
            }
        });

        setRecommendationTitle(tagType, tagValue);
        displayRecommendedItem(tagType, tagValue);
        refreshButton.setOnClickListener(v -> refreshRecommendation(tagType, tagValue));
    }





    private void setRecommendationTitle(String tagType, String tagValue) {
        // SharedPreferences에서 사용자명 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("user_name", "사용자");

        // 제목 설정
        String titleText = "";
        if (isSecondRecommendation) {
            titleText = "그렇다면 이런 메뉴도 있어요!";
        } else {
            if ("mood".equals(tagType)) {
                titleText = String.format("지금 %s님의 기분이 '%s' 상태라면 \n 이런 메뉴는 어떨까요?", username, tagValue);
            } else if ("weather".equals(tagType)) {
                titleText = String.format("지금 날씨가 %s 상태라면 \n 이런 메뉴는 어때요?", tagValue);
            }
        }

        recommendationTitle.setText(titleText);
    }

    private void displayRecommendedItem(String tagType, String tagValue) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        // 태그 타입에 따라 쿼리 실행, OFFSET 값 사용
        if ("mood".equals(tagType)) {
            cursor = db.rawQuery("SELECT * FROM food WHERE mood_tag = ? LIMIT 1 OFFSET ?", new String[]{tagValue, String.valueOf(offset)});
        } else if ("weather".equals(tagType)) {
            cursor = db.rawQuery("SELECT * FROM food WHERE weather_tag = ? LIMIT 1 OFFSET ?", new String[]{tagValue, String.valueOf(offset)});
        }

        if (cursor != null && cursor.moveToFirst()) {
            String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
            String foodImage = cursor.getString(cursor.getColumnIndex("food_img"));
            double calorie = cursor.getDouble(cursor.getColumnIndex("calorie"));

            foodNameTextView.setText(foodName);
            calorieTextView.setText("칼로리는 " + calorie + "에요!");

            String imagePath = getFilesDir() + "/images/" + foodImage;
            Glide.with(this)
                    .load(imagePath)
                    .error(R.drawable.characterlogo) // 이미지 로드 실패 시 기본 이미지
                    .into(foodImageView);
            cursor.close();
        } else {
            // 더 이상 데이터가 없으면 offset을 초기화하고 첫 번째 추천을 다시 보여주기
            offset = 0;
            displayRecommendedItem(tagType, tagValue);
        }
    }

    private void refreshRecommendation(String tagType, String tagValue) {
        isSecondRecommendation = true;
        offset++; // 다음 추천 항목을 위해 offset 증가
        setRecommendationTitle(tagType, tagValue);
        displayRecommendedItem(tagType, tagValue);
    }
}
