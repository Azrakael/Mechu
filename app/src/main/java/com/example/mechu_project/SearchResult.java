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
import com.bumptech.glide.Glide;

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
    LinearLayout food_detail;

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
        food_detail = findViewById(R.id.food_detail);
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
                    LinearLayout noResult = findViewById(R.id.noresult);
                    noResult.setVisibility(View.INVISIBLE);
                    LinearLayout foodItemLayout = (LinearLayout) LayoutInflater.from(SearchResult.this).inflate(R.layout.activity_food_item, containerLayout, false);

                    String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
                    ImageView foodImageView = foodItemLayout.findViewById(R.id.food_img);

                    // 이미지 파일의 경로 생성
                    String imagePath = getFilesDir() + File.separator + "images" + File.separator + foodName + ".png";
                    File imageFile = new File(imagePath);

                    if (imageFile.exists()) {
                        // 이미지 파일이 존재하는 경우 Glide를 사용하여 로드
                        Glide.with(SearchResult.this)
                                .load(imageFile)
                                .into(foodImageView);
                    }

                    TextView foodNameTextView = foodItemLayout.findViewById(R.id.food_name);
                    foodNameTextView.setText(foodName);

                    int calorie = cursor.getInt(cursor.getColumnIndex("calorie"));
                    TextView calorieTextView = foodItemLayout.findViewById(R.id.calorie);
                    calorieTextView.setText(calorie + "kcal");

                    int carbs = cursor.getInt(cursor.getColumnIndex("carbs"));
                    int protein = cursor.getInt(cursor.getColumnIndex("protein"));
                    int fat = cursor.getInt(cursor.getColumnIndex("fat"));



                    // foodItemLayout에 클릭 리스너 추가
                    foodItemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SearchResult.this, MenuDetail.class);
                            intent.putExtra("FOOD_NAME", foodName);
                            intent.putExtra("CALORIE", calorie);
                            intent.putExtra("CARBS", carbs);
                            intent.putExtra("PROTEIN", protein);
                            intent.putExtra("FAT", fat);
                            intent.putExtra("IMAGE_PATH", imagePath); // 이미지 경로 추가
                            startActivity(intent);
                        }
                    });

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


        // 즐겨찾기 버튼 클릭 처리
    public void onFavoriteButtonClick(View view) {
        view.startAnimation(scaleAnimation);
    }
}
}
