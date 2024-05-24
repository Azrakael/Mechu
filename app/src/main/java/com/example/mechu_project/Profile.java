package com.example.mechu_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
//이미지 글라이드 부분
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class Profile extends AppCompatActivity {

    LinearLayout calender;
    Button modify_myprofile, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        modify_myprofile = findViewById(R.id.modify_myprofile);
        calender = findViewById(R.id.calender);
        logout = findViewById(R.id.logout);

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
