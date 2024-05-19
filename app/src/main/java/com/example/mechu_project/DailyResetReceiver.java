package com.example.mechu_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DailyResetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 데이터베이스 인스턴스 가져오기
        SQLiteDatabase db = MyApplication.getDatabase();

        // 현재까지 섭취한 칼로리, 탄수화물, 단백질, 지방을 0으로 초기화
        db.execSQL("UPDATE user SET current_calorie = 0, current_carbs = 0, current_protein = 0, current_fat = 0");

        //로그로 확인
        Log.d("DailyResetReceiver", "섭취량이 초기화되었습니다.");
    }
}