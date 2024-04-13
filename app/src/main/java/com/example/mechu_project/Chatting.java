package com.example.mechu_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mechu_project.adapter.MessageAdapter;
import com.example.mechu_project.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chatting extends AppCompatActivity {

    RecyclerView recyclerView;
//    TextView tvWelcome;
    EditText etMsg;
    ImageButton btnSend;

    List<Message> messageList;
    MessageAdapter messageAdapter;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;

    private static final String MY_SECRET_KEY = "sss";
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
//        tvWelcome = findViewById(R.id.tvWelcome);
        etMsg = findViewById(R.id.etMsg);
        btnSend = findViewById(R.id.btnSend);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
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
//                tvWelcome.setVisibility(View.GONE);
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
        });
    }

    void callAPI(String question) {
        messageList.add(new Message("...", Message.SENT_BY_SYSTEM));

        List<String> menuList = new ArrayList<>(Arrays.asList(
                "치킨", "피자", "초콜렛", "커피", "샐러드", "김치찌개", "청국장", "된장찌개",
                "라면", "불고기", "비빔밥", "갈비", "떡볶이", "잡채", "파스타", "리조또",
                "스테이크", "햄버거", "샌드위치", "카레", "타코", "부리토", "스시", "돈가스",
                "치즈케이크", "애플파이", "와플", "펜케이크", "브라우니", "마카롱",
                "포테이토칩", "나초", "치킨윙", "치킨너겟", "피쉬앤칩스", "피쉬스테이크",
                "토스트", "오믈렛", "프렌치토스트", "크로와상", "바게트", "소세지",
                "해물파스타", "해물리조또", "해물찜", "굴비정식", "생선구이", "계란찜",
                "감자탕", "추어탕", "육개장", "갈비탕", "순두부찌개", "보쌈", "족발",
                "막국수", "냉면", "빈대떡"
        ));
        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();
        JSONArray arr = new JSONArray();
        
        //프롬프트
        String prompt = "Based on the user's mood or situation, recommend a menu item from the list: " +
                String.join(", ", menuList) + ". Please format the recommendation by enclosing the menu item in asterisks, like so: **menu item**. Provide a reason for your recommendation.";

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
}