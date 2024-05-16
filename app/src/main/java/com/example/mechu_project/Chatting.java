package com.example.mechu_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

    private static final String MY_SECRET_KEY = "ss";

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

        String initialMessage = "안녕하세요!! 저는 당신을 위한 메뉴 추천 도우미 메츄에요!! 어떤 메뉴를 추천해드릴까요? 지금의 기분이나 상황을 말씀해주세요!";
        addToChat(initialMessage, Message.SENT_BY_SYSTEM);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = etMsg.getText().toString().trim();
                addToChat(question, Message.SENT_BY_USER);
                etMsg.setText("");
                callAPI(question);
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

    void addResponse(String response) {
        runOnUiThread(() -> {
            messageList.remove(messageList.size() - 1);
            addToChat(response, Message.SENT_BY_SYSTEM);
            extractMenuAndShowDetails(response);
        });
    }

    void extractMenuAndShowDetails(String response) {
        // **메뉴명** 형식을 찾는 정규 표현식
        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String menuName = matcher.group(1);
            showMenuDetails(menuName);
        }
    }

    void showMenuDetails(String menuName) {
        SQLiteDatabase db = MyApplication.getDatabase();
        Cursor cursor = db.rawQuery("SELECT food_name, calorie, food_img FROM food WHERE food_name = ?", new String[]{menuName});
        if (cursor.moveToFirst()) {
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
            double calorie = cursor.getDouble(cursor.getColumnIndexOrThrow("calorie"));
            String foodImgPath = cursor.getString(cursor.getColumnIndexOrThrow("food_img"));

            Message menuMessage = new Message(null, Message.SENT_BY_SYSTEM, foodName, foodImgPath, calorie);
            runOnUiThread(() -> {
                messageList.add(menuMessage);
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

                // 이미지 데이터를 별도로 로드
                loadFoodImage(menuName, messageList.size() - 1);
            });
        }
        cursor.close();
    }

    void loadFoodImage(String menuName, int position) {
        SQLiteDatabase db = MyApplication.getDatabase();
        Cursor cursor = db.rawQuery("SELECT food_img FROM food WHERE food_name = ?", new String[]{menuName});
        if (cursor.moveToFirst()) {
            String foodImgPath = cursor.getString(cursor.getColumnIndexOrThrow("food_img"));
            File imgFile = new File(getFilesDir(), "images/" + foodImgPath); // 이미지 디렉토리 경로 포함

            Bitmap bitmap = null;
            if (imgFile.exists()) {
                bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }

            Bitmap finalBitmap = bitmap;
            runOnUiThread(() -> {
                if (finalBitmap != null) {
                    Toast.makeText(this, "이미지 로드 성공", Toast.LENGTH_SHORT).show();
                    // 이미지를 Message 객체에 추가하고 어댑터를 업데이트
                    messageList.get(position).setFoodImgPath(imgFile.getAbsolutePath());
                    messageAdapter.notifyItemChanged(position);
                } else {
                    Toast.makeText(this, "이미지 로드 실패 혹은 이미지 없음", Toast.LENGTH_SHORT).show();
                }
            });
        }
        cursor.close();
    }

    void callAPI(String question) {
        messageList.add(new Message("...", Message.SENT_BY_SYSTEM));

        List<String> menuList = getMenuListFromDB();
        String menuListString = String.join(", ", menuList);

        String prompt = "Based on the user's mood or situation, recommend a menu item from the list: " +
                menuListString + ". Please format the recommendation by enclosing the menu item in asterisks, like so: **menu item**. Provide a reason for your recommendation.";

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
            object.put("model", "gpt-4-turbo");
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
                addResponse("Failed to load response due to " + e.getMessage());
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
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().string());
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
