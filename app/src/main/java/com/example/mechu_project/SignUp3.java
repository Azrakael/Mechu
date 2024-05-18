package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class SignUp3 extends AppCompatActivity {

    EditText editHeight, editWeight, editGoalWeight;
    RadioGroup radioGroupGender;
    RadioButton radioButtonMale, radioButtonFemale;
    Spinner goalSpinner;
    DatabaseHelper dbHelper;

    String user_id, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        dbHelper = new DatabaseHelper(this);

        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        editGoalWeight = findViewById(R.id.editGoalWeight);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        goalSpinner = findViewById(R.id.goalSpinner);

        // 첫 번째 페이지에서 전달된 데이터 받기
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        Button signupNext = findViewById(R.id.signupNext);

        // Sign Up 버튼에 클릭 리스너 설정
        if (signupNext != null) {
            signupNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String height = editHeight.getText().toString();
                    String weight = editWeight.getText().toString();
                    String goalWeight = editGoalWeight.getText().toString();
                    String goal = goalSpinner.getSelectedItem().toString();
                    int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();

                    if (!height.isEmpty() && !weight.isEmpty() && !goalWeight.isEmpty() && selectedGenderId != -1) {
                        String gender = selectedGenderId == R.id.radioButtonMale ? "Male" : "Female";

                        if (isUserExists(user_id)) {
                            updateUser(user_id, gender, height, weight, goal, goalWeight);
                            showMessage("회원가입이 완료되었습니다.");

                            // SignUpEnd로
                            Intent intent = new Intent(SignUp3.this, SignUpEnd.class);
                            startActivity(intent);
                        } else {
                            showMessage("사용자를 찾을 수 없습니다.");
                        }
                    } else {
                        showMessage("모든 필드를 입력해주세요.");
                    }
                }
            });
        }

        // 뒤로 가기 버튼 클릭 리스너 설정
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로 가기 기능
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isUserExists(String user_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT user_id FROM user WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{user_id});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    private void updateUser(String user_id, String gender, String height, String weight, String goal, String goalWeight) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sex", gender);
        values.put("height", height);
        values.put("weight", weight);
        values.put("exercise_type", goal);
        values.put("target_weight", goalWeight);

        long result = db.update("user", values, "user_id = ?", new String[]{user_id});
        db.close();

        if (result != -1) {
            showMessage("데이터가 성공적으로 업데이트되었습니다.");
        } else {
            showMessage("데이터 업데이트에 실패했습니다.");
        }
    }
}