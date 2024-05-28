package com.example.mechu_project;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class ShowDetail extends AppCompatActivity {

    private static final String TAG = "ShowDetail";
    private ImageView menuImageView;
    private TextView menuTitleTextView, menuCalorieTextView, menuProteinTextView, menuFatTextView, menuCarbohydrateTextView, menuDetailTextView, loadingTextView;
    private ProgressBar proteinProgressBar, carbsProgressBar, fatProgressBar;
    private TextView proteinProgressText, carbsProgressText, fatProgressText;
    private Button button1; // 식단 추가 버튼
    private BottomSheetDialog bottomSheetDialog;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String MY_SECRET_KEY = "ssss";
    OkHttpClient client;
    private Handler handler;
    private Runnable updateMessageRunnable;

    DatabaseHelper dbHelper;
    String foodName;
    int calorie;
    int carbohydrateRatio;
    int proteinRatio;
    int fatRatio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        menuImageView = findViewById(R.id.menuImage);
        menuTitleTextView = findViewById(R.id.menuTitle);
        menuCalorieTextView = findViewById(R.id.menuCal);
        menuProteinTextView = findViewById(R.id.menuProtein);
        menuFatTextView = findViewById(R.id.menuFat);
        menuCarbohydrateTextView = findViewById(R.id.menuCarbohydrate);
        menuDetailTextView = findViewById(R.id.menuDetail);
        loadingTextView = findViewById(R.id.loadingTextView);
        proteinProgressBar = findViewById(R.id.proteinProgressBar);
        carbsProgressBar = findViewById(R.id.carbsProgressBar);
        fatProgressBar = findViewById(R.id.fatProgressBar);
        proteinProgressText = findViewById(R.id.proteinProgressText);
        carbsProgressText = findViewById(R.id.carbsProgressText);
        fatProgressText = findViewById(R.id.fatProgressText);
        button1 = findViewById(R.id.button1); // 식단 추가 버튼

        handler = new Handler();

        // 인텐트에서 데이터 가져오기
        String menuName = getIntent().getStringExtra("menuName");
        Log.d(TAG, "Received menuName: " + menuName);

        dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getFoodDetails(menuName);

        if (cursor != null && cursor.moveToFirst()) {
            foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
            String foodImg = cursor.getString(cursor.getColumnIndexOrThrow("food_img"));
            calorie = cursor.getInt(cursor.getColumnIndexOrThrow("calorie"));
            carbohydrateRatio = cursor.getInt(cursor.getColumnIndexOrThrow("carbs"));
            proteinRatio = cursor.getInt(cursor.getColumnIndexOrThrow("protein"));
            fatRatio = cursor.getInt(cursor.getColumnIndexOrThrow("fat"));

            // 데이터 설정
            menuTitleTextView.setText(foodName);
            menuCalorieTextView.setText(String.format("%s kcal", calorie));
            menuCarbohydrateTextView.setText(String.format("탄수화물: %s g", carbohydrateRatio));
            menuProteinTextView.setText(String.format("단백질: %s g", proteinRatio));
            menuFatTextView.setText(String.format("지방: %s g", fatRatio));
            Log.d(TAG, "Set food details: " + foodName + ", " + calorie + " kcal, " + carbohydrateRatio + "g carbs, " + proteinRatio + "g protein, " + fatRatio + "g fat");

            setCircularProgress(proteinProgressBar, proteinRatio, 50, proteinProgressText, "단백질");
            setCircularProgress(carbsProgressBar, carbohydrateRatio, 300, carbsProgressText, "탄수화물");
            setCircularProgress(fatProgressBar, fatRatio, 70, fatProgressText, "지방");

            if (foodImg != null) {
                Bitmap bitmap = ImageUtils.loadBitmapFromFile(this, foodImg);
                if (bitmap != null) {
                    menuImageView.setImageBitmap(bitmap);
                } else {
                    menuImageView.setImageResource(R.drawable.characterlogo); // 기본 이미지 설정
                }
                Log.d(TAG, "Loaded image from: " + foodImg);
            } else {
                menuImageView.setImageResource(R.drawable.characterlogo); // 기본 이미지 설정
                Log.d(TAG, "No image found, using default");
            }

            cursor.close();

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
        } else {
            Log.d(TAG, "No details found for menuName: " + menuName);
        }

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateMessageRunnable != null) {
                    handler.removeCallbacks(updateMessageRunnable);
                }
                finish(); // 현재 액티비티 종료
                Log.d(TAG, "Back button clicked, finishing activity");
            }
        });

        // 식단 추가 버튼 클릭 리스너 설정
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(ShowDetail.this);
                bottomSheetDialog.setContentView(R.layout.activity_bottom_sheet);

                Button buttonBreakfast = bottomSheetDialog.findViewById(R.id.buttonBreakfast);
                Button buttonLunch = bottomSheetDialog.findViewById(R.id.buttonLunch);
                Button buttonDinner = bottomSheetDialog.findViewById(R.id.buttonDinner);

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
    }

    private void setCircularProgress(ProgressBar progressBar, double value, double maxValue, TextView textView, String label) {
        int progress = (int) ((value / maxValue) * 100);
        progressBar.setProgress(progress);
        textView.setText(String.format("%.0fg/%.0fg", value, maxValue));
        Log.d(TAG, "Set circular progress for " + label + ": " + value + "/" + maxValue);
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
                    Toast.makeText(ShowDetail.this, "Failed to create JSON body: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ShowDetail.this, "Failed to fetch details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ShowDetail.this, "Error processing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                stopTextAnimation(loadingTextView);
                                loadingTextView.setVisibility(View.GONE);
                                menuDetailTextView.setText("상세 설명을 처리하는 중 오류가 발생했습니다.");
                                menuDetailTextView.setVisibility(View.VISIBLE);
                            });
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch details, response code: " + response.code());
                        runOnUiThread(() -> {
                            Toast.makeText(ShowDetail.this, "Failed to fetch details", Toast.LENGTH_SHORT).show();
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

        dbHelper.insertMealLog(dbHelper.getWritableDatabase(), userId, mealDate, mealTime, foodNum);
        dbHelper.updateUserIntake(dbHelper.getWritableDatabase(), userId, calorie, carbohydrateRatio, proteinRatio, fatRatio);

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
        Cursor cursor = dbHelper.getWritableDatabase().rawQuery("SELECT food_num FROM food WHERE food_name = ?", new String[]{foodName});
        if (cursor.moveToFirst()) {
            foodNum = cursor.getInt(cursor.getColumnIndex("food_num"));
        }
        cursor.close();
        return foodNum;
    }
}
