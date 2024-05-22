package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.bumptech.glide.Glide;
import java.io.File;

public class MenuDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        if (intent != null) {
            String foodName = intent.getStringExtra("FOOD_NAME");
            int calorie = intent.getIntExtra("CALORIE", 0);
            int carbohydrateRatio = intent.getIntExtra("CARBS", 0);
            int proteinRatio = intent.getIntExtra("PROTEIN", 0);
            int fatRatio = intent.getIntExtra("FAT", 0);
            String imagePath = intent.getStringExtra("IMAGE_PATH"); // 이미지 경로 가져오기

            // 가져온 데이터를 각각의 뷰에 설정
            TextView menuTitle = findViewById(R.id.menuTitle);
            menuTitle.setText(foodName);

            TextView menuCal = findViewById(R.id.menuCal);
            menuCal.setText(calorie + "kcal");

            TextView menuProtein = findViewById(R.id.menuProtein);
            menuProtein.setText("단백질 함량: " + proteinRatio + "g");

            TextView menuFat = findViewById(R.id.menuFat);
            menuFat.setText("지방 함량: " + fatRatio + "g");

            TextView menuCarbohydrate = findViewById(R.id.menuCarbohydrate);
            menuCarbohydrate.setText("탄수화물 함량: " + carbohydrateRatio + "g");

            // 이미지 뷰 설정
            ImageView menuImage = findViewById(R.id.menuImage);
            if (imagePath != null) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Glide.with(this)
                            .load(imageFile)
                            .into(menuImage);

                    // 탄수화물 원형 프로그레스 바 및 텍스트 설정
                    ProgressBar carbsProgressBar = findViewById(R.id.carbsProgressBar);
                    TextView carbsProgressText = findViewById(R.id.carbsProgressText);
                    carbsProgressBar.setProgress(carbohydrateRatio); // 탄수화물 비율로 프로그레스 설정
                    carbsProgressText.setText("탄수화물: " + carbohydrateRatio + "g"); // 탄수화물 비율로 텍스트 설정

                    // 단백질 원형 프로그레스 바 및 텍스트 설정
                    ProgressBar proteinProgressBar = findViewById(R.id.proteinProgressBar);
                    TextView proteinProgressText = findViewById(R.id.proteinProgressText);
                    proteinProgressBar.setProgress(proteinRatio); // 단백질 비율로 프로그레스 설정
                    proteinProgressText.setText("단백질: " + proteinRatio + "g"); // 단백질 비율로 텍스트 설정

                    // 지방 원형 프로그레스 바 및 텍스트 설정
                    ProgressBar fatProgressBar = findViewById(R.id.fatProgressBar);
                    TextView fatProgressText = findViewById(R.id.fatProgressText);
                    fatProgressBar.setProgress(fatRatio); // 지방 비율로 프로그레스 설정
                    fatProgressText.setText("지방: " + fatRatio + "g"); // 지방 비율로 텍스트 설정
                }
            }
        }


        ToggleButton dontLoveButton = findViewById(R.id.dontlove);


        dontLoveButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 하트가 금가는 애니메이션 설정
            if (isChecked) {
                dontLoveButton.setBackgroundResource(R.drawable.button_broken_heart);
                Drawable drawable = dontLoveButton.getBackground();
                if (drawable instanceof AnimatedVectorDrawableCompat) {
                    ((AnimatedVectorDrawableCompat) drawable).start();
                } else if (drawable instanceof AnimatedVectorDrawable) {
                    ((AnimatedVectorDrawable) drawable).start();
                }
            } else {
                dontLoveButton.setBackgroundResource(R.drawable.button_favorite_full);
            }
        });
    }
}
