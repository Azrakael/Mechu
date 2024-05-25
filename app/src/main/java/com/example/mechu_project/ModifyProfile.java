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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModifyProfile extends AppCompatActivity {
    private EditText editTextId, editPasswd, editEmail, editGoalWeight;
    private TextView kg;
    private Spinner goalSpinner;
    private Button completeSignUp;
    private String userId, userName, email, password, goal;
    private double goalWeight;

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
        kg= findViewById(R.id.kg);


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
                "SELECT user_name, password, email, target_weight, exercise_type FROM user WHERE user_id = ?",
                new String[]{userId});

        if (cursor.moveToFirst()) {
            int userNameIndex = cursor.getColumnIndex("user_name");
            int passwordIndex = cursor.getColumnIndex("password");
            int emailIndex = cursor.getColumnIndex("email");
            int goalWeightIndex = cursor.getColumnIndex("target_weight");
            int goalIndex= cursor.getColumnIndex("exercise_type");



            if (userNameIndex != -1) userName = cursor.getString(userNameIndex);
            if (passwordIndex != -1) password = cursor.getString(passwordIndex);
            if (emailIndex != -1) email = cursor.getString(emailIndex);
            if (goalWeightIndex != -1) goalWeight = cursor.getDouble(goalWeightIndex);
            if (goalIndex != -1) goal = cursor.getString(goalIndex);
        }
        cursor.close();




        // Goal Spinner 설정 (Setup goal spinner with data and selection)
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this, R.array.goal_options, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);


        if (goal != null) {
            int goalPosition = goalAdapter.getPosition(goal);
            goalSpinner.setSelection(goalPosition);
        }


        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGoal = parent.getItemAtPosition(position).toString();
                // 만약 "다이어트"가 선택되었을 때, editGoalWeight을 보이도록 설정
                if (selectedGoal.equals("다이어트") || selectedGoal.equals("벌크업")) {
                    editGoalWeight.setVisibility(View.VISIBLE);
                    kg.setVisibility(View.VISIBLE);
                } else {
                    // 다른 경우에는 editGoalWeight을 숨김
                    editGoalWeight.setVisibility(View.GONE);
                    kg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택이 없을 때의 동작, 여기서는 특별히 할 일이 없으므로 비워둡니다.
            }
        });


        // UI에 사용자 정보 설정
        editTextId.setText(userName);
        editPasswd.setText(password);
        editEmail.setText(email);
        editGoalWeight.setText(String.valueOf(goalWeight));
        // 스피너 설정 (선택된 값 설정 필요)


        // 완료 버튼 클릭 리스너 설정
        completeSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 업데이트 로직 작성
                String newUserName = editTextId.getText().toString();
                String newPassword = editPasswd.getText().toString();
                String newEmail = editEmail.getText().toString();
                String newActivityLevel = goalSpinner.getSelectedItem().toString();
                double newGoalWeight = Double.parseDouble(editGoalWeight.getText().toString());

                // DB 업데이트

                ContentValues values = new ContentValues();
                values.put("user_name", newUserName);
                values.put("password", newPassword);
                values.put("email", newEmail);
                values.put("target_weight", newGoalWeight);
                values.put("exercise_type", newActivityLevel);

                long result = dbHelper.getWritableDatabase().update("user", values, "user_id = ?", new String[]{userId});

                if (result != -1) {
                    showMessage("저장이 완료되었습니다.");
                    Intent it = new Intent(ModifyProfile.this,Profile.class);
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
