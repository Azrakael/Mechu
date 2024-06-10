package com.example.mechu_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressView extends View {
    private Paint paint;
    private float progress = 0;
    private int max = 100;

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.black));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        int x = width / 2;
        int y = height / 2;

        // 3D 효과
        canvas.save();
        canvas.rotate(-45, x, y);
        canvas.scale(1.2f, 0.8f, x, y);
        RectF oval = new RectF(x - size / 2, y - size / 2, x + size / 2, y + size / 2);
        canvas.drawArc(oval, 270, 360 * (progress / max), false, paint);
        canvas.restore();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }
}
