package com.example.mechu_project;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Recommend extends AppCompatActivity {

    private TextView moodBox, weatherBox;
    private ConstraintLayout mainLayout;
    private TextView option1, option2, option3, option4;
    private static final String TAG = "RecommendActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        moodBox = findViewById(R.id.moodBox);
        weatherBox = findViewById(R.id.weatherBox);
        mainLayout = findViewById(R.id.mainLayout);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        moodBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMainViews();
                showOptions(new String[]{"화남", "기분좋음", "우울함", "지루함"}, -1000f, "mood");
            }
        });

        weatherBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMainViews();
                showOptions(new String[]{"쾌청", "비", "눈", "바람"}, 1000f, "weather");
            }
        });

        findViewById(R.id.tellUsContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recommend.this, Chatting.class);
                startActivity(intent);
            }
        });
    }

    private void hideMainViews() {
        moodBox.setVisibility(View.GONE);
        weatherBox.setVisibility(View.GONE);
        findViewById(R.id.tellUsContainer).setVisibility(View.GONE);
        findViewById(R.id.infoText).setVisibility(View.GONE);
    }

    private void showOptions(String[] options, float fromX, final String tagType) {
        TextView[] optionViews = {option1, option2, option3, option4};

        for (int i = 0; i < options.length; i++) {
            TextView option = optionViews[i];
            option.setText(options[i]);
            option.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(option, "translationX", fromX, 0f);
            animator.setDuration(500);
            animator.start();

            final String selectedOption = options[i];
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Selected " + tagType + ": " + selectedOption);
                    Intent intent = new Intent(Recommend.this, RecommendedItemActivity.class);
                    intent.putExtra("TAG_TYPE", tagType);
                    intent.putExtra("TAG_VALUE", selectedOption);
                    startActivity(intent);
                }
            });
        }
    }
}
