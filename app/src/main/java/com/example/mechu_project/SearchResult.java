package com.example.mechu_project;

import static com.example.mechu_project.ImageUtils.loadBitmapFromFile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SearchResult extends AppCompatActivity {

    ImageView search_search1, backButton1, foodImageView;
    EditText resultText;
    TextView foodNameTextView, calorieTextView;

    // 하트 클릭시 색이 채워지는 애니메이션 추가 효과
    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // 뷰 초기화
        resultText = findViewById(R.id.ResultText);
        search_search1 = findViewById(R.id.search_search1);
        backButton1 = findViewById(R.id.backButton1);
        foodImageView = findViewById(R.id.food_img);
        foodNameTextView = findViewById(R.id.food_name);
        calorieTextView = findViewById(R.id.calorie);

        // 애니메이션 설정
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        // Intent에서 초기 검색어 가져오기
        String initialSearchTerm = getIntent().getStringExtra("SEARCH_TERM");
        if (initialSearchTerm != null) {
            resultText.setText(initialSearchTerm);
            resultText.setVisibility(View.VISIBLE);
            new LoadFoodInfoTask().execute(initialSearchTerm); // AsyncTask 실행
        }

        // 돋보기 이미지 클릭 시
        search_search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newSearchTerm = resultText.getText().toString();
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

    // EditText 업데이트 부분
    private void updateSearchResult(String searchTerm) {
        resultText.setText(searchTerm);
        new LoadFoodInfoTask().execute(searchTerm); // AsyncTask 실행
    }

    // 음식 정보를 비동기적으로 가져오는 AsyncTask
    private class LoadFoodInfoTask extends AsyncTask<String, Void, Cursor> {
        @Override
        protected Cursor doInBackground(String... params) {
            String searchText = params[0];
            DatabaseHelper dbHelper = new DatabaseHelper(SearchResult.this);
            return dbHelper.getFoodInfo(searchText);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            LinearLayout containerLayout = findViewById(R.id.food_container);
            containerLayout.removeAllViews(); // 이전 뷰 제거

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 음식 항목 레이아웃을 생성하고 설정
                    LinearLayout foodItemLayout = (LinearLayout) LayoutInflater.from(SearchResult.this).inflate(R.layout.activity_food_item, containerLayout, false);

                    String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
                    Bitmap foodImageBitmap = loadBitmapFromFile(SearchResult.this, foodName + ".png");
                    if (foodImageBitmap != null) {
                        ImageView foodImageView = foodItemLayout.findViewById(R.id.food_img);
                        foodImageView.setImageBitmap(foodImageBitmap);
                    }

                    TextView foodNameTextView = foodItemLayout.findViewById(R.id.food_name);
                    foodNameTextView.setText(foodName);

                    int calorie = cursor.getInt(cursor.getColumnIndex("calorie"));
                    TextView calorieTextView = foodItemLayout.findViewById(R.id.calorie);
                    calorieTextView.setText(calorie + "kcal");

                    containerLayout.addView(foodItemLayout);
                } while (cursor.moveToNext());
            } else {
                // 결과가 없는 경우
                LinearLayout noResult = findViewById(R.id.noresult);
                noResult.setVisibility(View.VISIBLE);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // 즐겨찾기 버튼 클릭 처리
    public void onFavoriteButtonClick(View view) {
        view.startAnimation(scaleAnimation);
    }
}
