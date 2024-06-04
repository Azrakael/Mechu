package com.example.mechu_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MenuDetail extends AppCompatActivity {

    // 하트 클릭시 색이 채워지는 애니메이션 추가 효과
    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;

    Button button1;
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    String foodName;
    int calorie;
    int carbohydrateRatio;
    int proteinRatio;
    int fatRatio;

    private static final String TAG = "MenuDetail";
    private TextView menuDetailTextView, loadingTextView;
    private ProgressBar proteinProgressBar, carbsProgressBar, fatProgressBar;
    private TextView proteinProgressText, carbsProgressText, fatProgressText;
    private Handler handler;
    private Runnable updateMessageRunnable;
    private BottomSheetDialog bottomSheetDialog;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String MY_SECRET_KEY = "sk-ㄴㄴ";
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        if (intent != null) {
            foodName = intent.getStringExtra("FOOD_NAME");
            calorie = intent.getIntExtra("CALORIE", 0);
            carbohydrateRatio = intent.getIntExtra("CARBS", 0);
            proteinRatio = intent.getIntExtra("PROTEIN", 0);
            fatRatio = intent.getIntExtra("FAT", 0);
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

                    // 탄수화물 프로그레스 바 및 텍스트 설정
                    ProgressBar carbsProgressBar = findViewById(R.id.carbsProgressBar);
                    TextView carbsProgressText = findViewById(R.id.carbsProgressText);
                    carbsProgressBar.setProgress(carbohydrateRatio);
                    carbsProgressText.setText("탄수화물: " + carbohydrateRatio + "g");

                    // 단백질 프로그레스 바 및 텍스트 설정
                    ProgressBar proteinProgressBar = findViewById(R.id.proteinProgressBar);
                    TextView proteinProgressText = findViewById(R.id.proteinProgressText);
                    proteinProgressBar.setProgress(proteinRatio);
                    proteinProgressText.setText("단백질: " + proteinRatio + "g");

                    // 지방 프로그레스 바 및 텍스트 설정
                    ProgressBar fatProgressBar = findViewById(R.id.fatProgressBar);
                    TextView fatProgressText = findViewById(R.id.fatProgressText);
                    fatProgressBar.setProgress(fatRatio);
                    fatProgressText.setText("지방: " + fatRatio + "g");
                }
            }
        }

        // 섭취 방법을 가져오기 위한 API 호출
        menuDetailTextView = findViewById(R.id.menuDetail);
        loadingTextView = findViewById(R.id.loadingTextView);
        handler = new Handler();

        // 초기화 로딩 메시지
        loadingTextView.setVisibility(View.VISIBLE);
        loadingTextView.setText("당신만을 위한 맞춤 섭취 방법을 작성중이에요~😊");
        startTextAnimation(loadingTextView);

        updateMessageRunnable = new Runnable() {
            @Override
            public void run() {
                loadingTextView.setText("앞으로...조금만 더..거의 다썼어요!😫");
                startTextAnimation(loadingTextView);
            }
        };
        handler.postDelayed(updateMessageRunnable, 5000);

        callAPIForDetails(foodName);

        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bottom Sheet Dialog를 생성하고 설정
                bottomSheetDialog = new BottomSheetDialog(MenuDetail.this);
                bottomSheetDialog.setContentView(R.layout.activity_bottom_sheet);

                Button buttonBreakfast = bottomSheetDialog.findViewById(R.id.buttonBreakfast);
                Button buttonLunch = bottomSheetDialog.findViewById(R.id.buttonLunch);
                Button buttonDinner = bottomSheetDialog.findViewById(R.id.buttonDinner);

                // 클릭 리스너 추가
                buttonBreakfast.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleMealLog("아침");
                    }
                });

                buttonLunch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleMealLog("점심");
                    }
                });

                buttonDinner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleMealLog("저녁");
                    }
                });

                bottomSheetDialog.show();
            }
        });

        // 클릭시 하트가 채워지는 부분 지속시간
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator); // 바운스 효과
    }

    private void callAPIForDetails(String menuName) {
        String userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getUserInfo(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String exerciseType = cursor.getString(cursor.getColumnIndexOrThrow("exercise_type"));
            double dailyCalorie = cursor.getDouble(cursor.getColumnIndexOrThrow("daily_calorie"));
            double dailyCarbs = cursor.getDouble(cursor.getColumnIndexOrThrow("daily_carbs"));
            double dailyProtein = cursor.getDouble(cursor.getColumnIndexOrThrow("daily_protein"));
            double dailyFat = cursor.getDouble(cursor.getColumnIndexOrThrow("daily_fat"));

            String prompt = createPrompt(menuName, exerciseType, dailyCalorie, dailyCarbs, dailyProtein, dailyFat);
            Log.d(TAG, "API call with prompt: " + prompt);

            JSONObject jsonBody = new JSONObject();
            JSONArray messagesArray = new JSONArray();
            try {
                JSONObject message = new JSONObject();
                message.put("role", "user");
                message.put("content", prompt);
                messagesArray.put(message);

                jsonBody.put("model", "gpt-4o");
                jsonBody.put("messages", messagesArray);
            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MenuDetail.this, "Failed to create JSON body: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    stopTextAnimation(loadingTextView);
                    loadingTextView.setVisibility(View.GONE);
                    menuDetailTextView.setText("상세 설명을 가져오는데 실패했습니다.");
                    menuDetailTextView.setVisibility(View.VISIBLE);
                });
                return;
            }

            Log.d(TAG, "JSON body created: " + jsonBody.toString());

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + MY_SECRET_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Failed to fetch details: " + e.getMessage(), e);
                    runOnUiThread(() -> {
                        Toast.makeText(MenuDetail.this, "Failed to fetch details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        stopTextAnimation(loadingTextView);
                        loadingTextView.setVisibility(View.GONE);
                        menuDetailTextView.setText("상세 설명을 가져오는데 실패했습니다.");
                        menuDetailTextView.setVisibility(View.VISIBLE);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBodyString = response.body().string();
                            Log.d(TAG, "API response: " + responseBodyString);
                            JSONObject jsonObject = new JSONObject(responseBodyString);
                            JSONArray choices = jsonObject.getJSONArray("choices");
                            String result = choices.getJSONObject(0).getJSONObject("message").getString("content");
                            String friendlyResult = makeMessageFriendly(result.trim());
                            runOnUiThread(() -> {
                                stopTextAnimation(loadingTextView);
                                loadingTextView.setVisibility(View.GONE);
                                menuDetailTextView.setText(friendlyResult);
                                menuDetailTextView.setVisibility(View.VISIBLE);
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error processing response: " + e.getMessage(), e);
                            runOnUiThread(() -> {
                                Toast.makeText(MenuDetail.this, "Error processing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                stopTextAnimation(loadingTextView);
                                loadingTextView.setVisibility(View.GONE);
                                menuDetailTextView.setText("상세 설명을 처리하는 중 오류가 발생했습니다.");
                                menuDetailTextView.setVisibility(View.VISIBLE);
                            });
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch details, response code: " + response.code());
                        runOnUiThread(() -> {
                            Toast.makeText(MenuDetail.this, "Failed to fetch details", Toast.LENGTH_SHORT).show();
                            stopTextAnimation(loadingTextView);
                            loadingTextView.setVisibility(View.GONE);
                            menuDetailTextView.setText("상세 설명을 가져오는데 실패했습니다.");
                            menuDetailTextView.setVisibility(View.VISIBLE);
                        });
                    }
                }
            });
        } else {
            Toast.makeText(this, "User information not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private String createPrompt(String menuName, String exerciseType, double dailyCalorie, double dailyCarbs, double dailyProtein, double dailyFat) {
        String prompt = "Please provide detailed intake instructions for the following menu item in Korean: " + menuName + ".\n";
        prompt += "User's exercise type: " + exerciseType + ".\n";
        prompt += "Daily intake goals - Calories: " + dailyCalorie + " kcal, Carbohydrates: " + dailyCarbs + " g, Protein: " + dailyProtein + " g, Fat: " + dailyFat + " g.\n";

        switch (exerciseType) {
            case "일반":
                prompt += "Provide general intake instructions for a regular user. Suggest good complementary dishes and dishes to avoid.";
                break;
            case "다이어트":
                prompt += "Provide intake instructions for a user on a diet. Mention any components of the dish to avoid and recommended exercises.";
                break;
            case "벌크업":
                prompt += "Provide intake instructions for a user trying to bulk up. Mention how to consume this dish for optimal results.";
                break;
            default:
                prompt += "Provide general intake instructions.";
                break;
        }

        prompt += "\nPlease provide the response in Korean.";
        return prompt;
    }

    private String makeMessageFriendly(String message) {
        String friendlyMessage = message + "\n\n맛있게 드세요! 😊";
        return friendlyMessage;
    }

    private void startTextAnimation(TextView textView) {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500); // 1.5초 동안 페이드 인/아웃
        fadeIn.setRepeatCount(Animation.INFINITE);
        fadeIn.setRepeatMode(Animation.REVERSE);
        textView.startAnimation(fadeIn);
    }

    private void stopTextAnimation(TextView textView) {
        textView.clearAnimation();
    }

    private void handleMealLog(String mealTime) {
        String userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        String mealDate = getCurrentDate();
        int foodNum = getFoodNumFromFoodName(foodName);

        if (foodNum == -1) {
            Toast.makeText(this, "Food Number not found for the given Food Name.", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.insertMealLog(db, userId, mealDate, mealTime, foodNum);

        // Intent에서 음식의 영양 정보를 가져옴
        int calorie = getIntent().getIntExtra("CALORIE", 0);
        int carbs = getIntent().getIntExtra("CARBS", 0);
        int protein = getIntent().getIntExtra("PROTEIN", 0);
        int fat = getIntent().getIntExtra("FAT", 0);

        dbHelper.updateUserIntake(db, userId, calorie, carbs, protein, fat);

        Toast.makeText(this, mealTime + " 식단에 추가되었습니다.", Toast.LENGTH_SHORT).show();

        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private int getFoodNumFromFoodName(String foodName) {
        int foodNum = -1;
        Cursor cursor = db.rawQuery("SELECT food_num FROM food WHERE food_name = ?", new String[]{foodName});
        if (cursor.moveToFirst()) {
            foodNum = cursor.getInt(cursor.getColumnIndex("food_num"));
        }
        cursor.close();
        return foodNum;
    }

    public void onFavoriteButtonClick(View view) {
        // 클릭한 버튼에 애니메이션 적용
        view.startAnimation(scaleAnimation);
    }
}
