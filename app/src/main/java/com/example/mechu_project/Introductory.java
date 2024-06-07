package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class Introductory extends AppCompatActivity {

    ImageView miniLogo, app_name, bar;
    LottieAnimationView lottie;
    LinearLayout login_layout;
    Button sign_up_button, letgo;
    EditText userName, password;
    CheckBox keepLogin;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);

        letgo = findViewById(R.id.letgo);
        miniLogo = findViewById(R.id.miniLogo);
        app_name = findViewById(R.id.appName);
        bar = findViewById(R.id.bar);
        lottie = findViewById(R.id.lottie);
        login_layout = findViewById(R.id.loginLayout);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        keepLogin = findViewById(R.id.keepLogin);
        loginButton = findViewById(R.id.loginButton);
        sign_up_button = findViewById(R.id.signupButton);

        //로그인창 초기투명도설정
        login_layout.setAlpha(0f);

        bar.animate().translationY(-800).setDuration(700).setStartDelay(2800);
        app_name.animate().translationY(-800).setDuration(700).setStartDelay(2800);
        miniLogo.animate().translationY(-800).setDuration(700).setStartDelay(2800);
        lottie.animate().translationY(3000).setDuration(700).setStartDelay(2500);

        login_layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                login_layout.setVisibility(View.VISIBLE);
                login_layout.animate().alpha(1f).setDuration(500).start();
            }
        }, 3400); // 3.4초 후에 실행

        // Sign Up 버튼에 클릭 리스너 설정

        if (sign_up_button != null) {
            sign_up_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // SignUp1로 이동
                    Intent intent = new Intent(Introductory.this, SignUp1.class);
                    startActivity(intent);
                }
            });
        }

        // 로그인 버튼에 클릭 리스너 설정
        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = userName.getText().toString().trim();
                    String userPassword = password.getText().toString().trim();

                    if (userId.isEmpty() || userPassword.isEmpty()) {
                        showMessage("아이디와 비밀번호를 입력하세요.");
                        return;
                    }

                    DatabaseHelper dbHelper = new DatabaseHelper(Introductory.this);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();

                    // 먼저 아이디가 존재하는지 확인
                    Cursor cursor = db.rawQuery("SELECT * FROM user WHERE user_id = ?", new String[]{userId});
                    if (cursor.getCount() == 0) {
                        showMessage("아이디가 존재하지 않습니다.");
                        cursor.close();
                        return;
                    }
                    cursor.close();

                    // 비밀번호가 맞는지 확인
                    cursor = db.rawQuery("SELECT * FROM user WHERE user_id = ? AND password = ?", new String[]{userId, userPassword});
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        // 로그인 성공, login_check를 1로 업데이트
                        ContentValues values = new ContentValues();
                        if (keepLogin.isChecked()) {
                            values.put("login_check", 1); // 로그인 상태 유지
                        } else {
                            values.put("login_check", 0); // 로그인 상태 유지 안 함
                        }
                        db.update("user", values, "user_id = ?", new String[]{userId});

                        // 로그인 상태를 SharedPreferences에 저장
                        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        String userName = cursor.getString(cursor.getColumnIndex("user_name"));
                        editor.putString("user_id", userId);
                        editor.putString("email", cursor.getString(cursor.getColumnIndex("email")));
                        editor.putString("user_name", userName);
                        editor.putBoolean("logged_in", true);
                        editor.apply();

                        // 로그인 상태 유지 및 홈 화면으로 이동
                        Toast.makeText(Introductory.this, userName + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Introductory.this, MainActivity.class);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                        finish(); // Introductory 액티비티를 종료
                    } else {
                        showMessage("비밀번호가 올바르지 않습니다.");
                    }
                    cursor.close();
                }
            });
        }

//        Button goChat = findViewById(R.id.goChat);
//        if (goChat != null) {
//            goChat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Chatting으로 이동
//                    Intent intent = new Intent(Introductory.this, Chatting.class);
//                    startActivity(intent);
//                }
//            });
//
//            letgo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Introductory.this, MainActivity.class);
//                    startActivity(intent);
//                }
//            });
//        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
