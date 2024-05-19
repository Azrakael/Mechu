package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SignUp4 extends AppCompatActivity {
    private RadioGroup radioGroupActivityLevel;
    private Button completeSignUp;

    private String userId, gender, goal;
    private double height, weight, targetWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);

        radioGroupActivityLevel = findViewById(R.id.radioGroupActivityLevel);
        completeSignUp = findViewById(R.id.completeSignUp);

        // SignUp3에서 user_id 값 가져오기
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");

        // DB에서 사용자 정보 가져오기
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT height, weight, target_weight, sex, exercise_type FROM user WHERE user_id = ?", new String[]{userId});

        if (cursor.moveToFirst()) {
            height = cursor.getDouble(cursor.getColumnIndex("height"));
            weight = cursor.getDouble(cursor.getColumnIndex("weight"));
            targetWeight = cursor.getDouble(cursor.getColumnIndex("target_weight"));
            gender = cursor.getString(cursor.getColumnIndex("sex"));
            goal = cursor.getString(cursor.getColumnIndex("exercise_type"));
        }
        cursor.close();

        // 완료 버튼의 클릭 리스너
        completeSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupActivityLevel.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    showMessage("활동량을 선택하세요.");
                    return;
                }

                double bmr = calculateBMR(height, weight, gender);
                String activityLevel = ((RadioButton) findViewById(selectedId)).getText().toString();
                double tdee = calculateTDEE(bmr, activityLevel);

                double dailyCalories, dailyProtein, dailyCarbs, dailyFat;
                if (goal.equals("다이어트")) {
                    dailyCalories = tdee - 500; // 다이어트 시 TDEE보다 500kcal 적게 섭취
                    dailyProtein = calculateDailyProtein(weight, targetWeight);
                    dailyCarbs = calculateDailyCarbs(dailyCalories, 0.4); // 탄수화물 비율 40%
                    dailyFat = calculateDailyFat(dailyCalories, 0.3); // 지방 비율 30%
                } else if (goal.equals("벌크업")) {
                    dailyCalories = tdee + 500; // 벌크업 시 TDEE보다 500kcal 많이 섭취
                    dailyProtein = calculateDailyProtein(weight, targetWeight);
                    dailyCarbs = calculateDailyCarbs(dailyCalories, 0.5); // 탄수화물 비율 50%
                    dailyFat = calculateDailyFat(dailyCalories, 0.2); // 지방 비율 20%
                } else {
                    dailyCalories = tdee; // 일반 목표 시 TDEE 유지
                    dailyProtein = calculateDailyProtein(weight, targetWeight);
                    dailyCarbs = calculateDailyCarbs(dailyCalories, 0.5); // 탄수화물 비율 50%
                    dailyFat = calculateDailyFat(dailyCalories, 0.3); // 지방 비율 30%
                }

                // Update user details in database
                ContentValues values = new ContentValues();
                values.put("tdee", tdee);
                values.put("exercise_type", activityLevel);
                values.put("daily_calorie", dailyCalories);
                values.put("daily_protein", dailyProtein);
                values.put("daily_carbs", dailyCarbs);
                values.put("daily_fat", dailyFat);

                long result = dbHelper.getWritableDatabase().update("user", values, "user_id = ?", new String[]{userId});

                if (result != -1) {
                    Intent intent = new Intent(SignUp4.this, SignUpEnd.class);
                    startActivity(intent);
                } else {
                    showMessage("저장에 실패했습니다.");
                }
            }
        });
    }

    private double calculateBMR(double height, double weight, String gender) {
        if (gender.equals("남자")) {
            return 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * 25); // 나이 25세로 고정
        } else {
            return 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * 25); // 나이 25세로 고정
        }
    }

    private double calculateTDEE(double bmr, String activityLevel) {
        switch (activityLevel) {
            case "운동 아예 안함 (BMR * 1.2)":
                return bmr * 1.2;
            case "가벼운 운동 (일주일에 1~2회, BMR * 1.375)":
                return bmr * 1.375;
            case "적당한 운동 (일주일에 3~4회, BMR * 1.55)":
                return bmr * 1.55;
            case "격렬한 운동 (일주일에 5~6회, BMR * 1.725)":
                return bmr * 1.725;
            case "힘든 운동 (일주일에 6~7회, BMR * 1.9)":
                return bmr * 1.9;
            default:
                return 0;
        }
    }

    private double calculateDailyProtein(double weight, double targetWeight) {
        return targetWeight * 1.6; // 기본 단백질 섭취량 계산 (단위: g)
    }

    private double calculateDailyCarbs(double dailyCalories, double carbRatio) {
        return (dailyCalories * carbRatio) / 4; // 탄수화물 비율에 따른 섭취량 계산 (단위: g)
    }

    private double calculateDailyFat(double dailyCalories, double fatRatio) {
        return (dailyCalories * fatRatio) / 9; // 지방 비율에 따른 섭취량 계산 (단위: g)
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
