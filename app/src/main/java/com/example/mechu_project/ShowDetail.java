package com.example.mechu_project;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String MY_SECRET_KEY = "sss";
    OkHttpClient client;
    private Handler handler;
    private Runnable updateMessageRunnable;

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

        handler = new Handler();

        // 인텐트에서 데이터 가져오기
        String menuName = getIntent().getStringExtra("menuName");
        Log.d(TAG, "Received menuName: " + menuName);

        // 데이터베이스에서 데이터 가져오기
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getFoodDetails(menuName);

        if (cursor != null && cursor.moveToFirst()) {
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
            String foodImg = cursor.getString(cursor.getColumnIndexOrThrow("food_img"));
            double calorie = cursor.getDouble(cursor.getColumnIndexOrThrow("calorie"));
            double carbs = cursor.getDouble(cursor.getColumnIndexOrThrow("carbs"));
            double protein = cursor.getDouble(cursor.getColumnIndexOrThrow("protein"));
            double fat = cursor.getDouble(cursor.getColumnIndexOrThrow("fat"));

            // 데이터 설정
            menuTitleTextView.setText(foodName);
            menuCalorieTextView.setText(String.format("%s kcal", calorie));
            menuCarbohydrateTextView.setText(String.format("탄수화물: %s g", carbs));
            menuProteinTextView.setText(String.format("단백질: %s g", protein));
            menuFatTextView.setText(String.format("지방: %s g", fat));
            Log.d(TAG, "Set food details: " + foodName + ", " + calorie + " kcal, " + carbs + "g carbs, " + protein + "g protein, " + fat + "g fat");

            // 원형 프로그레스 바 설정 및 텍스트 업데이트
            setCircularProgress(proteinProgressBar, protein, 50, proteinProgressText, "단백질");
            setCircularProgress(carbsProgressBar, carbs, 300, carbsProgressText, "탄수화물");
            setCircularProgress(fatProgressBar, fat, 70, fatProgressText, "지방");

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

            // 상세 설명 가져오는 중임을 알리는 메시지 설정 및 애니메이션 시작
            loadingTextView.setVisibility(View.VISIBLE);
            loadingTextView.setText("조금만 기다려주세요!! 열심히 적고있어요!!");
            startTextAnimation(loadingTextView);

            // 5초 후 메시지 변경
            updateMessageRunnable = new Runnable() {
                @Override
                public void run() {
                    loadingTextView.setText("거의 다 썼어요!! 조금만 더 기다려주세요ㅜㅜ");
                    startTextAnimation(loadingTextView);
                }
            };
            handler.postDelayed(updateMessageRunnable, 5000);

            // GPT API를 통해 상세 설명 가져오기
            callAPIForDetails(foodName);
        } else {
            Log.d(TAG, "No details found for menuName: " + menuName);
        }

        // 뒤로 가기 버튼 설정
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
        String prompt = "다음 메뉴 항목에 대한 상세 설명을 제공해주세요: " + menuName + ". 맛, 칼로리, 영양 정보에 중점을 두세요.";
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
                        runOnUiThread(() -> {
                            stopTextAnimation(loadingTextView);
                            loadingTextView.setVisibility(View.GONE);
                            menuDetailTextView.setText(result.trim());
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
    }
}
