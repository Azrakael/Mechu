package com.example.mechu_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
//이미지 글라이드 부분
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class Profile extends AppCompatActivity {

    LinearLayout calender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        calender = findViewById(R.id.calender);

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Profile.this,Calender.class);
                startActivity(it);
            }
        });



    }
}
