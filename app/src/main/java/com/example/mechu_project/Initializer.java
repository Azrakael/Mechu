package com.example.mechu_project;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class Initializer extends AppCompatActivity {

    private static final int REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION = 1;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 데이터베이스 인스턴스 가져오기
        db = MyApplication.getDatabase();

        // SharedPreferences에서 user_id 가져오기
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = preferences.getString("user_id", "");

        if (userId.isEmpty()) {
            // user_id가 없으면 기본적으로 Introductory 액티비티 실행
            Intent intent = new Intent(Initializer.this, Introductory.class);
            startActivity(intent);
            finish();
            return;
        }

        // 로그인 체크 확인 쿼리
        Cursor cursor = db.rawQuery("SELECT login_check FROM user WHERE user_id = ?", new String[]{userId});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int loginCheck = cursor.getInt(cursor.getColumnIndex("login_check"));
                cursor.close();

                if (loginCheck == 1) {
                    // login_check가 1이면 Introductory2 액티비티 실행
                    Intent intent = new Intent(Initializer.this, Introductory2.class);
                    startActivity(intent);
                } else {
                    // 그렇지 않다면 Introductory 액티비티 실행
                    Intent intent = new Intent(Initializer.this, Introductory.class);
                    startActivity(intent);
                }
            } else {
                cursor.close();
                // 기본적으로 Introductory 액티비티 실행
                Intent intent = new Intent(Initializer.this, Introductory.class);
                startActivity(intent);
            }
        } else {
            // 기본적으로 Introductory 액티비티 실행
            Intent intent = new Intent(Initializer.this, Introductory.class);
            startActivity(intent);
        }

        // 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION);
            } else {
                setDailyResetAlarm();
            }
        } else {
            setDailyResetAlarm();
        }

        finish(); // Initializer 액티비티를 종료
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SCHEDULE_EXACT_ALARM_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setDailyResetAlarm();
            } else {
                // 권한이 거부된 경우 처리
            }
        }
    }

    private void setDailyResetAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, DailyResetReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 매일 0시에 실행
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 현재 시간보다 늦은 0시로 설정
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        }
    }
}
