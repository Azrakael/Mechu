package com.example.mechu_project;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ModifyProfile extends AppCompatActivity {
    private EditText editTextId, editPasswd, editEmail, editGoalWeight;
    private Spinner goalSpinner;
    private Button completeSignUp;
    private String userId, email, password, goal, selectedGoal;
    private Double goalWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        // UI 요소 초기화
        editTextId = findViewById(R.id.editTextId);
        editPasswd = findViewById(R.id.editPasswd);
        editEmail = findViewById(R.id.editEmail);
        editGoalWeight = findViewById(R.id.editGoalWeight);
        goalSpinner = findViewById(R.id.goalSpinner);
        completeSignUp = findViewById(R.id.completeSignUp);

        // SharedPreferences에서 사용자 ID 가져오기
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = preferences.getString("user_id", null);

        if (userId == null) {
            showMessage("사용자 정보를 찾을 수 없습니다.");
            finish();
            return;
        }

        // DB에서 사용자 정보 가져오기
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT user_id, password, email, target_weight, exercise_type FROM user WHERE user_id = ?",
                new String[]{userId});

        if (cursor.moveToFirst()) {
            int userIdIndex = cursor.getColumnIndex("user_id");
            int passWordIndex = cursor.getColumnIndex("password");
            int emailIndex = cursor.getColumnIndex("email");
            int targetWeightIndex = cursor.getColumnIndex("target_weight");
            int exercisetypeIndex= cursor.getColumnIndex("exercise_type");

            if (userIdIndex != -1) userId = cursor.getString(userIdIndex);
            if (passWordIndex != -1) password = cursor.getString(passWordIndex);
            if (emailIndex != -1) email = cursor.getString(emailIndex);
            if (targetWeightIndex != -1) goalWeight = cursor.getDouble(targetWeightIndex);
            if (exercisetypeIndex != -1) goal = cursor.getString(exercisetypeIndex);
        }
        cursor.close();

        // Goal Spinner 설정
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this, R.array.goal_options, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);

        // Set initial selection for goalSpinner
        if (goal != null) {
            int goalPosition = goalAdapter.getPosition(goal);
            goalSpinner.setSelection(goalPosition);
        }

        // UI에 사용자 정보 설정
        editTextId.setText(userId);
        editPasswd.setText(password);
        editEmail.setText(email);
        editGoalWeight.setText(goalWeight != null ? String.valueOf(goalWeight) : "");

        // 스피너 설정 (선택된 값 설정 필요)
        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGoal = parent.getItemAtPosition(position).toString();
                if (selectedGoal.equals("일반")) {
                    editGoalWeight.setEnabled(false);
                    findViewById(R.id.setGoalWeightArea).setVisibility(View.GONE);
                } else {
                    editGoalWeight.setEnabled(true);
                    findViewById(R.id.setGoalWeightArea).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 동작 X
            }
        });

        // 완료 버튼 클릭 리스너 설정
        completeSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 업데이트 로직 작성
                String newUserId = editTextId.getText().toString().trim();
                String newPassword = editPasswd.getText().toString().trim();
                String newEmail = editEmail.getText().toString().trim();
                String stringGoalWeight = editGoalWeight.getText().toString();
                Double newGoalWeight = stringGoalWeight.isEmpty() ? null : Double.parseDouble(stringGoalWeight);

                // DB 업데이트
                ContentValues values = new ContentValues();
                values.put("user_id", newUserId);
                values.put("password", newPassword);
                values.put("email", newEmail);
                values.put("exercise_type", selectedGoal);
                if (newGoalWeight != null) {
                    values.put("target_weight", newGoalWeight);
                } else {
                    values.putNull("target_weight");
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user_id", newUserId);
                editor.apply();

                long result = dbHelper.getWritableDatabase().update("user", values, "user_id = ?", new String[]{userId});

                if (result != -1) {
                    showMessage("저장이 완료되었습니다.");
                    dbHelper.close();
                    Intent it = new Intent(ModifyProfile.this, Profile.class);
                    startActivity(it);
                } else {
                    showMessage("저장에 실패했습니다.");
                }
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
