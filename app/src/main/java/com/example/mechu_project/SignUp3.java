package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
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
    private EditText editTextHeight, editTextWeight, editTextGoalWeight;
    private RadioGroup radioGroupGender;
    private Spinner goalSpinner;
    private Button signupNext;

    private String userId, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        editTextHeight = findViewById(R.id.editHeight);
        editTextWeight = findViewById(R.id.editWeight);
        editTextGoalWeight = findViewById(R.id.editGoalWeight);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        goalSpinner = findViewById(R.id.goalSpinner);
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
                } else {
                    editTextGoalWeight.setEnabled(true);
                    editTextGoalWeight.setHint("목표체중");
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
                String gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
                String goal = goalSpinner.getSelectedItem().toString();

                if (height.isEmpty()) {
                    showMessage("키를 입력하세요.");
                    return;
                }

                if (weight.isEmpty()) {
                    showMessage("몸무게를 입력하세요.");
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

                // 목표가 일반이 아닐 때 목표체중을 확인
                if (!goal.equals("일반") && goalWeight.isEmpty()) {
                    showMessage("목표체중을 입력하세요.");
                    return;
                }

                // Save user details to database
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
                values.put("login_check", 1); //로그인 상태로 설정.

                if (!goal.equals("일반")) {
                    values.put("target_weight", Double.parseDouble(goalWeight));
                }

                long result = dbHelper.getWritableDatabase().insert("user", null, values);

                if (result != -1) {
                    showMessage("회원가입이 완료되었습니다.");
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