package com.example.mechu_project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // 하트 클릭시 색이 채워지는 애니메이션 추가 효과
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

        // 클릭시 하트가 채워지는 부분 지속시간
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator); // 바운스 효과

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == TAB_HOME) {
                    // 홈 화면으로 이동
                    return true;
                } else if (item.getItemId() == TAB_SEARCHING) {
                    // 검색 화면으로 이동
                    Intent searchIntent = new Intent(MainActivity.this, Search.class);
                    startActivity(searchIntent);
                    return true;
                } else if (item.getItemId() == TAB_MECHU) {
                    // 메뉴추천 화면으로 이동
                    Intent intent = new Intent(MainActivity.this, Chatting.class);
                    startActivity(intent);
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

        // 음식 항목을 동적으로 추가
        new LoadRandomFoodItemsTask().execute();
    }

    private class LoadRandomFoodItemsTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            return dbHelper.getRandomFoodItems();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            LinearLayout containerLayout = findViewById(R.id.food_container); // 여기에 음식 항목들을 추가할 것입니다.
            containerLayout.removeAllViews(); // 이전 뷰 제거

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 음식 항목 레이아웃을 생성하고 설정
                    LinearLayout foodItemLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_food_item, containerLayout, false);

                    String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
                    ImageView foodImageView = foodItemLayout.findViewById(R.id.food_img);

                    // 이미지 파일의 경로 생성
                    String imagePath = getFilesDir() + File.separator + "images" + File.separator + foodName + ".png";
                    File imageFile = new File(imagePath);

                    if (imageFile.exists()) {
                        // 이미지 파일이 존재하는 경우 Glide를 사용하여 로드
                        Glide.with(MainActivity.this)
                                .load(imageFile)
                                .into(foodImageView);
                    } else {
                        // 이미지가 없는 경우 기본 이미지 설정
                        foodImageView.setImageResource(R.drawable.ic_menu);
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
                            Intent intent = new Intent(MainActivity.this, MenuDetail.class);
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
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // 각 버튼의 클릭 이벤트 처리
    public void onFavoriteButtonClick(View view) {
        // 클릭한 버튼에 애니메이션 적용
        view.startAnimation(scaleAnimation);
    }
}
