package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SignUp1 extends AppCompatActivity {

    //체크박스 체크
    Button signupNext;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        signupNext = findViewById(R.id.signupNext);
        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        checkBox3 = findViewById(R.id.checkbox3);
        checkBox4 = findViewById(R.id.checkbox4);
        // 아아
        //checkBox1번이 체크되면 모두 체크
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = checkBox1.isChecked();
                checkBox2.setChecked(isChecked);
                checkBox3.setChecked(isChecked);
                checkBox4.setChecked(isChecked);
            }
        });

        // Sign Up 버튼에 클릭 리스너 설정
        signupNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checkBox2와 checkBox3이 모두 체크되었는지 확인
                if (checkBox2.isChecked() && checkBox3.isChecked()) {
                    // SignUp2로
                    Intent intent = new Intent(SignUp1.this, SignUp2.class);
                    startActivity(intent);
                } else {
                    // 두 체크박스 중 하나라도 체크되지 않은 경우 메시지 표시
                    showMessage("필수동의를 모두 체크해주세요.");
                }
            }
        });
    }

    // 메시지를 보여주는 간단한 메소드
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
