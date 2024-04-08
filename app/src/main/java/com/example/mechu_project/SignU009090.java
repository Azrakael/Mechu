package com.example.mechu_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SignU009090 extends AppCompatActivity {

    Button signupNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        Button signupNext = findViewById(R.id.signupNext);

        // Sign Up 버튼에 클릭 리스너 설정
        if (signupNext != null) {
            signupNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // SignUp2로
                    Intent intent = new Intent(SignU009090.this, SignUp2.class);
                    startActivity(intent);
                }
            });
        }

    }
}