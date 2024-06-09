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
    private TextView mFoodName, mFoodCal;
    private TextView lFoodName, lFoodCal;
    private TextView dFoodName, dFoodCal;
    private TextView morningCarbsInfo, morningProteinInfo, morningFatInfo;
    private TextView lunchCarbsInfo, lunchProteinInfo, lunchFatInfo;
    private TextView dinnerCarbsInfo, dinnerProteinInfo, dinnerFatInfo;
    private LinearLayout morningExpandedInfo, lunchExpandedInfo, dinnerExpandedInfo;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_record);

        // UI 요소 초기화
        initializeUIElements();

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // 로고 클릭 이벤트 설정
        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(FoodRecord.this, MainActivity.class);
                startActivity(it);
            }
        });

        // 뒤로 가기 버튼 클릭 이벤트 설정
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 메뉴 플러스 버튼 클릭 이벤트 설정
        setMenuPlusClickListeners();

        // BottomSheet 설정
        setUpBottomSheet();

        // 섭취 정보를 가져와서 업데이트
        updateIntakeInfo();

        // 아침, 점심, 저녁 메뉴 로드
        loadMealLog();

        // 메뉴명 클릭 이벤트 설정
        setMealClickListener(mFoodName, morningCarbsInfo, morningProteinInfo, morningFatInfo, "아침");
        setMealClickListener(lFoodName, lunchCarbsInfo, lunchProteinInfo, lunchFatInfo, "점심");
        setMealClickListener(dFoodName, dinnerCarbsInfo, dinnerProteinInfo, dinnerFatInfo, "저녁");
    }

    private void initializeUIElements() {
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
        lFoodName = findViewById(R.id.l_food_name);
        lFoodCal = findViewById(R.id.l_food_cal);
        dFoodName = findViewById(R.id.d_food_name);
        dFoodCal = findViewById(R.id.d_food_cal);

        morningCarbsInfo = findViewById(R.id.morning_carbs_info);
        morningProteinInfo = findViewById(R.id.morning_protein_info);
        morningFatInfo = findViewById(R.id.morning_fat_info);

        lunchCarbsInfo = findViewById(R.id.lunch_carbs_info);
        lunchProteinInfo = findViewById(R.id.lunch_protein_info);
        lunchFatInfo = findViewById(R.id.lunch_fat_info);

        dinnerCarbsInfo = findViewById(R.id.dinner_carbs_info);
        dinnerProteinInfo = findViewById(R.id.dinner_protein_info);
        dinnerFatInfo = findViewById(R.id.dinner_fat_info);
    }

    private void setMenuPlusClickListeners() {
        View.OnClickListener menuPlusClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FoodRecord.this, Search.class);
                startActivityForResult(it, 1);
            }
        };

        menuPlus1.setOnClickListener(menuPlusClickListener);
        menuPlus2.setOnClickListener(menuPlusClickListener);
        menuPlus3.setOnClickListener(menuPlusClickListener);
    }

    private void setUpBottomSheet() {
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
        bottomSheetBehavior.setPeekHeight(900); // 초기 높이 (더보기 높이)
        bottomSheetBehavior.setHideable(false); // 드래그로 숨기기 비활성화
    }

    private void setMealClickListener(TextView mealTextView, final TextView carbsInfo, final TextView proteinInfo, final TextView fatInfo, final String mealTime) {
        mealTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carbsInfo.getVisibility() == View.VISIBLE) {
                    carbsInfo.setVisibility(View.GONE);
                    proteinInfo.setVisibility(View.GONE);
                    fatInfo.setVisibility(View.GONE);
                } else {
                    loadNutrientInfo(mealTime, carbsInfo, proteinInfo, fatInfo);
                    carbsInfo.setVisibility(View.VISIBLE);
                    proteinInfo.setVisibility(View.VISIBLE);
                    fatInfo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadNutrientInfo(String mealTime, TextView carbsInfo, TextView proteinInfo, TextView fatInfo) {
        userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "사용자 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getNutrientInfo(userId, mealTime);
        if (cursor != null && cursor.moveToFirst()) {
            double carbs = cursor.getDouble(cursor.getColumnIndex("carbs"));
            double protein = cursor.getDouble(cursor.getColumnIndex("protein"));
            double fat = cursor.getDouble(cursor.getColumnIndex("fat"));

            carbsInfo.setText(String.format("탄수화물: %.1fg", carbs));
            proteinInfo.setText(String.format("단백질: %.1fg", protein));
            fatInfo.setText(String.format("지방: %.1fg", fat));

            cursor.close();
        } else {
            Toast.makeText(this, "영양 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateIntakeInfo() {
        userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "사용자 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getCurrentIntake(userId);
        if (cursor != null && cursor.moveToFirst()) {
            int currentCalorieIndex = cursor.getColumnIndex("current_calorie");
            int currentCarbsIndex = cursor.getColumnIndex("current_carbs");
            int currentProteinIndex = cursor.getColumnIndex("current_protein");
            int currentFatIndex = cursor.getColumnIndex("current_fat");
            int dailyCalorieIndex = cursor.getColumnIndex("daily_calorie");
            int dailyCarbsIndex = cursor.getColumnIndex("daily_carbs");
            int dailyProteinIndex = cursor.getColumnIndex("daily_protein");
            int dailyFatIndex = cursor.getColumnIndex("daily_fat");

            if (currentCalorieIndex != -1 && currentCarbsIndex != -1 && currentProteinIndex != -1 &&
                    currentFatIndex != -1 && dailyCalorieIndex != -1 && dailyCarbsIndex != -1 &&
                    dailyProteinIndex != -1 && dailyFatIndex != -1) {

                double currentCalorie = cursor.getDouble(currentCalorieIndex);
                double currentCarbs = cursor.getDouble(currentCarbsIndex);
                double currentProtein = cursor.getDouble(currentProteinIndex);
                double currentFat = cursor.getDouble(currentFatIndex);

                double dailyCalorie = cursor.getDouble(dailyCalorieIndex);
                double dailyCarbs = cursor.getDouble(dailyCarbsIndex);
                double dailyProtein = cursor.getDouble(dailyProteinIndex);
                double dailyFat = cursor.getDouble(dailyFatIndex);

                setCircularProgress(proteinProgressBar, currentProtein, dailyProtein, proteinProgressText, "g");
                setCircularProgress(carbsProgressBar, currentCarbs, dailyCarbs, carbsProgressText, "g");
                setCircularProgress(fatProgressBar, currentFat, dailyFat, fatProgressText, "g");

            } else {
                Toast.makeText(this, "섭취 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "섭취 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setCircularProgress(ProgressBar progressBar, double currentValue, double maxValue, TextView textView, String unit) {
        int progress = (int) ((currentValue / maxValue) * 100);
        progressBar.setProgress(progress);
        textView.setText(String.format("%.0f %s (%.0f%%)", currentValue, unit, (currentValue / maxValue) * 100));
    }

    private void loadMealLog() {
        userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "User ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadMealLogForTime("아침", mFoodName, mFoodCal);
        loadMealLogForTime("점심", lFoodName, lFoodCal);
        loadMealLogForTime("저녁", dFoodName, dFoodCal);
    }

    private void loadMealLogForTime(String mealTime, TextView foodName, TextView foodCal) {
        Cursor cursor = dbHelper.getMealLog(userId, mealTime);
        if (cursor != null && cursor.moveToFirst()) {
            foodName.setText(cursor.getString(cursor.getColumnIndex("food_name")));
            foodCal.setText(cursor.getString(cursor.getColumnIndex("calorie")) + "kcal");
            cursor.close();
        } else {
            foodName.setText("");
            foodCal.setText("");
        }
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updateIntakeInfo();
            loadMealLog();
        }
    }
}
