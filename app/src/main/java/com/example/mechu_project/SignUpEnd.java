package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SignUpEnd extends AppCompatActivity {

    ImageView mechuLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_end);

        mechuLogo = findViewById(R.id.mechuLogo);

        mechuLogo.setTranslationX(-1100);
        mechuLogo.animate().translationX(0).setDuration(700).setStartDelay(150);

        Button startNext = findViewById(R.id.goMain);
        startNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpEnd.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
