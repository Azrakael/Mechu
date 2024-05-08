package com.example.mechu_project;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;

public class Search extends AppCompatActivity {

    LinearLayout search_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_search = findViewById(R.id.search_search);

        search_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start from position 0
                search_search.setTranslationX(0);

                // Animate smoothly to position 850
                search_search.animate()
                        .translationX(850)
                        .setDuration(500) // Duration can be adjusted based on how fast you want it to move
                        .setInterpolator(new AccelerateDecelerateInterpolator()) // Smooth start and end
                        .start();
            }
        });
    }
}