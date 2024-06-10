package com.example.mechu_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class Search extends AppCompatActivity {
    ImageView search_search1, backButton23;
    LinearLayout search_search;
    View black_line;
    EditText edittext;
    Button deleteAllButton;
    ChipGroup chipGroup;
    Button cancel;

    private DatabaseHelper dbHelper;
    private static final String KEY_CHIPS = "chips";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_search1 = findViewById(R.id.search_search1);
        black_line = findViewById(R.id.black_line);
        edittext = findViewById(R.id.edittext);
        search_search = findViewById(R.id.search_search);
        deleteAllButton = findViewById(R.id.deleteAllButton);
        cancel = findViewById(R.id.cancel);
        backButton23 = findViewById(R.id.backButton23);
        chipGroup = findViewById(R.id.chip_group);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("SearchPrefs", MODE_PRIVATE);

        black_line.setVisibility(View.INVISIBLE);

        loadChips();

        backButton23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SearchActivity", "Back button clicked");
                Intent it = new Intent(Search.this, MainActivity.class);
                startActivity(it);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Search.this, MainActivity.class);
                startActivity(it);
            }
        });

        search_search.setOnClickListener(new View.OnClickListener() {
            private boolean isClicked = false;

            @Override
            public void onClick(View v) {
                if (isClicked) {
                    return;
                }

                isClicked = true;
                handleSearchClick();
            }
        });

        search_search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                String userId = userPrefs.getString("user_id", null);

                if (userId != null) {
                    dbHelper.hideAllSearchRecords(userId);
                }

                chipGroup.removeAllViews();
                saveChips();
            }
        });
    }

    private void addChip(String text) {
        if (isChipExists(text)) {
            return;
        }

        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                saveChips();
            }
        });

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipClick(chip.getText().toString());
            }
        });

        chipGroup.addView(chip);
    }

    private boolean isChipExists(String text) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(text)) {
                return true;
            }
        }
        return false;
    }

    private void handleChipClick(String chipText) {
        edittext.setText(chipText);
        handleSearchClick();
    }

    private void handleSearchClick() {
        black_line.setVisibility(View.VISIBLE);
        backButton23.setVisibility(View.VISIBLE);

        search_search1.animate()
                .translationX(850)
                .translationY(30)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        cancel.animate()
                .translationX(300)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        black_line.setTranslationX(-850);
        ViewGroup.LayoutParams params = black_line.getLayoutParams();
        params.width = 850;
        black_line.setLayoutParams(params);

        black_line.animate()
                .translationX(0)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        black_line.setVisibility(View.INVISIBLE);
                        edittext.setVisibility(View.VISIBLE);
                        edittext.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                })
                .start();
    }

    private void saveChips() {
        Set<String> chipsSet = new HashSet<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chipsSet.add(chip.getText().toString());
        }
        sharedPreferences.edit().putStringSet(KEY_CHIPS, chipsSet).apply();
    }

    private void loadChips() {
        chipGroup.removeAllViews();
        Set<String> chipsSet = sharedPreferences.getStringSet(KEY_CHIPS, new HashSet<>());
        for (String chipText : chipsSet) {
            addChip(chipText);
        }
    }

    private void performSearch() {
        String searchText = edittext.getText().toString().trim();
        Log.d("performSearch", "Search text: " + searchText);

        if (searchText.isEmpty()) {
            Toast.makeText(this, "메츄가 검색어 입력 안하면 혼내", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout noResult = findViewById(R.id.noresult);
        LinearLayout favoritesearch = findViewById(R.id.favoritesearch);

        SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = userPrefs.getString("user_id", null);

        if (userId != null) {
            dbHelper.insertOrUpdateSearchRecord(userId, searchText);
            Log.d("performSearch", "Inserted or updated search record for user: " + userId);
        }

        Cursor cursor = dbHelper.getFoodInfo(searchText);

        if (cursor != null && cursor.getCount() > 0) {
            Log.d("performSearch", "Search results found: " + cursor.getCount());
            if (!isChipExists(searchText)) {
                addChip(searchText);
                saveChips();
            }
            edittext.setText("");

            Intent intent = new Intent(Search.this, SearchResult.class);
            intent.putExtra("SEARCH_TERM", searchText);
            startActivity(intent);
        } else {
            Log.d("performSearch", "No search results found.");
            noResult.setVisibility(View.VISIBLE);
            favoritesearch.setVisibility(View.INVISIBLE);
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}