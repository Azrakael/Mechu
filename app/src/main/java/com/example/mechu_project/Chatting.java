package com.example.mechu_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mechu_project.adapter.MessageAdapter;
import com.example.mechu_project.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chatting extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText etMsg;
    ImageButton btnSend;

    List<Message> messageList;
    MessageAdapter messageAdapter;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;
    private Handler handler;
    private Runnable typingIndicatorRunnable;
    private int typingIndicatorIndex = 0;

    private static final String MY_SECRET_KEY = "sss";
    private static final String TAG = "Chatting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        recyclerView = findViewById(R.id.recyclerView);
        etMsg = findViewById(R.id.etMsg);
        btnSend = findViewById(R.id.btnSend);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, this);
        recyclerView.setAdapter(messageAdapter);

        handler = new Handler();

        String initialMessage = "안녕하세요!! 저는 당신을 위한 메뉴 추천 도우미 메츄에요!! 😃 어떤 메뉴를 추천해드릴까요? 지금의 기분이나 상황을 말씀해주세요!";
        addToChat(initialMessage, Message.SENT_BY_SYSTEM);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = etMsg.getText().toString().trim();
                if (!question.isEmpty()) {
                    addToChat(question, Message.SENT_BY_USER);
                    etMsg.setText("");
                    callAPI(question);
                }
            }
        });
    }

    void addToChat(String message, Integer sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    void addTypingIndicator() {
        String[] typingIndicatorTexts = {"잠시만 기다려 주세요... 🤔", "잠시만 기다려 주세요... 🤔.", "잠시만 기다려 주세요... 🤔..", "잠시만 기다려 주세요... 🤔..."};
        typingIndicatorRunnable = new Runnable() {
            @Override
            public void run() {
                if (typingIndicatorIndex >= typingIndicatorTexts.length) {
                    typingIndicatorIndex = 0;
                }
                messageList.set(messageList.size() - 1, new Message(typingIndicatorTexts[typingIndicatorIndex], Message.SENT_BY_SYSTEM));
                messageAdapter.notifyItemChanged(messageList.size() - 1);
                typingIndicatorIndex++;
                handler.postDelayed(this, 500);
            }
        };
        handler.post(typingIndicatorRunnable);
    }

    void removeTypingIndicator() {
        handler.removeCallbacks(typingIndicatorRunnable);
    }

    void addResponse(String response) {
        runOnUiThread(() -> {
            removeTypingIndicator();
            messageList.remove(messageList.size() - 1);

            if (validateMenuInResponse(response)) {
                addToChat(response, Message.SENT_BY_SYSTEM);
                extractMenuAndShowDetails(response);
            } else {
                addToChat("잠깐 문제가 생겼어요ㅠㅠ 조금만 더 기다려주세요", Message.SENT_BY_SYSTEM);
                callAPI(messageList.get(messageList.size() - 2).getMessage()); // 이전 질문을 다시 사용
            }
        });
    }

    boolean validateMenuInResponse(String response) {
        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(response);
        boolean isValid = true;
        SQLiteDatabase db = MyApplication.getDatabase();

        while (matcher.find()) {
            String menuName = matcher.group(1);
            Log.d(TAG, "Validating menu: " + menuName); // 메뉴명을 로그에 기록
            Cursor cursor = db.rawQuery("SELECT food_name FROM food WHERE food_name = ?", new String[]{menuName});
            if (!cursor.moveToFirst()) {
                isValid = false;
                Log.d(TAG, "Menu not found in database: " + menuName); // 데이터베이스에서 메뉴를 찾지 못한 경우 로그에 기록
            } else {
                Log.d(TAG, "Menu found in database: " + menuName); // 데이터베이스에서 메뉴를 찾은 경우 로그에 기록
            }
            cursor.close();
        }

        return isValid;
    }

    void extractMenuAndShowDetails(String response) {
        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(response);
        List<String> menuNames = new ArrayList<>();
        while (matcher.find()) {
            String menuName = matcher.group(1);
            menuNames.add(menuName);
            Log.d(TAG, "Extracted menu: " + menuName); // 추출한 메뉴명을 로그에 기록
        }
        if (!menuNames.isEmpty()) {
            for (String menuName : menuNames) {
                showMenuDetails(menuName);
            }
        }
    }

    void showMenuDetails(String menuName) {
        SQLiteDatabase db = MyApplication.getDatabase();
        Cursor cursor = db.rawQuery("SELECT food_name, calorie, food_img FROM food WHERE food_name = ?", new String[]{menuName});
        if (cursor.moveToFirst()) {
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
            double calorie = cursor.getDouble(cursor.getColumnIndexOrThrow("calorie"));
            String foodImgPath = cursor.getString(cursor.getColumnIndexOrThrow("food_img"));

            String imgFilePath = new File(getFilesDir(), "images/" + foodImgPath).getAbsolutePath();

            Log.d(TAG, "Showing details for menu: " + menuName); // 메뉴 상세 정보를 로그에 기록

            Message menuMessage = new Message(null, Message.SENT_BY_SYSTEM, foodName, imgFilePath, calorie);
            runOnUiThread(() -> {
                messageList.add(menuMessage);
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            });
        } else {
            Log.d(TAG, "Menu not found in showMenuDetails: " + menuName); // `showMenuDetails`에서 메뉴를 찾지 못한 경우 로그에 기록
            addToChat("요청하신 메뉴를 찾을 수 없습니다. 😔", Message.SENT_BY_SYSTEM);
        }
        cursor.close();
    }

    void callAPI(String question) {
        messageList.add(new Message("잠시만 기다려 주세요... 🤔", Message.SENT_BY_SYSTEM));
        addTypingIndicator();

        List<String> menuList = getMenuListFromDB();
        String menuListString = String.join(", ", menuList);

        // 사용자 정보 가져오기
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        UserNutritionalInfo userInfo = dbHelper.getLoggedInUserNutritionalInfo();

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You're a friendly assistant helping users choose a menu. Use a casual and friendly tone.");

        if (userInfo != null) {
            promptBuilder.append(" The user is on a ")
                    .append(userInfo.getExerciseType())
                    .append(" regimen. Their daily nutritional goals are ")
                    .append(userInfo.getDailyCalorie())
                    .append(" calories, ")
                    .append(userInfo.getDailyCarbs())
                    .append("g carbs, ")
                    .append(userInfo.getDailyProtein())
                    .append("g protein, and ")
                    .append(userInfo.getDailyFat())
                    .append("g fat. They have currently consumed ")
                    .append(userInfo.getCurrentCalorie())
                    .append(" calories, ")
                    .append(userInfo.getCurrentCarbs())
                    .append("g carbs, ")
                    .append(userInfo.getCurrentProtein())
                    .append("g protein, and ")
                    .append(userInfo.getCurrentFat())
                    .append("g fat. Please recommend a menu item from the list: ")
                    .append(menuListString)
                    .append(". The recommendation should fit within their remaining nutritional goals. Additionally, provide a reason why this menu item is suitable for their ")
                    .append(userInfo.getExerciseType())
                    .append(" goals. Format the menu item as **menu item**.")
                    .append(".Answers must be in Korean.");
        } else {
            promptBuilder.append(" Recommend a menu item from the list: ")
                    .append(menuListString)
                    .append(". Please format the recommendation by enclosing the menu item in asterisks, like so: **menu item**. Provide a reason for your recommendation.")
                    .append(".Answers must be in Korean.");
        }

        String prompt = promptBuilder.toString();

        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();
        JSONArray arr = new JSONArray();

        try {
            baseAi.put("role", "system");
            baseAi.put("content", prompt);
            userMsg.put("role", "user");
            userMsg.put("content", question);
            arr.put(baseAi);
            arr.put(userMsg);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject();
        try {
            object.put("model", "gpt-4o");
            object.put("messages", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("응답을 불러오지 못했습니다. 😢 " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (Response responseBody = response) {
                        JSONObject jsonObject = new JSONObject(responseBody.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        addResponse("응답을 처리하는 중 오류가 발생했습니다. 😢");
                    }
                } else {
                    addResponse("응답을 불러오지 못했습니다. 😢");
                }
            }
        });
    }

    private List<String> getMenuListFromDB() {
        List<String> menuList = new ArrayList<>();
        SQLiteDatabase db = MyApplication.getDatabase();
        Cursor cursor = db.rawQuery("SELECT food_name FROM food", null);
        if (cursor.moveToFirst()) {
            do {
                String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
                menuList.add(foodName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return menuList;
    }
}
