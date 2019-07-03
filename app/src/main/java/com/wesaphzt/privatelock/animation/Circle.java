package com.wesaphzt.privatelock.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class Circle extends View {

    private static final int START_ANGLE_POINT = 90;

    private final Paint paint;
    private final RectF rect;

    private float angle;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);

        final int strokeWidth = 10;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(strokeWidth);
        //circle color (currently set upon instance)
        //paint.setColor(Color.BLUE);

        //size
        rect = new RectF(strokeWidth, strokeWidth, 600 + strokeWidth, 600 + strokeWidth);

        //initial angle (optional)
        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, true, paint);
    }

    public float getAngle() {
        return angle;
    }

    public void setColor(int r, int g, int b) {
        paint.setColor(Color.rgb(r, g, b));
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}