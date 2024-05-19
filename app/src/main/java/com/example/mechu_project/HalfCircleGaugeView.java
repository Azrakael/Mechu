package com.example.mechu_project;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class HalfCircleGaugeView extends View {
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private Paint textPaint;
    private Paint messagePaint;
    private Paint circlePaint;
    private Paint glowPaint;
    private RectF gaugeRect;
    private int dailyCalorie = 2000; // 기본값
    private int currentCalorie = 0;
    private float animatedCalorie = 0;
    private DatabaseHelper dbHelper;

    public HalfCircleGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dbHelper = new DatabaseHelper(context); // DatabaseHelper 초기화
        initPaints();
        loadUserData(context); // 유저 데이터 로드
        startAnimation();
    }

    private void initPaints() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#e8d7c9")); // 배경색
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(20);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(Color.parseColor("#4caf50")); // 전경색 초기값을 초록색으로 설정
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(20);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        messagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        messagePaint.setColor(Color.GRAY);
        messagePaint.setTextSize(40);
        messagePaint.setTextAlign(Paint.Align.CENTER);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL);

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.FILL);
    }

    private void loadUserData(Context context) {
        // 현재 로그인된 사용자의 정보를 가져오기 위해 SharedPreferences 사용
        SharedPreferences preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userId = preferences.getString("user_id", null);

        if (userId != null) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT daily_calorie, current_calorie FROM user WHERE user_id = ?", new String[]{userId});

            if (cursor.moveToFirst()) {
                dailyCalorie = cursor.getInt(cursor.getColumnIndex("daily_calorie"));
                currentCalorie = cursor.getInt(cursor.getColumnIndex("current_calorie"));
            }
            cursor.close();
        }
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, currentCalorie);
        animator.setDuration(2000); // 애니메이션 지속 시간
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            animatedCalorie = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void setCurrentCalorie(int currentCalorie) {
        this.currentCalorie = currentCalorie;
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();

        if (gaugeRect == null) {
            gaugeRect = new RectF(40, height - (width - 80) / 2, width - 40, height + (width - 80) / 2);
        }

        // 배경 원호
        canvas.drawArc(gaugeRect, 180, 180, false, backgroundPaint);

        // 진행률에 따른 원
        float sweepAngle = 180 * (animatedCalorie / dailyCalorie);
        foregroundPaint.setColor(getColorForProgress(animatedCalorie, dailyCalorie));
        canvas.drawArc(gaugeRect, 180, sweepAngle, false, foregroundPaint);


        // 텍스트 표시
        String text = (int) animatedCalorie + "kcal / " + dailyCalorie + "kcal";
        canvas.drawText(text, width / 2, height / 2 + 40, textPaint);

        // 메시지 표시
        String message = getHumorousMessage(dailyCalorie - (int) animatedCalorie);
        canvas.drawText(message, width / 2, height / 2 + 120, messagePaint);
    }

    private String getHumorousMessage(int remainingCalories) {
        if (remainingCalories > 1000) {
            return "아직 배가 고프네요!";
        } else if (remainingCalories > 500) {
            return "조금 더 먹어볼까요?";
        } else if (remainingCalories > 300) {
            return "배가 조금 찬 것 같아요!";
        } else if (remainingCalories > 100) {
            return "이제 슬슬 배부르죠?";
        } else if (remainingCalories > 50) {
            return "거의 다 먹었어요!";
        } else if (remainingCalories > 0) {
            return "마지막 한 입!";
        } else {
            return "이..이제 그만!";
        }
    }

    private int getColorForProgress(float current, int total) {
        float ratio = current / total;
        if (ratio < 0.5) {
            return Color.GREEN;
        } else if (ratio < 0.75) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }
}
