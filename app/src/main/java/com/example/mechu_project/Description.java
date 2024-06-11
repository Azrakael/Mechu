package com.example.mechu_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


//메뉴추천
public class Description extends AppCompatActivity {

    ImageView backButton, logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        backButton = findViewById(R.id.backButton);
        logoImage = findViewById(R.id.logoImage);



        //버튼 클릭시 내 정보로 이동
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Description.this,Profile.class);
                startActivity(it);
            }
        });

        //버튼 클릭시 메인페이지로 이동
        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Description.this,MainActivity.class);
                startActivity(it);
            }
        });



    }
}
