package com.example.mechu_project;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mechu_project.R;

public class FoodRecordDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_record_detail);

        // Intent로부터 데이터 가져오기
        String mealType = getIntent().getStringExtra("MEAL_TYPE");

        // TextView에 데이터 설정
        if (mealType != null) {
            LinearLayout top_Layout = findViewById(R.id.top);
            LinearLayout mid_Layout = findViewById(R.id.mid);
            if(mealType.equals("아침")){
                addDynamicImageView(top_Layout, R.drawable.basic_breakfast_food);
                addDynamicTextView(mid_Layout, mealType);
            }
            if(mealType.equals("점심")){
                addDynamicImageView(top_Layout, R.drawable.basic_launch_food);
                addDynamicTextView(mid_Layout, mealType);
            }
            if(mealType.equals("저녁")){
                addDynamicImageView(top_Layout, R.drawable.basic_dinner_food);
                addDynamicTextView(mid_Layout, mealType);
            }
        }
        else {
            showMessage("MEAL_TYPE 정보가 없습니다.");
        }


    }

    // 동적으로 TextView를 추가하는 메서드
    private void addDynamicTextView(LinearLayout layout, String text) {
        // 새로운 TextView 생성
        TextView dynamicTextView = new TextView(this);

        // TextView에 텍스트 설정
        dynamicTextView.setText(text);

        // TextView에 레이아웃 파라미터 설정
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dynamicTextView.setTextSize(25); // 텍스트 크기 설정
        dynamicTextView.setTypeface(null, Typeface.BOLD);

        // TextView를 layout에 추가
        layout.addView(dynamicTextView, 0); // index가 1인 위치에 추가
    }

    //동적으로 ImageView를 추가하는 메서드
    private void addDynamicImageView(LinearLayout layout, int imageResource) {
        // 새로운 ImageView 생성
        ImageView dynamicImageView = new ImageView(this);

        // 이미지 설정
        dynamicImageView.setImageResource(imageResource);

        // ImageView에 레이아웃 파라미터 설정
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        layoutParams.setMargins(16, 0, 0, 0); // 왼쪽 여백 설정
        dynamicImageView.setLayoutParams(layoutParams);

        // ImageView를 layout에 추가
        layout.addView(dynamicImageView, 0);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
