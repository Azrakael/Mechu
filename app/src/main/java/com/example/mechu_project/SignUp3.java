package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class SignUp3 extends AppCompatActivity {
    private EditText editTextHeight, editTextWeight, editTextGoalWeight, editTextAge;
    private RadioGroup radioGroupGender;
    private Spinner goalSpinner, activityLevelSpinner;
    private Button signupNext;

    private String userId, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        editTextHeight = findViewById(R.id.editHeight);
        editTextWeight = findViewById(R.id.editWeight);
        editTextGoalWeight = findViewById(R.id.editGoalWeight);
        editTextAge = findViewById(R.id.editAge);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        goalSpinner = findViewById(R.id.goalSpinner);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        signupNext = findViewById(R.id.signupNext);

        // SignUp2에서 값 가져오기
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        password = intent.getStringExtra("password");
        email = intent.getStringExtra("email");

        // 스피너 항목별 목표체중 입력받기
        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGoal = parent.getItemAtPosition(position).toString();
                if (selectedGoal.equals("일반")) {
                    editTextGoalWeight.setEnabled(false);
                    editTextGoalWeight.setHint("목표체중 (선택사항)");
                    findViewById(R.id.setGoalWeightArea).setVisibility(View.GONE);
                    findViewById(R.id.setActivityLevelArea).setVisibility(View.GONE);
                } else {
                    editTextGoalWeight.setEnabled(true);
                    editTextGoalWeight.setHint("목표체중");
                    findViewById(R.id.setGoalWeightArea).setVisibility(View.VISIBLE);
                    findViewById(R.id.setActivityLevelArea).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 동작 X
            }
        });

        // signup 버튼의 클릭 리스너
        signupNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String height = editTextHeight.getText().toString().trim();
                String weight = editTextWeight.getText().toString().trim();
                String goalWeight = editTextGoalWeight.getText().toString().trim();
                String age = editTextAge.getText().toString().trim();
                String gender = "";
                if (radioGroupGender.getCheckedRadioButtonId() != -1) {
                    gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
                }
                String goal = goalSpinner.getSelectedItem().toString();
                String activityLevel = activityLevelSpinner.getSelectedItem().toString();

                if (height.isEmpty() && weight.isEmpty() && age.isEmpty() && gender.isEmpty() && goal.isEmpty() && goalWeight.isEmpty() && activityLevel.isEmpty()) {
                    showMessage("모든 필드를 채워주세요.");
                    return;
                }

                if (height.isEmpty()) {
                    showMessage("키를 입력하세요.");
                    return;
                }

                if (weight.isEmpty()) {
                    showMessage("몸무게를 입력하세요.");
                    return;
                }

                if (age.isEmpty()) {
                    showMessage("나이를 입력하세요.");
                    return;
                }

                if (gender.isEmpty()) {
                    showMessage("성별을 선택하세요.");
                    return;
                }

                if (goal.isEmpty()) {
                    showMessage("목표를 선택하세요.");
                    return;
                }

                // TDEE 계산
                double heightCm = Double.parseDouble(height);
                double weightKg = Double.parseDouble(weight);
                int ageYears = Integer.parseInt(age);
                double bmr = 0;
                if (gender.equals("남자")) {
                    bmr = 10 * weightKg + 6.25 * heightCm - 5 * ageYears + 5;
                } else {
                    bmr = 10 * weightKg + 6.25 * heightCm - 5 * ageYears - 161;
                }

                double activityMultiplier = 1.2; // 기본값
                if (!goal.equals("일반")) {
                    switch (activityLevel) {
                        case "운동 아예 안함 (BMR * 1.2)":
                            activityMultiplier = 1.2;
                            break;
                        case "가벼운 운동 (일주일에 1~2회, BMR * 1.375)":
                            activityMultiplier = 1.375;
                            break;
                        case "적당한 운동 (일주일에 3~4회, BMR * 1.55)":
                            activityMultiplier = 1.55;
                            break;
                        case "격렬한 운동 (일주일에 5~6회, BMR * 1.725)":
                            activityMultiplier = 1.725;
                            break;
                        case "힘든 운동 (일주일에 6~7회, BMR * 1.9)":
                            activityMultiplier = 1.9;
                            break;
                    }
                }

                double tdee = bmr * activityMultiplier;
                double dailyCalories = tdee;
                if (goal.equals("다이어트")) {
                    dailyCalories = tdee - 500; // 다이어트 목표는 TDEE에서 500kcal 적게
                } else if (goal.equals("벌크업")) {
                    dailyCalories = tdee + 500; // 벌크업 목표는 TDEE에서 500kcal 더 많이
                }

                double dailyProtein = weightKg * 2.2; // 단백질은 체중(kg) 당 2.2g
                double dailyFat = dailyCalories * 0.25 / 9; // 지방은 총 칼로리의 25%
                double dailyCarbs = (dailyCalories - (dailyProtein * 4) - (dailyFat * 9)) / 4; // 나머지는 탄수화물

                // 정수로 반올림
                dailyCalories = Math.round(dailyCalories);
                dailyProtein = Math.round(dailyProtein);
                dailyFat = Math.round(dailyFat);
                dailyCarbs = Math.round(dailyCarbs);

                // DB저장
                DatabaseHelper dbHelper = new DatabaseHelper(SignUp3.this);
                ContentValues values = new ContentValues();
                values.put("user_id", userId);
                values.put("user_name", userId); // 초기 사용자명 == 사용자 id 이후 내정보수정에서 수정하도록
                values.put("email", email);
                values.put("password", password);
                values.put("sex", gender);
                values.put("exercise_type", goal);
                values.put("height", Double.parseDouble(height));
                values.put("weight", Double.parseDouble(weight));
                values.put("age", ageYears);
                values.put("login_check", 1); // 로그인 상태로 설정.

                values.put("daily_calorie", (int) dailyCalories);
                values.put("daily_carbs", (int) dailyCarbs);
                values.put("daily_protein", (int) dailyProtein);
                values.put("daily_fat", (int) dailyFat);

                if (!goal.equals("일반")) {
                    values.put("target_weight", Double.parseDouble(goalWeight));
                }

                long result = dbHelper.getWritableDatabase().insert("user", null, values);

                if (result != -1) {
                    // 사용자 정보 저장
                    SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("user_id", userId);
                    editor.putString("email", email);
                    editor.putString("user_name", userId); // 초기 사용자명 == 사용자 id
                    editor.putBoolean("logged_in", true);
                    editor.apply();

                    showMessage(userId + "님 환영합니다.");
                    Intent intent = new Intent(SignUp3.this, SignUpEnd.class);
                    startActivity(intent);
                } else {
                    showMessage("회원가입에 실패했습니다.");
                }
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
