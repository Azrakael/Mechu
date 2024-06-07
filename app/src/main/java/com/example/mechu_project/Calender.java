package com.example.mechu_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
//이미지 글라이드 부분


public class Calender extends AppCompatActivity {

    ImageView backButton, logoImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        backButton = findViewById(R.id.backButton);
        logoImage = findViewById(R.id.logoImage);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Calender.this,Profile.class);
                startActivity(it);
            }
        });


        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Calender.this,MainActivity.class);
                startActivity(it);
            }
        });
    }
}
