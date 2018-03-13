package com.brightoil.mapdemo.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.brightoil.mapdemo.activity.R;

/**
 * Created by haizhong.lan on 7/18/2017.
 */

public class InvertedTriangleView extends View {

    private @ColorInt int lineColor;

    private @ColorInt int solidColor;

    private Paint paint = new Paint();
    private Path path = new Path();

    public InvertedTriangleView(Context context) {
        this(context, null);
    }

    public InvertedTriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InvertedTriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InvertedTriangleView);
        lineColor = a.getColor(R.styleable.InvertedTriangleView_line_color, context.getResources().getColor(R.color.white));
        solidColor = a.getColor(R.styleable.InvertedTriangleView_solid_color, context.getResources().getColor(R.color.white));
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(solidColor);

        float startX = getPaddingLeft();
        float startY = getPaddingTop();

        float secondX = getWidth() + getPaddingRight();
        float secondY = getPaddingTop();

        float thirdX = (getWidth() - getPaddingRight() - getPaddingLeft()) / 2 + getPaddingLeft();
        float thirdY = getHeight() - getPaddingBottom();

        path.moveTo(startX, 0 + startY);// 此点为多边形的起点
        path.lineTo(secondX, secondY);
        path.lineTo(thirdX, thirdY);
        path.close(); // 使这些点构成封闭的多边形

        canvas.drawPath(path, paint);

        paint.setColor(lineColor);
        paint.setStrokeWidth(2);
        // canvas.drawLine(startX,startY,secondX,startY,p);不画这条线
        canvas.drawLine(secondX, secondY, thirdX, thirdY, paint);
        canvas.drawLine(thirdX, thirdY, startX, startY, paint);

    }
}
