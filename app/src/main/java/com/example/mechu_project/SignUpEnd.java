package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

    }
}