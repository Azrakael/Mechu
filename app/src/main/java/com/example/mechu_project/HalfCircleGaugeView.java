package com.example.mechu_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HalfCircleGaugeView extends View {
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private RectF gaugeRect;

    public HalfCircleGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#e8d7c9")); // 배경색
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(40);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(Color.parseColor("#f6a44c")); // 전경색
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(40);
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
        // 전경 원호
        canvas.drawArc(gaugeRect, 180, 90, false, foregroundPaint); // 50% 진행률 예시
    }
}