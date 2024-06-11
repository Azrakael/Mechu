package com.example.mechu_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LikeList extends AppCompatActivity {

    LinearLayout linearLayoutContainer;
    DatabaseHelper dbHelper;
    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;
    ImageView backbutton, logoImage;
    TextView noLikesMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_list);

        linearLayoutContainer = findViewById(R.id.linearLayoutContainer);
        dbHelper = new DatabaseHelper(this);

        ArrayList<String> likedFoods = getIntent().getStringArrayListExtra("liked_foods");

        // 좋아요 애니메이션 초기화
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        noLikesMessage = findViewById(R.id.no_likes_message); // 좋아요 없는 경우 메시지 TextView

        if (likedFoods != null && !likedFoods.isEmpty()) {
            for (String foodName : likedFoods) {
                addFoodDetail(foodName);
            }
        } else {
            noLikesMessage.setVisibility(View.VISIBLE); // 좋아요한 음식이 없을 경우 메시지 표시
        }

        // 뒤로 가기 버튼 및 로고 설정
        backbutton = findViewById(R.id.backButton);
        logoImage = findViewById(R.id.logoImage);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LikeList.this, MainActivity.class);
                startActivity(it);
            }
        });
    }

    private void addFoodDetail(final String foodName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT food_name, food_img, calorie, carbs, protein, fat FROM food WHERE food_name = ?",
                new String[]{foodName});

        if (cursor.moveToFirst()) {
            String foodImgPath = cursor.getString(cursor.getColumnIndex("food_img"));
            double calorie = cursor.getDouble(cursor.getColumnIndex("calorie"));
            int carbs = cursor.getInt(cursor.getColumnIndex("carbs"));
            int protein = cursor.getInt(cursor.getColumnIndex("protein"));
            int fat = cursor.getInt(cursor.getColumnIndex("fat"));

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View foodDetailView = inflater.inflate(R.layout.activity_food_like, null);

            ImageView foodImg = foodDetailView.findViewById(R.id.food_img);
            TextView foodNameView = foodDetailView.findViewById(R.id.food_name);
            TextView calorieView = foodDetailView.findViewById(R.id.calorie);
            ToggleButton loveButton = foodDetailView.findViewById(R.id.love);

            foodNameView.setText(foodName);
            calorieView.setText(String.format("%.0f kcal", calorie));

            // 이미지 설정
            if (foodImgPath != null && !foodImgPath.isEmpty()) {
                Bitmap bitmap = ImageUtils.loadBitmapFromFile(this, foodImgPath);
                if (bitmap != null) {
                    foodImg.setImageBitmap(bitmap);
                } else {
                    // 이미지 로드 실패 시 기본 이미지 설정
                    foodImg.setImageResource(R.drawable.characterlogo);
                }
            } else {
                // 이미지 경로가 비어있을 경우 기본 이미지 설정
                foodImg.setImageResource(R.drawable.characterlogo);
            }

            // 좋아요 버튼 상태 설정
            loveButton.setChecked(dbHelper.isFoodLiked(db, getUserIdFromSharedPreferences(), foodName));

            // 좋아요 버튼 클릭 이벤트 설정
            loveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleLikeButton(loveButton, foodName);
                }
            });

            // 메뉴 아이템 클릭 이벤트 설정
            foodDetailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LikeList.this, ShowDetail.class);
                    intent.putExtra("menuName", foodName);
                    intent.putExtra("menuCalorie", calorie);
                    intent.putExtra("menuImage", foodImgPath);
                    startActivity(intent);
                }
            });

            linearLayoutContainer.addView(foodDetailView);
        }
        cursor.close();
    }

    private void handleLikeButton(ToggleButton heartButton, String foodName) {
        String userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        String likeDate = getCurrentDate();

        if (heartButton.isChecked()) {
            // 좋아요 기록 추가
            dbHelper.insertLike(dbHelper.getWritableDatabase(), userId, likeDate, foodName);
            Toast.makeText(this, foodName + "을(를) 좋아하시군요!", Toast.LENGTH_SHORT).show();
        } else {
            // 좋아요 기록 삭제
            dbHelper.removeLike(dbHelper.getWritableDatabase(), userId, foodName);
            Toast.makeText(this, foodName + "을(를) 좋아요에서 뺐어요", Toast.LENGTH_SHORT).show();
        }

        heartButton.startAnimation(scaleAnimation); // 애니메이션 효과 추가
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
