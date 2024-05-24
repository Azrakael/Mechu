package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

public class MenuDetail extends AppCompatActivity {


    // 하트 클릭시 색이 채워지는 애니메이션 추가 효과
    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;

    Button button1;



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





        // 기존 코드와 함께 아래의 내용을 추가합니다.
        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Bottom Sheet Dialog를 생성하고 설정합니다.
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MenuDetail.this);
                bottomSheetDialog.setContentView(R.layout.activity_bottom_sheet);

                // 아래 버튼들을 가져옵니다.
                Button buttonBreakfast = bottomSheetDialog.findViewById(R.id.buttonBreakfast);
                Button buttonLunch = bottomSheetDialog.findViewById(R.id.buttonLunch);
                Button buttonDinner = bottomSheetDialog.findViewById(R.id.buttonDinner);

                // 아래 버튼들에 클릭 리스너를 추가합니다.
                buttonBreakfast.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 아침 버튼을 클릭했을 때 할 작업을 추가합니다.
                    }
                });

                buttonLunch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 점심 버튼을 클릭했을 때 할 작업을 추가합니다.
                    }
                });

                buttonDinner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 저녁 버튼을 클릭했을 때 할 작업을 추가합니다.
                    }
                });

                // Bottom Sheet Dialog를 보여줍니다.
                bottomSheetDialog.show();
            }
        });

        // 클릭시 하트가 채워지는 부분 지속시간
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator); // 바운스 효과
    }








    public void onFavoriteButtonClick(View view) {
        // 클릭한 버튼에 애니메이션 적용
        view.startAnimation(scaleAnimation);
    }
}
