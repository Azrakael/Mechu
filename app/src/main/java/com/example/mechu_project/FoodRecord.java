package com.example.mechu_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FoodRecord extends AppCompatActivity {

    ImageView menuPlus1, menuPlus2, menuPlus3, backbutton, logoImage;
    private ProgressBar proteinProgressBar, carbsProgressBar, fatProgressBar;
    private TextView proteinProgressText, carbsProgressText, fatProgressText;
    private TextView mFoodName, mFoodCal, mFoodName2, mFoodCal2;
    private TextView lFoodName, lFoodCal, lFoodName2, lFoodCal2;
    private TextView dFoodName, dFoodCal, dFoodName2, dFoodCal2;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private String userId;  // userId 변수 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_record);

        menuPlus1 = findViewById(R.id.menuPlus1);
        menuPlus2 = findViewById(R.id.menuPlus2);
        menuPlus3 = findViewById(R.id.menuPlus3);
        backbutton = findViewById(R.id.backButton);
        logoImage = findViewById(R.id.logoImage);
        proteinProgressBar = findViewById(R.id.proteinProgressBar);
        carbsProgressBar = findViewById(R.id.carbsProgressBar);
        fatProgressBar = findViewById(R.id.fatProgressBar);
        proteinProgressText = findViewById(R.id.proteinProgressText);
        carbsProgressText = findViewById(R.id.carbsProgressText);
        fatProgressText = findViewById(R.id.fatProgressText);

        mFoodName = findViewById(R.id.m_food_name);
        mFoodCal = findViewById(R.id.m_food_cal);
        mFoodName2 = findViewById(R.id.m_food_name2);
        mFoodCal2 = findViewById(R.id.m_food_cal2);
        lFoodName = findViewById(R.id.l_food_name);
        lFoodCal = findViewById(R.id.l_food_cal);
        lFoodName2 = findViewById(R.id.l_food_name2);
        lFoodCal2 = findViewById(R.id.l_food_cal2);
        dFoodName = findViewById(R.id.d_food_name);
        dFoodCal = findViewById(R.id.d_food_cal);
        dFoodName2 = findViewById(R.id.d_food_name2);
        dFoodCal2 = findViewById(R.id.d_food_cal2);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(FoodRecord.this, MainActivity.class);
                startActivity(it);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menuPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FoodRecord.this, Search.class);
                startActivity(it);
            }
        });

        menuPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FoodRecord.this, Search.class);
                startActivity(it);
            }
        });

        menuPlus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FoodRecord.this, Search.class);
                startActivity(it);
            }
        });

        // BottomSheet 설정
        LinearLayout bottomSheet = findViewById(R.id.foodDetailBottomSheet);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // 초기 상태 설정 (더보기만 보이게)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // 클릭 이벤트 설정
        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        // 드래그 가능 설정
        bottomSheetBehavior.setPeekHeight(170); // 초기 높이 (더보기 높이)
        bottomSheetBehavior.setHideable(false); // 드래그로 숨기기 비활성화

        // 섭취 정보를 가져와서 업데이트
        updateIntakeInfo();
        // 아침, 점심, 저녁 메뉴 로드
        loadMealLog();
    }

    private void updateIntakeInfo() {
        userId = getUserIdFromSharedPreferences();  // userId 초기화
        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getCurrentIntake(userId);
        if (cursor != null && cursor.moveToFirst()) {
            double currentCalorie = cursor.getDouble(cursor.getColumnIndex("current_calorie"));
            double currentCarbs = cursor.getDouble(cursor.getColumnIndex("current_carbs"));
            double currentProtein = cursor.getDouble(cursor.getColumnIndex("current_protein"));
            double currentFat = cursor.getDouble(cursor.getColumnIndex("current_fat"));

            // ProgressBar 및 TextView 업데이트
            setCircularProgress(proteinProgressBar, currentProtein, 100, proteinProgressText);
            setCircularProgress(carbsProgressBar, currentCarbs, 300, carbsProgressText);
            setCircularProgress(fatProgressBar, currentFat, 70, fatProgressText);

            cursor.close();
        } else {
            Toast.makeText(this, "Failed to load intake data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMealLog() {
        userId = getUserIdFromSharedPreferences();  // userId 초기화
        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadMealLogForTime("아침", mFoodName, mFoodCal, mFoodName2, mFoodCal2);
        loadMealLogForTime("점심", lFoodName, lFoodCal, lFoodName2, lFoodCal2);
        loadMealLogForTime("저녁", dFoodName, dFoodCal, dFoodName2, dFoodCal2);
    }

    private void loadMealLogForTime(String mealTime, TextView foodName, TextView foodCal, TextView foodName2, TextView foodCal2) {
        Cursor cursor = dbHelper.getMealLog(userId, mealTime);
        if (cursor != null && cursor.moveToFirst()) {
            foodName.setText(cursor.getString(cursor.getColumnIndex("food_name")));
            foodCal.setText(cursor.getString(cursor.getColumnIndex("calorie")) + "kcal");
            if (cursor.moveToNext()) {
                foodName2.setText(cursor.getString(cursor.getColumnIndex("food_name")));
                foodCal2.setText(cursor.getString(cursor.getColumnIndex("calorie")) + "kcal");
            } else {
                foodName2.setText("");
                foodCal2.setText("");
            }
            cursor.close();
        } else {
            foodName.setText("");
            foodCal.setText("");
            foodName2.setText("");
            foodCal2.setText("");
        }
    }

    private void setCircularProgress(ProgressBar progressBar, double value, double maxValue, TextView textView) {
        int progress = (int) ((value / maxValue) * 100);
        progressBar.setProgress(progress);
        textView.setText(String.format("%.0f", value));
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }
}
