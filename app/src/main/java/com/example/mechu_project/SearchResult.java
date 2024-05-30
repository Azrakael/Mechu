package com.example.mechu_project;

import static com.example.mechu_project.ImageUtils.loadBitmapFromFile;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SearchResult extends AppCompatActivity {

    ImageView search_search1, backButton1, foodImageView, buttonFavorite1;
    EditText resultText;
    TextView foodNameTextView, calorieTextView;

    // 하트 클릭시 색이 채워지는 애니메이션 추가 효과
    LinearLayout food_detail;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private BottomSheetDialog bottomSheetDialog;
    private Handler handler;

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
        buttonFavorite1 = findViewById(R.id.buttonFavorite1);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        handler = new Handler();

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
                    LinearLayout noResult = findViewById(R.id.noresult);
                    noResult.setVisibility(View.INVISIBLE);
                    LinearLayout foodItemLayout = (LinearLayout) LayoutInflater.from(SearchResult.this).inflate(R.layout.activity_food_item, containerLayout, false);

                    String foodName = cursor.getString(cursor.getColumnIndex("food_name"));
                    ImageView foodImageView = foodItemLayout.findViewById(R.id.food_img);

                    String imagePath = getFilesDir() + File.separator + "images" + File.separator + foodName + ".png";
                    File imageFile = new File(imagePath);

                    if (imageFile.exists()) {
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

                    ImageView buttonFavorite1 = foodItemLayout.findViewById(R.id.buttonFavorite1);
                    buttonFavorite1.setOnClickListener(new View.OnClickListener() {
                        boolean isExpanded = false;

                        @Override
                        public void onClick(View v) {
                            final LinearLayout foodDetailLayout = foodItemLayout.findViewById(R.id.food_detail);
                            final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) foodDetailLayout.getLayoutParams();
                            int targetHeight = isExpanded ? (int) (150 * getResources().getDisplayMetrics().density) : (int) (310 * getResources().getDisplayMetrics().density);
                            final int initialHeight = foodDetailLayout.getHeight();

                            ValueAnimator valueAnimator = ValueAnimator.ofInt(initialHeight, targetHeight);
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int animatedValue = (int) animation.getAnimatedValue();
                                    params.height = animatedValue;
                                    foodDetailLayout.setLayoutParams(params);
                                }
                            });

                            valueAnimator.setDuration(500); // 애니메이션 지속 시간 (밀리초)
                            valueAnimator.start();

                            isExpanded = !isExpanded;
                        }
                    });

                    foodItemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SearchResult.this, MenuDetail.class);
                            intent.putExtra("FOOD_NAME", foodName);
                            intent.putExtra("CALORIE", calorie);
                            intent.putExtra("CARBS", carbs);
                            intent.putExtra("PROTEIN", protein);
                            intent.putExtra("FAT", fat);
                            intent.putExtra("IMAGE_PATH", imagePath);
                            startActivity(intent);
                        }
                    });

                    // Add button click listeners for meal logging
                    foodItemLayout.findViewById(R.id.buttonBreakfast).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userId = getUserIdFromSharedPreferences();
                            if (userId != null) {
                                handleMealLog("아침", foodName, calorie, carbs, protein, fat, userId);
                            } else {
                                Toast.makeText(SearchResult.this, "User ID not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    foodItemLayout.findViewById(R.id.buttonLunch).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userId = getUserIdFromSharedPreferences();
                            if (userId != null) {
                                handleMealLog("점심", foodName, calorie, carbs, protein, fat, userId);
                            } else {
                                Toast.makeText(SearchResult.this, "User ID not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    foodItemLayout.findViewById(R.id.buttonDinner).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userId = getUserIdFromSharedPreferences();
                            if (userId != null) {
                                handleMealLog("저녁", foodName, calorie, carbs, protein, fat, userId);
                            } else {
                                Toast.makeText(SearchResult.this, "User ID not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    containerLayout.addView(foodItemLayout);
                } while (cursor.moveToNext());
            } else {
                LinearLayout noResult = findViewById(R.id.noresult);
                noResult.setVisibility(View.VISIBLE);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void handleMealLog(String mealTime, String foodName, int calorie, int carbs, int protein, int fat, String userId) {
        String mealDate = getCurrentDate();
        int foodNum = getFoodNumFromFoodName(foodName);

        if (foodNum == -1) {
            Toast.makeText(this, "Food Number not found for the given Food Name.", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.insertMealLog(db, userId, mealDate, mealTime, foodNum);
        dbHelper.updateUserIntake(db, userId, calorie, carbs, protein, fat);

        Toast.makeText(this, mealTime + " 식단에 추가되었습니다.", Toast.LENGTH_SHORT).show();

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private int getFoodNumFromFoodName(String foodName) {
        int foodNum = -1;
        Cursor cursor = db.rawQuery("SELECT food_num FROM food WHERE food_name = ?", new String[]{foodName});
        if (cursor.moveToFirst()) {
            foodNum = cursor.getInt(cursor.getColumnIndex("food_num"));
        }
        cursor.close();
        return foodNum;
    }
}
