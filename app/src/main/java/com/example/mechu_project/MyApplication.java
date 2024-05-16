package com.example.mechu_project;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

public class MyApplication extends Application {
    private static DatabaseHelper dbHelper;
    private static SQLiteDatabase db;
    private static final String PREFS_NAME = "DatabasePrefs";
    private static final String KEY_DB_CREATED = "isDatabaseCreated";

    @Override
    public void onCreate() {
        super.onCreate();

        // SharedPreferences를 사용하여 데이터베이스 생성 여부 확인
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDatabaseCreated = prefs.getBoolean(KEY_DB_CREATED, false);

        dbHelper = new DatabaseHelper(this);

        if (!isDatabaseCreated) {
            // 데이터베이스 삭제 및 재생성
            dbHelper.deleteDatabase(this);
            dbHelper = new DatabaseHelper(this); // 다시 초기화
            db = dbHelper.getWritableDatabase(); // 데이터베이스 열기

            // 데이터베이스 생성 완료 상태 저장
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_DB_CREATED, true);
            editor.apply();
        } else {
            db = dbHelper.getWritableDatabase(); // 데이터베이스 열기
        }
    }

    public static SQLiteDatabase getDatabase() {
        return db;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (db != null && db.isOpen()) {
            db.close(); // 앱 종료 시 데이터베이스 닫기
        }
    }
}
