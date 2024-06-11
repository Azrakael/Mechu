package com.example.mechu_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // 하트 클릭시 색이 채워지는 애니메이션 추가 효과
    ScaleAnimation scaleAnimation;

    // 탭 상수 정의
    private static final int TAB_HOME = R.id.tab_home;
    private static final int TAB_SEARCHING = R.id.tab_searching;
    private static final int TAB_MECHU = R.id.tab_mechu;
    private static final int TAB_SETTINGS = R.id.tab_settings;
    private ConstraintLayout topLayout;
    private View topBar;
    private BottomNavigationView bottomNavigationView;
    private ScrollView scrollView;
    private boolean isTopLayoutVisible = true;
    private int lastScrollY = 0;
    private static final int SCROLL_THRESHOLD = 200;
    private Handler handler = new Handler();

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // BottomNavigationView 설정
        topLayout = findViewById(R.id.top_layout);
        topBar = findViewById(R.id.top_bar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        scrollView = findViewById(R.id.scroll_view);

        View halfCircleGauge = findViewById(R.id.halfCircleGauge);
        halfCircleGauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoodRecord.class);
                startActivity(intent);
            }
        });





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
                    Intent intent = new Intent(MainActivity.this, Recommend.class);
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

        // 사용자명 가져오기
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = preferences.getString("user_name", "사용자");

        // 음식 항목
        new LoadRandomFoodItemsTask().execute();
        // 사용자 맞춤 추천 항목
        new LoadRecommendedFoodItemsTask(userName).execute();
        // 저탄소 메뉴 항목
        new LoadLowCarbonFoodItemsTask().execute();

        // 스크롤 이벤트 감지하여 상단 레이아웃 visibility 조정
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private float initialY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float finalY = event.getY();
                        if (initialY - finalY > SCROLL_THRESHOLD && isTopLayoutVisible) { // 스크롤 업
                            topLayout.setVisibility(View.GONE);
                            topBar.setVisibility(View.VISIBLE);
                            isTopLayoutVisible = false;
                        } else if (finalY - initialY > SCROLL_THRESHOLD && !isTopLayoutVisible) { // 스크롤 다운
                            topLayout.setVisibility(View.VISIBLE);
                            topBar.setVisibility(View.GONE);
                            isTopLayoutVisible = true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    public void refreshPage(View view) {
        // 새로고침
        recreate();
    }

    private class LoadRandomFoodItemsTask extends AsyncTask<Void, Void, List<Cursor>> {
        @Override
        protected List<Cursor> doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            return dbHelper.getRandomFoodItems();
        }

        @Override
        protected void onPostExecute(List<Cursor> cursors) {
            LinearLayout category1Container = findViewById(R.id.category1_container);
            LinearLayout category2Container = findViewById(R.id.category2_container);
            TextView category1Text = findViewById(R.id.category1_text);
            TextView category2Text = findViewById(R.id.category2_text);
            LinearLayout cafeFoodContainer = findViewById(R.id.cafe_food_container);

            if (cursors.size() >= 2) {
                Cursor cursor1 = cursors.get(0);
                Cursor cursor2 = cursors.get(1);

                if (cursor1 != null && cursor1.moveToFirst()) {
                    String category1Name = cursor1.getString(cursor1.getColumnIndex("category_name"));
                    category1Text.setText("오늘은 '" + category1Name + "'이 땡겨요!");
                    do {
                        addFoodItemToContainer(cursor1, category1Container);
                    } while (cursor1.moveToNext());
                    cursor1.close();
                }

                if (cursor2 != null && cursor2.moveToFirst()) {
                    String category2Name = cursor2.getString(cursor2.getColumnIndex("category_name"));
                    category2Text.setText("'" + category2Name + "'에선 이게 맛있을거에요!");
                    do {
                        addFoodItemToContainer(cursor2, category2Container);
                    } while (cursor2.moveToNext());
                    cursor2.close();
                }
            }

             DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            Cursor cafeCursor = dbHelper.getRandomCafeItems();
            if (cafeCursor != null && cafeCursor.moveToFirst()) {
                do {
                    addFoodItemToContainer(cafeCursor, cafeFoodContainer);
                } while (cafeCursor.moveToNext());
                cafeCursor.close();
            }
        }

        private void addFoodItemToContainer(Cursor cursor, LinearLayout container) {
            LinearLayout foodItemLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_food_item_hz, container, false);

            String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
            String imagePath = getFilesDir() + File.separator + "images" + File.separator + foodName + ".png";
            int calorie = cursor.getInt(cursor.getColumnIndex("calorie"));
            int carbs = cursor.getInt(cursor.getColumnIndex("carbs"));
            int protein = cursor.getInt(cursor.getColumnIndex("protein"));
            int fat = cursor.getInt(cursor.getColumnIndex("fat"));

            ImageView foodImageView = foodItemLayout.findViewById(R.id.food_img);
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Glide.with(MainActivity.this)
                        .load(imageFile)
                        .into(foodImageView);
            } else {
                foodImageView.setImageResource(R.drawable.ic_menu);
            }

            TextView foodNameTextView = foodItemLayout.findViewById(R.id.food_name);
            foodNameTextView.setText(foodName);

            TextView calorieTextView = foodItemLayout.findViewById(R.id.calorie);
            calorieTextView.setText(calorie + "kcal");

            foodItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "메뉴 선택됨: " + foodName + ", 칼로리: " + calorie);
                    Intent intent = new Intent(MainActivity.this, MenuDetail.class);
                    intent.putExtra("FOOD_NAME", foodName);
                    intent.putExtra("CALORIE", calorie);
                    intent.putExtra("CARBS", carbs);
                    intent.putExtra("PROTEIN", protein);
                    intent.putExtra("FAT", fat);
                    intent.putExtra("IMAGE_PATH", imagePath);
                    startActivity(intent);
                }
            });

            container.addView(foodItemLayout);
        }
    }

    private class LoadRecommendedFoodItemsTask extends AsyncTask<Void, Void, List<FoodItem>> {
        private String userName;

        public LoadRecommendedFoodItemsTask(String userName) {
            this.userName = userName;
        }

        @Override
        protected List<FoodItem> doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String userId = preferences.getString("user_id", null);

            if (userId == null) {
                Log.e(TAG, "사용자 ID가 null입니다. 추천 식품을 로드할 수 없습니다.");
                return null;
            }

            List<FoodItem> foodItems = dbHelper.getRecommendedFoodItems(userId);
            Log.d(TAG, "추천 메뉴: " + foodItems);
            return foodItems;
        }

        @Override
        protected void onPostExecute(List<FoodItem> foodItems) {
            LinearLayout recommendedContainer = findViewById(R.id.user_recommended_container);
            TextView recommendedTitle = findViewById(R.id.recommended_title);
            recommendedTitle.setText(userName + "님을 위해 골라봤어요!");

            if (foodItems != null && !foodItems.isEmpty()) {
                for (FoodItem foodItem : foodItems) {
                    addFoodItemToContainer(foodItem, recommendedContainer);
                }
            } else {
                TextView emptyTextView = new TextView(MainActivity.this);
                emptyTextView.setText("추천 메뉴가 없습니다.");
                recommendedContainer.addView(emptyTextView);
            }
        }

        private void addFoodItemToContainer(FoodItem foodItem, LinearLayout container) {
            LinearLayout foodItemLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_food_item_hz, container, false);

            String foodName = foodItem.getFoodName();
            String imagePath = getFilesDir() + File.separator + "images" + File.separator + foodName + ".png";
            int calorie = (int) foodItem.getCalorie();
            int carbs = (int) foodItem.getCarbs();
            int protein = (int) foodItem.getProtein();
            int fat = (int) foodItem.getFat();

            ImageView foodImageView = foodItemLayout.findViewById(R.id.food_img);
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Glide.with(MainActivity.this)
                        .load(imageFile)
                        .into(foodImageView);
            } else {
                foodImageView.setImageResource(R.drawable.ic_menu);
            }

            TextView foodNameTextView = foodItemLayout.findViewById(R.id.food_name);
            foodNameTextView.setText(foodName);

            TextView calorieTextView = foodItemLayout.findViewById(R.id.calorie);
            calorieTextView.setText(calorie + "kcal");

            foodItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MenuDetail.class);
                    intent.putExtra("FOOD_NAME", foodName);
                    intent.putExtra("CALORIE", calorie);
                    intent.putExtra("CARBS", carbs);
                    intent.putExtra("PROTEIN", protein);
                    intent.putExtra("FAT", fat);
                    intent.putExtra("IMAGE_PATH", imagePath);
                    startActivity(intent);
                }
            });

            container.addView(foodItemLayout);
        }
    }

    // 저탄소 메뉴 항목을 로드하는 AsyncTask
    private class LoadLowCarbonFoodItemsTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            return dbHelper.getLowCarbonFoodItems();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            LinearLayout lowCarbonContainer = findViewById(R.id.low_carbon_container);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    addLowCarbonFoodItemToContainer(cursor, lowCarbonContainer);
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                TextView emptyTextView = new TextView(MainActivity.this);
                emptyTextView.setText("저탄소 메뉴가 없습니다.");
                lowCarbonContainer.addView(emptyTextView);
            }
        }

        private void addLowCarbonFoodItemToContainer(Cursor cursor, LinearLayout container) {
            LinearLayout foodItemLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_food_item_hz, container, false);

            String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
            String imagePath = getFilesDir() + File.separator + "images" + File.separator + foodName + ".png";
            int calorie = cursor.getInt(cursor.getColumnIndex("calorie"));
            int carbs = cursor.getInt(cursor.getColumnIndex("carbs"));
            int protein = cursor.getInt(cursor.getColumnIndex("protein"));
            int fat = cursor.getInt(cursor.getColumnIndex("fat"));

            ImageView foodImageView = foodItemLayout.findViewById(R.id.food_img);
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Glide.with(MainActivity.this)
                        .load(imageFile)
                        .into(foodImageView);
            } else {
                foodImageView.setImageResource(R.drawable.ic_menu);
            }

            TextView foodNameTextView = foodItemLayout.findViewById(R.id.food_name);
            foodNameTextView.setText(foodName);

            TextView calorieTextView = foodItemLayout.findViewById(R.id.calorie);
            calorieTextView.setText(calorie + "kcal");

            foodItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MenuDetail.class);
                    intent.putExtra("FOOD_NAME", foodName);
                    intent.putExtra("CALORIE", calorie);
                    intent.putExtra("CARBS", carbs);
                    intent.putExtra("PROTEIN", protein);
                    intent.putExtra("FAT", fat);
                    intent.putExtra("IMAGE_PATH", imagePath);
                    startActivity(intent);
                }
            });

            container.addView(foodItemLayout);
        }
    }

    // 각 버튼의 클릭 이벤트 처리
    public void onFavoriteButtonClick(View view) {
        view.startAnimation(scaleAnimation);
    }
}
