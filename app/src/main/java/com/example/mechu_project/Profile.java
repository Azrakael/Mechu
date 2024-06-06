package com.example.mechu_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
//이미지 글라이드 부분
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class Profile extends AppCompatActivity {

    LinearLayout calender, description;
    Button modify_myprofile, logout;

    TextView mytype, mytype1;
    private String userId;
    private Spinner goalSpinner;
    ImageView backbutton,logoImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        modify_myprofile = findViewById(R.id.modify_myprofile);
        calender = findViewById(R.id.calender);
        logout = findViewById(R.id.logout);
        description = findViewById(R.id.description);
        mytype = findViewById(R.id.mytype);
        mytype1 = findViewById(R.id.mytype1);
        logoImage = findViewById(R.id.logoImage);
        backbutton = findViewById(R.id.backButton);



        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Profile.this, MainActivity.class);
                startActivity(it);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Profile.this,MainActivity.class);
                startActivity(it);
            }
        });


        // SharedPreferences에서 사용자 ID 가져오기
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = preferences.getString("user_id", null);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT exercise_type FROM user WHERE user_id = ?",
                new String[]{userId});

        if (cursor.moveToFirst()) {
            // exercise_type 컬럼의 인덱스 가져오기
            int exerciseTypeIndex = cursor.getColumnIndex("exercise_type");
            // exercise_type 값 가져오기
            String exerciseType = cursor.getString(exerciseTypeIndex);
            // 가져온 값으로 TextView 설정
            mytype.setText(exerciseType);
            // 만약 exercise_type이 "일반"이라면 mytype1을 안 보이게 설정
            if (exerciseType.equals("일반")) {
                mytype1.setVisibility(View.GONE);
            } else {
                mytype1.setVisibility(View.VISIBLE);
            }
        }


        cursor.close();


        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Profile.this,Description.class);
                startActivity(it);
            }
        });


        modify_myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Profile.this,ModifyProfile.class);
                startActivity(it);
            }
        });

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Profile.this,Calender.class);
                startActivity(it);
            }
        });


        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃 동작 수행
                SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("user_id"); // 사용자 ID 값 삭제
                editor.putBoolean("logged_in", false); // 로그인 상태를 false로 변경
                editor.apply();


                Toast.makeText(Profile.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();


                // 로그인 화면으로 이동
                Intent intent = new Intent(Profile.this, Introductory.class);
                startActivity(intent);
                finish(); // 현재 화면 종료


            }
        });


    }
}
