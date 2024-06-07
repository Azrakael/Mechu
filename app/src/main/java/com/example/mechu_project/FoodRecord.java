package com.example.mechu_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FoodRecord extends AppCompatActivity {

    ImageView menuPlus1, menuPlus2, menuPlus3, backbutton, logoImage;
    private ProgressBar proteinProgressBar, carbsProgressBar, fatProgressBar;
    private TextView proteinProgressText, carbsProgressText, fatProgressText;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_record);

        menuPlus1 = findViewById(R.id.menuPlus1);
        menuPlus2 = findViewById(R.id.menuPlus2);
        menuPlus3 = findViewById(R.id.menuPlus3);
        backbutton = findViewById(R.id.backButton);
        logoImage = findViewById(R.id.logoImage);
        proteinProgressBar = findViewById(R.id.proteinProgressBar);
        carbsProgressBar = findViewById(R.id.carbsProgressBar);
        fatProgressBar = findViewById(R.id.fatProgressBar);
        proteinProgressText = findViewById(R.id.proteinProgressText);
        carbsProgressText = findViewById(R.id.carbsProgressText);
        fatProgressText = findViewById(R.id.fatProgressText);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(FoodRecord.this, MainActivity.class);
                startActivity(it);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menuPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FoodRecord.this, Search.class);
                startActivity(it);
            }
        });

        menuPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FoodRecord.this, Search.class);
                startActivity(it);
            }
        });

        menuPlus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(FoodRecord.this, Search.class);
                startActivity(it);
            }
        });

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

        // 드래그 가능 설정
        bottomSheetBehavior.setPeekHeight(170); // 초기 높이 (더보기 높이)
        bottomSheetBehavior.setHideable(false); // 드래그로 숨기기 비활성화

        // 섭취 정보를 가져와서 업데이트
        updateIntakeInfo();
    }

    private void updateIntakeInfo() {
        String userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getCurrentIntake(userId);
        if (cursor != null && cursor.moveToFirst()) {
            double currentCalorie = cursor.getDouble(cursor.getColumnIndex("current_calorie"));
            double currentCarbs = cursor.getDouble(cursor.getColumnIndex("current_carbs"));
            double currentProtein = cursor.getDouble(cursor.getColumnIndex("current_protein"));
            double currentFat = cursor.getDouble(cursor.getColumnIndex("current_fat"));

            // ProgressBar 및 TextView 업데이트
            setCircularProgress(proteinProgressBar, currentProtein, 100, proteinProgressText);
            setCircularProgress(carbsProgressBar, currentCarbs, 300, carbsProgressText);
            setCircularProgress(fatProgressBar, currentFat, 70, fatProgressText);

            cursor.close();
        } else {
            Toast.makeText(this, "Failed to load intake data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setCircularProgress(ProgressBar progressBar, double value, double maxValue, TextView textView) {
        int progress = (int) ((value / maxValue) * 100);
        progressBar.setProgress(progress);
        textView.setText(String.format("%.0f", value));
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }
}
