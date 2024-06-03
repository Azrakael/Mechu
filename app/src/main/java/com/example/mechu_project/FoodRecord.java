package com.example.mechu_project;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import android.view.View;
import android.widget.LinearLayout;


//메뉴추천
public class FoodRecord extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_record);

        // BottomSheet 설정
        LinearLayout bottomSheet = findViewById(R.id.foodDetailBottomSheet);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // 초기 상태 설정 (더보기만 보이게)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // 클릭 이벤트 설정
        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        LinearLayout morning_layout = findViewById(R.id.morningTab);
        LinearLayout lunch_layout = findViewById(R.id.lunchTab);
        LinearLayout dinner_layout = findViewById(R.id.dinnerTab);

        //"아침"영역에 해당하는 레이아웃 클릭했을 때 "아침" 정보 넘기게 설정
        morning_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodRecord.this, FoodRecordDetail.class);
                intent.putExtra("MEAL_TYPE", "아침"); // "아침" 정보를 인텐트로 넘기기
                startActivity(intent);
            }
        });

        lunch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodRecord.this, FoodRecordDetail.class);
                intent.putExtra("MEAL_TYPE", "점심");
                startActivity(intent);
            }
        });
        dinner_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodRecord.this, FoodRecordDetail.class);
                intent.putExtra("MEAL_TYPE", "저녁");
                startActivity(intent);
            }
        });

        // 드래그 가능 설정
        bottomSheetBehavior.setPeekHeight(170); // 초기 높이 (더보기 높이)
        bottomSheetBehavior.setHideable(false); // 드래그로 숨기기 비활성화
    }
}